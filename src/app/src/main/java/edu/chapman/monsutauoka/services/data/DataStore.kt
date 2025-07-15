package edu.chapman.monsutauoka.services.data

interface DataStore {

    fun save(key: String, value: String)

    fun load(key: String) : String?
}