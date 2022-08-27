package com.appttude.h_mal.easycc.repository

import com.appttude.h_mal.easycc.data.network.SafeApiRequest
import com.appttude.h_mal.easycc.data.network.api.BackupCurrencyApi
import com.appttude.h_mal.easycc.data.network.api.CurrencyApi
import com.appttude.h_mal.easycc.data.network.response.ResponseObject
import com.appttude.h_mal.easycc.data.prefs.PreferenceProvider
import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.data.repository.RepositoryImpl
import com.appttude.h_mal.easycc.models.CurrencyModel
import com.appttude.h_mal.easycc.utils.convertPairsListToString
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertFailsWith


class RepositoryNetworkTest : SafeApiRequest() {

    lateinit var repository: Repository

    @Mock
    lateinit var api: CurrencyApi

    @Mock
    lateinit var apiBackup: BackupCurrencyApi

    @Mock
    lateinit var prefs: PreferenceProvider

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repository = RepositoryImpl(api, apiBackup, prefs)
    }

    @Test
    fun getRateFromApi_positiveResponse() = runBlocking {
        //GIVEN - Create query string
        val s1 = "AUD"
        val s2 = "GBP"
        //create a successful retrofit response
        val mockCurrencyResponse = mock(ResponseObject::class.java)
        val re = Response.success(mockCurrencyResponse)
        val currencyModel = mock(CurrencyModel::class.java)

        //WHEN - loginApiRequest to return a successful response
        val currencyPair = convertPairsListToString(s1, s2)
        Mockito.`when`(api.getCurrencyRate(currencyPair)).thenReturn(re)
        Mockito.`when`(responseUnwrap { api.getCurrencyRate(currencyPair) }.getCurrencyModel()).thenReturn(currencyModel)

        //THEN - the unwrapped login response contains the correct values
        val currencyResponse = repository.getDataFromApi(s1, s2)
        assertEquals(currencyResponse, currencyModel)
    }

    @Test
    fun loginUser_negativeResponse() = runBlocking {
        //GIVEN
        val s1 = "AUD"
        val s2 = "GBP"

        //mock retrofit error response
        val mockBody = mock(ResponseBody::class.java)
        val mockRaw = mock(okhttp3.Response::class.java)
        val re = Response.error<String>(mockBody, mockRaw)

        //WHEN
        val currencyPair = convertPairsListToString(s1, s2)
        Mockito.`when`(api.getCurrencyRate(currencyPair)).thenAnswer { re }
        Mockito.`when`(apiBackup.getCurrencyRate(s1, s2)).thenAnswer { re }

        //THEN - assert exception is not null
        val ioExceptionReturned = assertFailsWith<IOException> {
            repository.getDataFromApi(s1, s2)
        }
        assertEquals(ioExceptionReturned.message, "Error Code: 0")
    }

}