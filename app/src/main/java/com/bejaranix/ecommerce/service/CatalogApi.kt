package com.bejaranix.ecommerce.service

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CatalogApi {

    @GET("search")
    fun searchPlaylists(
        @Query("query") query: String,
        @Header("X-IBM-Client-Id") header:String
    ): Single<CatalogModel>

}