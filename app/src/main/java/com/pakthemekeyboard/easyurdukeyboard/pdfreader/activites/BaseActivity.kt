package com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.ads.SingletonAds
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.isAlreadyPurchased
import kotlinx.android.synthetic.main.layout_rate_dialog.view.*
import java.io.File
import java.net.URLConnection


open class BaseActivity : AppCompatActivity() {
    companion object {
        var INDEX_AD_COUNT: Int = 1
        var FAVOURITE_AD_COUNT: Int = 1
    }


    fun showRateUsDialog() {
        val inflater = LayoutInflater.from(this)
        val alertDialogBuilder = AlertDialog.Builder(this)
        val dialogView = inflater.inflate(R.layout.layout_rate_dialog, null)
        var alertDialog: AlertDialog? = null

        dialogView.dialogRateBtn.setOnClickListener {
            openAppInPlayStore()
            alertDialog?.dismiss()
        }

        dialogView.dialogCross.setOnClickListener {
            alertDialog?.dismiss()
            finish()

        }

        alertDialogBuilder.setView(dialogView)
        alertDialogBuilder
            .setCancelable(true)

        alertDialog = alertDialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()
    }

    fun openAppInPlayStore() {
        val uri = Uri.parse("market://details?id=$packageName")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show()
        }
    }

    fun shareFile(path: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)

        ShareCompat.IntentBuilder.from(this)
            .setStream(uri)
            .setType(URLConnection.guessContentTypeFromName(file.name))
            .startChooser()
    }


    open fun showIndexAd(counter: Int) {
        if (counter % 2 == 0) {
            if(!isAlreadyPurchased())
            showInterstitial()
        }
    }

    fun showInterstitial() {
        if (SingletonAds.instance.isLoaded && ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(
                Lifecycle.State.STARTED)) {
            SingletonAds.instance.show()
        }
    }


    fun showExitDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle).apply {
            setTitle("Rate Us")
            setMessage("We will appreciate if you rate us")
        }

        alertDialogBuilder.apply {
            setPositiveButton("Rate Us") { _, _ ->
                openAppInPlayStore()
            }

            setNegativeButton("Exit") { dialogInterface, _ ->

                super.onBackPressed()
            }
        }

        alertDialogBuilder.setCancelable(true)
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}