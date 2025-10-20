package com.example.pokeshop.data.dao

import androidx.room.*
import com.example.pokeshop.data.entities.SaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales")
    fun getAllSales(): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE id = :saleId")
    suspend fun getSaleById(saleId: Long): SaleEntity?

    @Query("SELECT * FROM sales WHERE date BETWEEN :startDate AND :endDate")
    fun getSalesByDateRange(startDate: Long, endDate: Long): Flow<List<SaleEntity>>

    @Insert
    suspend fun insertSale(sale: SaleEntity): Long

    @Update
    suspend fun updateSale(sale: SaleEntity)

    @Query("UPDATE sales SET status = :status WHERE id = :saleId")
    suspend fun updateSaleStatus(saleId: Long, status: String)

    @Delete
    suspend fun deleteSale(sale: SaleEntity)
}