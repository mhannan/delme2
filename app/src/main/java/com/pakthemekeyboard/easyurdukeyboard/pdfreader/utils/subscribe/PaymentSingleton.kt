package com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe

import android.content.Context
import com.anjlab.android.iab.v3.BillingProcessor
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.Constants

class PaymentSingleton {


    companion object {

        private var paymentInstance: BillingProcessor? = null

        fun init(context: Context) {
            if (paymentInstance == null) {
                paymentInstance = BillingProcessor(context, Constants.billingKey, null)
            }
        }

        fun getInstance(): BillingProcessor {
            return paymentInstance!!
        }

    }

}