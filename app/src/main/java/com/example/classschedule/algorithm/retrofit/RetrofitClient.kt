package com.example.classschedule.algorithm.retrofit

import com.example.classschedule.algorithm.osrms.OSRMService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null || retrofit!!.baseUrl().toString() != baseUrl) {

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return retrofit!!
    }
    fun getOSRMService(baseUrl: String): OSRMService {
        return getClient(baseUrl).create(OSRMService::class.java)
    }

}
