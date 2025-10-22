package com.example.pokeshop.data.repository

import com.example.pokeshop.data.dao.SaleDetailDao
import com.example.pokeshop.data.entities.SaleDetailEntity
import kotlinx.coroutines.flow.Flow

class SaleDetailRepository(private val saleDetailDao: SaleDetailDao) {

    fun getSaleDetailsBySaleId(saleId: Long): Flow<List<SaleDetailEntity>> =
        saleDetailDao.getSaleDetailsBySaleId(saleId)

    suspend fun insertSaleDetail(saleDetail: SaleDetailEntity): Long =
        saleDetailDao.insertSaleDetail(saleDetail)

    suspend fun updateSaleDetail(saleDetail: SaleDetailEntity) =
        saleDetailDao.updateSaleDetail(saleDetail)

    suspend fun deleteSaleDetail(saleDetail: SaleDetailEntity) =
        saleDetailDao.deleteSaleDetail(saleDetail)

    suspend fun deleteSaleDetailsBySaleId(saleId: Long) =
        saleDetailDao.deleteSaleDetailsBySaleId(saleId)
}