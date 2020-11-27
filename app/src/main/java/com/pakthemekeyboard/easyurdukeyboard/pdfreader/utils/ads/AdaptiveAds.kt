package com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.ads

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.google.android.gms.ads.AdSize

class AdaptiveAds(private var contextA: Context) {
    val adSize: AdSize
        get() {
            val window =
                (contextA.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            val display = window.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val widthPixels = outMetrics.widthPixels.toFloat()
            val density = outMetrics.density
            val adWidth = (widthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                contextA,
                adWidth
            )
        }

}