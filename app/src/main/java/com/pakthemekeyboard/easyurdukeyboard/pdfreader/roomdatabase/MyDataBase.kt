package com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase.dao.FavouriteDao
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase.entities.FavouritePdfEntity


@Database(entities = [FavouritePdfEntity::class], version = 3)
abstract class MyDatabase : RoomDatabase() {

    abstract fun getfaFavouriteDao(): FavouriteDao

    companion object {
        private var instance: MyDatabase? = null

        @JvmStatic
        fun getInstance(context: Context) =
            instance ?: synchronized(this)
            {
                instance
                    ?: Room.databaseBuilder(context.applicationContext,
                        MyDatabase::class.java,
                        "database")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries().build()
                        .also { instance = it }
            }

        fun destroyInstance() {
            instance = null
        }
    }
}