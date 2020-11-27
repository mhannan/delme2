package com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites.ScannedFilesActivity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.Constants
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.CustomDialog
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.ads.AdaptiveAds
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.ads.SingletonAds
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.isAlreadyPurchased
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun Context.shareApp() =
    this.startActivity(Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Check out the App at: https://play.google.com/store/apps/details?id=$packageName"
        )
        type = "text/plain"
    })

fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(applicationContext, msg, length).show()
}


//set action bar
fun AppCompatActivity.setUpActionBar(s: String, back: Boolean = false) {

    supportActionBar?.apply {
        title = s
        setDisplayHomeAsUpEnabled(true)
        setDisplayShowHomeEnabled(true)
    }
}

// startnewActivity
inline fun <reified A : Activity> Context.startNewActivity() {
    this.startActivity(Intent(this, A::class.java))
}

inline fun <reified A : Activity> Context.startActivitywithExtras(extras: Intent.() -> Unit) {
    val intent = Intent(this, A::class.java)
    extras(intent)
    this.startActivity(intent)
}


fun RecyclerView.adapterAndManager(
    adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    isLinearHorizontal: Boolean = false
) {
    this.layoutManager =
        if (isLinearHorizontal)
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        else
            LinearLayoutManager(context)
    this.itemAnimator = DefaultItemAnimator()
    this.adapter = adapter
}

fun FragmentActivity.openChooser() {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    val uri = Uri.parse("/storage/emulated/0")
    intent.setDataAndType(uri, "application/pdf")
    startActivityForResult(Intent.createChooser(intent, "Open folder"), Constants.PDF_REQUEST_CODE)
}

fun Long.millisToDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return calendar.time
}

fun Date.formatToDMY(): String {
    val df = SimpleDateFormat("dd/MM/yyyy");
    return df.format(this)
}

fun Activity.sizeFormatter(size: Long) =
    android.text.format.Formatter.formatShortFileSize(
        this,
        size
    )

///// glide
fun ImageView.loadImageWithGlide(imageId: Int, context: Context) {
    Glide.with(context)
        .load(imageId)
        .into(this);
}

fun Activity.allPermission() {

    val notFound = mutableListOf<Int>()
    val permission = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    for (i in permission.indices) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, permission[i])
            -> {
            }
            else -> {
                notFound.add(i)
            }
        }
    }
    if (notFound.size < 1) return
    val requiredPermission =
        arrayOfNulls<String>(notFound.size)
    for (i in 0 until notFound.size) {
        requiredPermission[i] = permission[notFound[i]]
    }
    ActivityCompat.requestPermissions(this, requiredPermission, 100)
}


fun Context.savePdf(
    bitmap: Bitmap,
    savePath: File?
) {
    val pdfDocument = PdfDocument()
    val pageInfo = PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()
    paint.color = Color.WHITE
    canvas.drawPaint(paint)
    val b = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, false)

    paint.color = Color.BLUE
    canvas.drawBitmap(b, 0f, 0f, null)
    pdfDocument.finishPage(page)
    try {
        val out = FileOutputStream(savePath!!)
        pdfDocument.writeTo(out)
        out.flush()
        out.close()
        pdfDocument.close()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(
            this,
            resources.getString(R.string.pdf_failed_to_save_msg),
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun Context.getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    val windowmanager =
        getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowmanager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Context.showSaveDialog(
    outputFile: String?
) {
    val inflater = LayoutInflater.from(this)
    val promptsView: View = inflater.inflate(R.layout.dialog_name_save, null)
    val alertDialogBuilder =
        AlertDialog.Builder(this)

    alertDialogBuilder.setView(promptsView)
    val userInput = promptsView.findViewById<EditText>(R.id.et_saveFileName)
    val done =
        promptsView.findViewById<ImageView>(R.id.btn_done)


    // set dialog message
    alertDialogBuilder
        .setCancelable(false)

    // create alert dialog
    val alertDialog = alertDialogBuilder.create().apply {
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(
            getScreenWidth() / 100 * 80,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    done.setOnClickListener { v: View? ->

        val newFileName = userInput.text.toString().trim()
        if (newFileName.isNotEmpty()) {
            val oldFile = File(Constants.getDir(this), outputFile)
            val newFile =
                File(Constants.getDir(this), newFileName + Constants.PDF)

            if (newFile.exists()) {
                Toast.makeText(this, "file name already exist", Toast.LENGTH_SHORT).show()
            } else {
                oldFile.renameTo(newFile)
                alertDialog.dismiss()
                CustomDialog(this).show()
                startNewActivity<ScannedFilesActivity>()


                Constants.reset_flag=true;
            }
        } else {
            Toast.makeText(this, "Please enter the name", Toast.LENGTH_SHORT).show()
        }
    }
    // show it
    alertDialog.show()
}


fun Context.showBanner(frameLayout: FrameLayout)
{
    val adView = AdView(this)
    val adaptiveAds = AdaptiveAds(this)
    adView.adUnitId = getString(R.string.bannerId)
    frameLayout.addView(adView)
    adView.adSize = adaptiveAds.adSize

    val adRequest: AdRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)
}

