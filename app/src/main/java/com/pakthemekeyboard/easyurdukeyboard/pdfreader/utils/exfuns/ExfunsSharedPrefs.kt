package com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PdfModel

fun Context.getMyPreferences(): SharedPreferences =
    getSharedPreferences("mySharedPrefs", Context.MODE_PRIVATE)

inline fun SharedPreferences.editPrefs(editor: SharedPreferences.Editor.() -> Unit) {
    val editPrefs = this.edit()
    editor(editPrefs)
    editPrefs.apply()
}

fun Context.saveArrayList(favUri: PdfModel) {
    val list = getFavArrayList("fav")
    list.add(favUri)
    val json = Gson().toJson(list)
    getMyPreferences().editPrefs {
        putString("fav", json)
    }
}

fun Context.getFavArrayList(key: String = "fav"): MutableList<PdfModel> {
    val json = getMyPreferences().getString(key, null)
    return Gson().fromJson(json, object : TypeToken<ArrayList<PdfModel>>() {}.type) ?: mutableListOf()
}

