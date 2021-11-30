package com.example.pethub.feedAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pethub.R
import com.example.pethub.favoriteAdapter.FavoriteAdapter
import com.example.pethub.retrofit.Feed
import kotlinx.android.synthetic.main.feed_item.view.*

class FeedAdapter(var list: MutableList<Feed>) : RecyclerView.Adapter<FeedAdapter.Holder>() {
    lateinit var mListener : FeedAdapter.OnClickListener
    class Holder(itemView: View, listener : OnClickListener) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.IVfavorite.setOnClickListener {
                listener.onImageViewClick(adapterPosition)
            }
        }
    }

    interface OnClickListener {
        fun onImageViewClick(position: Int)
    }

    fun setOnImageViewClickListener(listener : OnClickListener) {
        mListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        view.findViewById<TextView>(R.id.title)

        return Holder(view, mListener)
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