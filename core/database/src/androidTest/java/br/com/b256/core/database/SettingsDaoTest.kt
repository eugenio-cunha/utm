package br.com.b256.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.b256.core.database.dao.SettingsDao
import br.com.b256.core.database.model.SettingsEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsDaoTest {
    private lateinit var settingsDao: SettingsDao
    private lateinit var db: RoomDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context = context,
            klass = RoomDatabase::class.java,
        ).build()
        settingsDao = db.settings()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun settingsDao_fetches() = runTest {
        val entity = SettingsEntity(id = "0", biometrics = true)

        settingsDao.upsert(entity = entity)
        val result = settingsDao.find().first()

        assertEquals(entity, result)
    }
}
