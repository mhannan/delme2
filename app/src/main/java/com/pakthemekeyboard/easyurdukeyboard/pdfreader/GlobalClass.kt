package com.pakthemekeyboard.easyurdukeyboard.pdfreader

import androidx.multidex.MultiDexApplication
import com.google.android.gms.ads.MobileAds
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.ads.SingletonAds
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.PaymentSingleton

class GlobalClass : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        SingletonAds.init(this)
        PaymentSingleton.Companion.init(this)
    }
}