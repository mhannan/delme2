package com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase.dao

import androidx.room.*
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase.entities.FavouritePdfEntity


@Dao
interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPdfFav(pdfEntity: FavouritePdfEntity)

    @Query("SELECT * FROM favourite_table")
    fun retriveFavourite(): MutableList<FavouritePdfEntity>

    @Delete
    fun deleteFavouriteItem(pdfEntity: FavouritePdfEntity)

    @Query("DELETE FROM favourite_table")
    fun deleteAll()

    @Query("UPDATE favourite_table SET pdfName = :pdfName WHERE id = :id")
    fun updateFavName(id: Long, pdfName: String?)

    @Query("UPDATE favourite_table SET pdfName = :pdfName WHERE pdfName = :name")
    fun updateFavByName(name: String, pdfName: String?)

    @Query("UPDATE favourite_table SET pdfPath = :newPath WHERE pdfName = :name")
    fun updateFavPath(name: String, newPath: String?)
}