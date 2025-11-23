package com.example.pokeshop.data.network

import com.example.pokeshop.data.entities.RolEntity // Importa tu entidad
import com.google.gson.GsonBuilder // Nuevo import
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL_HOST = "http://10.0.2.2"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .retryOnConnectionFailure(true)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder()
        .registerTypeAdapter(RolEntity::class.java, RolDeserializer()) // Usamos tu deserializador
        .setLenient()
        .create()

    fun <T> createService(serviceClass: Class<T>, port: String): T {
        val retrofit = Retrofit.Builder()
            .baseUrl("$BASE_URL_HOST:$port/")
            .client(okHttpClient)
            // Usamos el gson personalizado aquí
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(serviceClass)
    }
}