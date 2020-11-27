package com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites.index

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites.FavouriteActivity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites.PdfListClass
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites.PdfViewActivity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites.PdfScanActivity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.adapters.PdfAdapter
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.mergermodule.ShowFiles
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PdfModel
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.Constants
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.PathConverter
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.*
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.PaymentSingleton
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.isAlreadyPurchased
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.subcriptionDialoge
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.indexbottommenu.*
import kotlinx.android.synthetic.main.indexbottommenu.unlock
import kotlinx.android.synthetic.main.layout_adview.*
import kotlinx.android.synthetic.main.layout_index_header.*
import kotlinx.android.synthetic.main.layout_rate_dialog.view.*
import kotlinx.android.synthetic.main.unlockdialoglayout.*
import java.lang.Exception


class IndexActivity : PdfListClass() {


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_drawer)

        val bundle = Bundle()
        bundle.putString("layout", "IndexActivity")
        bundle.putInt("layoutID", R.layout.activity_drawer)
        super.onCreate(bundle)
        fetchingfiles()
        if(isAlreadyPurchased())
            constraintAd.visibility=View.GONE
        else {
            showBanner(adView)
            showSubcriptionDialog()
            constraintAd.visibility=View.VISIBLE
        }

        drawerClickListeners()
    }

    override fun onResume() {
       if(Constants.reset_flag){
           fetchingfiles()
           Constants.reset_flag=false
       }
        if (isAlreadyPurchased()) {
            constraintAd.visibility=View.GONE
        }
        super.onResume()
    }


    override fun listeners() {
        selectericon.visibility=View.GONE
        dots.setOnClickListener {
            showDialog()
            clearSearchView()
            selectericonfirst.visibility=View.GONE
            selectericon.visibility=View.VISIBLE
        }
         h_subscribe.setOnClickListener {

             showSubcriptionDialog()

         }

        openScanBtn.setOnClickListener {
            startNewActivity<PdfScanActivity>()
            clearSearchView()
        }

        merge.setOnClickListener {
            startActivitywithExtras<ShowFiles> {
                putExtra("Type", "Simple")
            }
            clearSearchView()
        }
        indexRateus.setOnClickListener {
            openAppInPlayStore()
        }


        drawerIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        super.listeners()
    }

    private fun showSubcriptionDialog() {
        if (!isAlreadyPurchased())
            subcriptionDialoge(this,this)
        else
            Toast.makeText(applicationContext, "Already Purchased", Toast.LENGTH_SHORT).show()
    }

    private fun showDialog() {
        val dialog = Dialog(this,R.style.CustomDialog)
        val windows = dialog.window
        windows.let {
            windows?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            windows?.setGravity(Gravity.BOTTOM)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.indexbottommenu)
        dialog.show()

        dialog.pdflouder.setOnClickListener {
            startActivitywithExtras<ShowFiles> {
                putExtra("Type", "PdfReader")
                dialog.dismiss()
            }
        }
        dialog.favourite.setOnClickListener {
            startNewActivity<FavouriteActivity>()
            dialog.dismiss()
        }
        dialog.lockpdf.setOnClickListener { startActivitywithExtras<ShowFiles> {
            putExtra("Type", "Lock")
            dialog.dismiss()
        } }
        dialog.unlock.setOnClickListener { startActivitywithExtras<ShowFiles> {
            putExtra("Type", "UnLock")
            dialog.dismiss()
        } }

        dialog.setOnDismissListener { selectericon.visibility=View.GONE
        selectericonfirst.visibility=View.VISIBLE}
    }

    private fun drawerClickListeners() {
        drawerClose.setOnClickListener {
            closeDrawer()
        }

        home.setOnClickListener {
            closeDrawer()
        }

        browseFolder.setOnClickListener {
            openChooser()
            closeDrawer()
        }
        shareApp.setOnClickListener {
            shareApp()
            closeDrawer()
        }
        rateApp.setOnClickListener {
            showRateUsDialog()
            closeDrawer()
        }
    }

    private fun closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PDF_REQUEST_CODE) {

            data?.let {

                val results = it.data

                results?.let { uri ->
                    try {
                        if(uri!=null){
                            startActivitywithExtras<PdfViewActivity> {
                                putExtra("pass","")
                                putExtra(Constants.PDF_OBJ,
                                    PdfModel(PathConverter.getFilePath(this@IndexActivity,uri),PathConverter.getRealPath(this@IndexActivity,uri),"pdf","",false,false,"")

                                )
                            }}
                    }catch (e:Exception){
                     Toast.makeText(this@IndexActivity,"File Does Not Exist ",Toast.LENGTH_SHORT).show()

                    }


                }


            }
        }
        if (PaymentSingleton.getInstance().handleActivityResult(requestCode, resultCode, data))
        {
            if (isAlreadyPurchased()) {
                   Toast.makeText(this,"successFullyPurchased",Toast.LENGTH_SHORT).show()
                    constraintAd.visibility=View.GONE
            }
        }
    }

    /// adapter click override for ads
    override fun onClick(pdfViewholder: PdfAdapter.PdfViewholder, pdfModel: PdfModel, id: Int, view: View) {
        super.onClick(pdfViewholder, pdfModel, id, view)

    }

    override fun onBackPressed() {
        if (isSearchClick) {
            clearSearchView()
        } else {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START)
            else
                showRateUsDialog()

        }
    }


}
