package com.zipzaptaxi.live.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.databinding.ItemViewHomeBinding
import com.zipzaptaxi.live.databinding.ItemViewNotificationsBinding
import com.zipzaptaxi.live.databinding.ItemViewPaidBinding
import com.zipzaptaxi.live.databinding.ItemViewTransactionsBinding
import com.zipzaptaxi.live.model.NotificationListModel
import com.zipzaptaxi.live.model.TdsDataModel
import com.zipzaptaxi.live.utils.extractTimeIn12HourFormat

class TransactionsAdapter(val context:Context): RecyclerView.Adapter<TransactionsAdapter.TransactionsHolder>() {

    var onItemClick: ((id: Int) -> Unit)? = null

    var list= ArrayList<TdsDataModel.Data.Transaction>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsHolder {
        val view = ItemViewTransactionsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TransactionsHolder(view)
    }

    inner class TransactionsHolder(val binding: ItemViewTransactionsBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: TdsDataModel.Data.Transaction) {
            binding.tvTransactionId.text= "Transaction Id: "+data.transaction_id
            binding.tvAmount.text= "Amount: "+data.amount
            binding.tvAmtType.text= data.aspects_of_transaction
            if(data.status=="credit") {
                binding.tvAmtType.setTextColor(context.resources.getColor(R.color.approved))
            }else{
                binding.tvAmtType.setTextColor(context.resources.getColor(R.color.red_color))
            }
            binding.tvTime.text=data.created_at

        }
    }
    override fun onBindViewHolder(holder: TransactionsHolder, position: Int) {
        holder.onBind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }

}