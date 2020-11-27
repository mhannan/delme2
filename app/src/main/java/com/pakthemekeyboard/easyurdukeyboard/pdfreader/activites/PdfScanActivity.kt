package com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.Constants
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.savePdf
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.showBanner
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.showSaveDialog
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.startNewActivity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.isAlreadyPurchased
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import kotlinx.android.synthetic.main.activity_scan.*
import kotlinx.android.synthetic.main.layout_adview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class PdfScanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        if(!isAlreadyPurchased()){
        showBanner(adView)
            constraintAd.visibility= View.VISIBLE
        }
        else
            constraintAd.visibility= View.GONE
        listeners()
    }

    private fun listeners() {
        imageView5.setOnClickListener {checkPermission()  }
        starScanBtn.setOnClickListener {
            checkPermission()
        }

        openGallery.setOnClickListener {
            startScan(ScanConstants.OPEN_MEDIA)
        }

        openScannedFiles.setOnClickListener {
            startNewActivity<ScannedFilesActivity>()
        }

        scannerBack.setOnClickListener {
            finish()
        }
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA
                ),
                Constants.CAMERA_REQUEST_CODE
            )
        }
    }

    private fun startScan(preference: Int) {
        val intent = Intent(
            this,
            ScanActivity::class.java
        )
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
        startActivityForResult(intent, Constants.SCAN_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val resultUri =
                data!!.extras!!.getParcelable<Uri>(ScanConstants.SCANNED_RESULT)

            val currentTime = System.currentTimeMillis()
            val fname =
                resources.getString(R.string.pdf_savefile_name) + currentTime + Constants.PDF
            val file = File(Constants.getDir(this), fname)


            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(this.contentResolver, resultUri!!)
                ImageDecoder.decodeBitmap(source)
                //ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, resultUri!!))
                MediaStore.Images.Media.getBitmap(contentResolver, resultUri)

            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, resultUri)
            }

            contentResolver.delete(resultUri!!, null, null)
            CoroutineScope(Dispatchers.IO).launch {
                savePdf(bitmap, file)
            }
//                backBtn.setVisibility(View.VISIBLE)
            showSaveDialog(fname)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
        }

        //super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                startScan(ScanConstants.OPEN_CAMERA)
            }
        }
    }
}