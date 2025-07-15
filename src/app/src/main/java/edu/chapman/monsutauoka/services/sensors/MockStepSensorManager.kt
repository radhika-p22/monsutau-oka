package edu.chapman.monsutauoka.services.sensors

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import edu.chapman.monsutauoka.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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