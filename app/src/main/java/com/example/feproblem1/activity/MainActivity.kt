package com.example.feproblem1.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feproblem1.service.ApiService
import com.example.feproblem1.model.FindFalconeRequest
import com.example.feproblem1.model.FindFalconeResponse
import com.example.feproblem1.model.Planet
import com.example.feproblem1.adapter.PlanetSelectionAdapter
import com.example.feproblem1.model.TokenResponse
import com.example.feproblem1.model.Vehicle
import com.example.feproblem1.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://findfalcone.geektrust.com/") // Updated API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private lateinit var planetAdapter: PlanetSelectionAdapter
    private val selectedPlanets = mutableMapOf<Int, String>()
    private val selectedVehicles = mutableMapOf<Int, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadPlanetsAndVehicles()

        binding.btnFindFalcone.setOnClickListener { findFalcone() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewPlanets.layoutManager = LinearLayoutManager(this)
    }

    private fun loadPlanetsAndVehicles() {
        apiService.getPlanets().enqueue(object : Callback<List<Planet>> {
            override fun onResponse(call: Call<List<Planet>>, response: Response<List<Planet>>) {
                if (response.isSuccessful) {
                    val planets = response.body()?.map { it.name } ?: emptyList()
                    apiService.getVehicles().enqueue(object : Callback<List<Vehicle>> {
                        override fun onResponse(call: Call<List<Vehicle>>, response: Response<List<Vehicle>>) {
                            if (response.isSuccessful) {
                                val vehicles = response.body()?.map { it.name } ?: emptyList()
                                planetAdapter = PlanetSelectionAdapter(planets, vehicles) { position, planet, vehicle ->
                                    selectedPlanets[position] = planet
                                    selectedVehicles[position] = vehicle
                                }
                                binding.recyclerViewPlanets.adapter = planetAdapter
                            } else {
                                Log.e("VEHICLE_ERROR", "Error loading vehicles: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<List<Vehicle>>, t: Throwable) {
                            Toast.makeText(this@MainActivity, "Failed to load vehicles", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Log.e("PLANET_ERROR", "Error loading planets: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Planet>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to load planets", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun findFalcone() {
        if (selectedPlanets.size < 4 || selectedVehicles.size < 4) {
            Toast.makeText(this, "Please select 4 planets and vehicles", Toast.LENGTH_LONG).show()
            return
        }

        binding.tvResult.visibility = View.VISIBLE
        binding.tvResult.text = "Searching for Falcone..."

        apiService.getToken().enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (!response.isSuccessful || response.body() == null) {
                    Log.e("TOKEN_ERROR", "Failed to get token: ${response.errorBody()?.string()}")
                    binding.tvResult.text = "Failed to get token."
                    return
                }
                val tokenResponse = response.body()
                Log.d("TOKEN_SUCCESS", "Token received: ${tokenResponse?.token}")

                tokenResponse?.let {
                    val request = FindFalconeRequest(
                        it.token,
                        selectedPlanets.values.toList(),
                        selectedVehicles.values.toList()
                    )
                    apiService.findFalcone(request).enqueue(object : Callback<FindFalconeResponse> {
                        override fun onResponse(call: Call<FindFalconeResponse>, response: Response<FindFalconeResponse>) {
                            if (response.isSuccessful) {
                                val result = response.body()
                                binding.tvResult.text = if (result?.status == "success")
                                    "Found Falcone at ${result.planet_name}!" else "Falcone not found."
                            } else {
                                Log.e("FIND_ERROR", "Error finding Falcone: ${response.errorBody()?.string()}")
                                binding.tvResult.text = "Failed to search for Falcone."
                            }
                        }

                        override fun onFailure(call: Call<FindFalconeResponse>, t: Throwable) {
                            Log.e("FIND_ERROR", "Request failed: ${t.message}")
                            binding.tvResult.text = "Failed to search for Falcone."
                        }
                    })
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e("TOKEN_ERROR", "Request failed: ${t.message}")
                binding.tvResult.text = "Failed to get token."
            }
        })
    }
}
