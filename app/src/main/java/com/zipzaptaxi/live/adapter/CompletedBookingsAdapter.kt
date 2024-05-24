package com.zipzaptaxi.live.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zipzaptaxi.live.databinding.ItemViewHomeBinding
import com.zipzaptaxi.live.utils.extensionfunctions.isGone

class CompletedBookingsAdapter(val context: Context): RecyclerView.Adapter<CompletedBookingsAdapter.CompletedBookingsHolder>() {

    var onItemClick: ((id: Int) -> Unit)? = null

    var list= ArrayList<BookingListResponse.Data>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedBookingsHolder {
        val view = ItemViewHomeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CompletedBookingsHolder(view)
    }


    inner class CompletedBookingsHolder(val binding: ItemViewHomeBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: BookingListResponse.Data) {
            binding.btnAccept.isGone()

            if(data.trip=="oneway"){
                binding.txtDropDate.text= "Time Estimation"
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
            //binding.tvpickupCharges.text= data.pickup_charges
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
    override fun onBindViewHolder(holder: CompletedBookingsHolder, position: Int) {
        holder.onBind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }

}