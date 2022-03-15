package com.example.pethub.favoriteAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pethub.R
import com.example.pethub.retrofit.Ad
import com.example.pethub.retrofit.AdPost
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.feed_item.view.*

class FavoriteAdapter(var list: MutableList<Ad>) : RecyclerView.Adapter<FavoriteAdapter.Holder>() {
    lateinit var mListener: OnClickListener
    class Holder(itemView: View, listener: OnClickListener) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.IVfavorite.setOnClickListener {
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

    fun setOnImageViewClickListener(listener: OnClickListener) {
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
        holder.itemView.IVfavorite.setImageResource(R.drawable.ic_favorite_pressed)
        if (!curItem.url.isNullOrEmpty()) {
            Picasso.get().load(curItem.url).into(holder.itemView.imageView)
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

}