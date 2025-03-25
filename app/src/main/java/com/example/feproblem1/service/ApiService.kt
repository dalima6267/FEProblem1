package com.example.feproblem1.service


import com.example.feproblem1.model.FindFalconeRequest
import com.example.feproblem1.model.FindFalconeResponse
import com.example.feproblem1.model.Planet
import com.example.feproblem1.model.TokenResponse
import com.example.feproblem1.model.Vehicle
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST



interface ApiService {

    @GET("planets")
    fun getPlanets(): Call<List<Planet>>

    @GET("vehicles")
    fun getVehicles(): Call<List<Vehicle>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("token")
    fun getToken(): Call<TokenResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("find")
    fun findFalcone(@Body request: FindFalconeRequest): Call<FindFalconeResponse>
}
