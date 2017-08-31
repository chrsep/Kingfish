package com.directdev.portal.interactors

import com.directdev.portal.network.NetworkHelper
import com.directdev.portal.repositories.FlagRepository
import com.directdev.portal.repositories.TermRepository
import com.directdev.portal.repositories.TimeStampRepository
import com.directdev.portal.repositories.UserCredRepository
import com.directdev.portal.utils.SigninException
import io.reactivex.Single
import org.joda.time.Minutes
import javax.inject.Inject
import javax.inject.Named

/**-------------------------------------------------------------------------------------------------
 * Handles everything related to authenticating with Binusmaya
 *------------------------------------------------------------------------------------------------*/

class AuthInteractor @Inject constructor(
        private val bimayApi: NetworkHelper,
        private val userCredRepo: UserCredRepository,
        private val flagRepo: FlagRepository,
        private val termRepo: TermRepository,
        @Named("auth") private val timeStampRepo: TimeStampRepository
) {
    private var isRequesting = false
    private lateinit var request: Single<String>

    // Regex patterns to extract data from index.html and loader.js
    private val loaderPattern = "<script src=\".*login/loader.*\""
    private val fieldsPattern = "<input type=\"hidden\" name=\".*\" value=\".*\" />"
    private val usernamePattern = "<input type=\"text\" name=\".*placeholder=\"Username\""
    private val passwordPattern = "<input type=\"password\" name=\".*placeholder=\"Password\""

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
            cookie = it.headers().get("Set-Cookie") ?: ""

            // Extracts the link to loader.php from index.html
            val result = Regex(loaderPattern).find(indexHtml)?.value ?: ""
            val serial = decodeHtml(result.substring(40, result.length - 1))

            // Retrieve loader.js from loader.php
            bimayApi.getLoaderJs(cookie, serial)
        }.flatMap {
            val loaderJs = it.body()?.string() ?: ""
            val fieldsMap = constructFields(indexHtml, loaderJs, username, password)

            // Authenticate with Binusmaya using the extracted fields from index.html & loader.js as
            // the request parameter
            bimayApi.authenticate(cookie, fieldsMap)
        }.map {
            // Checks if login is successful
            val redirectLocation = it.headers().get("Location") ?: "none"
            if (redirectLocation != "https://binusmaya.binus.ac.id/block_user.php")
                throw SigninException(redirectLocation)

            // Make sure user is logged in as student, not as a staff etc.
            bimayApi.switchRole(cookie)
        }.flatMap {
            bimayApi.getTerms(cookie)
        }.map {
            termRepo.save(it)
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
        val user = Regex(usernamePattern).find(indexHtml)?.value ?: ""
        val pass = Regex(passwordPattern).find(indexHtml)?.value ?: ""
        val extraFields = Regex(fieldsPattern).findAll(loaderJs).toList()[0].value.split(" ")
        val userStr = decodeHtml(user.substring(25, user.length - 45))
        val passStr = decodeHtml(pass.substring(29, pass.length - 24))

        val fieldsMap = HashMap<String, String>()
        fieldsMap.put(passStr, password)
        fieldsMap.put(userStr, username)
        fieldsMap.put(decodeHtml(extraFields[2].substring(6, extraFields[2].length - 1)),
                decodeHtml(extraFields[3].substring(6, extraFields[3].length - 1)))
        fieldsMap.put(decodeHtml(extraFields[6].substring(6, extraFields[6].length - 1)),
                decodeHtml(extraFields[7].substring(6, extraFields[7].length - 1)))
        return fieldsMap
    }

    private fun decodeHtml(input: String) = input.replace("%2F", "/").replace("%3D", "=")

    // TODO: This is similar to the one from journalInteractor, might be able to be refactored out
    fun isSyncOverdue(): Boolean {
        val minutesInt = Minutes.minutesBetween(timeStampRepo.getLastSync(), timeStampRepo.today()).minutes
        return Math.abs(minutesInt) > 25
    }
}