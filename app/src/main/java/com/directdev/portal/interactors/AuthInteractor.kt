package com.directdev.portal.interactors

import android.util.Log
import com.directdev.portal.network.NetworkHelper
import com.directdev.portal.repositories.FlagRepository
import com.directdev.portal.repositories.TimeStampRepository
import com.directdev.portal.repositories.UserCredRepository
import com.directdev.portal.utils.SigninException
import io.reactivex.Single
import org.joda.time.Minutes
import java.net.URLDecoder
import javax.inject.Inject
import javax.inject.Named

/**-------------------------------------------------------------------------------------------------
 * Handles everything related to authenticating with Binusmaya
 *------------------------------------------------------------------------------------------------*/

class AuthInteractor @Inject constructor(
        private val bimayApi: NetworkHelper,
        private val userCredRepo: UserCredRepository,
        private val flagRepo: FlagRepository,
        @Named("auth") private val timeStampRepo: TimeStampRepository
) {
    private var isRequesting = false
    private lateinit var request: Single<String>

    // Regex patterns to extract data from index.html and loader.js
    private val fieldsRegex = """<input type=\"hidden\" name=\"([^\"]*)\" value=\"([^\"]*)\"""".toRegex()
    private val loginRegex = """document\.write\(\"([^\)]+)\"\)""".toRegex()

    // Returns a Single containing the authenticated cookie
    fun execute(
            username: String = userCredRepo.getUsername(),
            password: String = userCredRepo.getPassword()
    ): Single<String> {
        if (!isSyncOverdue()) return Single.just(userCredRepo.getCookie())
        var indexHtml = ""
        var cookie = ""
        request = if (isRequesting) request else bimayApi.getIndexHtml().flatMap {
            indexHtml = it.body()?.string() ?: ""
            it.headers().toMultimap().get("Set-Cookie")?.forEach { s: String? ->
//                cookie += s + "; "
                if(cookie.length==0)
                    cookie += s
            }

            // Extracts the link to loader.php from index.html

            val serial = loginRegex.find(indexHtml)?.groups?.get(1)?.value ?:""
            Log.v("Test", "Cookie: " + cookie)

            // Retrieve loader.js from loader.php
            bimayApi.getLoaderJs(cookie, serial, "https://binusmaya.binus.ac.id/login/")
        }.flatMap {
            val loaderJs = it.body()?.string() ?: ""
            Log.v("Loader", "Lod:" + loaderJs)
            val fieldsMap = constructFields(indexHtml, loaderJs, username, password)

            // Authenticate with Binusmaya using the extracted fields from index.html & loader.js as
            // the request parameter
            bimayApi.authenticate(cookie, fieldsMap)
        }.map {
            // Checks if login is successful
            val redirectLocation = it.headers().get("Location") ?: "none"
            Log.v("Test", "R: " + redirectLocation)
            if (redirectLocation != "https://binusmaya.binus.ac.id/block_user.php")
                throw SigninException(redirectLocation)
        }.flatMap {
            bimayApi.switchRole(cookie)
        }.map {
            cookie
        }.doAfterSuccess {
            userCredRepo.saveAll(username, password, cookie)
            timeStampRepo.updateLastSyncDate()
            flagRepo.save(isLoggedIn = true)
        }.doAfterTerminate {
            isRequesting = false
        }
        isRequesting = true
        return request
    }

    fun resetLastSyncDate() = timeStampRepo.resetLastSyncDate()

    /*----------------------------------------------------------------------------------------------
     * To sign in, 4 fields are required:
     * 1. Password field: randomized key, password as value (extracted from login page's Index.html)
     * 2. Username field: randomized key, username as value (extracted from login page's Index.html)
     * 3&4. Field with randomized key and value  (extracted from  loader.php, which returned a js file)
     *
     * The function below extracts all of this and combined them into one single HashMap.
     *--------------------------------------------------------------------------------------------*/

    private fun constructFields(
            indexHtml: String,
            loaderJs: String,
            username: String,
            password: String
    ): HashMap<String, String> {
        val extraFields = fieldsRegex.findAll(loaderJs)
        val loginMatches = loginRegex.findAll(indexHtml)

        val userStr = loginMatches.elementAt(1)?.groups?.get(1)?.value ?:""
        val passStr = loginMatches.elementAt(2)?.groups?.get(1)?.value ?:""
        val loginStr = loginMatches.elementAt(3)?.groups?.get(1)?.value ?:""

        Log.v("Test", "User: " + userStr)
        Log.v("Test", "Pass: " + passStr)
        Log.v("Test", "Login: " + loginStr)

        val fieldsMap = HashMap<String, String>()
        fieldsMap.put(userStr, username)
        fieldsMap.put(passStr, password)
        fieldsMap.put(loginStr, "Login")

        Log.v("Test", "username: " + username)
        Log.v("Test", "password: " + password)

        extraFields.forEach { matchResult ->
            fieldsMap.put(matchResult.groups?.get(1)?.value ?:"", matchResult.groups?.get(2)?.value ?:"")
            Log.v("Test", matchResult.groups?.get(1)?.value + "\n" + matchResult.groups?.get(2)?.value)
        }

        return fieldsMap
    }

    private fun decodeHtml(input: String) = URLDecoder.decode(input,"UTF-8")

    // TODO: This is similar to the one from journalInteractor, might be able to be refactored out
    fun isSyncOverdue(): Boolean {
        val minutesInt = Minutes.minutesBetween(timeStampRepo.getLastSync(), timeStampRepo.today()).minutes
        return Math.abs(minutesInt) > 25
    }
}