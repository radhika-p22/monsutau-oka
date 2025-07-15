package edu.chapman.monsutauoka.services.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import edu.chapman.monsutauoka.MainActivity

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