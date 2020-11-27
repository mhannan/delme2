package com.pakthemekeyboard.easyurdukeyboard.pdfreader.mergermodule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.itextpdf.kernel.pdf.*
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites.BaseActivity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites.PdfViewActivity
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.loudreader.PdfLoudReader
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PdfModel
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PopupMenuModel
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.*
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.*
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.PaymentSingleton
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.isAlreadyPurchased
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.subscribe.subcriptionDialoge
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.MenuBaseAdapter
import kotlinx.android.synthetic.main.activity_show_files.*
import kotlinx.android.synthetic.main.dialog_edittext.view.*
import kotlinx.android.synthetic.main.layout_adview.*
import kotlinx.android.synthetic.main.layout_index_header.*
import kotlinx.android.synthetic.main.lockdialoglayout.*
import kotlinx.android.synthetic.main.unlockdialoglayout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ShowFiles : BaseActivity(), MergeAdapter.OnPdfItemClik, AlertDialogUtils.AlertDialogClick {

    var pdfreadercounter=0
    var lockcounter=0
    var unlockcounter=0
    var commanPath = "/storage/emulated/0"
    val pdfList = mutableListOf<PdfModel>()
    var type="no"
    var resume_falg=true
    open var mergelistAdapter: MergeAdapter? = null
    lateinit var dialogUtils: AlertDialogUtils
    lateinit var customPopupMenu: CustomPowerMenu<PopupMenuModel, MenuBaseAdapter<PopupMenuModel>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_files)
        if(!File("/storage/emulated/0/Cam Scanner").exists())
           File("/storage/emulated/0/Cam Scanner").mkdirs()

        type=intent.getStringExtra("Type").toString()
        if(intent.getStringExtra("Type").equals("Merged"))
            commanPath=Constants.Merge_FOLDER
        hideExtraViews()

        mergelistAdapter ?: MergeAdapter(this, pdfList,this).also { mergelistAdapter = it }
        customPopupMenu = PopupMenuUtils.initPopupMenu(this)
        dialogUtils = AlertDialogUtils(this, this)

    }

    private fun fetchingfiles() {
        pdfList.clear()
        progressBar.visibility=View.VISIBLE
        CoroutineScope(Dispatchers.Default).launch {
            val dir: File? = File(commanPath)
            getFile(dir!!)

            runOnUiThread {
                pdfList.sortBy {
                    it.pdfName
                }
                setupRecyclerView()
                progressBar.visibility=View.GONE
            }


        }
    }

    fun hideExtraViews() {
        h_subscribe.visibility=View.GONE
        drawerIcon.setImageResource(R.drawable.ic_back)
         drawerIcon.setOnClickListener { finish() }
        indexRateus.visibility = View.GONE

        if(type == "Merged"||type =="PdfReader"||type=="Lock"||type=="UnLock")
        {
           indexSearch.visibility=View.GONE

               if(type=="PdfReader")
                text.visibility=View.VISIBLE

            when (type) {
                "PdfReader" -> headerText.text = "PDF Reader"
                "Lock" -> headerText.text = "Lock PDF"
                "UnLock" -> headerText.text = "UnLock PDF"
                "Merged"->  headerText.text = "Merge PDF"
            }
        }
        else {
            headerText.text = "Merge PDF"
            indexSearch.visibility = View.VISIBLE
            indexSearch.setImageResource(R.drawable.ic_enabled_checkmark)
            indexSearch.setOnClickListener {
                val selectedpdffiles = ArrayList<PdfModel>()
                var counter=0
                mergelistAdapter?.pdfNameList?.forEach{
                    if(it.selected)
                    {
                        counter++
                        selectedpdffiles.add(it)
                        it.selected=false
                    }
                }
                if(counter>1) {
                    startActivitywithExtras<ReorderScreen> {
                        putParcelableArrayListExtra(Constants.PDF_OBJ, selectedpdffiles)
                    }
                    mergelistAdapter?.notifyDataSetChanged()
                    resume_falg=true
                }
                else
                    Toast.makeText(applicationContext,"Select More Then 1 File For Merge",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private  fun getFile(dir: File) {

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
        }catch (e:Exception){

        }

    }

    private fun setupPdfModel(file: File) {
        if (file.name.endsWith(".pdf")) {

            val type = file.name
                .substring(file.name.lastIndexOf(".") + 1)

            val pdfModel =
                PdfModel(
                    file.name,
                    file.absolutePath,
                    type,
                    sizeFormatter(file.length()),
                    false,
                    false,
                    file.lastModified().millisToDate().formatToDMY()
                )
            try {
                val pdfReader = com.itextpdf.text.pdf.PdfReader(file.absolutePath)
                 pdfReader.isEncrypted

               if(!this.type.equals("unlock",true))
                 pdfList.add(pdfModel)
            } catch (e: Exception) {
                if(e.message.equals("Bad user password",true)&&!this.type.equals("lock",true)) {
                    pdfModel.lockpdf = true
                    pdfList.add(pdfModel)
                }
            }


        }
    }
    fun setupRecyclerView() {
        recyclerviews.adapterAndManager(
            mergelistAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
        )
    }

    open fun handleCustomMenu(view: View, pdfModel: PdfModel, pos: Int) {

        customPopupMenu.setOnMenuItemClickListener { position, item ->

            when (position) {
                0 -> {
                    dialogUtils.showEditDialog("Rename", pdfModel, pos)
                    KeyboardUtils.showInputMethod(this)
                    Constants.reset_flag=true
                }
                1 -> {
                    File(pdfModel.pdfPath).delete()
                    pdfList.remove(pdfModel)
                    mergelistAdapter!!.notifyDataSetChanged()
                    Constants.reset_flag=true
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

    override fun onClick(pdfViewholder: MergeAdapter.PdfViewholder, pdfModel: PdfModel, id: Int, view: View) {
        when (id) {
            R.id.popupMenu -> {
                handleCustomMenu(view, pdfModel, pdfViewholder.adapterPosition)
            }
            R.id.pdficon -> {

                if(pdfModel.lockpdf){
                   val  passworddialog = Dialog(this@ShowFiles)
                    val windows = passworddialog.window
                    windows.let {
                        windows?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        windows?.setGravity(Gravity.CENTER)
                        passworddialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    }
                    passworddialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    passworddialog.setContentView(R.layout.unlockdialoglayout)
                    passworddialog.show()
                    passworddialog.unlock.text="Password"
                    passworddialog.unlock.setOnClickListener {
                        if(passworddialog.passwords.text.isEmpty())
                            Toast.makeText(this@ShowFiles,"Enter Password",Toast.LENGTH_SHORT).show()
                        else
                        {
                            passworddialog.unlock_pg.visibility=View.VISIBLE
                            passworddialog. pdfView.useBestQuality(true)

                            val file = File(pdfModel.pdfPath!!)
                            passworddialog. pdfView.fromUri(Uri.fromFile(file))
                                .defaultPage(0)
                                .password(passworddialog.passwords.text.toString())
                                .enableAnnotationRendering(true)
                                .scrollHandle(DefaultScrollHandle(this))
                                .spacing(10)
                                .onLoad{

                                    startActivitywithExtras<PdfViewActivity> {
                                        putExtra("pass", passworddialog.passwords.text.toString())
                                        putExtra(Constants.PDF_OBJ, pdfModel)
                                    }
                                    passworddialog.dismiss()

                                }
                                .onError{ Toast.makeText(this,"Wrong Password",Toast.LENGTH_SHORT).show()
                                    passworddialog.unlock_pg.visibility=View.GONE}
                                .load()

                        }
                    }
                    passworddialog.cancels.setOnClickListener { passworddialog.dismiss() }
                }
                else {
                    startActivitywithExtras<PdfViewActivity> {

                        putExtra("pass", "")
                        putExtra(Constants.PDF_OBJ, pdfModel)
                    }
                }
            }
            else -> {
                when {
                    type.equals("Simple",true) -> {
                        mergelistAdapter!!.apply {
                            if(pdfModel.lockpdf)
                                Toast.makeText(applicationContext,"File Is Protected",Toast.LENGTH_SHORT).show()
                            else {

                                pdfModel.selected = !pdfModel.selected
                                notifyDataSetChanged()
                            }
                            }
                    }
                    type.equals("Merged",true) -> {
                        startActivitywithExtras<PdfViewActivity> {
                            putExtra(Constants.PDF_OBJ, pdfModel)
                        }
                    }

                    type.equals("PdfReader",true) -> {
                        resume_falg=false
                       if(pdfreadercounter<1|| isAlreadyPurchased()) {

                            IncrementPdfReaderPref()
                            if(pdfModel.lockpdf){Toast.makeText(applicationContext,"File Is Protected",Toast.LENGTH_SHORT).show()}

                          else {
                                startActivitywithExtras<PdfLoudReader> {
                                    putExtra("path", pdfModel.pdfPath)
                                }
                            }
                        }
                        else {
                            ShowSubscribeDialog()
                        }
                    }
                    type.equals("lock",true) -> {
                        resume_falg=false


                        if(pdfModel.lockpdf){Toast.makeText(applicationContext,"File Already Protected",Toast.LENGTH_SHORT).show()}
                           else {
                            if (lockcounter<1 || isAlreadyPurchased())
                                ShowLockDialog(pdfModel.pdfPath,pdfModel.pdfName)
                            else
                                ShowSubscribeDialog()
                        }
                    }
                    else -> {
                        resume_falg=false
                        if(pdfModel.lockpdf){
                            if (unlockcounter<1 || isAlreadyPurchased())
                              ShowUnLockDialog(pdfModel.pdfPath,pdfModel.pdfName)
                            else
                                ShowSubscribeDialog()
                        }
                        else
                            Toast.makeText(applicationContext,"File Not Protected",Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }

    private fun ShowUnLockDialog(pdfPath: String, pdfName: String) {
        val dialog = Dialog(this)
        val windows = dialog.window
        windows.let {
            windows?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            windows?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.unlockdialoglayout)
        dialog.show()
        dialog.unlock.setOnClickListener {
            if(dialog.passwords.text.isEmpty())
                Toast.makeText(this,"Enter Password",Toast.LENGTH_SHORT).show()
            else
            {
                dialog.unlock_pg.visibility=View.VISIBLE
                try {
                    Constants.reset_flag=true;
                    CoroutineScope(Dispatchers.Default).launch {
                      val response=decryptPdf(pdfPath, "/storage/emulated/0/Cam Scanner/" + pdfName.split(".")[0]+"_decrypt" + ".pdf", dialog.passwords.text.toString())
                        runOnUiThread {
                            dialog.unlock_pg.visibility=View.GONE
                            if(response.contains("fine",true)) {
                                IncrementunLockPref()
                                Toast.makeText(
                                    this@ShowFiles,
                                    "File Decrypt Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()
                                startActivitywithExtras<PdfViewActivity> {


                                    putExtra("pass", "")
                                    Log.d("hellomy",response.split("@")[1])
                                    putExtra(Constants.PDF_OBJ, PdfModel(
                                        pdfName.split(".")[0]+"_decrypt",
                                        response.split("@")[1],
                                        "",
                                        "",
                                        false,
                                        false,
                                        ""
                                    ))
                                }
                                fetchingfiles()
                            }
                            else {
                                Toast.makeText(
                                    this@ShowFiles,
                                    "Error->$response",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this,"Error ->"+e.message,Toast.LENGTH_SHORT).show()
                    dialog.unlock_pg.visibility=View.GONE
                }
            }
        }
        dialog.cancels.setOnClickListener { dialog.dismiss() }

    }

    private fun ShowLockDialog(pdfPath: String, pdfName: String) {

        val dialog = Dialog(this)
        val windows = dialog.window
        windows.let {
            windows?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            windows?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.lockdialoglayout)
        dialog.show()
        dialog.lock.setOnClickListener {

            if(dialog.password.text.isEmpty())
               Toast.makeText(this,"Enter Password",Toast.LENGTH_SHORT).show()
            else
            {
                 disableInterface(dialog)
                 dialog.lock_pg.visibility=View.VISIBLE
                try {
                    CoroutineScope(Dispatchers.Default).launch {
                      val response=  encryptPdf(
                            pdfPath,
                            "/storage/emulated/0/Cam Scanner/" + pdfName.split(".")[0]+"_encrypt" + ".pdf",
                            dialog.password.text.toString()
                        )
                        runOnUiThread {
                            ableInterface(dialog)
                            dialog.lock_pg.visibility=View.GONE
                            if(response=="fine") {
                                IncrementLockPref()
                                Constants.reset_flag=true;
                                Toast.makeText(
                                    this@ShowFiles,
                                    "File Protected Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                dialog.dismiss()
                                fetchingfiles()
                            }
                            else {
                                Toast.makeText(
                                    this@ShowFiles,
                                    "Error ->$response",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this,"Error ->"+e.message,Toast.LENGTH_SHORT).show()
                    dialog.lock_pg.visibility=View.GONE
                }
            }
        }
        dialog.cancel.setOnClickListener { dialog.dismiss() }


    }

    private fun disableInterface(dialog: Dialog) {
        dialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun ableInterface(dialog: Dialog) {
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

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
                    if(isRenamed){
                        mergelistAdapter!!.apply {
                            listForSearch[pos].pdfName = "$renameDialogText.pdf"
                            listForSearch[pos].pdfPath = pdfPath
                            notifyDataSetChanged()
                        }
                    }

            }
        }
    }

    private fun encryptPdf(pdfPath: String, des: String,pass:String):String {
        try{
        val pdfDoc = PdfDocument(
            PdfReader(pdfPath),
            PdfWriter(
                des, WriterProperties().setStandardEncryption(
                    pass.toByteArray(),
                    pass.toByteArray(),
                    EncryptionConstants.ALLOW_PRINTING,
                    EncryptionConstants.ENCRYPTION_AES_128 or EncryptionConstants.DO_NOT_ENCRYPT_METADATA
                )
            )
        )
        pdfDoc.close()
            return "fine"
        }catch (e: Exception){
            return e.message!!
        }
       /* var pdfDoc: PdfDocument? = null
        try {
            pdfDoc = PdfDocument(
                PdfReader(pdfPath),
                PdfWriter(
                    des, WriterProperties().setStandardEncryption(
                        pass.toByteArray(),
                        pass.toByteArray(),
                        EncryptionConstants.ALLOW_PRINTING,
                        EncryptionConstants.ENCRYPTION_AES_128 or EncryptionConstants.DO_NOT_ENCRYPT_METADATA
                    )
                )
            )
            return "fine"
        } catch (e: Exception) {
            e.printStackTrace()
            return e.message!!
        }
        pdfDoc!!.close()*/
    }
    private fun decryptPdf(pdfPath: String, des: String, pass: String):String {
        return try {
            PdfDocument(
                PdfReader(pdfPath, ReaderProperties().setPassword(pass.toByteArray())),
                PdfWriter(des)
            ).use { document ->
                val userPasswordBytes = document.reader.computeUserPassword()
                val userPassword =
                    userPasswordBytes?.let { String(it) }
                println(userPassword)
            }
            "fine@$des"
        } catch (e: Exception) {
            e.printStackTrace()
            e.message!!
        }
    }

    override fun onResume() {
        super.onResume()
        ReadPdfReaderPref()
        if(resume_falg)
            fetchingfiles()
        resume_falg=false
    }

    private fun IncrementPdfReaderPref() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("CounterPref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        pdfreadercounter++
        editor.putInt("pdfreadercounter",pdfreadercounter)
        editor.apply()
        editor.commit()
    }
    private fun IncrementLockPref() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("CounterPref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        lockcounter++
        editor.putInt("lockcounter",lockcounter)
        editor.apply()
        editor.commit()
    }
    private fun IncrementunLockPref() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("CounterPref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        unlockcounter++
        editor.putInt("unlockcounter",unlockcounter)
        editor.apply()
        editor.commit()
    }
    private fun ReadPdfReaderPref() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("CounterPref", Context.MODE_PRIVATE)
        pdfreadercounter=sharedPreferences.getInt("pdfreadercounter",0)
        lockcounter=sharedPreferences.getInt("lockcounter",0)
        unlockcounter=sharedPreferences.getInt("unlockcounter",0)
    }



    private fun ShowSubscribeDialog() {

        if (!isAlreadyPurchased()) {
            subcriptionDialoge(this,this)
        }else {
            Toast.makeText(applicationContext, "Already Purchased", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (PaymentSingleton.getInstance().handleActivityResult(requestCode, resultCode, data)) {
            if (isAlreadyPurchased()) {
                Toast.makeText(this,"successFullyPurchased",Toast.LENGTH_SHORT).show()

            }
        }
    }



}