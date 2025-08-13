package edu.chapman.monsutauoka.ui.second

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import edu.chapman.monsutauoka.NotificationReceiver
import edu.chapman.monsutauoka.databinding.FragmentBetaBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.ui.MainFragmentBase

class BetaFragment : MainFragmentBase<FragmentBetaBinding>() {

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
}
