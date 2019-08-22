package com.granzotto.mqttchat.screens

import android.content.Intent
import android.os.Bundle
import com.granzotto.mqttchat.BaseActivity
import com.granzotto.mqttchat.R
import com.granzotto.mqttchat.SharedPreferencesHelper
import com.granzotto.mqttchat.mqtt.MqttManager
import kotlinx.android.synthetic.main.activity_main.*

@ExperimentalStdlibApi
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SharedPreferencesHelper.username?.let {
            usernameTextView.text = "Hey, $it"
        }

        logoffButton.setOnClickListener {
            SharedPreferencesHelper.clearPreferences()
            MqttManager.disconnect()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        startButton.setOnClickListener { onStartButtonClicked() }
    }

    private fun onStartButtonClicked() {
        val userToChat = userEditText.text.toString()
        if (userToChat.isEmpty()) return
        startActivity(ChatActivity.getIntent(this, userToChat))
    }
}
