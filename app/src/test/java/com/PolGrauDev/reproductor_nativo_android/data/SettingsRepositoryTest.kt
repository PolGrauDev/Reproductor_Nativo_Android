package com.PolGrauDev.reproductor_nativo_android.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SettingsRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    // DataStore's own actor needs a live dispatcher to actually process edit()/data — a paused
    // TestScope/StandardTestDispatcher here would never advance on its own and edit() would hang.
    private val dataStoreScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        // Deliberately not tempFolder.newFile(...): that pre-creates an empty target file, and
        // on Windows File.renameTo() refuses to overwrite an existing file — DataStore's own
        // .tmp -> final rename on the first write then fails with a misleading "multiple
        // instances of DataStore" IOException. Passing a path that doesn't exist yet lets
        // DataStore create it itself on first write.
        val dataStore = PreferenceDataStoreFactory.create(scope = dataStoreScope) {
            File(tempFolder.root, "settings_test.preferences_pb")
        }
        repository = SettingsRepository(dataStore)
    }

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun `fadeDurationMs defaults to 0`() = runTest {
        assertEquals(0, repository.fadeDurationMs.first())
    }

    @Test
    fun `sleepTimerDefaultMinutes defaults to 30`() = runTest {
        assertEquals(30, repository.sleepTimerDefaultMinutes.first())
    }

    @Test
    fun `setFadeDurationMs updates the flow`() = runTest {
        repository.setFadeDurationMs(3_000)

        assertEquals(3_000, repository.fadeDurationMs.first())
    }

    @Test
    fun `setSleepTimerDefaultMinutes updates the flow`() = runTest {
        repository.setSleepTimerDefaultMinutes(15)

        assertEquals(15, repository.sleepTimerDefaultMinutes.first())
    }
}
