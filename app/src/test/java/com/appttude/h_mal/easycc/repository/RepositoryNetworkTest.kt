package com.appttude.h_mal.easycc.repository

import android.content.Context
import com.appttude.h_mal.easycc.BuildConfig
import com.appttude.h_mal.easycc.mvvm.data.network.api.CurrencyApi
import com.appttude.h_mal.easycc.mvvm.data.network.response.ResponseObject
import com.appttude.h_mal.easycc.mvvm.data.prefs.PreferenceProvider
import com.appttude.h_mal.easycc.mvvm.data.repository.Repository
import com.appttude.h_mal.easycc.mvvm.data.repository.RepositoryImpl
import com.appttude.h_mal.easycc.mvvm.utils.convertPairsListToString
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


class RepositoryNetworkTest{

    lateinit var repository: Repository

    @Mock
    lateinit var api: CurrencyApi
    @Mock
    lateinit var prefs: PreferenceProvider
    @Mock
    lateinit var context: Context

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repository = RepositoryImpl(api, prefs, context)
    }

    @Test
    fun getRateFromApi_positiveResponse() = runBlocking {
        //GIVEN - Create query string
        val s1 = "AUD - Australian Dollar"
        val s2 = "GBP - British Pound"
        val query = convertPairsListToString(s1, s2)
        //create a successful retrofit response
        val mockCurrencyResponse = mock(ResponseObject::class.java)
        val re = Response.success(mockCurrencyResponse)

        //WHEN - loginApiRequest to return a successful response
        Mockito.`when`(api.getCurrencyRate(query)).thenReturn(re)

        //THEN - the unwrapped login response contains the correct values
        val currencyResponse = repository.getData(s1,s2)
        assertNotNull(currencyResponse)
        assertEquals(currencyResponse, mockCurrencyResponse)
    }

    @Test
    fun loginUser_negativeResponse() = runBlocking {
        //GIVEN
        val s1 = "AUD - Australian Dollar"
        val s2 = "GBP - British Pound"
        val query = convertPairsListToString(s1, s2)
        //mock retrofit error response
        val mockBody = mock(ResponseBody::class.java)
        val mockRaw = mock(okhttp3.Response::class.java)
        val re = Response.error<String>(mockBody, mockRaw)

        //WHEN
        Mockito.`when`(api.getCurrencyRate(query)).thenAnswer { re }

        //THEN - assert exception is not null
        val ioExceptionReturned = assertFailsWith<IOException> {
            repository.getData(s1, s2)
        }
        assertNotNull(ioExceptionReturned)
        assertNotNull(ioExceptionReturned.message)
    }

}