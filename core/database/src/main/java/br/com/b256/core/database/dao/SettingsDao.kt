package br.com.b256.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import br.com.b256.core.database.model.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("select * from settings")
    fun find(): Flow<SettingsEntity>

    @Upsert
    suspend fun upsert(entity: SettingsEntity)
}
