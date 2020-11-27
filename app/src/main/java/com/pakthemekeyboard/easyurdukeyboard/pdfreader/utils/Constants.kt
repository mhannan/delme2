package com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils

import android.content.Context
import android.os.Environment

object Constants {

    fun getDir(context: Context) = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()
    /*const val  billingKey = "android.test.purchased"
    const val   monthlySubscriptionId = "android.test.purchased"*/
    const val  billingKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmeKsMwRIZu+dW9ymF1WXVvNdKu6aYEC95gaoNI1Z2ftgYA4lmXMCtb0MOS/tTpg2ClJKBzimUzGvG3kb2Yf4n9qfbTHnH75h8iQa59o95Z2Qvhb4bQGaAKYA16hZNufuV9pexM9iUTkV1DuQ4YlrO4CCWKK4d+ZVz7n47Nvd528ifRecwvmTi+l82LQr+AzXrV6qM15TsxFOhFu79TJJk6xr+0h7/Pku4Cm6WDQGQ2Zu6dUHlY/xOOTaAcZp47w/wl0HEfhgJCN+7mamS1I0Hl/1RD16QOteE1MenSSHBtdQ0XPfyHYbA/GgcvNqjO5kh2RRctwq44BMXxuDwblMHQIDAQAB"
    const val   monthlySubscriptionId = "sku_submonthly_unlimitedfeatures"
    const val PDF: String = ".pdf"
    const val CAMERA_REQUEST_CODE: Int = 2
    const val PDF_NAME: String = "pdfName"
    const val PDF_PATH: String = "pdfPath"
    const val PDF_TYPE: String = "pdfType"
    const val PDF_OBJ: String = "pdfObj"
     var reset_flag=false
    const val PDF_REQUEST_CODE: Int = 0
    const val SCAN_REQUEST_CODE: Int = 1

    private const val MAIN_FOLDER = "/Cam Scanner/"
    private val FOLDER_PATH = "/storage/emulated/0$MAIN_FOLDER"
    val PDF_FOLDER = FOLDER_PATH + "PDF"
    val Merge_FOLDER = FOLDER_PATH + "Merge PDF File"

}