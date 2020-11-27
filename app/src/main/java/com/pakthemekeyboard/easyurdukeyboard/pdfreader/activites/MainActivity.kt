package com.pakthemekeyboard.easyurdukeyboard.pdfreader.activites

import android.os.Bundle
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R

class MainActivity : PdfListClass() {


    override fun onCreate(savedInstanceState: Bundle?) {

        val bundle = Bundle()
        bundle.putString("layout", "MainActivity")
        setContentView(R.layout.activity_index)
        super.onCreate(bundle)


    }





}
