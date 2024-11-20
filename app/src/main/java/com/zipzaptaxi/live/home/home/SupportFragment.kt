package com.zipzaptaxi.live.home.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.cache.CacheConstants
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentSupportBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.model.SupportResponseModel
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.viewmodel.OthersViewModel


class SupportFragment : Fragment(), Observer<RestObservable> {

    private lateinit var binding: FragmentSupportBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding

    private val viewModel:OthersViewModel by lazy {
        OthersViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSupportBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CacheConstants.Current = "support"
        setToolbar()
        getSupportData()
        setOnClicks()
    }

    private fun setOnClicks() {
        binding.tvPhone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            val phone= "tel:"+binding.tvPhone.text.toString()
            intent.setData(Uri.parse(phone))
            startActivity(intent)
        }
    }

    private fun getSupportData() {
        viewModel.getSupportDataApi(requireActivity(),true)
        viewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }

        toolbarBinding.toolbarTitle.text= getString(R.string.support)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                 if (value.data is SupportResponseModel) {
                    val data: SupportResponseModel = value.data
                    if (data.code == AppConstant.success_code) {

                        binding.tvEmail.text= data.data.email
                        binding.tvPhone.text= data.data.phone
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