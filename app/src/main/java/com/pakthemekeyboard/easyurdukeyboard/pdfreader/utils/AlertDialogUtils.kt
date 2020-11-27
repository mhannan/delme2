package com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils

import android.app.Activity
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PdfModel
import kotlinx.android.synthetic.main.dialog_edittext.view.*
import kotlinx.android.synthetic.main.dialog_file_details.view.*


class AlertDialogUtils(
    val activity: Activity,
    val alertDialogClick: AlertDialogClick
) {

//    companion object {
//        private var instance: AlertDialogUtils? = null
//
//        fun getDialogInstance(activity: Activity, alertDialogClick: AlertDialogClick) =
//            instance ?: AlertDialogUtils(activity, alertDialogClick).also { instance = it }
//    }

    fun showEditDialog(title: String, pdfModel: PdfModel, pos: Int) {
        val d: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.MyAlertDialogStyle)
        val dialogView = activity.layoutInflater.inflate(R.layout.dialog_edittext, null)
        d.setTitle(title)
        // d.setMessage(message)
        d.setView(dialogView)

        // excluding the extension
        dialogView.dialogeditText.setText(
            pdfModel.pdfName.substring(
                0,
                pdfModel.pdfName.lastIndexOf(".")
            )
        )

        d.setPositiveButton(
            "Ok"
        ) { _, _ ->
            alertDialogClick.onPositiveCLick(dialogView, pdfModel, pos)
        }
        d.setNegativeButton(
            "cancel"
        ) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }


        val alertDialog: AlertDialog = d.create()

        val dialogTitle = TextView(activity)
        dialogTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        d.setCustomTitle(dialogTitle)
        alertDialog.show()
    }

    fun showDetailDialog(
        title: String,
        pdfModel: PdfModel
    ) {
        val d: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.MyAlertDialogStyle)
        val dialogView = activity.layoutInflater.inflate(R.layout.dialog_file_details, null)
        d.setTitle(title)
        // d.setMessage(message)
        d.setView(dialogView)
        dialogView.detailFileName.text = "File name : ${pdfModel.pdfName}"
        dialogView.detailFilePath.text = "Path : ${pdfModel.pdfPath}"
        dialogView.detailLastModified.text = "Last modified : ${pdfModel.lastModified}"
        dialogView.detailSize.text = "Size : ${pdfModel.size}"

        d.setNegativeButton(
            "Ok"
        ) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }


        val alertDialog: AlertDialog = d.create()
        alertDialog.show()
    }


    interface AlertDialogClick {
        fun onPositiveCLick(view: View, pdfModel: PdfModel, pos: Int)
    }
}