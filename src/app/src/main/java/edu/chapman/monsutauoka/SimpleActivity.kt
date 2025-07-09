package edu.chapman.monsutauoka

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.chapman.monsutauoka.databinding.ActivitySimpleBinding

class SimpleActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySimpleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySimpleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    fun setupButtons() {
        binding.simpleButton1.setOnClickListener {
            binding.simpleTextView.text = getString(R.string._true)
        }

        binding.simpleButton2.setOnClickListener {
            binding.simpleTextView.text = getString(R.string._false)
        }
    }
}