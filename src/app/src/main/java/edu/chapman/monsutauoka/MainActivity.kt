package edu.chapman.monsutauoka

import android.content.SharedPreferences
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    private var _previousCount: Float? = null

    private val _steps = MutableStateFlow(0f)
    val steps: StateFlow<Float> = _steps

    init {
        _key = "${this::class.simpleName}.${::steps.name}" // "StepCounterService.steps"
        _steps.value = dataStore.load(_key)?.toFloatOrNull() ?: 0f
    }

    fun updateSteps(newCount: Float) {
        if (_previousCount == null) {
            _previousCount = newCount
            return
        }

        _steps.value += newCount - _previousCount!!
        _previousCount = newCount

        dataStore.save(_key, _steps.value.toString())
    }
}

class SharedViewModel {

}

interface StepSensorManager {
    fun onResume()
    fun onPause()
}

class MockStepSensorManager(val mainActivity: MainActivity) : StepSensorManager {
    private var stepCount: Float = 0f

    init {
        mainActivity.lifecycleScope.launch {
            mainActivity.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    delay(1000L) // wait 1 second
                    mainActivity.updateSteps(stepCount++)
                }
            }
        }
    }

    override fun onResume() {
    }

    override fun onPause() {
    }
}

class RealStepSensorManager(
    val mainActivity: MainActivity,
    val sensorManager: SensorManager,
    val stepCounterSensor: Sensor
) : StepSensorManager, SensorEventListener {

    override fun onResume() {
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        if (event.sensor.type != Sensor.TYPE_STEP_COUNTER) {
            return
        }

        val count = event.values[0]
        mainActivity.updateSteps(count)
    }

    // We have to implement this function to meet the sensor event listener interface;
    // however we don't care about accuracy, so we can simply ignore this event
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

class MainActivity : AppCompatActivity() {



    private lateinit var stepCounterService: StepCounterService
    private lateinit var stepSensorManager: StepSensorManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSensors()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNav()
    }

    fun setupSensors() {

        // We know that we have permissions because the entry activity should have asserted that
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (sensor != null) {
            stepSensorManager = RealStepSensorManager(this, sensorManager, sensor)
        } else {
            Toast.makeText(this, "Using MOCK Step Counter", Toast.LENGTH_SHORT).show()
            stepSensorManager = MockStepSensorManager(this)
        }


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
        stepSensorManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        stepSensorManager.onPause()
    }

    fun updateSteps(newStepCount: Float) {
        stepCounterService.updateSteps(newStepCount)
        Log.v(TAG, newStepCount.toString())
    }

    fun getStepCounterService(): StepCounterService {
        return stepCounterService
    }
}