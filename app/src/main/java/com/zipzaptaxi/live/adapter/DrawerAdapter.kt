package com.zipzaptaxi.live.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zipzaptaxi.live.R

class DrawerAdapter(
    private val context: Context,
    private val data: List<String>
) : RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val textView: TextView = itemView.findViewById(R.id.tvItem)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            // Handle item click here
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.drawer_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
