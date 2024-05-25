package com.zipzaptaxi.live.home.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.adapter.VehicleListAdapter
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentVehicleListBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.VehicleListResponse
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.isVisible
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.utils.showCustomAlertWithCancel
import com.zipzaptaxi.live.viewmodel.VehicleViewModel

class VehicleListFragment : Fragment(), Observer<RestObservable> {
    private lateinit var binding: FragmentVehicleListBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding

    private val vehicleViewModel:VehicleViewModel by lazy {
        VehicleViewModel()
    }
    private val vehicleListAdapter: VehicleListAdapter by lazy {
        VehicleListAdapter(requireContext())
    }

    var pos=0

    private var arrayList= ArrayList<VehicleListResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVehicleListBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setAdapter()
    }

    private fun setAdapter() {
        binding.rvVehicleList.adapter = vehicleListAdapter
        vehicleListAdapter.list= arrayList

        vehicleListAdapter.onItemClick = {
            val bundle = Bundle()
            bundle.putInt("id", arrayList[it].id)
            bundle.putString("status",arrayList[it].verification)
            bundle.putString("from","click")
           findNavController().navigate(R.id.action_vehicleListFragment_to_carDetailActivity,bundle)
        }

        vehicleListAdapter.onDeleteClick={
            showCustomAlertWithCancel(requireContext(),"Are you sure you want to delete "+arrayList[it].number_plate +" ?","Yes","No",{
                pos=it
                vehicleViewModel.deleteVehicleApi(requireActivity(),true,arrayList[it].id.toString())

            },{})
        }

        /*vehicleListAdapter.onEditClick={
            if(arrayList[it].verification=="rejected"){
                showCustomAlert(requireContext(),"Cab is rejected so you cannot edit","OK",{})
            }else if(arrayList[it].verification=="pending"){
                showCustomAlert(requireContext(),"Cab is not approved yet so you cannot edit","OK",{})

            }else{
                val intent= Intent(requireContext(), CarDetailActivity::class.java)
                intent.putExtra("id",arrayList[it].id)
                intent.putExtra("status",arrayList[it].verification)
                intent.putExtra("from","edit")
                startActivity(intent)
            }
        }*/
    }

    override fun onResume() {
        super.onResume()
        getVehicleListApi()
    }

    private fun getVehicleListApi() {

        vehicleViewModel.vehicleListApi(requireActivity(),true)
        vehicleViewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    private fun setToolbar() {
        toolbarBinding.ivAdd.isVisible()
        toolbarBinding.ivAdd.setOnClickListener {
            findNavController().navigate(R.id.action_vehicleListFragment_to_carDetailFragment)
        }
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

            toolbarBinding.toolbar.setNavigationOnClickListener {
                (activity as MainActivity).openCloseDrawer()
            }
            toolbarBinding.toolbarTitle.text= getString(R.string.vehicle_list)

    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                 if (value.data is VehicleListResponse) {
                    val data: VehicleListResponse = value.data
                    if (data.code == AppConstant.success_code) {
                        arrayList.clear()
                        if(data.data.isEmpty()){
                            binding.rvVehicleList.isGone()
                            binding.tvNoData.isVisible()
                        }
                        else {
                            binding.rvVehicleList.isVisible()
                            binding.tvNoData.isGone()

                            arrayList.addAll(data.data)
                            vehicleListAdapter.notifyDataSetChanged()
                        }
                    }else{
                        AppUtils.showErrorAlert(requireActivity(), data.message)

                    }
                } else if (value.data is BaseResponseModel) {
                     val data: BaseResponseModel = value.data
                     if (data.code == AppConstant.success_code) {
                         arrayList.removeAt(pos)
                         vehicleListAdapter.notifyDataSetChanged()
                     }else{
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