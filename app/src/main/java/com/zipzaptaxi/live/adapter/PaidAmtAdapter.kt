package com.zipzaptaxi.live.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zipzaptaxi.live.databinding.ItemViewHomeBinding
import com.zipzaptaxi.live.databinding.ItemViewPaidBinding
import com.zipzaptaxi.live.model.GetTransactionsModel

class PaidAmtAdapter: RecyclerView.Adapter<PaidAmtAdapter.PaidAmtHolder>() {

    var onItemClick: ((id: Int) -> Unit)? = null

    var list= ArrayList<GetTransactionsModel.Data>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaidAmtHolder {
        val view = ItemViewPaidBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PaidAmtHolder(view)
    }


    inner class PaidAmtHolder(val binding: ItemViewPaidBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(
            data: GetTransactionsModel.Data
    ) {
         binding.tvAmount.text= data.amount
         binding.tvDate.text= data.created_at
        }
    }
    override fun onBindViewHolder(holder: PaidAmtHolder, position: Int) {
        holder.onBind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }

}