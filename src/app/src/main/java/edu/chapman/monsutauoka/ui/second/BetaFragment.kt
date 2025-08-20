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
    private val TREATS_PER_HAPPINESS = 2

    private val PREF_NAME = "pet_prefs"
    private val PREF_KEY_HAPPINESS = "happiness_level"
    private val PREF_KEY_LAST_UPDATE = "happiness_last_update"
    private val PREF_KEY_TREATS = "treat_count"
    private val PREF_KEY_TREAT_PROGRESS = "treat_progress"

    // Notification constants (action string + request code + threshold)
    companion object {
        private const val ACTION_LOW_HAPPINESS = "edu.chapman.monsutauoka.action.LOW_HAPPINESS"
        private const val NOTIF_REQ_CODE = 1001
        private const val LOW_HAPPINESS_THRESHOLD = 3
    }

    private val handler = Handler(Looper.getMainLooper())

    // Minute-by-minute decay: every tick lowers happiness (down to min), saves it, updates UI, then re-posts itself.
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

    // Convenience getter for the system AlarmManager used to schedule/cancel the notification alarm.
    val alarmManager
        get() = mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /** Inflates the view binding for this fragment (hooking up to fragment_beta.xml). */
    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBetaBinding {
        return FragmentBetaBinding.inflate(inflater, container, false)
    }

    /**
     * Sets up click handlers, loads saved happiness, applies any "missed" decay while away,
     * updates UI, and starts the recurring 60s decay timer.
     */
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

        // Apply decay that would have happened while the screen was not visible.
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

    /** Updates the "X/20" happiness text and swaps the monster image to match the current range. */
    private fun updateHappinessDisplay() {
        binding.textHappinessValue.text = "$happinessLevel/$maxHappiness"
        updateMoodImage(happinessLevel)
    }

    /** Chooses which drawable to show based on happiness (<=3, <=8, <=12, <=16, else). */
    private fun updateMoodImage(level: Int) {
        val resId = when {
            level <= 3  -> R.drawable.pikachu_angry
            level <= 8  -> R.drawable.pikachu_worried
            level <= 12 -> R.drawable.pikachu_normal
            level <= 16 -> R.drawable.pikachu_content
            else        -> R.drawable.pikachu_happy
        }
        binding.monsterimageView.setImageResource(resId)
    }

    /** Persists the latest happiness/timestamp when the fragment is paused. */
    override fun onPause() {
        super.onPause()
        saveHappinessLevel() // make sure the most recent values are persisted
    }

    /** When returning to this screen, refresh treats and happiness/mood display. */
    override fun onResume() {
        super.onResume()
        updateTreatsDisplay() // refresh when returning from Alpha or Gamma
        updateHappinessDisplay()
    }

    /** Stops the decay timer and saves happiness once more to avoid double timers or stale data. */
    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(happinessDecayRunnable) // avoid leaks / double timers
        saveHappinessLevel()
    }

    /** Writes happiness value and "last updated" timestamp to SharedPreferences. */
    private fun saveHappinessLevel() {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(PREF_KEY_HAPPINESS, happinessLevel)
            .putLong(PREF_KEY_LAST_UPDATE, System.currentTimeMillis())
            .apply()
    }

    /** Reads the current treat count from SharedPreferences and displays it. */
    private fun updateTreatsDisplay() {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val treats = prefs.getInt(PREF_KEY_TREATS, 0)
        binding.textTreatsValue.text = treats.toString()
    }

    /**
     * Spends one treat (if any), tracks progress toward the next +1 happiness,
     * adds happiness every TREATS_PER_HAPPINESS treats, then saves & refreshes UI.
     */
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
                updateHappinessDisplay()  // updates the text and image
                saveHappinessLevel()      // refreshes last-update timestamp
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

    /**
     * Calculates when happiness will reach ≤3 (based on last update + 1/min decay),
     * then schedules an alarm for that time (exact if permitted, otherwise inexact).
     */
    fun scheduleNotification(context: Context) {
        // compute when happiness will hit <= 3
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val lastUpdate = prefs.getLong(PREF_KEY_LAST_UPDATE, System.currentTimeMillis())
        val decrementsNeeded = (happinessLevel - LOW_HAPPINESS_THRESHOLD).coerceAtLeast(0)
        val targetTime = lastUpdate + decrementsNeeded * happinessDecayIntervalMillis
        val triggerAt = maxOf(System.currentTimeMillis() + 1_000L, targetTime)

        val pi = createLowHappinessPendingIntent(context)

        // cancel any existing alarm with the same PI
        alarmManager.cancel(pi)

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                // Android 12+: need SCHEDULE_EXACT_ALARM or exemption
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                } else {
                    // fallback: inexact (won't crash)
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                    Toast.makeText(
                        context,
                        "Scheduled (approx). Enable exact alarms in Settings for precise timing.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
            Toast.makeText(context, "Notification scheduled", Toast.LENGTH_SHORT).show()
        } catch (se: SecurityException) {
            // Safety net if an OEM throws anyway
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            Toast.makeText(
                context,
                "Scheduled (approx). Exact alarm permission not granted.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /** Cancels the low-happiness alarm by recreating the same PendingIntent and passing it to AlarmManager.cancel(). */
    fun cancelNotification(context: Context) {
        val pi = createLowHappinessPendingIntent(context)
        alarmManager.cancel(pi)
        Toast.makeText(context, "Notification cancelled", Toast.LENGTH_SHORT).show()
    }

    /** Builds the unique PendingIntent that wakes NotificationReceiver when happiness is low (≤3). */
    private fun createLowHappinessPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_LOW_HAPPINESS
            putExtra("title", "Your monster needs you!")
            putExtra("text", "Happiness is low (≤ 3). Time to feed or play.")
        }
        return PendingIntent.getBroadcast(
            context,
            NOTIF_REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
