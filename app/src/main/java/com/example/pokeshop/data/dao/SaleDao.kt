package com.example.pokeshop.data.dao

import androidx.room.*
import com.example.pokeshop.data.entities.SaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    //funcion para obtener todas las ventas
    @Query("SELECT * FROM sales")
    fun getAllSales(): Flow<List<SaleEntity>>

    //funcion para obtener ventas segun su id
    @Query("SELECT * FROM sales WHERE id = :saleId")
    suspend fun getSaleById(saleId: Long): SaleEntity?

    //funcion para obtener ventas en un rango de fechas
    @Query("SELECT * FROM sales WHERE date BETWEEN :startDate AND :endDate")
    fun getSalesByDateRange(startDate: Long, endDate: Long): Flow<List<SaleEntity>>

    //funcion para crear ventas
    @Insert
    suspend fun insertSale(sale: SaleEntity): Long

    //funcion para editar ventas
    @Update
    suspend fun updateSale(sale: SaleEntity)

    //funcion para cambiar estado de una venta
    @Query("UPDATE sales SET status = :status WHERE id = :saleId")
    suspend fun updateSaleStatus(saleId: Long, status: String)

    //funcion para borrar ventas
    @Delete
    suspend fun deleteSale(sale: SaleEntity)
}