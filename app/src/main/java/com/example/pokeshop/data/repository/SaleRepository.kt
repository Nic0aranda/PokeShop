package com.example.pokeshop.data.repository

import com.example.pokeshop.data.dao.SaleDao
import com.example.pokeshop.data.entities.SaleEntity
import kotlinx.coroutines.flow.Flow

class SaleRepository(private val saleDao: SaleDao) {

    fun getAllSales(): Flow<List<SaleEntity>> = saleDao.getAllSales()

    suspend fun getSaleById(saleId: Long): SaleEntity? = saleDao.getSaleById(saleId)

    suspend fun insertSale(sale: SaleEntity): Long = saleDao.insertSale(sale)

    suspend fun updateSale(sale: SaleEntity) = saleDao.updateSale(sale)

    suspend fun deleteSale(sale: SaleEntity) = saleDao.deleteSale(sale)

    suspend fun updateSaleStatus(saleId: Long, status: String) =
        saleDao.updateSaleStatus(saleId, status)
}