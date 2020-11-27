package com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase.repository

import android.content.Context
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase.MyDatabase
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase.entities.FavouritePdfEntity

class FavouriteRepository(val context: Context) {

    val favDao = MyDatabase.getInstance(context).getfaFavouriteDao()

    companion object {

        private var favInstance: FavouriteRepository? = null

        fun getInstance(context: Context) =
            favInstance ?: FavouriteRepository(context).also { favInstance = it }
    }

    fun insertFav(favouritePdfEntity: FavouritePdfEntity) {
        favDao.insertPdfFav(favouritePdfEntity)
    }

    fun retrieveAllFav() =
        favDao.retriveFavourite()

    fun deleteFav(favouritePdfEntity: FavouritePdfEntity) {
        favDao.deleteFavouriteItem(favouritePdfEntity)
    }

    fun updateFav(id: Long, name: String) {
        favDao.updateFavName(id, name)
    }

    fun updateFavByName(name: String, newName: String) {
        favDao.updateFavByName(name, newName)
    }

    fun updateFavPath(name: String, newPath: String) {
        favDao.updateFavPath(name, newPath)
    }
}