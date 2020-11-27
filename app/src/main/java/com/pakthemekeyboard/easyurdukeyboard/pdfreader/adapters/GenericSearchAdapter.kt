package com.pakthemekeyboard.easyurdukeyboard.pdfreader.adapters

import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.utils.interfaces.Searchable

abstract class GenericSearchAdapter<T : Searchable>(var listForSearch: MutableList<T>) :
    RecyclerView.Adapter<BaseViewHolder>(), Filterable {

    private var onNothingFound: (() -> Unit)? = null
    var originalList = listForSearch

    fun search(s: String?, onNothingFound: (() -> Unit)?) {
        this.onNothingFound = onNothingFound
        filter.filter(s)
    }

    override fun getItemCount() = listForSearch.count()

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val searchResults: MutableList<T> = if (constraint!! == "") {
                    originalList
                } else {
                    originalList.filter {
                        it.wordForSearch().toLowerCase().startsWith(constraint.toString().toLowerCase())
                    } as MutableList<T>

                }
                return FilterResults().also {
                    it.values = searchResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                listForSearch = results!!.values as MutableList<T>
                if (listForSearch.isNullOrEmpty()) {
                    onNothingFound?.invoke()
                }
                notifyDataSetChanged()
            }
        }
    }

}