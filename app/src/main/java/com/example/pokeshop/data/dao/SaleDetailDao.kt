package com.example.pokeshop.data.dao

import androidx.room.*
import com.example.pokeshop.data.entities.SaleDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDetailDao {
    @Query("SELECT * FROM sale_details WHERE saleId = :saleId")
    fun getSaleDetailsBySaleId(saleId: Long): Flow<List<SaleDetailEntity>>

    @Query("SELECT * FROM sale_details WHERE productId = :productId")
    fun getSaleDetailsByProductId(productId: Long): Flow<List<SaleDetailEntity>>

    @Insert
    suspend fun insertSaleDetail(saleDetail: SaleDetailEntity): Long

    @Update
    suspend fun updateSaleDetail(saleDetail: SaleDetailEntity)

    @Delete
    suspend fun deleteSaleDetail(saleDetail: SaleDetailEntity)

    @Query("DELETE FROM sale_details WHERE saleId = :saleId")
    suspend fun deleteSaleDetailsBySaleId(saleId: Long)
}