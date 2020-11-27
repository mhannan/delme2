package com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_table")
data class FavouritePdfEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val pdfName: String,
    val pdfPath: String,
    val pdfType: String,
    val pdfSize:String,
    val pdfLastModefied:String
)