package com.zipzaptaxi.live.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zipzaptaxi.live.databinding.ItemViewHomeBinding
import com.zipzaptaxi.live.databinding.ItemViewHomeCityBinding
import com.zipzaptaxi.live.databinding.ItemViewPaidBinding
import com.zipzaptaxi.live.model.AddDeleteCityModel

class HomeCityAdapter: RecyclerView.Adapter<HomeCityAdapter.HomeCityHolder>() {

    var onDeleteClick: ((id: Int) -> Unit)? = null

    var list= ArrayList<AddDeleteCityModel.Data>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCityHolder {
        val view = ItemViewHomeCityBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HomeCityHolder(view)
    }


    inner class HomeCityHolder(val binding: ItemViewHomeCityBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(data:AddDeleteCityModel.Data
    ) {
            binding.tvCity.text= data.city
            binding.ivDelete.setOnClickListener {
                onDeleteClick?.invoke(adapterPosition)
            }
        }
    }
    override fun onBindViewHolder(holder: HomeCityHolder, position: Int) {
        holder.onBind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(newList: List<AddDeleteCityModel.Data>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}