package com.zipzaptaxi.live.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.model.DrawerModel

class ItemAdapter(val context: Context, private val items: ArrayList<DrawerModel>, val clickOnItem: ClickOnItem) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItem: TextView = view.findViewById(R.id.tvItem)
        val ivItem: ImageView = view.findViewById(R.id.ivItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvItem.text = items[position].item
        Glide.with(context).load(items[position].image).into(holder.ivItem)

        holder.itemView.setOnClickListener {
            clickOnItem.onDrawerItemClick(position,items)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

interface ClickOnItem {
    fun onDrawerItemClick(position: Int, items: ArrayList<DrawerModel>)

}
