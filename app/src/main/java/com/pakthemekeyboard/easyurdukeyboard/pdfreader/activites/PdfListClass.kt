package com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.Uri

import android.os.Bundle
import android.util.Log

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.adapters.PdfAdapter
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PdfModel
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PopupMenuModel
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase.entities.FavouritePdfEntity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.roomdatabase.repository.FavouriteRepository
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.AlertDialogUtils
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.Constants
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.KeyboardUtils
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.PopupMenuUtils
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.*
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.MenuBaseAdapter
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.dialog_edittext.view.*
import kotlinx.android.synthetic.main.layout_index_header.*
import kotlinx.android.synthetic.main.unlockdialoglayout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


@SuppressLint("Registered")
open class PdfListClass : BaseActivity(), PdfAdapter.OnPdfItemClik, AlertDialogUtils.AlertDialogClick, SearchView.OnQueryTextListener {


    lateinit var dialogUtils: AlertDialogUtils
    lateinit var favouriteRepository: FavouriteRepository
    lateinit var favEntityList: MutableList<FavouritePdfEntity>
    lateinit var customPopupMenu: CustomPowerMenu<PopupMenuModel, MenuBaseAdapter<PopupMenuModel>>

    var isSearchClick: Boolean = false
    val pdfList = mutableListOf<PdfModel>()
    open var pdfListAdapter: PdfAdapter? = null
    val commanPath = "/storage/emulated/0"

    private fun initilization() {
        favouriteRepository = FavouriteRepository.getInstance(this)
        favEntityList = favouriteRepository.retrieveAllFav()
        dialogUtils = AlertDialogUtils(this, this)


        pdfListAdapter ?: PdfAdapter(this, pdfList, this).also { pdfListAdapter = it }
        customPopupMenu = PopupMenuUtils.initPopupMenu(this)

    }

    fun setupRecyclerView() {

        rvPdfList.adapterAndManager(
            pdfListAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
        )
    }

    open fun getFile(dir: File) {

        try {
            val listFile: Array<File> = dir.listFiles()!!
            if (listFile.isNotEmpty()) {

                listFile.forEach {
                    if (it.isDirectory) {
                        getFile(it)
                    } else {
                        setupPdfModel(it)
                    }
                }
            }
        }catch (e: Exception){

        }

    }

    fun setupPdfModel(file: File) {
        if (file.name.endsWith(".pdf")) {
            val type = file.name
                .substring(file.name.lastIndexOf(".") + 1)

            val pdfModel =
                PdfModel(
                    file.name,
                    file.absolutePath,
                    type,
                    sizeFormatter(file.length()),
                    false, false,
                    file.lastModified().millisToDate().formatToDMY()
                )
            try {
                val pdfReader = com.itextpdf.text.pdf.PdfReader(file.absolutePath)
                    pdfReader.isEncrypted
                    pdfList.add(pdfModel)


            } catch (e: Exception) {
                if(e.message.equals("Bad user password",true)) {
                    pdfModel.lockpdf = true
                    pdfList.add(pdfModel)
                }
                Log.d("hel11",pdfModel.pdfName+"=>"+e.message)
            }

        }
    }

    fun showPopUpMenu(view: View): PopupMenu {

        val wrapper: Context = ContextThemeWrapper(this, R.style.PopupMenu)

        val popup = PopupMenu(wrapper, view)
        popup.inflate(R.menu.pdf_popup_menu)

        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popup)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
            val width = mPopup.javaClass
                .getDeclaredMethod("getWidth")
                .invoke(mPopup) as Int

            mPopup.javaClass
                .getDeclaredMethod("setHorizontalOffset", Integer::class.java)
                .invoke(mPopup, width)

            mPopup.javaClass
                .getDeclaredMethod("show")
                .invoke(mPopup)

        } catch (e: Exception) {
        } finally {
            popup.show()
        }

        return popup
    }

    open fun handleCustomMenu(view: View, pdfModel: PdfModel, pos: Int) {

        customPopupMenu.setOnMenuItemClickListener { position, item ->

            when (position) {
                0 -> {
                    dialogUtils.showEditDialog("Rename", pdfModel, pos)
                    KeyboardUtils.showInputMethod(this)
                }
                1 -> {
                    File(pdfModel.pdfPath).delete()
                    pdfList.remove(pdfModel)
                    pdfListAdapter!!.notifyDataSetChanged()
                }
                2 -> {
                    shareFile(pdfModel.pdfPath)
                }
                3 -> {
                    dialogUtils.showDetailDialog("Details", pdfModel)
                }
            }
            customPopupMenu.dismiss()
        }
        customPopupMenu.showAsAnchorRightTop(view)

    }

    /* open fun handllePopUpMenu(view: View, pdfModel: PdfModel, pos: Int) {
         showPopUpMenu(view).setOnMenuItemClickListener {
             when (it.itemId) {
                 R.id.popupRename -> {

                     true
                 }
                 R.id.popupDelete -> {


                     true
                 }
                 R.id.popupShare -> {

                     true
                 }
                 R.id.popupDetails -> {

                     true
                 }
                 else -> false
             }

         }
     }*/

    open fun listeners() {
        indexSearch.setOnClickListener {
            toggleSearch()
            searchView.requestFocus()
            KeyboardUtils.showInputMethod(this)
        }
        searchView.setOnQueryTextListener(this)
        searchBack.setOnClickListener { clearSearchView() }

    }

    fun toggleSearch() {
        isSearchClick = true
        layoutIndexHeader.visibility = View.GONE
        searchLayout.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutId = savedInstanceState?.getString("layout")

        val layout = savedInstanceState?.getInt("layoutID")
        setContentView(layout!!)
        listeners()
        when (layoutId) {
            "IndexActivity" -> {
                //  showToast("index")
            }
            "FavouriteActivity" -> {

            }
        }

        initilization()
    }

    fun fetchingfiles() {
        rvPdfList.visibility=View.GONE
        pdfList.clear()
        progressBar.visibility=View.VISIBLE
        CoroutineScope(Dispatchers.Default).launch {
            val dir: File? = File(commanPath)
            getFile(dir!!)

            runOnUiThread {
                rvPdfList.visibility=View.VISIBLE
                pdfList.sortBy {
                    it.pdfName
                }
                setupRecyclerView()
                progressBar.visibility=View.GONE
            }


        }
    }



    override fun onQueryTextChange(newText: String?): Boolean {
        performSearch(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        performSearch(query)
        return true
    }

    private fun performSearch(s: String?) {
        pdfListAdapter!!.search(s) {
         //   showToast("No results found")
        }
    }

    /// adapter item click
    override fun onClick(pdfViewholder: PdfAdapter.PdfViewholder, pdfModel: PdfModel, id: Int, view: View) {
        if (id == R.id.popupMenu) {
            handleCustomMenu(view, pdfModel, pdfViewholder.adapterPosition)

        } else {
            if(pdfModel.lockpdf){
                val dialog = Dialog(this)
                val windows = dialog.window
                windows.let {
                    windows?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    windows?.setGravity(Gravity.CENTER)
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                }
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.unlockdialoglayout)
                dialog.show()
                dialog.unlock.text="Password"
                dialog.unlock.setOnClickListener {
                    if(dialog.passwords.text.isEmpty())
                        Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
                    else
                    {
                        dialog.unlock_pg.visibility=View.VISIBLE
                        dialog. pdfView.useBestQuality(true)

                        val file = File(pdfModel.pdfPath!!)
                        dialog. pdfView.fromUri(Uri.fromFile(file))
                            .defaultPage(0)
                            .password(dialog.passwords.text.toString())
                            .enableAnnotationRendering(true)
                            .scrollHandle(DefaultScrollHandle(this))
                            .spacing(10)
                            .onLoad{

                                startActivitywithExtras<PdfViewActivity> {
                                    putExtra("pass", dialog.passwords.text.toString())
                                    putExtra(Constants.PDF_OBJ, pdfModel)
                                }
                                dialog.dismiss()
                                showIndexAd(INDEX_AD_COUNT++)

                            }
                            .onError{ Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show()
                                dialog.unlock_pg.visibility=View.GONE}
                            .load()
                    }
                }
                dialog.cancels.setOnClickListener { dialog.dismiss() }
            }
            else {

                startActivitywithExtras<PdfViewActivity> {

                    putExtra("pass", "")
                    putExtra(Constants.PDF_OBJ, pdfModel)
                }
                showIndexAd(INDEX_AD_COUNT++)
            }

        }
    }


    fun clearSearchView() {
        isSearchClick = false
        layoutIndexHeader.visibility = View.VISIBLE
        searchLayout.visibility = View.GONE
        searchView.setQuery("", false);
        searchView.clearFocus()
    }


    override fun onPositiveCLick(view: View, pdfModel: PdfModel, pos: Int) {
        val renameDialogText = view.dialogeditText.text.toString()
        val pdfPath = pdfModel.pdfPath.substring(
            0,
            pdfModel.pdfPath.lastIndexOf('/') + 1
        ) + renameDialogText + ".pdf"

        if (renameDialogText.isEmpty()) {
            showToast("Please enter file name")
        } else {

            val renamedFile = File(pdfPath.trim())

            if (renamedFile.exists()) {
                showToast("file name already exist")
            } else {
                val oldFile = File(pdfModel.pdfPath) //create file with old name
                val isRenamed: Boolean = oldFile.renameTo(renamedFile)

                if (isRenamed) {
                    // executing the parallel jobs in backgroud thread
                    val job = CoroutineScope(IO).launch {
                        withContext(Dispatchers.Default) {
                            favouriteRepository.updateFavPath(
                                pdfList[pos].pdfName,
                                pdfPath
                            )
                        }
                        withContext(Dispatchers.Default) {
                            favouriteRepository.updateFavByName(
                                pdfList[pos].pdfName,
                                "$renameDialogText.pdf"
                            )
                        }


                    }

                    job.invokeOnCompletion {
                        CoroutineScope(Main).launch {

                            pdfListAdapter!!.apply {
                                listForSearch[pos].pdfName = "$renameDialogText.pdf"
                                listForSearch[pos].pdfPath = pdfPath
                                notifyDataSetChanged()
                            }

                        }
                    }
                }
            }
        }
    }

}