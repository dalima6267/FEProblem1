package com.example.feproblem1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.feproblem1.databinding.ItemPlanetSelectionBinding

class PlanetSelectionAdapter(
private val planetList: List<String>,
private val vehicleList: List<String>,
private val onSelectionChanged: (Int, String, String) -> Unit
) : RecyclerView.Adapter<PlanetSelectionAdapter.PlanetViewHolder>() {

    private val selectedPlanets = mutableMapOf<Int, String>()
    private val selectedVehicles = mutableMapOf<Int, String>()

    inner class PlanetViewHolder(private val binding: ItemPlanetSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.spinnerPlanets.adapter =
                ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_dropdown_item, planetList)
            binding.spinnerVehicles.adapter =
                ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_dropdown_item, vehicleList)

            binding.spinnerPlanets.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    selectedPlanets[position] = planetList[pos]
                    notifySelectionChanged(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle no selection if needed
                }
            }


            binding.spinnerVehicles.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    selectedVehicles[position] = vehicleList[pos]
                    notifySelectionChanged(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle no selection if needed
                }
        }}

        private fun notifySelectionChanged(position: Int) {
            val planet = selectedPlanets[position] ?: ""
            val vehicle = selectedVehicles[position] ?: ""
            if (planet.isNotEmpty() && vehicle.isNotEmpty()) {
                onSelectionChanged(position, planet, vehicle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanetViewHolder {
        val binding = ItemPlanetSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanetViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 4
}