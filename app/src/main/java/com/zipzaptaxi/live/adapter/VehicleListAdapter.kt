package com.zipzaptaxi.live.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.databinding.ItemViewDriiverListBinding
import com.zipzaptaxi.live.databinding.ItemViewVehicleListBinding
import com.zipzaptaxi.live.model.DriverListResponse
import com.zipzaptaxi.live.model.VehicleListResponse
import com.zipzaptaxi.live.utils.extensionfunctions.firstCap

class VehicleListAdapter(val context: Context): RecyclerView.Adapter<VehicleListAdapter.VehicleListAdapterHolder>() {

    var onItemClick: ((id: Int) -> Unit)? = null
    var onDeleteClick: ((id: Int) -> Unit)? = null
    var onEditClick: ((id: Int) -> Unit)? = null

    var list= ArrayList<VehicleListResponse.Data>()


    inner class VehicleListAdapterHolder(var binding: ItemViewVehicleListBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: VehicleListResponse.Data) {

            binding.tvVehicleNo.text= "Vehicle No.: "+data.number_plate
            binding.tvVehicleModel.text= "Model: "+data.vehicle_model
            Glide.with(context).load(data.img).error(R.drawable.cab_side_photo).into(binding.ivCab)
            if(data.verification=="rejected"){
                binding.tvtVerify.setTextColor(context.resources.getColor(R.color.red))

            } else if(data.verification=="pending"){
                binding.tvtVerify.setTextColor(context.resources.getColor(R.color.blueFb))
            }
            else{
                binding.tvtVerify.setTextColor(context.resources.getColor(R.color.approved))

            }
            binding.tvtVerify.text= data.verification.firstCap()

            binding.llMain.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }

            binding.ivDelete.setOnClickListener{
                onDeleteClick?.invoke(adapterPosition)
            }
           /* binding.ivEdit.setOnClickListener{
                onEditClick?.invoke(adapterPosition)
            }*/

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleListAdapterHolder {
        val view = ItemViewVehicleListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VehicleListAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleListAdapterHolder, position: Int) {
        holder.onBind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }


}