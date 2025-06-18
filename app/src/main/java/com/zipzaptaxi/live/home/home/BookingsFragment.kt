package com.zipzaptaxi.live.home.home

import BookingListResponse
import MyBookingsResponse
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.adapter.ActiveBookingsAdapter
import com.zipzaptaxi.live.adapter.CompletedBookingsAdapter
import com.zipzaptaxi.live.bookings.BookingDetail
import com.zipzaptaxi.live.cache.CacheConstants
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.databinding.FragmentBookingsBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.viewmodel.BookingViewModel

class BookingsFragment : Fragment(), Observer<RestObservable> {

    private val viewModel: BookingViewModel
            by lazy { ViewModelProvider(this)[BookingViewModel::class.java] }

    private val activeBookingsAdapter: ActiveBookingsAdapter by lazy {
        ActiveBookingsAdapter(requireContext())
    }

    private val completedBookingsAdapter:CompletedBookingsAdapter by lazy {
        CompletedBookingsAdapter(requireContext())
    }

    private var status="active"

    private var arrayList = ArrayList<MyBookingsResponse.Data>()
    private var compArrayList = ArrayList<MyBookingsResponse.Data>()

    private lateinit var binding: FragmentBookingsBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookingsBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CacheConstants.Current = "bookings"
        setToolbar()
        setAdapter()
        getBookingsApi(status)
        setOnClicks()

    }

    private fun setOnClicks() {
        binding.btnActiveBookings.setOnClickListener {
            status = "active"
            binding.btnActiveBookings.setBackgroundResource(R.drawable.bg_white_button_corners)
            binding.btnCompleted.setBackgroundResource(R.drawable.bg_grey_unselecte)
            binding.rvActiveBookings.visibility = View.VISIBLE
            binding.rvCompleted.visibility = View.GONE
            getBookingsApi(status)
        }

        binding.btnCompleted.setOnClickListener {
            status = "completed"
            binding.btnCompleted.setBackgroundResource(R.drawable.bg_white_button_corners)
            binding.btnActiveBookings.setBackgroundResource(R.drawable.bg_grey_unselecte)
            binding.rvActiveBookings.visibility = View.GONE
            binding.rvCompleted.visibility = View.VISIBLE
            getBookingsApi(status)

        }
    }

    private fun getBookingsApi(status: String) {
        viewModel.getAssignedBookingApi(requireActivity(),true,status, getUser(requireContext()).user_type!!)
        viewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    private fun setAdapter() {
        binding.rvActiveBookings.adapter = activeBookingsAdapter
        activeBookingsAdapter.list= arrayList

        activeBookingsAdapter.onItemClick = {
            val bundle = Bundle()
            bundle.putInt("id", arrayList[it].id)
            findNavController().navigate(R.id.action_bookingsFragment_to_bookingDetail,bundle)

        }

        setCompletedBookingsAdapter()
    }

    private fun setCompletedBookingsAdapter() {
        binding.rvCompleted.adapter=completedBookingsAdapter
        completedBookingsAdapter.list= compArrayList
        completedBookingsAdapter.onItemClick = {

            val bundle = Bundle()
            bundle.putInt("id", compArrayList[it].id)
            findNavController().navigate(R.id.action_bookingsFragment_to_bookingDetail,bundle)

        }
    }

    private fun setToolbar() {
        toolbarBinding.tvCabFree.isGone()
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
           (activity as MainActivity).openCloseDrawer()
        }
        toolbarBinding.tvCabFree.setOnClickListener {

        }
        toolbarBinding.toolbarTitle.text= getString(R.string.app_name)
    }


    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is MyBookingsResponse) {
                    val data: MyBookingsResponse = value.data
                    if (data.code == AppConstant.success_code) {
                        arrayList.clear()
                        compArrayList.clear()

                        if (status == "active") {
                            arrayList.addAll(data.data)
                            if (arrayList.size == 0) {
                                binding.rvActiveBookings.visibility = View.GONE
                                binding.tvNoData.visibility = View.VISIBLE
                                binding.rvCompleted.visibility = View.GONE

                            } else {
                                binding.rvActiveBookings.visibility = View.VISIBLE
                                binding.rvCompleted.visibility = View.GONE
                                binding.tvNoData.visibility = View.GONE
                                Log.i("=====", arrayList.size.toString())
                                activeBookingsAdapter.notifyDataSetChanged()
                            }
                        } else {
                            compArrayList.addAll(data.data)

                            if (compArrayList.size == 0) {
                                binding.rvActiveBookings.visibility = View.GONE
                                binding.rvCompleted.visibility = View.GONE
                                binding.tvNoData.visibility = View.VISIBLE

                            } else {
                                binding.rvActiveBookings.visibility = View.GONE
                                binding.rvCompleted.visibility = View.VISIBLE
                                binding.tvNoData.visibility = View.GONE
                                Log.i("=====", arrayList.size.toString())
                                completedBookingsAdapter.notifyDataSetChanged()
                            }
                        }
                    }else {
                        AppUtils.showErrorAlert(requireActivity(), data.message)
                    }
                }
            }

            Status.ERROR -> {
                if (value.data != null) {
                    showToast(value.data as String)
                } else {
                    showToast(value.error!!.toString())
                }
            }

            Status.LOADING -> {

            }

            else -> {}

        }
    }

}