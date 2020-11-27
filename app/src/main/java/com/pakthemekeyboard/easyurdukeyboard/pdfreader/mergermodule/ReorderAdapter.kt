package com.pakthemekeyboard.easyurdukeyboard.pdfreader.mergermodule

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.models.PdfModel
import kotlinx.android.synthetic.main.reorderitemlayout.view.*
import java.util.*

class ReorderAdapter(val context: Context, var pdfNameList: MutableList<PdfModel>) : RecyclerView.Adapter<ReorderAdapter.ViewHolder>(), ItemMoveCallback.ItemTouchHelperContract

{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.reorderitemlayout, parent, false))
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(pdfNameList[position])
    }
    override fun getItemCount(): Int {
        return pdfNameList.size
    }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val filename = itemView.findViewById<TextView>(R.id.pdfName)
       private val remove:ImageView= itemView.findViewById(R.id.remove)
        fun bindItems(file: PdfModel) {
            filename.text = file.pdfName
            remove.setOnClickListener {

                pdfNameList.removeAt(adapterPosition)
                notifyDataSetChanged()
                if(pdfNameList.size<2)
                    (context as Activity).finish()


            }
        }
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(pdfNameList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(pdfNameList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override  fun onRowSelected(viewholder: ViewHolder?) {

        viewholder?.itemView?.main?.setBackgroundColor(Color.LTGRAY)
    }

    override fun onRowClear(viewholder:ViewHolder?) {
        viewholder?.itemView?.main?.setBackgroundColor(Color.WHITE)
    }
}


