package com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites.index.IndexActivity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.adapters.PdfAdapter
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PdfModel
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.AlertDialogUtils
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.KeyboardUtils
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.showBanner
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.startActivitywithExtras
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.startNewActivity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.isAlreadyPurchased
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.dialog_edittext.view.*
import kotlinx.android.synthetic.main.layout_adview.*
import kotlinx.android.synthetic.main.layout_index_header.*

class FavouriteActivity : PdfListClass(), AlertDialogUtils.AlertDialogClick {

    lateinit var favPdfList: MutableList<PdfModel>
    lateinit var alertDialogUtils: AlertDialogUtils
    //var pdfAdapter: PdfAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
        val bundle = Bundle()
        bundle.putString("layout", "FavouriteActivity")
        bundle.putInt("layoutID", R.layout.activity_index)
        setContentView(R.layout.activity_index)

        super.onCreate(bundle)
        if(!isAlreadyPurchased()){
        showInterstitial()
        showBanner(adView)
            constraintAd.visibility=View.VISIBLE
        }
        else {
            constraintAd.visibility=View.GONE
        }
        initilization()
        retrieveAllFavs()
        setupRecyclerView()

    }


    private fun initilization() {
        alertDialogUtils = AlertDialogUtils(this, this)
        hideExtraViews()

        headerText.text = "Favourites"
        favPdfList = mutableListOf()
        pdfListAdapter = PdfAdapter(this, favPdfList, this).also { pdfListAdapter = it }
    }

    fun hideExtraViews() {
        drawerIcon.setImageResource(R.drawable.ic_back)
        indexRateus.visibility = View.GONE
        actionLayout.visibility= View.GONE
        dots.visibility= View.GONE
        pdfListIcon.visibility= View.GONE
        openScanBtn.visibility= View.GONE
        merge.visibility= View.GONE
    }



    override fun listeners() {
        drawerIcon.setOnClickListener { finish() }

        pdfListIcon.setOnClickListener {
            startNewActivity<IndexActivity>()
        }

        openScanBtn.setOnClickListener {
            startNewActivity<PdfScanActivity>()
        }
        super.listeners()
    }

    private fun retrieveAllFavs() {
        // list from parent class
        favEntityList.forEach {
            favPdfList.add(
                PdfModel(
                    it.pdfName,
                    it.pdfPath,
                    it.pdfType,
                    it.pdfSize,
                    false,false, it.pdfLastModefied
                )
            )
        }
    }

//    private fun setupRecyclerView() {
//        rvPdfList.adapterAndManager(
//            pdfListAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
//        )
//    }


    // method from parent class (pdf class)
    override fun handleCustomMenu(view: View, pdfModel: PdfModel, pos: Int) {
        customPopupMenu.setOnMenuItemClickListener { position, item ->

            when (position) {
                0 -> {  // rename
                    alertDialogUtils.showEditDialog("Rename", pdfModel, pos)
                    KeyboardUtils.showInputMethod(this)
                }
                1 -> {  // delete
                    favouriteRepository.deleteFav(favEntityList[pos])
                    favPdfList.removeAt(pos)
                    pdfListAdapter?.notifyItemRemoved(pos)
                }
                2 -> {  //share
                    shareFile(pdfModel.pdfPath)
                }
                3 -> {  // details
                    alertDialogUtils.showDetailDialog("Details", pdfModel)
                }
            }
            customPopupMenu.dismiss()
        }
        customPopupMenu.showAsAnchorRightTop(view)
    }

    override fun onPositiveCLick(view: View, pdfModel: PdfModel, pos: Int) {
        val renameDialogText = view.dialogeditText.text.toString() + ".pdf"
        favouriteRepository.updateFav(favEntityList[pos].id!!.toLong(), renameDialogText)
        favPdfList[pos].pdfName = renameDialogText
        pdfListAdapter!!.notifyItemChanged(pos)
    }

    /// adapter click override for ads
    override fun onClick(
        pdfViewholder: PdfAdapter.PdfViewholder,
        pdfModel: PdfModel,
        id: Int,
        view: View
    ) {
        super.onClick(pdfViewholder, pdfModel, id, view)
        showIndexAd(FAVOURITE_AD_COUNT++)
    }

    override fun onBackPressed() {
        if (isSearchClick) {
            clearSearchView()
        } else {

            startActivitywithExtras<IndexActivity> {
                flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
    }
}
