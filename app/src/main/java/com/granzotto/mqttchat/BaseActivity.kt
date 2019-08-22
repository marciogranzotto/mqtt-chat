package com.granzotto.mqttchat

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    private var loadingDialog: AlertDialog? = null

    fun showLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = AlertDialog.Builder(this, R.style.LoadingDialogTheme)
            .setView(R.layout.view_loading_dialog)
            .setCancelable(false)
            .show()
    }

    fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }

}