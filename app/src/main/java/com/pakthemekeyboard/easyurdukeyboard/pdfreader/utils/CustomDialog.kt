package com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.*
import android.widget.LinearLayout
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.exfuns.getScreenWidth

class CustomDialog(context: Context) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout: View = inflater.inflate(
            R.layout.layout_custom_toast,
            null
        )
        setContentView(layout)
        show()
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        val window = window
        window!!.setGravity(Gravity.CENTER)
        window.setLayout(
            context.getScreenWidth() / 100 * 80,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        Handler().postDelayed({ dismiss() }, 1000)
    }

}