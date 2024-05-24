package com.zipzaptaxi.live.home.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentTermsCondBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.TermsPrivacyResponseModel
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.viewmodel.OthersViewModel

class TermsCondFragment() : Fragment(), Observer<RestObservable> {
    private lateinit var binding: FragmentTermsCondBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding

    var type=""
    private val viewModel: OthersViewModel by lazy {
        OthersViewModel()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTermsCondBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        val bundle= arguments
        type= bundle?.getString("type")!!
        getData()

    }

    private fun getData() {
        viewModel.getTermsDataApi(requireActivity(),true)
        viewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_termsCondFragment_to_settingsFragment)

        }
        if(type=="1"){
            toolbarBinding.toolbarTitle.text= getString(R.string.terms_and_conditions)

        }else{
            toolbarBinding.toolbarTitle.text= getString(R.string.privacy_policy)

        }

    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is TermsPrivacyResponseModel) {
                    val data: TermsPrivacyResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        if(type=="1"){
                            binding.tvContent.text= HtmlCompat.fromHtml(data.data.terms_and_conditions, HtmlCompat.FROM_HTML_MODE_COMPACT)
                        }else{
                            binding.tvContent.text= HtmlCompat.fromHtml(data.data.privacy_policy, HtmlCompat.FROM_HTML_MODE_COMPACT)
                        }

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