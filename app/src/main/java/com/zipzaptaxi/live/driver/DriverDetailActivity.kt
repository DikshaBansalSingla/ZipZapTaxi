package com.zipzaptaxi.live.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.cache.CacheConstants
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.ActivityDriverDetailBinding
import com.zipzaptaxi.live.databinding.FragmentDriverListBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.model.DriverDetailResponse
import com.zipzaptaxi.live.utils.extensionfunctions.openImagePopUp
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.viewmodel.DriverViewModel

class DriverDetailActivity : Fragment(), Observer<RestObservable> {


    val viewModel:DriverViewModel by lazy {
        DriverViewModel()
    }

    private lateinit var binding:ActivityDriverDetailBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    private var driver_id= 0
    var jsonData:DriverDetailResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ActivityDriverDetailBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        CacheConstants.Current = "driverDetail"
        val bundle= arguments
        driver_id= bundle?.getInt("id")!!

        getDetailApi(driver_id)

        setOnClicks()
    }

    private fun setOnClicks() {
        binding.ivAadhaarFront.setOnClickListener {
            openImagePopUp(jsonData?.aadhar_card_front, requireContext())
        }
        binding.ivAadhaarBack.setOnClickListener {
            openImagePopUp(jsonData?.aadhar_card_back, requireContext())
        }
        binding.ivLicFront.setOnClickListener {
            openImagePopUp(jsonData?.driving_license_front, requireContext())
        }
        binding.ivLicBack.setOnClickListener {
            openImagePopUp(jsonData?.driving_license_back, requireContext())
        }
    }

    private fun getDetailApi(id: Int) {

        viewModel.driverDetailApi(requireActivity(),true,id, getUser(requireContext()).user_type.toString())
        viewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        toolbarBinding.toolbarTitle.text= getString(R.string.driver_detail)

    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is DriverDetailResponse) {
                    val data: DriverDetailResponse = value.data
                    if (data.code == AppConstant.success_code) {
                        jsonData= data.data
                        setData(data.data)
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

        }    }

    private fun setData(data: DriverDetailResponse.Data) {

        binding.tvName.text= data.name
        binding.tvPhone.text= data.phone
        binding.tvEmail.text= data.email
        binding.tvLicNo.text= data.driving_license_number
        binding.tvAadharNo.text= data.aadhar_card_number

        Glide.with(this).load(data.aadhar_card_front).into(binding.ivAadhaarFront)
        Glide.with(this).load(data.aadhar_card_back).into(binding.ivAadhaarBack)
        Glide.with(this).load(data.driving_license_front).into(binding.ivLicFront)
        Glide.with(this).load(data.driving_license_back).into(binding.ivLicBack)

    }
}