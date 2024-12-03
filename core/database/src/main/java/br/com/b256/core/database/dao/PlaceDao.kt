package br.com.b256.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import br.com.b256.core.database.model.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM place ORDER BY id ASC;")
    fun find(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM place WHERE id = :id;")
    suspend fun findById(id: Int): PlaceEntity

    @Insert
    suspend fun create(value: PlaceEntity)

    @Upsert
    suspend fun upsert(value: PlaceEntity)

    @Query("DELETE FROM place WHERE id = :id;")
    suspend fun deleteById(id: Int)
}
