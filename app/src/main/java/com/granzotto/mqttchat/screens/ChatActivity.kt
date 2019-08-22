package com.granzotto.mqttchat.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.github.bassaer.chatmessageview.model.Message
import com.google.android.material.snackbar.Snackbar
import com.granzotto.mqttchat.BaseActivity
import com.granzotto.mqttchat.R
import com.granzotto.mqttchat.SharedPreferencesHelper
import com.granzotto.mqttchat.models.User
import com.granzotto.mqttchat.mqtt.MqttManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chat.*

@ExperimentalStdlibApi
class ChatActivity : BaseActivity() {

    companion object {
        private const val USER_TO_CHAT = "UserToChat"

        fun getIntent(context: Context, userToChat: String): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(USER_TO_CHAT, userToChat)
            return intent
        }
    }

    private var yourUser: User? = null
    private var myUser: User? = null
    private val compositeDisposable = CompositeDisposable()

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        createUsers()
        startChat()

        chatView.setOnClickSendButtonListener(View.OnClickListener {
            sendMessage(chatView.inputText)
            chatView.inputText = ""
        })
    }

    private fun createUsers() {
        val myUserName = SharedPreferencesHelper.username ?: "Me"
        myUser = User(0, myUserName, null)

        val yourUserName = intent.getStringExtra(USER_TO_CHAT)
        title = yourUserName
        yourUser = User(1, yourUserName, null)
    }

    private fun startChat() {
        val myUserName = myUser?.getName() ?: return
        val yourUserName = yourUser?.getName() ?: return
        val disposable = MqttManager.subscribe("chat/$myUserName/$yourUserName")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ mqttMessage ->
                val payload = mqttMessage.payload.decodeToString()
                receivedMessage(payload)?.let { chatView.receive(it) }
            }, {
                Snackbar.make(rootView, "Error starting the chat!", Snackbar.LENGTH_LONG).show()
            })
        compositeDisposable.add(disposable)
    }

    private fun receivedMessage(content: String): Message? {
        val yourUser = yourUser ?: return null
        return Message.Builder()
            .setUser(yourUser)
            .setRight(false)
            .hideIcon(true)
            .setText(content)
            .build()
    }

    private fun sendMessage(content: String) {
        val myUser = myUser ?: return
        val myUserName = myUser.getName() ?: return
        val yourUserName = yourUser?.getName() ?: return
        val topic = "chat/$yourUserName/$myUserName"
        val disposable = MqttManager.publish(topic, content.toByteArray())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                chatView.send(
                    Message.Builder()
                        .setUser(myUser)
                        .setRight(true)
                        .hideIcon(true)
                        .setText(content)
                        .build()
                )
            }, {
                Snackbar.make(rootView, "Error sending message!", Snackbar.LENGTH_LONG).show()
            })
        compositeDisposable.add(disposable)
    }
}
