package com.zipzaptaxi.live.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.databinding.ItemViewDriiverListBinding
import com.zipzaptaxi.live.model.DriverListResponse

class DriverListAdapter(val context: Context): RecyclerView.Adapter<DriverListAdapter.DriverListAdapterHolder>() {

    var onItemClick: ((id: Int) -> Unit)? = null
    var onDeleteClick: ((id: Int) -> Unit)? = null

    var list= ArrayList<DriverListResponse.Data>()


    inner class DriverListAdapterHolder(var binding: ItemViewDriiverListBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: DriverListResponse.Data) {

            binding.tvName.text= "Name: "+data.name
            binding.tvLicNo.text= "License No.: "+data.driving_license_number

            if(data.verification=="Pending"){
                binding.tvStatus.setTextColor(context.resources.getColor(R.color.blueFb))

            }else if(data.verification=="Rejected"){
                binding.tvStatus.setTextColor(context.resources.getColor(R.color.red))

            }else{
                binding.tvStatus.setTextColor(context.resources.getColor(R.color.approved))

            }
            binding.tvStatus.text= data.verification

            binding.llMain.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }

            binding.ivDelete.setOnClickListener{
                onDeleteClick?.invoke(adapterPosition)
            }

            /* if(data.trip=="oneway"){
                binding.txtDropDate.text= "Time Estimation"
                binding.tvDropDate.text= data.time_to_cover
            }else{
                binding.tvDropDate.text= data.ride_end_date+" "+data.ride_end_time
            }*/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverListAdapterHolder {
        val view = ItemViewDriiverListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DriverListAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: DriverListAdapterHolder, position: Int) {
        holder.onBind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }


}