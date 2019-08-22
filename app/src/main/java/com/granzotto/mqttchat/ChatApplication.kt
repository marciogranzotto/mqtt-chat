package com.granzotto.mqttchat

import android.app.Application
import com.granzotto.mqttchat.mqtt.MqttConstants
import com.granzotto.mqttchat.mqtt.MqttManager

class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesHelper.setup(this)
        with(MqttConstants) {
            MqttManager.setup(BROKER_URL, BROKER_PORT, USER, PASSWORD)
        }
    }
}