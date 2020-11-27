package com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites.index.IndexActivity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.ads.SingletonAds
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.loadImageWithGlide
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.startNewActivity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.isAlreadyPurchased
import kotlinx.android.synthetic.main.activity_splash.*
// some comment here
class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        iv_splash.loadImageWithGlide(R.drawable.splash, this)

         permission()



    }
private fun permission(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            100
        )
    } else {
        handler()
    }
}
    private fun handler() {
        Handler(Looper.getMainLooper()).postDelayed({

         startNewActivity<IndexActivity>()
            finish()
            if(!isAlreadyPurchased())
             showInterstitialAd()


        }, 6000)
    }

    private fun showInterstitialAd() {
        if (SingletonAds.instance.isLoaded && ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
            SingletonAds.instance.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                handler()
            } else {
                finish()
            }
        }
    }




}