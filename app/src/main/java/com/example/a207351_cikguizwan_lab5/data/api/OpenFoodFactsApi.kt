package com.example.a207351_cikguizwan_lab5.data.api

import retrofit2.http.GET
import retrofit2.http.Path

data class ProductResponse(
    val code: String,
    val product: Product?
)

data class Product(
    val product_name: String?,
    val nutriments: Nutriments?
)

data class Nutriments(
    val energy_kcal_100g: Double?,
    val proteins_100g: Double?,
    val carbohydrates_100g: Double?,
    val fat_100g: Double?
)

interface OpenFoodFactsApi {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProduct(@Path("barcode") barcode: String): ProductResponse
}