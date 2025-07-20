package edu.chapman.monsutauoka.ui.first

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import edu.chapman.monsutauoka.services.StepCounterService

class AlphaViewModel(stepCounterService: StepCounterService) : ViewModel() {

    val steps: LiveData<Float> = stepCounterService.steps.asLiveData()
}