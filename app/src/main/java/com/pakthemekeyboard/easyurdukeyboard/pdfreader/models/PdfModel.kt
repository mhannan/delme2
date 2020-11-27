package com.pakthemekeyboard.easyurdukeyboard.pdfreader.models

import android.os.Parcelable
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.interfaces.Searchable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PdfModel(
    var pdfName: String,
    var pdfPath: String,
    val type: String,
    val size: String,
    var selected:Boolean,
    var lockpdf:Boolean,
    val lastModified: String) : Parcelable, Searchable {

    override fun wordForSearch(): String {
        return pdfName
    }
}