package com.pakthemekeyboard.easyurdukeyboard.pdfreader.mergermodule

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.adapters.BaseViewHolder
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.adapters.GenericSearchAdapter
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PdfModel
import kotlinx.android.synthetic.main.row_pdf_list.view.*

class MergeAdapter(
    val context: Context,
    val pdfNameList: MutableList<PdfModel>,
    val onPdfItemClik: OnPdfItemClik
) : GenericSearchAdapter<PdfModel>(pdfNameList)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return PdfViewholder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_pdf_list, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if(listForSearch[position].selected) {
            holder.view.tickicon.visibility = View.VISIBLE
            holder.view.main.setBackgroundColor(Color.LTGRAY)

        }
        else {
            holder.view.tickicon.visibility = View.GONE
            holder.view.main.setBackgroundColor(Color.WHITE)


        }
        if (holder is PdfViewholder) {
            val names = listForSearch[position]
            holder.bindData(names)
        }
    }


    inner class PdfViewholder(private val v: View) : BaseViewHolder(v) {

        init {
            view.setOnClickListener(this)
            view.pdficon.setOnClickListener(this)
            view.popupMenu.setOnClickListener(this)
        }

        fun bindData(name: PdfModel) {
            with(view)
            {
                pdfName.text = name.pdfName
                if(name.lockpdf)
                    lockicon.visibility=View.VISIBLE
                else
                    lockicon.visibility=View.GONE
            }
        }

        override fun onClick(v: View?) {
            try {
                onPdfItemClik.onClick(this, listForSearch[adapterPosition], v!!.id, v)
            } catch (e: Exception) {

            }
        }
    }
    interface OnPdfItemClik {
        fun onClick(pdfViewholder: MergeAdapter.PdfViewholder, pdfModel: PdfModel, id: Int, view: View)
    }

}