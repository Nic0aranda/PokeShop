package com.example.pokeshop.data.dao

import androidx.room.*
import com.example.pokeshop.data.entities.SaleDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDetailDao {

    //funcion para obtener el detalle de venta por id de venta
    @Query("SELECT * FROM sale_details WHERE saleId = :saleId")
    fun getSaleDetailsBySaleId(saleId: Long): Flow<List<SaleDetailEntity>>

    //funcion para obtener el detalle de venta por id de producto
    @Query("SELECT * FROM sale_details WHERE productId = :productId")
    fun getSaleDetailsByProductId(productId: Long): Flow<List<SaleDetailEntity>>

    //funcion para crear detalle de venta
    @Insert
    suspend fun insertSaleDetail(saleDetail: SaleDetailEntity): Long

    //funcion para editar detalle de venta
    @Update
    suspend fun updateSaleDetail(saleDetail: SaleDetailEntity)

    //funcion para borrar detalle de venta
    @Delete
    suspend fun deleteSaleDetail(saleDetail: SaleDetailEntity)

    //funcion para borrar detalle de venta por id de venta
    @Query("DELETE FROM sale_details WHERE saleId = :saleId")
    suspend fun deleteSaleDetailsBySaleId(saleId: Long)
}