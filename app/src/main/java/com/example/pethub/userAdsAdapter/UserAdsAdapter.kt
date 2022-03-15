package com.example.pethub.userAdsAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pethub.R
import com.example.pethub.retrofit.Ad
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.feed_item.view.*
import kotlinx.android.synthetic.main.feed_item.view.tvAddress
import kotlinx.android.synthetic.main.feed_item.view.tvPrice
import kotlinx.android.synthetic.main.feed_item.view.tvTitle
import kotlinx.android.synthetic.main.user_feed_item.view.*
import org.intellij.lang.annotations.JdkConstants

class UserAdsAdapter(var list: MutableList<Ad>) : RecyclerView.Adapter<UserAdsAdapter.Holder>() {
    lateinit var mListener : UserAdsAdapter.OnClickListener
    inner class Holder(itemView: View, listener : OnClickListener) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.editImage.setOnClickListener {
                listener.onEditImageViewClick(adapterPosition)
            }
            itemView.deleteImage.setOnClickListener {
                listener.onDeleteImageViewClick(adapterPosition)
            }
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    interface OnClickListener {
        fun onEditImageViewClick(position: Int)
        fun onDeleteImageViewClick(position: Int)
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener : OnClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_feed_item, parent, false)
        return Holder(view, mListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val curItem = list[position]
        holder.itemView.tvTitle.text = curItem.title
        holder.itemView.tvPrice.text = "Цена: " + curItem.price
        holder.itemView.tvAddress.text = "Город: " + curItem.city
        if (!curItem.url.isNullOrEmpty()) {
            Picasso.get().load(curItem.url).into(holder.itemView.userImageView)
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