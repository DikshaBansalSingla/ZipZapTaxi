package com.zipzaptaxi.live.home.home

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.cache.saveUser
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentProfileBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.FileUploadResponse
import com.zipzaptaxi.live.model.LoginResponseModel
import com.zipzaptaxi.live.utils.ImagePickerFragment
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.prepareMultiPart
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.extensionfunctions.toast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.viewmodel.AuthViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class ProfileFragment : ImagePickerFragment(), Observer<RestObservable> {

    lateinit var binding: FragmentProfileBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    private val AUTOCOMPLETE_REQUEST_CODE = 1011
    private var mLatitude: String=""
    private var mLongitude: String=""
    lateinit var data:LoginResponseModel.Data
    private var mProfileImagePath = ""
    private lateinit var mValidationClass: ValidationsClass

    val viewModel:AuthViewModel by lazy {
        AuthViewModel()
    }

    val map = HashMap<String, RequestBody>()
    private val imageMap= HashMap<String,String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.map_key))
        }
        data= getUser(requireContext())
        setToolbar()
        mValidationClass= ValidationsClass.getInstance()
        setOnClicks()
        setData()

    }

    private fun setData() {
        Glide.with(requireContext()).load(data.profile_pic).error(R.drawable.profile_user).into(binding.img)
        binding.etName.setText(data.name)
        binding.etMail.setText(data.email)
        binding.etPhone.setText(data.phone)
        binding.etAddress.setText(data.address)
        binding.etCompAddress.setText(data.complete_address)
        binding.etRefCode.setText(data.self_referal_code)
    }

    private fun setOnClicks() {

        binding.img.setOnClickListener {
            getImage(requireActivity(),0,false)
        }
        binding.etAddress.setOnClickListener {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.ADDRESS
            )
            val countryList : ArrayList<String> = ArrayList()
            countryList.add("IN")
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountries(countryList)
                .build(requireContext())
            locationLauncher.launch(intent)
        }

        binding.btnUpdate.setOnClickListener {
            if(binding.etAddress.text.toString().isNullOrEmpty()){
                AppUtils.showErrorAlert(requireActivity(),"Please enter your address")
            }else{
                imageMap["address"]= binding.etAddress.text.toString()
                imageMap["complete_address"]= binding.etCompAddress.text.toString()
                viewModel.updateProfileApi(requireActivity(),true,imageMap)
            }
        }
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }
        toolbarBinding.toolbarTitle.text= getString(R.string.profile)
    }

    override fun selectedImage(imagePath: String?, code: Int) {
        var bodyimage: MultipartBody.Part? = null

        map["image_of"] = mValidationClass.createPartFromString("vendor_profile")
        if (imagePath != null && code==0) {
            mProfileImagePath = imagePath
            bodyimage = prepareMultiPart("image", File(mProfileImagePath))

            map["image_name"] = mValidationClass.createPartFromString("profile_pic")
            uploadDocApi(bodyimage)
            Glide.with(this).load(imagePath).error(R.drawable.placeholder).into(binding.img)
        }
    }
    private fun uploadDocApi(bodyImage: MultipartBody.Part) {
        viewModel.fileUploadApi(requireActivity(),true,map,bodyImage)
        viewModel.mResponse.observe(this, this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {

                try {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    mLatitude=place.latLng!!.latitude.toString()
                    mLongitude=place.latLng!!.longitude.toString()
                    Log.e("latLongCheck",place.latLng!!.latitude.toString()+"=="+place.latLng!!.longitude.toString())
                    binding.etAddress.setText( place.name)

                } catch (e: Exception) {
                    Log.e("error",e.printStackTrace().toString())
                }
            }
        }
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is LoginResponseModel) {
                    val data: LoginResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        saveUser(requireContext(), data.data)
                        AppUtils.showSuccessAlert(
                            requireActivity(),
                            "Profile Updated Successfully"
                        )
                        findNavController().navigate(R.id.action_profileFragment_to_homeFragment)

                    }
                }else if (value.data is FileUploadResponse) {
                    val data: FileUploadResponse = value.data
                    if (data.code == AppConstant.success_code) {
                        imageMap[data.data.image_name] = data.data.image_path

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
    private val locationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(result.data!!)

            Log.d("Place: ", place.name!!.toString() + " " + place.address)
            Log.d(ContentValues.TAG, "locationGet: " + place.name)

            Log.d(ContentValues.TAG, "locationGet: ${place.plusCode}")

                binding.etAddress.setText(place.address)


        } else {
            println(resources.getString(R.string.something_went_wrong_while_getting_locations))
        }
    }


}