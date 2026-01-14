package com.smartchip.aidrink.android.mqtt

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("mqtt_prefs")

object TopicPreferences {

    private val LAST_TOPIC = stringPreferencesKey("last_topic")

    suspend fun saveTopic(context: Context, topic: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_TOPIC] = topic
        }
    }

    suspend fun getLastTopic(context: Context): String? {
        val prefs = context.dataStore.data.first()
        return prefs[LAST_TOPIC]
    }

    suspend fun clear(context: Context) {
        context.dataStore.edit { it.clear() }
    }
}