package com.zipzaptaxi.live.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zipzaptaxi.live.databinding.ItemViewHomeBinding
import com.zipzaptaxi.live.databinding.ItemViewNotificationsBinding
import com.zipzaptaxi.live.databinding.ItemViewPaidBinding
import com.zipzaptaxi.live.model.NotificationListModel
import com.zipzaptaxi.live.utils.extractTimeIn12HourFormat

class NotificationAdapter: RecyclerView.Adapter<NotificationAdapter.NotificationHolder>() {

    var onItemClick: ((id: Int) -> Unit)? = null

    var list= ArrayList<NotificationListModel.Data>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHolder {
        val view = ItemViewNotificationsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotificationHolder(view)
    }


    inner class NotificationHolder(val binding: ItemViewNotificationsBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(
            data: NotificationListModel.Data
    ) {
            binding.tvMessage.text= data.message
            binding.tvTime.text=data.created_at.extractTimeIn12HourFormat()


            binding.llMain.setOnClickListener {
                if(data.redirect=="yes"){
                    onItemClick?.invoke(adapterPosition)
                }
            }
        }
    }
    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        holder.onBind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }

}