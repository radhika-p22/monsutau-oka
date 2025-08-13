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
    private val PREF_KEY_LAST_UPDATE = "happiness_last_update"

    private val handler = Handler(Looper.getMainLooper())
    private val happinessDecayRunnable = object : Runnable {
        override fun run() {
            if (happinessLevel > minHappiness) {
                happinessLevel--
                saveHappinessLevel() // persist every tick
                updateHappinessDisplay()
            }
            handler.postDelayed(this, happinessDecayIntervalMillis)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBetaBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Load saved value
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        happinessLevel = prefs.getInt(PREF_KEY_HAPPINESS, happinessLevel).coerceIn(minHappiness, maxHappiness)

        // 2) Optional: apply decay that should’ve happened while away
        val lastUpdate = prefs.getLong(PREF_KEY_LAST_UPDATE, 0L)
        if (lastUpdate > 0L) {
            val elapsed = System.currentTimeMillis() - lastUpdate
            val minutesElapsed = (elapsed / happinessDecayIntervalMillis).toInt()
            if (minutesElapsed > 0) {
                happinessLevel = (happinessLevel - minutesElapsed).coerceAtLeast(minHappiness)
            }
        }

        updateHappinessDisplay()
        handler.postDelayed(happinessDecayRunnable, happinessDecayIntervalMillis)
    }

    private fun updateHappinessDisplay() {
        binding.textHappinessValue.text = "$happinessLevel/$maxHappiness"
    }

    override fun onPause() {
        super.onPause()
        saveHappinessLevel() // make sure the most recent values are persisted
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(happinessDecayRunnable) // avoid leaks / double timers
        saveHappinessLevel()
    }

    private fun saveHappinessLevel() {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(PREF_KEY_HAPPINESS, happinessLevel)
            .putLong(PREF_KEY_LAST_UPDATE, System.currentTimeMillis())
            .apply()
    }
}