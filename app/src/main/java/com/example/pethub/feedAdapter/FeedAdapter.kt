package com.example.pethub.feedAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pethub.R
import com.example.pethub.retrofit.Ad
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.feed_item.view.*

class FeedAdapter(var list: MutableList<Ad>, val favList: MutableList<Ad>) : RecyclerView.Adapter<FeedAdapter.Holder>() {
    lateinit var mListener : FeedAdapter.OnClickListener
    inner class Holder(itemView: View, listener : OnClickListener) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.IVfavorite.setOnClickListener {
                val idList = mutableListOf<Int?>()
                for (item in favList) {
                    idList.add(item.id)
                }
                if (!idList.contains(list[adapterPosition].id)) {
                    itemView.IVfavorite.setImageResource(R.drawable.ic_favorite_pressed)
                } else {
                    itemView.IVfavorite.setImageResource(R.drawable.ic_favorite_border)
                }
                listener.onImageViewClick(adapterPosition)
            }
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    interface OnClickListener {
        fun onImageViewClick(position: Int)
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener : OnClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return Holder(view, mListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val curItem = list[position]
        holder.itemView.tvTitle.text = curItem.title
        holder.itemView.tvPrice.text = "Цена: " + curItem.price
        holder.itemView.tvAddress.text = "Город: " + curItem.city
        if (!curItem.url.isNullOrEmpty()) {
            Picasso.get().load(curItem.url).into(holder.itemView.imageView)
        }
        val idList = mutableListOf<Int?>()
        for (item in favList) {
            idList.add(item.id)
        }
        if (!idList.contains(list[position].id)) {
            holder.itemView.IVfavorite.setImageResource(R.drawable.ic_favorite_border)
        } else {
            holder.itemView.IVfavorite.setImageResource(R.drawable.ic_favorite_pressed)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(adList: MutableList<Ad>) {
        list.clear()
        list.addAll(adList)
        notifyDataSetChanged()
    }

    fun updateFavList(newFavList: MutableList<Ad>) {
        favList.clear()
        favList.addAll(newFavList)
        notifyDataSetChanged()
    }

}