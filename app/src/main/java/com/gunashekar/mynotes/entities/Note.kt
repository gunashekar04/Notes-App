package com.gunashekar.mynotes.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass


@Entity(tableName = "notes")
data class Note (
        @ColumnInfo(name = "title") val title: String,
        @ColumnInfo(name = "date_time") val dateTime: String,
        @ColumnInfo(name = "subtitle") val subtitle: String,
        @ColumnInfo(name = "note_text") val noteText: String
) : java.io.Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo(name = "image_path")
    var imagePath: String? = null
    @ColumnInfo(name = "color")
    var color: String? = null
    @ColumnInfo(name = "web_link")
    var webLink: String? = null

    override fun toString(): String {
        return "$title : $dateTime"
    }
}