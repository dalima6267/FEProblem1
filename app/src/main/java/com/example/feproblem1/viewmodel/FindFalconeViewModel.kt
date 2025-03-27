package com.example.feproblem1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feproblem1.model.*
import com.example.feproblem1.service.ApiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FindFalconeViewModel(private val apiService: ApiService) : ViewModel() {

    private val _planets = MutableStateFlow<List<Planet>>(emptyList())
    val planets: StateFlow<List<Planet>> = _planets

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val _falconeResult = MutableStateFlow<FindFalconeResponse?>(null)
    val falconeResult: StateFlow<FindFalconeResponse?> = _falconeResult

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadPlanetsAndVehicles()
    }

    private fun loadPlanetsAndVehicles() {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                val planetResponse = apiService.getPlanets()
                val vehicleResponse = apiService.getVehicles()

                if (planetResponse.isSuccessful && vehicleResponse.isSuccessful) {
                    _planets.emit(planetResponse.body() ?: emptyList())
                    _vehicles.emit(vehicleResponse.body() ?: emptyList())
                } else {
                    _errorMessage.emit("Failed to fetch data")
                    _planets.emit(emptyList())
                    _vehicles.emit(emptyList())
                }
            } catch (e: Exception) {
                _errorMessage.emit("Error: ${e.localizedMessage}")
                _planets.emit(emptyList())
                _vehicles.emit(emptyList())
            }
        }
    }

    fun getToken() {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                val response = apiService.getToken()
                if (response.isSuccessful) {
                    _token.emit(response.body()?.token)
                } else {
                    _errorMessage.emit("Failed to retrieve token")
                    _token.emit(null)
                }
            } catch (e: Exception) {
                _errorMessage.emit("Error: ${e.localizedMessage}")
                _token.emit(null)
            }
        }
    }

    fun findFalcone(
        selectedPlanets1: String,
        selectedPlanets: List<String>,
        selectedVehicles: List<String>
    ) {
        val tokenValue = _token.value ?: return

        val request = FindFalconeRequest(
            token = tokenValue,
            planetNames = selectedPlanets.joinToString(","),  // Convert list to comma-separated string
            vehicleNames = selectedVehicles
        )

        viewModelScope.launch {
            _errorMessage.value = null
            try {
                val response = apiService.findFalcone(request)
                if (response.isSuccessful) {
                    _falconeResult.emit(response.body())
                } else {
                    _errorMessage.emit("Failed to find Falcone")
                    _falconeResult.emit(null)
                }
            } catch (e: Exception) {
                _errorMessage.emit("Error: ${e.localizedMessage}")
                _falconeResult.emit(null)
            }
        }
    }
}
