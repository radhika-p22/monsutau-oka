package edu.chapman.monsutauoka.ui.second

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import edu.chapman.monsutauoka.NotificationReceiver
import edu.chapman.monsutauoka.R
import edu.chapman.monsutauoka.databinding.FragmentBetaBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.ui.MainFragmentBase

class BetaFragment : MainFragmentBase<FragmentBetaBinding>() {

    private var happinessLevel = 15
    private val maxHappiness = 20
    private val minHappiness = 1
    private val happinessDecayIntervalMillis = 60_000L // 60 seconds
    private val PREF_KEY_FEED_PROGRESS = "feed_progress"   // 0..4 treats spent toward +1 happiness
    private val TREATS_PER_HAPPINESS = 5


    private val PREF_NAME = "pet_prefs"
    private val PREF_KEY_HAPPINESS = "happiness_level"
    private val PREF_KEY_LAST_UPDATE = "happiness_last_update"
    private val PREF_KEY_TREATS = "treat_count"
    private val PREF_KEY_TREAT_PROGRESS = "treat_progress"

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

    val alarmManager
        get() = mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBetaBinding {
        return FragmentBetaBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, ::onViewCreated.name)

        binding.buttonSchedule.setOnClickListener {
            scheduleNotification(mainActivity)

        }
        binding.buttonCancel.setOnClickListener {
            cancelNotification(mainActivity)
        }

        binding.buttonFeed.setOnClickListener {
            feedPet()
        }

        super.onViewCreated(view, savedInstanceState)

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
        updateTreatsDisplay()
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

    private fun feedPet() {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        var treats = prefs.getInt(PREF_KEY_TREATS, 0)
        if (treats <= 0) {
            Toast.makeText(requireContext(), "No treats left!", Toast.LENGTH_SHORT).show()
            return
        }

        // Spend one treat
        treats--

        // Track how many treats have been spent toward the next happiness point
        var feedProgress = prefs.getInt(PREF_KEY_FEED_PROGRESS, 0) + 1
        if (feedProgress >= TREATS_PER_HAPPINESS) {
            feedProgress = 0
            if (happinessLevel < maxHappiness) {
                happinessLevel += 1
                updateHappinessDisplay()  // updates the text and persists via saveHappinessLevel()
                saveHappinessLevel()      // also refreshes last-update timestamp
            }
        } else {
            // If happiness didn't change, still persist the current happiness timestamp
            saveHappinessLevel()
        }

        // Persist treats and feed progress
        prefs.edit()
            .putInt(PREF_KEY_TREATS, treats)
            .putInt(PREF_KEY_FEED_PROGRESS, feedProgress)
            .apply()

        // Refresh UI
        updateTreatsDisplay()
    }


    fun scheduleNotification(context: Context) {
        val pendingIntent = createPendingIntent(context)

        val triggerTime = System.currentTimeMillis() + 1000

        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)

        Toast.makeText(mainActivity, "Scheduled", Toast.LENGTH_SHORT).show()
    }

    fun cancelNotification(context: Context) {
        val pendingIntent = createPendingIntent(context)

        alarmManager.cancel(pendingIntent)

        Toast.makeText(mainActivity, "Cancelled", Toast.LENGTH_SHORT).show()
    }

    fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java)

        intent.putExtra("hello", "world")

        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    override fun onResume() {
        super.onResume()
        updateTreatsDisplay() // refresh when returning from Alpha
    }

    private fun updateTreatsDisplay() {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val treats = prefs.getInt(PREF_KEY_TREATS, 0)
        binding.textTreatsValue.text = treats.toString()
    }
}
