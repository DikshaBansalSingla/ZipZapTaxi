package com.zipzaptaxi.live.home.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zipzaptaxi.live.bookings.BookingDetail
import com.zipzaptaxi.live.databinding.CancelBottomSheetBinding
import com.zoptal.vpn.util.hideKeyBoard

class CancelBottomSheet(
    private val bookingDetail: BookingDetail,
    private val delete_msg: String,
) :
    BottomSheetDialogFragment(){

    private lateinit var binding: CancelBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = CancelBottomSheetBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)

        binding.tvDescDialog.text= delete_msg

            binding.btnYes.setOnClickListener {

                    hideKeyBoard(requireContext(), requireView())
                    bookingDetail.cancelRide()
                    dialog?.dismiss()
                }

        binding.btnNo.setOnClickListener {
            dialog?.dismiss()
        }

    }
}