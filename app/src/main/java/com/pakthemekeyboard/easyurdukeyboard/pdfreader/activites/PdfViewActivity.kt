package com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PdfModel
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase.entities.FavouritePdfEntity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase.repository.FavouriteRepository
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.Constants
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.showBanner
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.showToast
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.isAlreadyPurchased
import kotlinx.android.synthetic.main.activity_pdf_view.*
import kotlinx.android.synthetic.main.layout_adview.*
import kotlinx.android.synthetic.main.layout_pdf_view_header.*
import kotlinx.android.synthetic.main.lockdialoglayout.view.*
import java.io.File


class PdfViewActivity : BaseActivity(), OnLoadCompleteListener, OnErrorListener {


    var lastModified: String? = null
    var size: String? = null
    private var type: String? = null
    var path: String? = null
    var fileName: String? = null
    var pdfModel: PdfModel? = null
    var pass: String = ""
    lateinit var favouriteRepository: FavouriteRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)
        favouriteRepository = FavouriteRepository.getInstance(this)

        if(!isAlreadyPurchased()){
            showBanner(adView)
            constraintAd.visibility= View.VISIBLE
        }
        else
            constraintAd.visibility= View.GONE
        getIntentExtras()
        pdfFileName.text = fileName
        showPDF()
        listeners()
        toggelFavourited()
    }


    private fun listeners() {
        pdfViewFav.setOnClickListener {
            favPdf()
        }

        back.setOnClickListener {
            finish()
        }
    }

    private fun favPdf() {
        val isSelectedFavIcon = pdfViewFav.drawable.constantState ==
                ContextCompat.getDrawable(
                    this@PdfViewActivity,
                    R.drawable.ic_favorited
                )?.constantState
        if (isSelectedFavIcon) {
            showToast("Already favourite")
        } else {
            favouriteRepository.insertFav(
                FavouritePdfEntity(
                    null,
                    fileName!!,
                    path!!,
                    type!!,
                    size!!,
                    lastModified!!
                )
            )
            //saveArrayList(PdfModel(fileName!!, path!!, type!!))
            pdfViewFav.setImageResource(R.drawable.ic_favorited)
            showToast("Marked as favourite")
        }
    }

    private fun getIntentExtras() {
        intent.extras?.let {
            pdfModel = it.getParcelable(Constants.PDF_OBJ)
            pdfModel?.let { pdfModel ->
                fileName = pdfModel.pdfName
                path = pdfModel.pdfPath
                type = pdfModel.type
                size = pdfModel.size
                lastModified = pdfModel.lastModified
            }
        }
     pass= intent.getStringExtra("pass").toString()
    }

    private fun showPDF() {
        if(path.isNullOrBlank())
            Toast.makeText(this,"Fail to Load ",Toast.LENGTH_SHORT).show()
        else {
            val file = File(path!!)
            pdfView.useBestQuality(true)


            pdfView.fromUri(Uri.fromFile(file))
                .defaultPage(0)
                .password(pass)
                // .onPageChange(this)
                .enableAnnotationRendering(true)
                // .onLoad(this)
                .scrollHandle(DefaultScrollHandle(this))
                .spacing(10)
                // in dp
                // .onPageError(this)
                .onLoad(this)
                .onError(this)
                .load()




        }
    }

    private fun toggelFavourited() {
        favouriteRepository.retrieveAllFav().forEach {
            if (it.pdfName == fileName &&
                it.pdfType == type &&
                it.pdfPath == path
            ) {
                pdfViewFav.setImageResource(R.drawable.ic_favorited)
            }
        }
    }

    override fun loadComplete(nbPages: Int) {
    }

    override fun onError(t: Throwable?) {
        if(pass.isNotEmpty())
            Toast.makeText(this,"Wrong Password",Toast.LENGTH_SHORT).show()
    }


}
