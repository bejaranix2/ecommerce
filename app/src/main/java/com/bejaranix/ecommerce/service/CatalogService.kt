package com.bejaranix.ecommerce.service

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CatalogService {

    private val BASE_URL = "https://00672285.us-south.apigw.appdomain.cloud/demo-gapsi/"
    private val HEADER_VALUE = "adb8204d-d574-4394-8c1a-53226a40876e"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(CatalogApi::class.java)

    public fun fetchCatalogQuery(q:String): Single<CatalogModel> = api.searchPlaylists(q,HEADER_VALUE)
}