package com.example.feproblem1.service

import com.example.feproblem1.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("planets")
    suspend fun getPlanets(): Response<List<Planet>>

    @GET("vehicles")
    suspend fun getVehicles(): Response<List<Vehicle>>

    @POST("token")
    suspend fun getToken(): Response<TokenResponse>

    @POST("find")
    suspend fun findFalcone(@Body request: FindFalconeRequest): Response<FindFalconeResponse>
}
