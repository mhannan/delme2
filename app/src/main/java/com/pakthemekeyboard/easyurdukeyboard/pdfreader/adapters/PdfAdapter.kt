package com.pakthemekeyboard.easyurdukeyboard.pdfreader.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PdfModel
import kotlinx.android.synthetic.main.row_pdf_list.view.*

class PdfAdapter(val context: Context, private val pdfNameList: MutableList<PdfModel>, val onPdfItemClik: OnPdfItemClik) : GenericSearchAdapter<PdfModel>(pdfNameList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return PdfViewholder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_pdf_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is PdfViewholder) {
            val names = listForSearch[position]
            holder.bindData(names)
        }
    }


    inner class PdfViewholder(private val v: View) : BaseViewHolder(v){

        init {
            view.setOnClickListener(this)
            view.popupMenu.setOnClickListener(this)
        }

        fun bindData(name: PdfModel) {
            with(view)
            {
                if(name.lockpdf)
                lockicon.visibility=View.VISIBLE
                else
                lockicon.visibility=View.GONE

                
                pdfName.text = name.pdfName
            }
        }

        override fun onClick(v: View?) {
            try {
                onPdfItemClik.onClick(this, listForSearch[adapterPosition], v!!.id, v)
            }catch (e: Exception){

            }
        }
    }

    interface OnPdfItemClik {
        fun onClick(pdfViewholder: PdfViewholder, pdfModel: PdfModel, id: Int, view: View)
    }
}