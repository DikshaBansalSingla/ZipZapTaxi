package com.zipzaptaxi.live.bookings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.databinding.ActivityBookingDetailBinding
import com.zipzaptaxi.live.databinding.ActivityBookingHistoryBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding

class BookingHistoryActivity : AppCompatActivity() {

    lateinit var binding: ActivityBookingHistoryBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBookingHistoryBinding.inflate(layoutInflater)
        toolbarBinding= binding.appToolbar
        setContentView(binding.root)

        setToolbar()
        setAdapter()
    }

    private fun setAdapter() {

    }

    private fun setToolbar() {

    }
}