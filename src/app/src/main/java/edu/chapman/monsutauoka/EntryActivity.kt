package edu.chapman.monsutauoka

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import edu.chapman.monsutauoka.databinding.ActivityEntryBinding
import edu.chapman.monsutauoka.extensions.TAG

class EntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        if (hasPermission) {
            goToMainActivity()
            return
        }

        binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonContinue.setOnClickListener {
            if (hasPermission) {
                goToMainActivity()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    0
                )
            }
        }
    }

    val hasPermission : Boolean
        get() {
            val isPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            )

            return isPermissionGranted == PackageManager.PERMISSION_GRANTED
        }

    fun goToMainActivity() {
        Log.i(TAG, ::goToMainActivity.name)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}