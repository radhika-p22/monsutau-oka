package edu.chapman.monsutauoka.ui.third

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GammaViewModel : ViewModel() {
    val num: MutableLiveData<Int> = MutableLiveData<Int>(0)
}