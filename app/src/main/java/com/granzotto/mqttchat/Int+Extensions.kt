package com.granzotto.mqttchat

import android.content.res.Resources

fun Int.dpToPx() = this.times(Resources.getSystem().displayMetrics.density)
