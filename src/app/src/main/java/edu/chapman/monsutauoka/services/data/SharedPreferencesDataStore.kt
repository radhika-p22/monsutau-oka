package edu.chapman.monsutauoka.services.data

import android.content.SharedPreferences

class SharedPreferencesDataStore (val prefs: SharedPreferences) : DataStore {

    override fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun load(key: String) : String? {
        return prefs.getString(key, null)
    }
}