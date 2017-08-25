package com.directdev.portal.interactors

import com.directdev.portal.network.NetworkHelper
import com.directdev.portal.repositories.FlagRepository
import com.directdev.portal.repositories.UserCredRepository
import com.directdev.portal.utils.SigninException
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Handles everything related to authenticating with Binusmaya
 *------------------------------------------------------------------------------------------------*/

class AuthInteractor @Inject constructor(
        private val bimayApi: NetworkHelper,
        private val userCredRepo: UserCredRepository,
        private val flagRepo: FlagRepository
) {
    private val loaderPattern = "<script src=\".*login/loader.*\""
    private val fieldsPattern = "<input type=\"hidden\" name=\".*\" value=\".*\" />"
    private val usernamePattern = "<input type=\"text\" name=\".*placeholder=\"Username\""
    private val passwordPattern = "<input type=\"password\" name=\".*placeholder=\"Password\""
    private var isRequesting = false
    private lateinit var request: Single<Response<ResponseBody>>

    fun execute(username: String, password: String): Single<Response<ResponseBody>> {
        var indexHtml = ""
        var cookie = ""
        request = if (isRequesting) request else bimayApi.getIndexHtml().flatMap {
            indexHtml = it.body()?.string() ?: ""
            cookie = it.headers().get("Set-Cookie") ?: ""

            val result = Regex(loaderPattern).find(indexHtml)?.value ?: ""
            val serial = decodeHtml(result.substring(40, result.length - 1))

            Thread.sleep(2000)
            bimayApi.getRandomizedFields(cookie, serial)
        }.flatMap {
            val loaderjs = it.body()?.string() ?: ""
            val fieldsMap = constructFields(indexHtml, loaderjs, username, password)
            bimayApi.authenticate(cookie, fieldsMap)
        }.flatMap {
            val redirectLocation = it.headers().get("Location") ?: "none"
            if (redirectLocation != "https://binusmaya.binus.ac.id/block_user.php")
                throw SigninException(redirectLocation)
            bimayApi.switchRole(cookie)
        }.doAfterSuccess {
            userCredRepo.save(cookie, username, password)
            flagRepo.save(isLoggedIn = true)
        }.doAfterTerminate {
            isRequesting = false
        }
        isRequesting = true
        return request
    }

    fun getCredentials() = userCredRepo.getAll()

    /*----------------------------------------------------------------------------------------------
     * To login, 4 fields are required:
     * 1. Password field: randomized key, password as value (extracted from login page's Index.html)
     * 2. Username field: randomized key, username as value (extracted from login page's Index.html)
     * 3&4. Field with randomized key and value  (extracted from  loader.php, which returned a js file)
     *--------------------------------------------------------------------------------------------*/

    private fun constructFields(indexHtml: String, loaderJs: String, username: String, password: String): HashMap<String, String> {
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
}