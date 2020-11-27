package com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.Constants
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.ads.SingletonAds
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.showBanner
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.isAlreadyPurchased
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.layout_adview.*
import kotlinx.android.synthetic.main.layout_index_header.*
import java.io.File

class ScannedFilesActivity : PdfListClass() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //super.onCreate(savedInstanceState)
        val bundle = Bundle()
        bundle.putString("layout", "ScannedFilesActivity")
        bundle.putInt("layoutID", R.layout.activity_index)
        setContentView(R.layout.activity_index)
        super.onCreate(bundle)



        if(!isAlreadyPurchased()){
            showBanner(adView)
            constraintAd.visibility= View.VISIBLE
            if (SingletonAds.instance.isLoaded&& ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
                SingletonAds.instance.show()
        }
        else
            constraintAd.visibility= View.GONE
        hideExtraViews()
        fetchingfiles()
        setupRecyclerView()
    }


    fun hideExtraViews() {
        drawerIcon.setImageResource(R.drawable.ic_back)

        headerText.text = "PDF files"
        actionLayout.visibility = View.GONE
        indexRateus.visibility = View.GONE
        openScanBtn.visibility=View.GONE
        merge.visibility=View.GONE
        dots.visibility=View.GONE
        pdftxt.visibility = View.GONE
        scantxt.visibility=View.GONE
        mergetxt.visibility=View.GONE
        moretxt.visibility=View.GONE
    }

    override fun getFile(dir: File) {
        pdfList.clear()
        val file = File(Constants.getDir(this))
        val pdfFiles: Array<File> = file.listFiles()!!


        pdfFiles.apply {
            sortBy {
                it.lastModified()
            }
            reverse()
            forEach {
                setupPdfModel(it)
            }
        }
    }

    override fun listeners() {
        super.listeners()
        drawerIcon.setOnClickListener { finish() }
    }

}