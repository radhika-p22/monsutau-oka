package edu.chapman.monsutauoka
import android.content.Context
import java.time.LocalTime

enum class Activity {
    PLAY,
    SIT,
    POUT,
    EAT,
    BRUSH_TEETH
}

enum class Mood {
    HAPPY,
    AMBIVALENT,
    SAD
}

interface MyStorage {
    fun getString(key: String) : String?
    fun saveString(str: String)
}

class PikaActivityPlanner(myStorage: MyStorage) {

    var mood: Mood

    init {
        val savedMood = myStorage.getString("mood")

        mood = if (savedMood != null) {
            try {
                Mood.valueOf(savedMood)
            } catch (e: IllegalArgumentException) {
                Mood.HAPPY // default if stored value is invalid
            }
        } else {
            Mood.HAPPY // default if nothing is stored
        }
    }

    fun getActivity(currentTime: LocalTime = LocalTime.now()): Activity {
        TODO("not implemented")
        //val currentTime = LocalTime.now()

        return when {
            // Between 1:00 PM and 2:00 PM
            currentTime.isAfter(LocalTime.of(13, 0).minusNanos(1)) &&
                    currentTime.isBefore(LocalTime.of(14, 0)) -> Activity.EAT

            // Between 2:00 PM and 3:00 PM
            currentTime.isAfter(LocalTime.of(14, 0).minusNanos(1)) &&
                    currentTime.isBefore(LocalTime.of(15, 0)) -> Activity.BRUSH_TEETH

            // Otherwise, pick activity based on current mood
            else -> when (mood) {
                Mood.HAPPY -> Activity.PLAY
                Mood.AMBIVALENT -> Activity.SIT
                Mood.SAD -> Activity.POUT
            }
        }
    }

    fun saveMood(context: Context) {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("mood", mood.name)
            .apply()
    }
}

interface ActivityPlanner {
    var mood: Mood
    fun getActivity(currentTime: LocalTime = LocalTime.now()): Activity
    fun saveMood(context: android.content.Context)
}