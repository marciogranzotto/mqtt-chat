package com.granzotto.mqttchat.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.granzotto.mqttchat.BaseActivity
import com.granzotto.mqttchat.R
import com.granzotto.mqttchat.SharedPreferencesHelper
import com.granzotto.mqttchat.mqtt.ConnectionListener
import com.granzotto.mqttchat.mqtt.MqttManager
import kotlinx.android.synthetic.main.activity_login.*

@ExperimentalStdlibApi
class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = getString(R.string.login)

        loginButton.setOnClickListener { login() }

        SharedPreferencesHelper.username?.let {
            connect()
        }
    }

    private fun login() {
        val username = userEditText.text?.toString() ?: return
        if (username.isBlank()) return
        SharedPreferencesHelper.username = username
        connect()
    }

    private fun connect() {
        showLoadingDialog()
        MqttManager.connect(object : ConnectionListener {
            override fun onConnected() {
                dismissLoadingDialog()
                goToMainScreen()
            }

            override fun onConnectionError(exception: Throwable?) {
                dismissLoadingDialog()
                exception?.let { Log.e("LoginActivity", "Error connecting", it) }
                Snackbar.make(rootView, "Error connecting to broker!", Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun goToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
