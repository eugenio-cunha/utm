package br.com.b256.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "biometrics")
    val biometrics: Boolean,
)
