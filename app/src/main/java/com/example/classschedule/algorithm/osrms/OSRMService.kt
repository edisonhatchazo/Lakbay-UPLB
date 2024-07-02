package com.example.classschedule.algorithm.osrms


import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OSRMService {
    @GET("route/v1/{profile}/{coordinates}")
    suspend fun getRoute(
        @Path("profile") profile: String,
        @Path("coordinates") coordinates: String,
        @Query("steps") steps: Boolean = true
    ): RouteResponse


}

