package com.example.feproblem1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.feproblem1.databinding.ItemPlanetSelectionBinding
import com.example.feproblem1.model.Planet
import com.example.feproblem1.model.Vehicle

class PlanetSelectionAdapter(
    private val planets: List<Planet>,
    private val vehicles: List<Vehicle>,
    private val onSelectionChanged: (String, String, Int, Boolean) -> Unit
) : RecyclerView.Adapter<PlanetSelectionAdapter.PlanetViewHolder>() {

    private val selectedPlanets = mutableSetOf<String>()
    private val selectedPlanetVehicleMap = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanetViewHolder {
        val binding = ItemPlanetSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanetViewHolder, position: Int) {
        val planet = planets[position]
        holder.bind(planet)
    }

    override fun getItemCount(): Int = planets.size

    inner class PlanetViewHolder(private val binding: ItemPlanetSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(planet: Planet) {
            binding.tvPlanetName.text = planet.name

            // Set up vehicle dropdown
            val vehicleNames = vehicles.map { it.name }
            val adapter = ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_dropdown_item, vehicleNames)
            binding.spinnerVehicles.adapter = adapter

            // Restore previous selection state
            if (selectedPlanets.contains(planet.name)) {
                binding.spinnerVehicles.visibility = View.VISIBLE
                val selectedVehicle = selectedPlanetVehicleMap[planet.name]
                val vehicleIndex = vehicles.indexOfFirst { it.name == selectedVehicle }
                if (vehicleIndex != -1) {
                    binding.spinnerVehicles.setSelection(vehicleIndex)
                }
            } else {
                binding.spinnerVehicles.visibility = View.GONE
            }

            // Handle planet selection
            binding.root.setOnClickListener {
                if (selectedPlanets.contains(planet.name)) {
                    // Deselect planet
                    selectedPlanets.remove(planet.name)
                    selectedPlanetVehicleMap.remove(planet.name)
                    binding.spinnerVehicles.visibility = View.GONE
                    onSelectionChanged(planet.name, "", 0, false)
                } else {
                    if (selectedPlanets.size < 4) {
                        selectedPlanets.add(planet.name)
                        binding.spinnerVehicles.visibility = View.VISIBLE
                        binding.spinnerVehicles.setSelection(0) // Default vehicle selection
                    }
                }
            }

            // Handle vehicle selection
            binding.spinnerVehicles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                    if (selectedPlanets.contains(planet.name)) {
                        val selectedVehicle = vehicles[position]
                        val travelTime = planet.distance / selectedVehicle.speed
                        selectedPlanetVehicleMap[planet.name] = selectedVehicle.name
                        onSelectionChanged(planet.name, selectedVehicle.name, travelTime, true)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }
}
