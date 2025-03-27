package com.example.feproblem1.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feproblem1.adapter.PlanetSelectionAdapter
import com.example.feproblem1.databinding.ActivityMainBinding
import com.example.feproblem1.service.RetrofitClient
import com.example.feproblem1.viewmodel.FindFalconeViewModel
import com.example.feproblem1.viewmodel.FindFalconeViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FindFalconeViewModel
    private lateinit var planetAdapter: PlanetSelectionAdapter
    private val selectedPlanets = mutableListOf<String>()
    private val selectedVehicles = mutableListOf<String>()
    private val travelTimes = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = FindFalconeViewModelFactory(RetrofitClient.apiService)
        viewModel = ViewModelProvider(this, factory)[FindFalconeViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        binding.btnFindFalcone.setOnClickListener { findFalcone() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewPlanets.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.planets.collectLatest { planets ->
                viewModel.vehicles.collectLatest { vehicles ->
                    if (planets.isNotEmpty() && vehicles.isNotEmpty()) {
                        planetAdapter = PlanetSelectionAdapter(planets, vehicles) { planet, vehicle, time, isSelected ->
                            if (isSelected) {
                                if (selectedPlanets.size < 4) {
                                    selectedPlanets.add(planet)
                                    selectedVehicles.add(vehicle)
                                    travelTimes.add(time)
                                }
                            } else {
                                val index = selectedPlanets.indexOf(planet)
                                if (index != -1) {
                                    selectedPlanets.removeAt(index)
                                    selectedVehicles.removeAt(index)
                                    travelTimes.removeAt(index)
                                }
                            }
                            updateTotalTime()
                        }
                        binding.recyclerViewPlanets.adapter = planetAdapter
                    }
                }
            }
        }
    }

    private fun updateTotalTime() {
        val totalTime = travelTimes.sum()
        binding.tvTotalTime.text = "Total Time: $totalTime hours"
    }

    private fun findFalcone() {
        if (selectedPlanets.size < 4 || selectedVehicles.size < 4) {
            Toast.makeText(this, "Please select 4 planets and 4 vehicles", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            viewModel.getToken()
            viewModel.token.collectLatest { token ->
                if (!token.isNullOrEmpty()) {
                    viewModel.findFalcone(selectedPlanets.joinToString(","), selectedVehicles, selectedPlanets)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.falconeResult.collectLatest { result ->
                val intent = Intent(this@MainActivity, ResultActivity::class.java)
                intent.putExtra("result_status", result?.status)
                intent.putExtra("planet_name", result?.planetName)
                intent.putExtra("total_time", travelTimes.sum())
                startActivity(intent)
            }
        }
    }
}
