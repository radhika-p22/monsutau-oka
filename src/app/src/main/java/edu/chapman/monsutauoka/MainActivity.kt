package edu.chapman.monsutauoka

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import edu.chapman.monsutauoka.databinding.ActivityMainBinding
import edu.chapman.monsutauoka.extensions.TAG
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

interface DataStore {

    fun save(key: String, value: String)

    fun load(key: String) : String?
}

class SharedPreferencesDataStore (val prefs: SharedPreferences) : DataStore {

    override fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun load(key: String) : String? {
        return prefs.getString(key, null)
    }
}

class StepCounterService(val dataStore: DataStore) {

    private val _key: String
    private var _steps: Float
    private var initialCount: Float? = null

    val steps: Float
        get() = _steps

    init {
        _key = "${this::class.simpleName}.${::steps.name}" // "StepCounterService.steps"
        _steps = dataStore.load(_key)?.toFloatOrNull() ?: 0f
    }

    fun updateSteps(newCount: Float) {
        if (initialCount == null) {
            initialCount = newCount
            return
        }

        _steps += newCount - initialCount!!
        dataStore.save(_key, _steps.toString())
    }
}

class SharedViewModel {

}

interface StepCounterManager {
    fun onResume()
    fun onPause()
}

class MockStepCounterManager(val mainActivity: MainActivity) : StepCounterManager {
    override fun onResume() {

    }

    override fun onPause() {
    }
}

class SensorStepCounterManager(
    val mainActivity: MainActivity,
    val sensorManager: SensorManager,
    val stepCounterSensor: Sensor
) : StepCounterManager, SensorEventListener {

    override fun onResume() {
        sensorManager.registerListener(mainActivity, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager.unregisterListener(mainActivity)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event.sensor.type != Sensor.TYPE_STEP_COUNTER) {
            return
        }

        val count = event.values[0]
        stepCounterService.updateSteps(count)
        Log.v(TAG, count.toString())
    }

    // We have to implement this function to meet the sensor event listener interface;
    // however we don't care about accuracy, so we can simply ignore this event
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var stepCounterService: StepCounterService
    private lateinit var sensorManager: SensorManager
    private lateinit var stepCounterSensor: Sensor
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSensors()
        setupNav()
    }

    fun setupSensors() {

        // We know that we have permissions because the entry activity should have asserted that
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (sensor == null) {
            Toast.makeText(this, "No step counter sensor!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // AHHHHHH
        stepCounterSensor = sensor

        val sharedPreferences = getSharedPreferences(this::class.simpleName, MODE_PRIVATE)
        val dataStore = SharedPreferencesDataStore(sharedPreferences)
        stepCounterService = StepCounterService(dataStore)
    }

    fun setupNav() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_alpha,
                R.id.navigation_beta,
                R.id.navigation_gamma
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_STEP_COUNTER) {
            return
        }

        val count = event.values[0]
        stepCounterService.updateSteps(count)
        Log.v(TAG, count.toString())
    }

    // We have to implement this function to meet the sensor event listener interface;
    // however we don't care about accuracy, so we can simply ignore this event
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    fun updateSteps(newStepCount: Float) {
        stepCounterService.updateSteps(newStepCount)
        Log.v(TAG, newStepCount.toString())
    }
}