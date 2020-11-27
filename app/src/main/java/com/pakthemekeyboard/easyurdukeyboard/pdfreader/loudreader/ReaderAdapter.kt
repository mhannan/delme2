package com.pakthemekeyboard.easyurdukeyboard.pdfreader.loudreader
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R


class ReaderAdapter (val context: Context, var list: MutableList<String>,var playingposition:Int): RecyclerView.Adapter<ReaderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pdfreaderitem, parent, false))
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindItems(list[position])
    }
    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         private  val sentence = itemView.findViewById<TextView>(R.id.sentence)!!

        fun bindItems(singlesentence: String) {
            sentence.text = "$singlesentence."
            if(adapterPosition==playingposition)
                sentence.setBackgroundColor(ResourcesCompat.getColor(context.resources,R.color.yellow,context.theme))
            else
            sentence.setBackgroundColor(ResourcesCompat.getColor(context.resources,R.color.lightgrey,context.theme))
        }
    }


}