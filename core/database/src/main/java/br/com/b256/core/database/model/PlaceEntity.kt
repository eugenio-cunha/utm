package br.com.b256.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.b256.core.model.Place
import kotlinx.datetime.Instant

@Entity(tableName = "place")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "date")
    val date: Instant,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "altitude")
    val altitude: Double,

    @ColumnInfo(name = "accuracy")
    val accuracy: Float,
)

fun PlaceEntity.asModel(): Place = Place(
    id = id,
    title = title,
    date = date,
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    accuracy = accuracy,
)

fun Place.asEntity(): PlaceEntity = PlaceEntity(
    id = id,
    title = title,
    date = date,
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    accuracy = accuracy,
)
