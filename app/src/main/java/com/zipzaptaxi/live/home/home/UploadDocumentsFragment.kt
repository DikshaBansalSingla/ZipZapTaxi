package com.zipzaptaxi.live.home.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.cache.getToken
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.ActivityUploadDocumentsBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.DocResponseModel
import com.zipzaptaxi.live.model.FileUploadResponse
import com.zipzaptaxi.live.utils.ImagePickerFragment
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.prepareMultiPart
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.utils.helper.AppUtils.Companion.showErrorAlert
import com.zipzaptaxi.live.viewmodel.AuthViewModel
import com.zipzaptaxi.live.viewmodel.DocsViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import java.io.File


class UploadDocumentsFragment : ImagePickerFragment(), Observer<RestObservable> {

    private var mAadharFPath = ""
    private var mAadharBPath = ""
    private var mPanFPath = ""
    private var mPanBPath = ""

    private var mDlFrontPath = ""
    private var mDlBackPath = ""
    private var mCertPath = ""

    private lateinit var mValidationClass: ValidationsClass
    private var jsonArray = JSONArray()

    var verified=0
    private var docData:DocResponseModel.Data?=null

    private val viewModel: AuthViewModel
            by lazy { ViewModelProvider(this)[AuthViewModel::class.java] }


    private val docViewModel: DocsViewModel
            by lazy { ViewModelProvider(this)[DocsViewModel::class.java] }

    private lateinit var binding: ActivityUploadDocumentsBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    val map = HashMap<String, RequestBody>()
    private val imageMap= HashMap<String,String>()

    override fun selectedImage(imagePath: String?, code: Int) {
        var bodyimage: MultipartBody.Part? = null

        map["image_of"] = mValidationClass.createPartFromString("vendor")
        if (imagePath != null && code==0) {
            mAadharFPath = imagePath
            bodyimage = prepareMultiPart("image", File(mAadharFPath))

            map["image_name"] = mValidationClass.createPartFromString("aadhar_card_front")
            uploadDocApi(bodyimage)
            Glide.with(this).load(imagePath).error(R.drawable.placeholder).into(binding.ivAadhaarFront)


        }else if (imagePath != null && code==1) {
            mAadharBPath = imagePath
            bodyimage = prepareMultiPart("image", File(mAadharFPath))

            map["image_name"] = mValidationClass.createPartFromString("aadhar_card_back")
            uploadDocApi(bodyimage)
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivAadhaarBack)
        }else if (imagePath != null && code==2) {
            mPanFPath = imagePath
            bodyimage = prepareMultiPart("image", File(mAadharFPath))

            map["image_name"] = mValidationClass.createPartFromString("pan_card_front")
            uploadDocApi(bodyimage)

            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivPanFront)

        }else if (imagePath != null && code==3) {
            mPanBPath = imagePath
            bodyimage = prepareMultiPart("image", File(mPanBPath))

            map["image_name"] = mValidationClass.createPartFromString("pan_card_back")
            uploadDocApi(bodyimage)
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivPanBack)

        }/*else if (imagePath != null && code==4) {
            mVoterFPath = imagePath

            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivVoterFront)
        }else if (imagePath != null && code==5) {
            mVoterBPath = imagePath

            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivVoterBack)
        }*/else if (imagePath != null && code==4) {
            mDlFrontPath = imagePath
            bodyimage = prepareMultiPart("image", File(mDlFrontPath))

            map["image_name"] = mValidationClass.createPartFromString("driving_license_front")
            uploadDocApi(bodyimage)
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivDLFront)
        }else if (imagePath != null && code==5) {
            mDlBackPath = imagePath
            bodyimage = prepareMultiPart("image", File(mDlBackPath))

            map["image_name"] = mValidationClass.createPartFromString("driving_license_back")
            uploadDocApi(bodyimage)
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivDLBack)

        }else if (imagePath != null && code==6) {
            mCertPath = imagePath
            bodyimage = prepareMultiPart("image", File(mCertPath))

            map["image_name"] = mValidationClass.createPartFromString("police_verification")
            uploadDocApi(bodyimage)

            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivCert)
        }
    }

    private fun uploadDocApi(bodyimage: MultipartBody.Part) {
        viewModel.fileUploadApi(requireActivity(),true,map,bodyimage)
        viewModel.mResponse.observe(this, this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ActivityUploadDocumentsBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mValidationClass= ValidationsClass.getInstance()
        setToolbar()

//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//
//            (activity as? MainActivity)?.replaceFragment(HomeFragment(), true)
//        }

        Log.i("token====", getToken(requireContext())!!)
        getAllDocs()
        setOnClicks()
    }

    private fun getAllDocs() {
        docViewModel.getAllDocsApi(requireActivity(),true)
        docViewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }
        toolbarBinding.toolbarTitle.text= getString(R.string.documents)
    }

    private fun setOnClicks() {

        binding.ivAadhaarFront.setOnClickListener {
            if (verified == 1 || verified == 2) {
                openImagePopUp(docData?.aadhar_card_front, requireContext())
            } else {
                getImage(requireActivity(), 0, false)
            }
        }
        binding.ivAadhaarBack.setOnClickListener {

            if (verified == 1 || verified == 2) {
                openImagePopUp(docData?.aadhar_card_back, requireContext())
            } else {
                getImage(requireActivity(), 1, false)
            }
        }
        binding.ivPanFront.setOnClickListener {
                if (verified == 1 || verified == 2) {
                    openImagePopUp(docData?.pan_card_front, requireContext())
                } else {
                    getImage(requireActivity(), 2, false)
                }
        }
        binding.ivPanBack.setOnClickListener {

                if (verified == 1 || verified == 2) {
                    openImagePopUp(docData?.pan_card_back, requireContext())
                } else {
                    getImage(requireActivity(), 3, false)
                }
        }
            /* binding.ivVoterFront.setOnClickListener {

             getImage(requireActivity(), 4)
         }
         binding.ivVoterBack.setOnClickListener {

             getImage(requireActivity(), 5)
         }*/
        binding.ivDLFront.setOnClickListener {
                if (verified == 1 || verified == 2) {
                    openImagePopUp(docData?.driving_license_front, requireContext())
                } else {
                    getImage(requireActivity(), 4, false)
                }
        }
        binding.ivDLBack.setOnClickListener {
                if (verified == 1 || verified == 2) {
                    openImagePopUp(docData?.driving_license_back, requireContext())
                } else {
                    getImage(requireActivity(), 5, false)
                }
        }
        binding.ivCert.setOnClickListener {
                if (verified == 1 || verified == 2) {
                    openImagePopUp(docData?.police_verification, requireContext())
                } else {
                    getImage(requireActivity(), 6, false)

                }
        }

        binding.btnNext.setOnClickListener {
                if (mAadharFPath.isNullOrEmpty() || mAadharBPath.isNullOrEmpty() || mPanFPath.isNullOrEmpty() ||
                    mPanBPath.isNullOrEmpty() || mDlFrontPath.isNullOrEmpty() || mDlBackPath.isNullOrEmpty()
                ) {
                    showErrorAlert(requireActivity(), "Please upload all the documents")
                } else {
                    uploadAllDocs()
                }
        }
    }

    private fun uploadAllDocs() {
        Log.i("map",imageMap.toString())
        docViewModel.uploadDocsApi(requireActivity(),true,imageMap)
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
                else if (value.data is BaseResponseModel) {
                    val data: BaseResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                       AppUtils.showSuccessAlert(requireActivity(),"Documents Uploaded Successfully")
                        findNavController().navigate(R.id.action_uploadDocumentsFragment_to_homeFragment)

                    }
                }
                else if (value.data is DocResponseModel) {
                    val data: DocResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        docData= data.data
                        if(data.data.verification.equals("approved")){
                            binding.tvDocStatus.setTextColor(resources.getColor(R.color.green))

                            binding.tvDocStatus.text= getString(R.string.documents_verified)

                            verified=2
                            setData(data.data)
                        }else if(data.data.verification.equals("pending")){
                           binding.tvDocStatus.setTextColor(resources.getColor(R.color.red))
                            binding.tvDocStatus.text=
                                getString(R.string.documents_verification_is_pending)

                            verified=1
                            setData(data.data)
                        }else if(data.data.verification.equals("rejected")){
                            binding.tvDocStatus.setTextColor(resources.getColor(R.color.red))
                            binding.tvDocStatus.text=
                                getString(R.string.rejected)
                            verified=0
                        }
                        else{
                            binding.tvDocStatus.isGone()
                            verified=0
                        }
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

    private fun setData(data: DocResponseModel.Data) {
        binding.btnNext.isGone()
        Glide.with(requireContext()).load(data.aadhar_card_front).into(binding.ivAadhaarFront)
        Glide.with(requireContext()).load(data.aadhar_card_back).into(binding.ivAadhaarBack)
        Glide.with(requireContext()).load(data.pan_card_front).into(binding.ivPanFront)
        Glide.with(requireContext()).load(data.pan_card_back).into(binding.ivPanBack)
        Glide.with(requireContext()).load(data.driving_license_front).into(binding.ivDLFront)
        Glide.with(requireContext()).load(data.driving_license_back).into(binding.ivDLBack)
        if(!data.police_verification.isNullOrEmpty()){
            Glide.with(requireContext()).load(data.police_verification).into(binding.ivCert)
        }else{
            Glide.with(requireContext()).load(R.drawable.placeholder).into(binding.ivCert)
        }

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