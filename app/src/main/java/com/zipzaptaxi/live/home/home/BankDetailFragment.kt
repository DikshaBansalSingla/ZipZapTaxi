package com.zipzaptaxi.live.home.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentBankDetailsBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.GetBankDetailsModel
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.utils.helper.AppUtils.Companion.showErrorAlert
import com.zipzaptaxi.live.viewmodel.OthersViewModel

class BankDetailFragment() : Fragment(), Observer<RestObservable> {
    private lateinit var binding: FragmentBankDetailsBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    private lateinit var mValidationClass: ValidationsClass

    private val viewModel: OthersViewModel by lazy {
        OthersViewModel()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBankDetailsBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mValidationClass= ValidationsClass.getInstance()
        setToolbar()
        getData()

        setClicks()

    }

    private fun setClicks() {
        binding.btnUpdate.setOnClickListener {
            if(isValid()){
                val data= HashMap<String,String>()
                data["name"]= binding.etName.text.toString().trim()
                data["account_no"]= binding.etAccNumber.text.toString().trim()
                data["bank_name"]= binding.etBankName.text.toString().trim()
                data["ifsc_code"]= binding.etIfsc.text.toString().trim()

                if(!binding.etUpi.text.toString().isNullOrEmpty()){
                    data["upi"]= binding.etUpi.text.toString().trim()
                }

                viewModel.addBankAccApi(requireActivity(),true,data)

            }
        }
    }

    private fun isValid(): Boolean {
        var check = false
        if (!mValidationClass.isNetworkConnected)
            showErrorAlert(requireActivity(), resources.getString(R.string.no_internet))
        else if (mValidationClass.checkStringNull(binding.etName.text.toString()))
            showErrorAlert(requireActivity(), resources.getString(R.string.error_name))
        else if (mValidationClass.checkStringNull(binding.etAccNumber.text.toString()))
            showErrorAlert(requireActivity(), resources.getString(R.string.please_enter_your_account_number))
        else if (!mValidationClass.isValidBankAccountNumber(binding.etAccNumber.text.toString()))
            showErrorAlert(requireActivity(), resources.getString(R.string.account_number_is_not_valid))
        else if (mValidationClass.checkStringNull(binding.etBankName.text.toString().trim()))
            showErrorAlert(requireActivity(), resources.getString(R.string.error_bank_name))
        else if (mValidationClass.checkStringNull(binding.etIfsc.text.toString().trim()))
            showErrorAlert(requireActivity(), resources.getString(R.string.error_ifsc_code))
        else if (!mValidationClass.isValidIFSC(binding.etIfsc.text.toString().trim()))
            showErrorAlert(requireActivity(), resources.getString(R.string.valid_ifsc_code))

        else
            check = true
        return check
    }

    private fun getData() {
        viewModel.getBankDetailApi(requireActivity(),true, getUser(requireContext()).user_type!!)
        viewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_bankDetailFragment_to_settingsFragment)

        }
        toolbarBinding.toolbarTitle.text= getString(R.string.bank_details)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is GetBankDetailsModel) {
                    val data: GetBankDetailsModel = value.data
                    if (data.code == AppConstant.success_code) {
                        data.data.apply {
                            if(this.bank_name.isNullOrEmpty() && this.upi.isNullOrEmpty()){
                                return
                            }else{
                                setData(data.data)
                            }
                        }

                    }else{
                        showErrorAlert(requireActivity(), data.message)
                    }
                } else if (value.data is BaseResponseModel) {
                    val data: BaseResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        AppUtils.showSuccessAlert(requireActivity(),"Bank Account added successfully")
                        findNavController().navigate(R.id.action_bankDetailFragment_to_settingsFragment)
                    }else{
                        showErrorAlert(requireActivity(), data.message)
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

    private fun setData(data: GetBankDetailsModel.Data) {
        binding.etBankName.setText(data.bank_name)
        binding.etAccNumber.setText(data.account_no)
        binding.etIfsc.setText(data.ifsc_code)
        binding.etName.setText(data.name)
        binding.etUpi.setText(data.upi)


        binding.etBankName.isEnabled= false
        binding.etAccNumber.isEnabled= false
        binding.etIfsc.isEnabled= false
        binding.etName.isEnabled= false
        binding.etUpi.isEnabled= false

    }

}