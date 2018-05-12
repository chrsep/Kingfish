package com.directdev.portal.interactors

import androidx.test.runner.AndroidJUnit4
import com.directdev.portal.network.BimayApi
import com.directdev.portal.repositories.FlagRepository
import com.directdev.portal.repositories.TimeStampRepository
import com.directdev.portal.repositories.UserCredRepository
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import okhttp3.ResponseBody
import org.joda.time.DateTime
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/20/17.
 *------------------------------------------------------------------------------------------------*/

@RunWith(AndroidJUnit4::class)
class AuthInteractorUnitTest {

    @Test
    fun succesfulLogin() {
        //Given
        val mockBimayApi = mock<BimayApi>{
            on { getIndexHtml() } doReturn mock<Single<Response<ResponseBody>>>()
            on { getLoaderJs(anyOrNull(), anyOrNull(), anyOrNull()) } doReturn mock<Single<Response<ResponseBody>>>()
        }
        val mockUserCredRepo = mock<UserCredRepository>{
            on { getCookie() } doReturn ""
        }
        val mockFlagRepo = mock<FlagRepository>()
        val mockTimeStampRepo = mock<TimeStampRepository> {
            on { getLastSync() } doReturn DateTime().minusYears(1)
            on { today() } doReturn DateTime.now()
        }
        val authInteractor = AuthInteractor(
                mockBimayApi,
                mockUserCredRepo,
                mockFlagRepo,
                mockTimeStampRepo)
        //When
        //TODO: This crash because some nullpointerexception caused by moccking the networkService
        //Investigate why
        authInteractor.execute("", "")
        //Then
        verify(mockBimayApi).getIndexHtml()
    }
}