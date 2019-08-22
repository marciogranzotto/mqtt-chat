package com.granzotto.mqttchat.models

import android.graphics.Bitmap
import com.github.bassaer.chatmessageview.model.IChatUser

data class User(internal var id: Int?, internal var name: String, internal var icon: Bitmap?) :
    IChatUser {
    override fun getId(): String {
        return this.id!!.toString()
    }

    override fun getName(): String? {
        return this.name
    }

    override fun getIcon(): Bitmap? {
        return this.icon
    }

    override fun setIcon(icon: Bitmap) {
        this.icon = icon
    }
}