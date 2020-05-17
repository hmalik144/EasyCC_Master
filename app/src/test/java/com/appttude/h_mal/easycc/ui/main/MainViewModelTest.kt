package com.appttude.h_mal.easycc.ui.main

import android.os.Bundle
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.appttude.h_mal.easycc.data.network.response.ResponseObject
import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.models.CurrencyObject
import kotlinx.coroutines.runBlocking
import org.junit.Before
import com.appttude.h_mal.easycc.utils.observeOnce
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import kotlin.time.seconds

class MainViewModelTest {

    // Run tasks synchronously
    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: MainViewModel

    @Mock
    lateinit var repository: Repository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = MainViewModel(repository)
    }

    @Test
    fun initiate_validBundleValues_successResponse() = runBlocking{
        //GIVEN
        val currencyOne = "AUD - Australian Dollar"
        val currencyTwo = "GBP - British Pound"
        val bundle = mock(Bundle()::class.java)
        val responseObject = mock(ResponseObject::class.java)

        //WHEN
        Mockito.`when`(bundle.getString("parse_1")).thenReturn(currencyOne)
        Mockito.`when`(bundle.getString("parse_2")).thenReturn(currencyTwo)
        Mockito.`when`(repository.getData(currencyOne, currencyTwo)).thenReturn(responseObject)

        //THEN
        viewModel.initiate(bundle)
        viewModel.operationStartedListener.observeOnce {
            assertEquals(true, it)
        }
        viewModel.operationFinishedListener.observeOnce {
            assertEquals(true, it.first)
            Log.i("tag", "${it.first}  ${it.second}")
            assertNull(it.second)
        }
    }

    @Test
    fun initiate_invalidBundleValues_successfulResponse() = runBlocking{
        //GIVEN
        val currencyOne = "AUD - Australian Dollar"
        val currencyTwo = "GBP - British Pound"
        val pair = Pair(currencyOne, currencyTwo)
        val responseObject = mock(ResponseObject::class.java)

        //WHEN
        Mockito.`when`(repository.getConversionPair()).thenReturn(pair)
        Mockito.`when`(repository.getData(currencyOne, currencyTwo)).thenReturn(responseObject)

        //THEN
        viewModel.initiate(null)
        viewModel.operationStartedListener.observeOnce {
            assertEquals(true, it)
        }
        viewModel.operationFinishedListener.observeOnce {
            assertEquals(true, it.first)
            assertNull(it.second)
        }
    }

    @Test
    fun initiate_sameBundleValues_successfulResponse() = runBlocking{
        //GIVEN
        val currencyOne = "AUD - Australian Dollar"
        val bundle = mock(Bundle()::class.java)

        //WHEN
        Mockito.`when`(bundle.getString("parse_1")).thenReturn(null)
        Mockito.`when`(bundle.getString("parse_2")).thenReturn(null)
        Mockito.`when`(repository.getConversionPair()).thenReturn(Pair(currencyOne, currencyOne))

        //THEN
        viewModel.initiate(bundle)
        viewModel.operationStartedListener.observeOnce {
            assertEquals(true, it)
        }
        viewModel.operationFinishedListener.observeOnce {
            assertEquals(true, it.first)
            assertNull(it.second)
        }
    }

    @Test
    fun initiate_invalidValues_unsuccessfulResponse() = runBlocking {
        //GIVEN
        val bundle = mock(Bundle()::class.java)

        //WHEN
        Mockito.`when`(bundle.getString("parse_1")).thenReturn(null)
        Mockito.`when`(bundle.getString("parse_2")).thenReturn(null)
        Mockito.`when`(repository.getConversionPair()).thenReturn(Pair(null, null))

        //THEN
        viewModel.initiate(bundle)
        viewModel.operationStartedListener.observeOnce {
            assertEquals(true, it)
        }
        viewModel.operationFinishedListener.observeOnce {
            assertEquals(false, it.first)
            assertEquals("Select currencies", it.second)
        }
    }



    @Test
    fun setCurrencyName_validValues_successResponse() = runBlocking{
        //GIVEN
        val currencyOne = "AUD - Australian Dollar"
        val currencyTwo = "GBP - British Pound"
        viewModel.rateIdTo = currencyTwo
        val tag = "top"
        val responseObject = mock(ResponseObject::class.java)

        //WHEN
        Mockito.`when`(repository.getData(currencyOne, currencyTwo)).thenReturn(responseObject)

        //THEN
        viewModel.setCurrencyName(tag, currencyOne)
        viewModel.operationStartedListener.observeOnce {
            assertEquals(true, it)
        }
        viewModel.operationFinishedListener.observeOnce {
            assertEquals(true, it.first)
            Log.i("tag", "${it.first}  ${it.second}")
            assertNull(it.second)
        }
    }

    @Test
    fun setCurrencyName_sameValues_successfulResponse() = runBlocking{
        //GIVEN
        val currencyOne = "AUD - Australian Dollar"
        val currencyTwo = "GBP - British Pound"
        viewModel.rateIdTo = currencyOne
        val tag = "top"
        val responseObject = mock(ResponseObject::class.java)

        //WHEN
        Mockito.`when`(repository.getData(currencyOne, currencyTwo)).thenReturn(responseObject)

        //THEN
        viewModel.setCurrencyName(tag, currencyOne)
        viewModel.operationStartedListener.observeOnce {
            assertEquals(true, it)
        }
        viewModel.operationFinishedListener.observeOnce {
            assertEquals(true, it.first)
            assertNull(it.second)
        }
    }

    @Test
    fun setCurrencyName_invalidValues_unsuccessfulResponse() = runBlocking{
        //GIVEN
        val currencyOne = "AUD - Australian Dollar"
        val currencyTwo = "GBP - British Pound"
        val tag = "top"
        val responseObject = mock(ResponseObject::class.java)

        //WHEN
        Mockito.`when`(repository.getData(currencyOne, currencyTwo)).thenReturn(responseObject)

        //THEN
        viewModel.setCurrencyName(tag, currencyOne)
        viewModel.operationStartedListener.observeOnce {
            assertEquals(true, it)
        }
        viewModel.operationFinishedListener.observeOnce {
            assertEquals(false, it.first)
            assertNotNull(it.second)
        }
    }

    @Test
    fun getConversion_validValue_successfulResponse() {
        //GIVEN
        val inputDouble = "2.0"

        //THEN
        val returnVal = viewModel.getConversion(inputDouble)
        assertEquals(returnVal, inputDouble)
    }

    @Test
    fun getConversion_invalidValue_unsuccessfulResponse() {
        //THEN
        val returnVal = viewModel.getConversion("t")
        assertNull(returnVal)
    }

    @Test
    fun getReciprocalConversion_validValue_successfulResponse() {
        //GIVEN
        val inputDouble = "2.0"

        //THEN
        val returnVal = viewModel.getReciprocalConversion(inputDouble)
        assertEquals(returnVal, "2.0")
    }

    @Test
    fun getReciprocalConversion_invalidValue_unsuccessfulResponse() {
        //THEN
        val returnVal = viewModel.getReciprocalConversion("t")
        assertNull(returnVal)
    }
}