package com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PdfModel
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.FileUtils

fun Context.getFileName(uri: Uri): PdfModel? {
    return null
}

//getCursorContent(uri)
//    when (uri.scheme) {
//        ContentResolver.SCHEME_CONTENT -> getCursorContent(uri)
//        else -> null
//    }
fun Activity.getCursorContent(uri: Uri): PdfModel? {
    val projection = arrayOf(
        OpenableColumns.DISPLAY_NAME,
        OpenableColumns.SIZE,
        DocumentsContract.Document.COLUMN_LAST_MODIFIED
    )

    var pdfModel: PdfModel? = null
    try {
    contentResolver.query(uri, projection, null, null, null)?.let { cursor ->
        runOnUiThread {
            cursor.apply {
                if (moveToFirst()) {
                    val name = getString(0)
                    val size = sizeFormatter(getString(1).toLong())
                    val lastModified = getString(2).toLong().millisToDate().formatToDMY()
                    val path = FileUtils.getRealPath(this@getCursorContent, uri)
                    pdfModel = PdfModel(name, path, "pdf", size,false,false, lastModified)

                } else null
            }.also {
                it.close()
            }
        }
    } } catch (e: Exception) {
    }
    return pdfModel
}

