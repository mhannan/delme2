package com.pakthemekeyboard.easyurdukeyboard.pdfreader.mergermodule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfReader
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PdfModel
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.Constants
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.startActivitywithExtras
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.PaymentSingleton
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.isAlreadyPurchased
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.subcriptionDialoge
import kotlinx.android.synthetic.main.activity_reorder_screen.*
import kotlinx.android.synthetic.main.layout_index_header.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class ReorderScreen : AppCompatActivity() {
    var IsPDFMerged=false
    var counter=0

   private  var selectedpdffiles = ArrayList<Parcelable>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reorder_screen)
         ReadMergePref();
        val bundle = intent.extras
         selectedpdffiles = bundle!!.getParcelableArrayList<Parcelable>(Constants.PDF_OBJ)!!

        recyclerviews.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerviews.isNestedScrollingEnabled = false
        recyclerviews.setHasFixedSize(true)
        val adapter = ReorderAdapter(this, selectedpdffiles as MutableList<PdfModel>)
        val callback: ItemTouchHelper.Callback = ItemMoveCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerviews)
        recyclerviews.adapter = adapter
        merge.setOnClickListener {
            if(counter<1||isAlreadyPurchased()) {

                progressBar.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Default).launch {
                    mergePdfFiles(selectedpdffiles as MutableList<PdfModel>)
                }
            }
            else {
             ShowSubscribeDialog()
            }
        }
        hideExtraViews()
    }





    private fun hideExtraViews() {
        h_subscribe.visibility=View.GONE
        headerText.text = "Reorder PDF"
        drawerIcon.setOnClickListener { finish() }
        drawerIcon.setImageResource(R.drawable.ic_back)
        indexRateus.visibility = View.GONE
        indexSearch.visibility = View.GONE

    }
    private fun mergePdfFiles(selectedpdffiles: MutableList<PdfModel>) {
        try {
            var pdfreader: PdfReader
            PdfReader.unethicalreading = true
            val document = Document()
            val file = File(Constants.Merge_FOLDER)
            if (!file.exists()) file.mkdirs()
            var fileName="Merge_" + System.currentTimeMillis()
            val copy = PdfCopy(
                document,
                FileOutputStream(File(file, "$fileName.pdf"))
            )
            document.open()
            var numOfPages: Int
            for (pdfPath in selectedpdffiles) {
                pdfreader = PdfReader(pdfPath.pdfPath)
                numOfPages = pdfreader.numberOfPages
                for (page in 1..numOfPages)
                copy.addPage(copy.getImportedPage(pdfreader, page))
            }
             IsPDFMerged = true
            document.close()
            runOnUiThread {
                progressBar.visibility=View.GONE
                IncrementMergePref()
                startActivitywithExtras<ShowFiles> {
                    putExtra("Type", "Merged")
                }
                finish()
                Constants.reset_flag=true;
            }
        } catch (e: Exception) {
            IsPDFMerged = false
            e.printStackTrace()
            runOnUiThread {
                progressBar.visibility = View.GONE
                Toast.makeText(applicationContext,"Error->"+e.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun IncrementMergePref() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("CounterPref", Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
        counter++
        editor.putInt("mergecounter",counter)
        editor.apply()
        editor.commit()

    }
    private fun ReadMergePref() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("CounterPref", Context.MODE_PRIVATE)
        counter=sharedPreferences.getInt("mergecounter",0)

    }
    private fun ShowSubscribeDialog() {

        if (!isAlreadyPurchased()) {
            subcriptionDialoge(this,this);
        }else {
            Toast.makeText(applicationContext, "Already Purchased", Toast.LENGTH_SHORT).show();
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (PaymentSingleton.getInstance().handleActivityResult(requestCode, resultCode, data)) {
            if (isAlreadyPurchased()) {
                Toast.makeText(this,"SuccessFullyPurchased",Toast.LENGTH_SHORT).show()

            }
        }
    }
}