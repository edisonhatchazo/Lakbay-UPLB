package com.example.classschedule.algorithm.retrofit

import android.util.Log
import com.example.classschedule.algorithm.osrms.OSRMService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {
        Log.d("client","Getting Client")
        if (retrofit == null || retrofit!!.baseUrl().toString() != baseUrl) {
            Log.d("client","Making Client")
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        Log.d("client","Client Get")
        return retrofit!!
    }
    fun getOSRMService(baseUrl: String): OSRMService {
        return getClient(baseUrl).create(OSRMService::class.java)
    }

}
