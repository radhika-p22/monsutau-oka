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

    private val permissions = arrayOf(
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.POST_NOTIFICATIONS)

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        if (hasPermissions) {
            goToMainActivity()
            return
        }

        binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonContinue.setOnClickListener {
            if (hasPermissions) {
                goToMainActivity()
            } else {
                ActivityCompat.requestPermissions(this, permissions, 0)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (hasPermissions) {
            goToMainActivity()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (hasPermissions) {
            goToMainActivity()
        }
    }

    val hasPermissions : Boolean
        get() {
            permissions.forEach { permission ->
                val isPermissionGranted = ContextCompat.checkSelfPermission(
                    this,
                    permission
                )

                if (isPermissionGranted != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }

            return true
        }

    fun goToMainActivity() {
        Log.i(TAG, ::goToMainActivity.name)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}