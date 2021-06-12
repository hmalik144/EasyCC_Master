package com.appttude.h_mal.easycc.repository

import com.appttude.h_mal.easycc.data.network.api.BackupCurrencyApi
import com.appttude.h_mal.easycc.data.network.api.CurrencyApi
import com.appttude.h_mal.easycc.data.prefs.PreferenceProvider
import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.data.repository.RepositoryImpl
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals

class RepositoryStorageTest {

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
    fun saveAndRetrieve_PositiveResponse() {
        //GIVEN
        val s1 = "AUD - Australian Dollar"
        val s2 = "GBP - British Pound"
        val pair = Pair(s1, s2)
        repository.setConversionPair(s1, s2)

        //WHEN
        Mockito.`when`(prefs.getConversionPair()).thenReturn(pair)

        //THEN
        assertEquals(pair, repository.getConversionPair())
    }

    @Test
    fun saveAndRetrieveWidgetPairs_PositiveResponse() {
        //GIVEN
        val s1 = "AUD - Australian Dollar"
        val s2 = "GBP - British Pound"
        val id = 1234
        val pair = Pair(s1, s2)
        repository.setWidgetConversionPairs("forename", "Surname", id)

        //WHEN
        Mockito.`when`(prefs.getWidgetConversionPair(id)).thenReturn(pair)

        //THEN
        assertEquals(pair, repository.getWidgetConversionPairs(id))
    }
}