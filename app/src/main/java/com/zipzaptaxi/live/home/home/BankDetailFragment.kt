package com.zipzaptaxi.live.home.home

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentBankDetailsBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.DocResponseModel
import com.zipzaptaxi.live.model.FileUploadResponse
import com.zipzaptaxi.live.model.GetBankDetailsModel
import com.zipzaptaxi.live.utils.ImagePickerFragment
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.isVisible
import com.zipzaptaxi.live.utils.extensionfunctions.openImagePopUp
import com.zipzaptaxi.live.utils.extensionfunctions.prepareMultiPart
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.utils.helper.AppUtils.Companion.showErrorAlert
import com.zipzaptaxi.live.utils.showCustomAlertWithCancel
import com.zipzaptaxi.live.viewmodel.AuthViewModel
import com.zipzaptaxi.live.viewmodel.OthersViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class BankDetailFragment() : ImagePickerFragment(), Observer<RestObservable> {
    private lateinit var binding: FragmentBankDetailsBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    private lateinit var mValidationClass: ValidationsClass
    private var mPanFPath = ""
    private var mBankDetail = ""
    val map = HashMap<String, RequestBody>()
    private val imageMap= HashMap<String,String>()
    var verified=0

    private var data: GetBankDetailsModel.Data?=null

    private val docViewModel: AuthViewModel
            by lazy { ViewModelProvider(this)[AuthViewModel::class.java] }

    private val viewModel: OthersViewModel by lazy {
        OthersViewModel()
    }
    var bank_id=0
    override fun selectedImage(imagePath: String?, code: Int) {
        var bodyimage: MultipartBody.Part? = null

        map["image_of"] = mValidationClass.createPartFromString("bank")

        if (imagePath != null && code==0) {
            mPanFPath = imagePath
            bodyimage = prepareMultiPart("image", File(mPanFPath))

            map["image_name"] = mValidationClass.createPartFromString("pan_card_front")
            uploadDocApi(bodyimage)

            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivPanFront)

        }else if (imagePath != null && code==1) {
            mBankDetail = imagePath
            bodyimage = prepareMultiPart("image", File(mBankDetail))

            map["image_name"] = mValidationClass.createPartFromString("bank_passbook_front")
            uploadDocApi(bodyimage)
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivBankDetail)

        }
    }

    private fun uploadDocApi(bodyimage: MultipartBody.Part) {
        docViewModel.fileUploadApi(requireActivity(),true,map,bodyimage)
        docViewModel.mResponse.observe(this, this)
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

        binding.ivPanFront.setOnClickListener {
            if (verified == 1) {
               openImagePopUp(data?.pan_card_front, requireContext())
            } else {
                getImage(requireActivity(), 0, false)
            }
        }
        binding.ivBankDetail.setOnClickListener {

            if (verified == 1) {
                openImagePopUp(data?.bank_passbook_front, requireContext())
            } else {
                getImage(requireActivity(), 1, false)
            }
        }
        binding.btnUpdate.setOnClickListener {
            if(isValid()){

                imageMap["name"]= binding.etName.text.toString().trim()
                imageMap["account_no"]= binding.etAccNumber.text.toString().trim()
                imageMap["bank_name"]= binding.etBankName.text.toString().trim()
                imageMap["ifsc_code"]= binding.etIfsc.text.toString().trim()
                if(!binding.etUpi.text.toString().isNullOrEmpty()){
                    imageMap["upi"]= binding.etUpi.text.toString().trim()
                }
                showCustomAlertWithCancel(requireContext(),"You cannot edit your details once added. Kindly re-check or submit.",
                    "Submit","Cancel",{
                        viewModel.addBankAccApi(requireActivity(),true,imageMap)
                    },{}
                )

              //  viewModel.addBankAccApi(requireActivity(),true,data)

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
        else if(mPanFPath.isNullOrEmpty())
            showErrorAlert(requireActivity(), resources.getString(R.string.upload_pan_card))
        else if(mBankDetail.isNullOrEmpty())

            showErrorAlert(requireActivity(), resources.getString(R.string.upload_bank_detail))

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
            findNavController().navigate(R.id.action_bankDetailFragment_to_homeFragment)

        }
        toolbarBinding.toolbarTitle.text= getString(R.string.bank_details)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {

                if (value.data is FileUploadResponse) {
                    val data: FileUploadResponse = value.data
                    if (data.code == AppConstant.success_code) {
                        imageMap[data.data.image_name] = data.data.image_path

                    }else{
                        showErrorAlert(requireActivity(),data.message)
                    }
                }
                else if (value.data is GetBankDetailsModel) {
                    val bankData: GetBankDetailsModel = value.data
                    if (bankData.code == AppConstant.success_code) {
                        bankData.data.apply {
                            if(this.bank_name.isNullOrEmpty() && this.upi.isNullOrEmpty()){
                                return
                            }else{
                                verified=1
                                data= bankData.data
                                setData(bankData.data)
                            }
                        }

                    }else{
                        showErrorAlert(requireActivity(), bankData.message)
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

    private fun setData(data:GetBankDetailsModel.Data) {
        bank_id= data.id
        binding.tvNote.isVisible()
        binding.etBankName.setText(data.bank_name)
        binding.etAccNumber.setText(data.account_no)
        binding.etIfsc.setText(data.ifsc_code)
        binding.etName.setText(data.name)
        binding.etUpi.setText(data.upi)
        Glide.with(requireContext()).load(data.pan_card_front).into(binding.ivPanFront)
        Glide.with(requireContext()).load(data.bank_passbook_front).into(binding.ivBankDetail)


        binding.etBankName.isEnabled= false
        binding.etAccNumber.isEnabled= false
        binding.etIfsc.isEnabled= false
        binding.etName.isEnabled= false
        binding.etUpi.isEnabled= false
        binding.btnUpdate.isGone()

    }

    /**
     * Method for Opening Images
     */
    private fun openImagePopUp(imageRes: String?, ctx: Context) {
        val popup: View
        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (layoutInflater != null) {
            popup = layoutInflater.inflate(R.layout.porflioeimage_popup, null)
            val popupWindow = PopupWindow(
                popup,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                true
            )
            popupWindow.showAtLocation(popup, Gravity.CENTER, 0, 0)
            popupWindow.isTouchable = false
            popupWindow.isOutsideTouchable = false
            val headImagePopUp = popup.findViewById<PhotoView>(R.id.headImagePopUp)
            val backPress = popup.findViewById<ImageView>(R.id.backpress)
            backPress.setOnClickListener {
                popupWindow.dismiss()
            }

            Glide.with(ctx).load(imageRes).into(headImagePopUp)

        }
    }

}