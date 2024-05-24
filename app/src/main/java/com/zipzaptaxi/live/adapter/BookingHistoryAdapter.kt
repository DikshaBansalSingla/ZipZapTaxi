package com.zipzaptaxi.live.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zipzaptaxi.live.databinding.ItemViewBookingHistoryBinding
import com.zipzaptaxi.live.databinding.ItemViewHomeBinding
import com.zipzaptaxi.live.model.GetWalletModel
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.isVisible
import java.util.ArrayList

class BookingHistoryAdapter: RecyclerView.Adapter<BookingHistoryAdapter.BookingHistoryHolder>() {

    var onItemClick: ((id: Int) -> Unit)? = null

    var list= ArrayList<GetWalletModel.Data.Booking>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingHistoryHolder {
        val view = ItemViewBookingHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BookingHistoryHolder(view)
    }


    inner class BookingHistoryHolder(val binding: ItemViewBookingHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(
            data: GetWalletModel.Data.Booking
    ) {

            if(data.trip=="oneway"){
                binding.txtDropDate.text= "Time Estimation"
                binding.tvDropDate.text= data.time_to_cover
            }else{
                binding.tvDropDate.text= data.ride_end_date+" "+data.ride_end_time
            }

            binding.tvTripId.text="Trip ID: "+data.booking_unique_id
            binding.tvFrom.text=data.source
            binding.tvDrop.text=data.destination
            binding.tvStartDate.text=data.ride_date+" "+data.ride_time
            binding.tvCarModel.text= data.cab_model
            binding.tvAmount.text= "₹"+data.price
            if(data.status=="cancelled"){
                binding.clCompleted.isGone()
                binding.txtCancelDate.isVisible()
                binding.tvCancelDate.isVisible()
                binding.txtPenalty.isVisible()
                binding.tvPenalty.isVisible()

                binding.tvCancelDate.text= data.cancellation_time
                binding.tvPenalty.text= data.penalty.toString()
            }else{
                binding.clCompleted.isVisible()
                binding.txtCancelDate.isGone()
                binding.tvCancelDate.isGone()
                binding.txtPenalty.isGone()
                binding.tvPenalty.isGone()
                binding.tvExtraKm.text= data.extra_km_driven.toString()
                binding.tvExtraPrice.text= data.extra_charge
                binding.tvTotalPrice.text= data.total_price_with_extra_km.toString()
            }


            binding.llMain.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }
        }
    }
    override fun onBindViewHolder(holder: BookingHistoryHolder, position: Int) {
        holder.onBind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }

}