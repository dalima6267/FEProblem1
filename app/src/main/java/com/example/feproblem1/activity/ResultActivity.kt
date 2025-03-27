package com.example.feproblem1.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.feproblem1.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val status = intent.getStringExtra("result_status")
        val planetName = intent.getStringExtra("planet_name")
        val totalTime = intent.getIntExtra("total_time", 0)

        if (status == "success") {
            binding.tvResultMessage.text = "Found Falcone at $planetName!"
        } else {
            binding.tvResultMessage.text = "Falcone not found."
        }

        binding.tvFinalTime.text = "Total Travel Time: $totalTime hours"

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
