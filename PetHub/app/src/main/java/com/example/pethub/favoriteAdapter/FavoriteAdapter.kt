package com.example.pethub.favoriteAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pethub.R
import com.example.pethub.retrofit.Feed
import kotlinx.android.synthetic.main.feed_item.view.*

class FavoriteAdapter(var list: MutableList<Feed>) : RecyclerView.Adapter<FavoriteAdapter.Holder>() {
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val curItem = list[position]
        holder.itemView.tvTitle.text = curItem.title
        holder.itemView.tvPrice.text = "Цена: " + curItem.price
        holder.itemView.tvAddress.text = "Адрес: " + curItem.x_coord
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(feedList: MutableList<Feed>) {
        list.clear()
        list.addAll(feedList)
        notifyDataSetChanged()
    }

}