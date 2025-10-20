package com.example.pokeshop.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.pokeshop.data.dao.*
import com.example.pokeshop.data.entities.*

@Database(
    entities = [
        UserEntity::class,
        RolEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        SaleEntity::class,
        SaleDetailEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun rolDao(): RolDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun saleDetailDao(): SaleDetailDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pokeshop_database"
                ).fallbackToDestructiveMigration() // Para desarrollo
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}