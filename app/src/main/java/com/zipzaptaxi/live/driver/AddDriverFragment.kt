package com.zipzaptaxi.live.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.cache.CacheConstants
import com.zipzaptaxi.live.cache.getIsDialogOpen
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.cache.saveIsDialogOpen
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentAddDriverBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.FileUploadResponse
import com.zipzaptaxi.live.utils.ImagePickerFragment
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.prepareMultiPart
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.utils.showAlertWithCancel
import com.zipzaptaxi.live.viewmodel.AuthViewModel
import com.zipzaptaxi.live.viewmodel.DriverViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddDriverFragment : ImagePickerFragment(), Observer<RestObservable> {

    private lateinit var binding: FragmentAddDriverBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    private lateinit var mValidationClass: ValidationsClass
    private val hashMap= HashMap<String, RequestBody>()

    private val finalMap= HashMap<String,String>()
    private var mAadharFPath = ""
    private var mAadharBPath = ""
    private var mLicFPath = ""
    private var mLicBPath = ""
    private var mProfileImagePath = ""

    private val viewModel: AuthViewModel
            by lazy { ViewModelProvider(requireActivity())[AuthViewModel::class.java] }

    private val driverViewModel: DriverViewModel
            by lazy { ViewModelProvider(requireActivity())[DriverViewModel::class.java] }


    override fun selectedImage(imagePath: String?, code: Int) {
        var bodyimage: MultipartBody.Part? = null

        hashMap["image_of"] = mValidationClass.createPartFromString("driver")

        if (imagePath != null && code==0) {
            mAadharFPath = imagePath
            bodyimage = prepareMultiPart("image", File(mAadharFPath))

            hashMap["image_name"] = mValidationClass.createPartFromString("aadhar_card_front")
            uploadDocApi(bodyimage)
            Glide.with(requireActivity()).load(imagePath).error(R.drawable.placeholder).into(binding.ivAadharFront)


        }else if (imagePath != null && code==1) {
            mAadharBPath = imagePath
            bodyimage = prepareMultiPart("image", File(mAadharBPath))

            hashMap["image_name"] = mValidationClass.createPartFromString("aadhar_card_back")
            uploadDocApi(bodyimage)

            Glide.with(requireActivity()).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivAadharBack)

        }else if (imagePath != null && code==2) {
            mLicFPath = imagePath
            bodyimage = prepareMultiPart("image", File(mLicFPath))

            hashMap["image_name"] = mValidationClass.createPartFromString("driving_license_front")
            uploadDocApi(bodyimage)
            Glide.with(requireActivity()).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivDLFront)
        }else if (imagePath != null && code==3) {
            mLicBPath = imagePath
            bodyimage = prepareMultiPart("image", File(mLicBPath))

            hashMap["image_name"] = mValidationClass.createPartFromString("driving_license_back")
            uploadDocApi(bodyimage)
            Glide.with(requireActivity()).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivDLBack)
        } else if (imagePath != null && code==4) {
            hashMap["image_of"] = mValidationClass.createPartFromString("driver_profile")

            mProfileImagePath = imagePath
            bodyimage = prepareMultiPart("image", File(mProfileImagePath))

            hashMap["image_name"] = mValidationClass.createPartFromString("profile_pic")
            uploadDocApi(bodyimage)
            Glide.with(requireActivity()).load(imagePath).error(R.drawable.placeholder).into(binding.img)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for requireActivity() fragment
        binding = FragmentAddDriverBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CacheConstants.Current = "driver"
        mValidationClass=ValidationsClass.getInstance()

        setToolbar()

        var opened= getIsDialogOpen(requireContext())
        setOnClicks(opened)
    }

    private fun uploadDocApi(bodyimage: MultipartBody.Part) {
        viewModel.fileUploadApi(requireActivity(),false,hashMap,bodyimage)
        viewModel.mResponse.observe(viewLifecycleOwner, this)
    }

    private fun setOnClicks(opened: Boolean) {
        binding.btnAdd.setOnClickListener {
            addDriver()
        }

        binding.ivAadharFront.setOnClickListener {
            if(!opened){
                showAlertWithCancel(requireContext(),
                    "Zipzap Taxi Partner would like to access your camera and gallery to enable photos uploads. Your photos will only be use within the app and will not be shared. Please Allow to grant permission.","Allow","Deny",
                    {
                        saveIsDialogOpen(requireContext(),true)
                        getImage(requireActivity(), 0,false)

                    },{

                    })
            }else{
                getImage(requireActivity(), 0,false)

            }
        }

        binding.ivAadharBack.setOnClickListener {
            if(!opened){
                showAlertWithCancel(requireContext(),
                    "Zipzap Taxi Partner would like to access your camera and gallery to enable photos uploads. Your photos will only be use within the app and will not be shared. Please Allow to grant permission.","Allow","Deny",
                    {
                        saveIsDialogOpen(requireContext(),true)
                        getImage(requireActivity(), 1,false)

                    },{

                    })
            }else{
                getImage(requireActivity(), 1,false)

            }
        }

        binding.ivDLFront.setOnClickListener {
            if (!opened) {
                showAlertWithCancel(requireContext(),
                    "Zipzap Taxi Partner would like to access your camera and gallery to enable photos uploads. Your photos will only be use within the app and will not be shared. Please Allow to grant permission.",
                    "Allow",
                    "Deny",
                    {
                        saveIsDialogOpen(requireContext(), true)
                        getImage(requireActivity(), 2, false)

                    },
                    {

                    })
            } else {


                getImage(requireActivity(), 2, false)
            }
        }

        binding.ivDLBack.setOnClickListener {
            if(!opened){
                showAlertWithCancel(requireContext(),
                    "Zipzap Taxi Partner would like to access your camera and gallery to enable photos uploads. Your photos will only be use within the app and will not be shared. Please Allow to grant permission.","Allow","Deny",
                    {
                        saveIsDialogOpen(requireContext(),true)
                        getImage(requireActivity(), 3,false)

                    },{

                    })
            }else {
                getImage(requireActivity(), 3, false)
            }
        }
        binding.img.setOnClickListener {
            if(!opened){
                showAlertWithCancel(requireContext(),
                    "Zipzap Taxi Partner would like to access your camera and gallery to enable photos uploads. Your photos will only be use within the app and will not be shared. Please Allow to grant permission.","Allow","Deny",
                    {
                        saveIsDialogOpen(requireContext(),true)
                        getImage(requireActivity(), 4,false)

                    },{

                    })
            }else {
                getImage(requireActivity(), 4, false)
            }
        }
    }

    private fun addDriver() {

        if(isValid()){
            finalMap["name"]= binding.etName.text.toString()
            finalMap["phone"]= "+91"+binding.etPhone.text.toString()
            finalMap["email"]= binding.etEmail.text.toString()
            finalMap["aadhar_card_number"]= binding.etAadharNo.text.toString()
            finalMap["driving_license_number"]= binding.etLicNo.text.toString()
            finalMap["user_type"] = getUser(requireContext()).user_type.toString()
            driverViewModel.addUpdateDriverApi(requireActivity(),true,finalMap)
            driverViewModel.mResponse.observe(viewLifecycleOwner,this)

        }

    }
    private fun isValid(): Boolean {
        var check = false
        if (!mValidationClass.isNetworkConnected)
            AppUtils.showErrorAlert(requireActivity(), resources.getString(R.string.no_internet))
        else if (mValidationClass.checkStringNull(binding.etName.text.toString()))
            AppUtils.showErrorAlert(requireActivity(), resources.getString(R.string.error_name))
        else if (mValidationClass.checkStringNull(binding.etPhone.text.toString()))
            AppUtils.showErrorAlert(requireActivity(), resources.getString(R.string.error_phone))
        else if (!mValidationClass.validatePhoneNumber(binding.etPhone.text.toString()))
            AppUtils.showErrorAlert(requireActivity(), resources.getString(R.string.error_valid_phone))
        else if (mValidationClass.checkStringNull(binding.etAadharNo.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), resources.getString(R.string.error_aadhar))
        else if (mValidationClass.checkStringNull(binding.etLicNo.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), resources.getString(R.string.error_lic_no))
        else if (mAadharFPath.isNullOrEmpty() || mAadharBPath.isNullOrEmpty()|| mLicFPath.isNullOrEmpty()|| mLicBPath.isNullOrEmpty())
            AppUtils.showErrorAlert(requireActivity(), resources.getString(R.string.upload_documents))

        else
            check = true
        return check
    }


    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_addDriverFragment2_to_driverListFragment)
        }
        toolbarBinding.toolbarTitle.text= getString(R.string.add_driver)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is FileUploadResponse) {
                    val data: FileUploadResponse = value.data
                    if (data.code == AppConstant.success_code) {
                        finalMap[data.data.image_name] = data.data.image_path

                    }else{
                        AppUtils.showErrorAlert(requireActivity(), data.message)
                    }
                }
                else if (value.data is BaseResponseModel) {
                    val data: BaseResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        AppUtils.showSuccessAlert(
                            requireActivity(),
                            getString(R.string.driver_added_successfully), onBackPressed = true
                        )
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