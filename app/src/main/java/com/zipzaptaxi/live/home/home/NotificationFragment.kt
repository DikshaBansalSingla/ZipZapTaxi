package com.zipzaptaxi.live.home.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.adapter.NotificationAdapter
import com.zipzaptaxi.live.cache.CacheConstants
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentNotificationListBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.model.NotificationListModel
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.isVisible
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.viewmodel.OthersViewModel

class NotificationFragment : Fragment(), Observer<RestObservable> {

    private lateinit var binding: FragmentNotificationListBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding

    private val adapter: NotificationAdapter by lazy {
        NotificationAdapter()
    }

    private val viewModel: OthersViewModel by lazy {
        OthersViewModel()
    }

    val arrayList = ArrayList<NotificationListModel.Data>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNotificationListBinding.inflate(inflater, container, false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CacheConstants.Current = "notifications"
        setToolbar()
        setAdapter()
        getData()
    }

    private fun getData() {
        viewModel.getNotificationListApi(requireActivity(), true, getUser(requireContext()).user_type!!)
        viewModel.mResponse.observe(viewLifecycleOwner, this)
    }

    private fun setAdapter() {
        binding.rvNotificationList.adapter = adapter
        adapter.list = arrayList

        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putInt("id", arrayList[it].booking_id.toInt())
            findNavController().navigate(R.id.action_notificationFragment_to_bookingDetail, bundle)
        }
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }

        toolbarBinding.toolbarTitle.text = getString(R.string.notifications)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is NotificationListModel) {
                    val data: NotificationListModel = value.data
                    if (data.code == AppConstant.success_code) {
                        arrayList.clear()
                        if (data.data.isNullOrEmpty()) {
                            binding.rvNotificationList.isGone()
                            binding.tvNoData.isVisible()
                        } else {
                            binding.rvNotificationList.isVisible()
                            binding.tvNoData.isGone()

                            arrayList.addAll(data.data)
                            adapter.notifyDataSetChanged()
                        }

                    } else {
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