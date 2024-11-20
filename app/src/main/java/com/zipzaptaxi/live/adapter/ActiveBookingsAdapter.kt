package com.zipzaptaxi.live.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.databinding.ItemViewHomeBinding
import com.zipzaptaxi.live.utils.extensionfunctions.isGone

class ActiveBookingsAdapter(val context: Context): RecyclerView.Adapter<ActiveBookingsAdapter.ActiveBookingsHolder>() {

    var onItemClick: ((id: Int) -> Unit)? = null

    var list= ArrayList<MyBookingsResponse.Data>()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveBookingsHolder {
        val view = ItemViewHomeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ActiveBookingsHolder(view)
    }


    inner class ActiveBookingsHolder(val binding: ItemViewHomeBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: MyBookingsResponse.Data) {
            binding.btnAccept.isGone()
            if(data.trip=="oneway"){
                binding.txtDropDate.text= context.getString(R.string.time_estimation)
                binding.tvDropDate.text= data.time_to_cover
            }else{
                binding.tvDropDate.text= data.ride_end_date+" "+data.ride_end_time
            }

            binding.tvTripType.text=data.trip
            binding.tvTripId.text="Trip ID: "+data.booking_unique_id
            binding.tvFrom.text=data.source
            binding.tvDrop.text=data.destination
            binding.tvStartDate.text=data.ride_date+" "+data.ride_time
            binding.tvCarModel.text= data.cab_model
            binding.tvAmount.text= data.price
            binding.tvPassengers.text= data.passengers
            binding.tvLuggage.text= data.bags
           // binding.tvpickupCharges.text= data.pickup_charges
            binding.tvStateToll.text= data.state_tax
            binding.tvTollTax.text= data.toll_tax
            binding.tvNightPickup.text= data.night_pick
            binding.tvAirportTax.text= data.airport_charge
            Glide.with(context).load(data.cab_image).into(binding.ivCar)
           /* if(data.night_charges.isNullOrEmpty()){
                binding.tvNightCharges.text= "0"
            }else{
                binding.tvNightCharges.text= data.night_charges
            }
*/
            binding.tvNightPickup.text= data.night_pick

            binding.tvKmLimit.text= data.distance
            binding.tvCarOilType.text= data.car_type


            binding.llMain.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }
        }
    }
    override fun onBindViewHolder(holder: ActiveBookingsHolder, position: Int) {
        holder.onBind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }

}