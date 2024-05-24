package com.zipzaptaxi.live.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.adapter.DriverListAdapter
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentDriverListBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.DriverListResponse
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.isVisible
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.showCustomAlertWithCancel
import com.zipzaptaxi.live.viewmodel.DriverViewModel


class DriverListFragment : Fragment(),
    Observer<RestObservable> {

    private lateinit var binding:FragmentDriverListBinding
    private lateinit var toolbarBinding:LayoutToolbarBinding
    val arrayList= ArrayList<DriverListResponse.Data>()
    var position=0

    val viewModel:DriverViewModel by lazy {
        DriverViewModel()
    }

    private val driverListAdapter: DriverListAdapter by lazy {
        DriverListAdapter(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDriverListBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setAdapter()
    }

    override fun onResume() {
        super.onResume()
        getDriverListApi()
    }

    private fun getDriverListApi() {
        viewModel.driverListApi(requireActivity(),true)
        viewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    private fun setAdapter() {
        binding.rvDriverList.adapter = driverListAdapter
        driverListAdapter.list= arrayList
        driverListAdapter.onItemClick ={
            val bundle = Bundle()
            bundle.putInt("id", arrayList[it].id)

            findNavController().navigate(R.id.action_driverListFragment_to_driverDetailActivity,bundle)

        }

        driverListAdapter.onDeleteClick ={
            showCustomAlertWithCancel(requireContext(),"Are you sure you want to delete the driver?","Yes","No",{
                position=it
                viewModel.deleteDriverApi(requireActivity(),true,arrayList[it].id.toString())

            },{})

        }

    }

    private fun setToolbar() {
        toolbarBinding.ivAdd.isVisible()
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }
        toolbarBinding.ivAdd.setOnClickListener {
            findNavController().navigate(R.id.action_driverListFragment_to_addDriverFragment2)
        }
        toolbarBinding.toolbarTitle.text= getString(R.string.driver_list)
    }


    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is DriverListResponse) {
                    val data: DriverListResponse = value.data
                    if (data.code == AppConstant.success_code) {
                        arrayList.clear()
                        if(data.data.isNullOrEmpty()){
                            binding.rvDriverList.isGone()
                            binding.tvNoData.isVisible()
                        }
                        else {
                            binding.rvDriverList.isVisible()
                            binding.tvNoData.isGone()

                            arrayList.addAll(data.data)
                            driverListAdapter.notifyDataSetChanged()
                        }
                    }
                }

              else if (value.data is BaseResponseModel) {
                    val data: BaseResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        arrayList.removeAt(position)
                        driverListAdapter.notifyDataSetChanged()
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