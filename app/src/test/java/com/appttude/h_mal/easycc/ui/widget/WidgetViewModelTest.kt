package com.appttude.h_mal.easycc.ui.widget

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.ui.BaseViewModelTest
import com.appttude.h_mal.easycc.utils.observeOnce
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

private const val currencyOne = "AUD - Australian Dollar"
private const val currencyTwo = "GBP - British Pound"

class WidgetViewModelTest : BaseViewModelTest<WidgetViewModel>(){

    // Run tasks synchronously
    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    override lateinit var viewModel: WidgetViewModel

    @Mock
    lateinit var repository: Repository

    @Before
    override fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = WidgetViewModel(repository)

        super.setUp()
    }

    @Test
    fun initiate_validInput_successfulResponse() {
        //GIVEN
        val appId = 123
        val pair = Pair(currencyOne, currencyTwo)

        //WHEN
        Mockito.`when`(repository.getWidgetConversionPairs(appId)).thenReturn(pair)

        //THEN
        viewModel.initiate(123)
        assertEquals(viewModel.rateIdFrom, currencyOne)
        assertEquals(viewModel.rateIdTo, currencyTwo)
    }

    @Test
    fun initiate_validInputNoWidgetPair_successfulResponse() {
        //GIVEN
        val appId = 123
        val pair = mock(Pair::class.java)
        val array = arrayOf(currencyOne)

        //WHEN
        Mockito.`when`(repository.getWidgetConversionPairs(appId)).thenAnswer { pair }
        Mockito.`when`(repository.getWidgetConversionPairs(appId).first).thenReturn(null)
        Mockito.`when`(repository.getWidgetConversionPairs(appId).second).thenReturn(null)
        Mockito.`when`(repository.getCurrenciesList()).thenReturn(array)

        //THEN
        viewModel.initiate(123)
        assertEquals(viewModel.rateIdFrom, currencyOne)
        assertEquals(viewModel.rateIdTo, currencyOne)
    }

    @Test
    fun getSubmitDialogMessage_validInput_successfulResponse() {
        //GIVEN
        viewModel.rateIdFrom = currencyOne
        viewModel.rateIdTo = currencyTwo

        //THEN
        val dialogResult = viewModel.getSubmitDialogMessage()
        assertEquals(dialogResult, "Create widget for AUDGBP?")
    }

    @Test
    fun submitSelectionOnClick_validInput_successfulResponse() {
        //GIVEN
        viewModel.rateIdFrom = currencyOne
        viewModel.rateIdTo = currencyTwo

        //THEN
        viewModel.submitSelectionOnClick()

        dataPost.observeOnce {
            assert(it is Unit)
        }
    }

    @Test
    fun submitSelectionOnClick_invalidInput_unsuccessfulResponse() {
        //GIVEN
        viewModel.rateIdFrom = currencyOne
        viewModel.rateIdTo = currencyOne

        //THEN
        viewModel.submitSelectionOnClick()

        errorPost.observeOnce {
            assertEquals("Selected rates cannot be the same", it)
        }
    }

    @Test
    fun submitSelectionOnClick_noInput_unsuccessfulResponse() {
        //GIVEN
        viewModel.rateIdFrom = null
        viewModel.rateIdTo = null

        //THEN
        viewModel.submitSelectionOnClick()

        errorPost.observeOnce {
            assertEquals("Selections incomplete", it)
        }
    }
}