package com.example.feproblem1.model

data class FindFalconeRequest(val token: String, val planet_names: List<String>, val vehicle_names: List<String>)