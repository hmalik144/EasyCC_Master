package com.appttude.h_mal.easycc.ui.main

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.models.CurrencyModel
import com.appttude.h_mal.easycc.ui.BaseViewModelTest
import com.appttude.h_mal.easycc.utils.MainCoroutineRule
import com.appttude.h_mal.easycc.utils.observeOnce
import com.nhaarman.mockitokotlin2.doAnswer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest : BaseViewModelTest<MainViewModel>(){

    // Run tasks synchronously
    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    override lateinit var viewModel: MainViewModel

    @Mock
    lateinit var repository: Repository

    private val currencyOne = "AUD - Australian Dollar"
    private val currencyTwo = "GBP - British Pound"

    @Before
    override fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = MainViewModel(repository)

        super.setUp()
    }

    @Test
    fun initiate_validBundleValues_successResponse() = runBlocking {
        //GIVEN
        val bundle = mock(Bundle()::class.java)
        val responseObject = mock(CurrencyModel::class.java)

        //WHEN
        Mockito.`when`(bundle.getString("parse_1")).thenReturn(currencyOne)
        Mockito.`when`(bundle.getString("parse_2")).thenReturn(currencyTwo)
        Mockito.`when`(repository.getDataFromApi(currencyOne, currencyTwo))
            .thenReturn(responseObject)

        //THEN
        viewModel.initiate(bundle)

        dataPost.observeOnce {
            assertEquals(it, responseObject)
        }
    }

    @Test
    fun initiate_invalidBundleValues_successfulResponse() = runBlocking {
        //GIVEN
        val bundle = mock(Bundle()::class.java)
        val error = "Corrupted data found"

        //WHEN
        Mockito.`when`(bundle.getString("parse_1")).thenReturn(currencyOne)
        Mockito.`when`(bundle.getString("parse_2")).thenReturn(currencyTwo)
        Mockito.`when`(repository.getDataFromApi(currencyOne, currencyTwo))
            .doAnswer { throw IOException(error) }

        //THEN
        viewModel.initiate(bundle)

        errorPost.observeOnce {
            assertEquals(error, it)
        }
    }

    @Test
    fun initiate_sameBundleValues_successfulResponse() = runBlocking {
        //GIVEN
        val bundle = mock(Bundle()::class.java)
        val responseObject = mock(CurrencyModel::class.java)

        //WHEN
        Mockito.`when`(bundle.getString("parse_1")).thenReturn(null)
        Mockito.`when`(bundle.getString("parse_2")).thenReturn(null)
        Mockito.`when`(repository.getConversionPair()).thenReturn(Pair(currencyOne, currencyOne))
        Mockito.`when`(repository.getDataFromApi(currencyOne, currencyTwo))
            .thenReturn(responseObject)

        //THEN
        viewModel.initiate(bundle)

        dataPost.observeOnce {
            assertEquals(responseObject, it)
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

        errorPost.observeOnce {
            assertEquals("Select both currencies", it)
        }
    }


    @Test
    fun setCurrencyName_validValues_successResponse() = runBlocking {
        //GIVEN
        viewModel.rateIdTo = currencyTwo
        val tag = "top"
        val responseObject = mock(CurrencyModel::class.java)

        //WHEN
        Mockito.`when`(repository.getDataFromApi(currencyOne, currencyTwo))
            .thenReturn(responseObject)

        //THEN
        viewModel.setCurrencyName(tag, currencyOne)

        dataPost.observeOnce {
            assertEquals(responseObject, it)
        }
    }

    @Test
    fun setCurrencyName_sameValues_successfulResponse() = runBlocking {
        //GIVEN
        viewModel.rateIdTo = currencyOne
        val tag = "top"
        val responseObject = mock(CurrencyModel::class.java)

        //WHEN
        Mockito.`when`(repository.getDataFromApi(currencyOne, currencyTwo))
            .thenReturn(responseObject)

        //THEN
        viewModel.setCurrencyName(tag, currencyOne)
        dataPost.observeOnce {
            assertEquals(responseObject, it)
        }
    }

    @Test
    fun setCurrencyName_invalidValues_unsuccessfulResponse() = runBlocking {
        //GIVEN
        val error = "Data is corrupted"
        viewModel.rateIdTo = "corrupted"
        val tag = "top"
        val responseObject = mock(CurrencyModel::class.java)

        //WHEN
        Mockito.`when`(repository.getDataFromApi(currencyOne, "corrupted"))
            .doAnswer { throw IOException(error) }

        //THEN
        viewModel.setCurrencyName(tag, currencyOne)
        errorPost.observeOnce {
            assertEquals(error, it)
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