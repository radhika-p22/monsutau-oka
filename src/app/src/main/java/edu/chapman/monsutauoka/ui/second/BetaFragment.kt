package edu.chapman.monsutauoka.ui.second

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.chapman.monsutauoka.databinding.FragmentBetaBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.ui.MainFragmentBase
import android.os.Handler
import android.os.Looper
import android.content.Context


class BetaFragment : MainFragmentBase<FragmentBetaBinding>() {

    private var happinessLevel = 15
    private val maxHappiness = 20
    private val minHappiness = 1
    private val happinessDecayIntervalMillis = 60_000L // 60 seconds
    private val PREF_NAME = "pet_prefs"
    private val PREF_KEY_HAPPINESS = "happiness_level"


    private val handler = Handler(Looper.getMainLooper())
    private val happinessDecayRunnable = object : Runnable {
        override fun run() {
            if (happinessLevel > minHappiness) {
                happinessLevel--
                updateHappinessDisplay()
            }
            handler.postDelayed(this, happinessDecayIntervalMillis)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBetaBinding {
        return FragmentBetaBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, ::onViewCreated.name)

        updateHappinessDisplay() // show starting value
        handler.postDelayed(happinessDecayRunnable, happinessDecayIntervalMillis) // begin countdown
    }

    private fun updateHappinessDisplay() {
        binding.textHappinessValue.text = "$happinessLevel/$maxHappiness"
        saveHappinessLevel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(happinessDecayRunnable) // clean up!
    }

    private fun saveHappinessLevel() {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(PREF_KEY_HAPPINESS, happinessLevel).apply()
    }

}