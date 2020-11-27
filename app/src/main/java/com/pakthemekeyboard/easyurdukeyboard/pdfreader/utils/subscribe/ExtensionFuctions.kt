package com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.Constants

fun subcriptionDialoge(context: Context,activity: Activity) {
    try {
        val inflater = LayoutInflater.from(context)
        val promptsView = inflater.inflate(R.layout.subscribedialog, null)
        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setView(promptsView)
        val sub = promptsView.findViewById(R.id.subscribe) as Button
        val priceTxt = promptsView.findViewById(R.id.pricetxt) as TextView
        val cancel = promptsView.findViewById(R.id.cancel) as ImageView

        val priceList = PaymentSingleton.getInstance()
                .getSubscriptionListingDetails(Constants.monthlySubscriptionId)
        val price = if (priceList?.priceText.isNullOrEmpty())
            "No Internet"
        else
            priceList?.priceText
        priceTxt.text = price



        alertDialogBuilder
                .setCancelable(true)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        sub.setOnClickListener {
           // PaymentSingleton.getInstance().subscribe(activity, Constants.monthlySubscriptionId)
            PaymentSingleton.getInstance()  .purchase(activity, Constants.monthlySubscriptionId)
            alertDialog.dismiss()
        }

        cancel.setOnClickListener {
            alertDialog.dismiss()
        }

    } catch (ex: Exception) {
        Toast.makeText(context,ex.message,Toast.LENGTH_SHORT).show()
    }
}

fun isAlreadyPurchased(): Boolean {
  //  return PaymentSingleton.getInstance().isSubscribed(Constants.monthlySubscriptionId)
    return PaymentSingleton.getInstance().isPurchased(Constants.monthlySubscriptionId)
}



