package com.zipzaptaxi.live.home.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.databinding.LayoutUserVerificationBinding
import com.zipzaptaxi.live.bookings.BookingDetail
import com.zipzaptaxi.live.cache.saveIsDialogOpen
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.model.FileUploadResponse
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.prepareMultiPart
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.utils.showAlertWithCancel
import com.zipzaptaxi.live.viewmodel.AuthViewModel
import com.zoptal.vpn.util.hideKeyBoard
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

class GetOtpFragment(
    private val bookingDetail: BookingDetail,
    private val rideType: String,
    private val otp: String,
    private val opened: Boolean
) :
    BottomSheetDialogFragment(), Observer<RestObservable> {

    private lateinit var binding: LayoutUserVerificationBinding

    private lateinit var validationsClass: ValidationsClass

    val map = HashMap<String, RequestBody>()
    val hashMap= HashMap<String,String>()

    private val viewModel: AuthViewModel
            by lazy { ViewModelProvider(this)[AuthViewModel::class.java] }

    var mImagePath = ""
    var mSelfie=""
    var imgType=""
    private lateinit var mImageFile: File

    private val imageCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = Uri.fromFile(mImageFile)
                val picturePath = getAbsolutePath(uri)
                compressImage(File(picturePath))

                // selectedImage(picturePath, mCode)
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = LayoutUserVerificationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)

        validationsClass = ValidationsClass.getInstance()
        if (rideType == "start") {

            binding.tvExtraMins.isGone()
            binding.etExtraMins.isGone()
            binding.tvOtherCharges.isGone()
            binding.etOtherCharges.isGone()
            binding.btnStart.setOnClickListener {


                if(binding.pinView.text.toString().trim()!=otp){
                    AppUtils.showErrorAlert(requireActivity(), "Wrong OTP please re-enter the otp")
                    binding.pinView.text?.clear()
                    return@setOnClickListener
                }

                if (!validationsClass.isNetworkConnected) {
                    AppUtils.showErrorAlert(requireActivity(), resources.getString(R.string.no_internet))
                } else if (binding.pinView.text.toString().trim().length < 4) {
                    AppUtils.showErrorAlert(
                        requireActivity(),
                        resources.getString(R.string.valid_otp)
                    )
                } else if (mImagePath.isNullOrEmpty()) {
                    AppUtils.showErrorAlert(
                        requireActivity(), resources.getString(R.string.please_upload_odometer_img)
                    )
                } else if (mSelfie.isNullOrEmpty()) {
                    AppUtils.showErrorAlert(
                        requireActivity(),"Please upload your image"
                    )
                }else if(binding.etReading.text.toString().isNullOrEmpty()){
                    AppUtils.showErrorAlert(
                        requireActivity(), getString(R.string.please_enter_odometer_reading)
                    )
                }

                else {
                    hideKeyBoard(requireContext(), requireView())
                    bookingDetail.startRide(binding.pinView.text.toString(),hashMap,binding.etReading.text.toString())
                    dialog?.dismiss()
                }
            }
        } else {
            binding.btnStart.text = getString(R.string.end_trip)
            binding.tvTitle.isGone()
            binding.pinView.isGone()
            binding.tvSelfie.isGone()
            binding.ivSelfie.isGone()
            binding.tvDesc.text = "You need to upload the image to end the ride"
            binding.btnStart.setOnClickListener {
                if (mImagePath.isNullOrEmpty()) {
                    AppUtils.showErrorAlert(
                        requireActivity(),
                        resources.getString(R.string.please_upload_odometer_img)
                    )
                } else if(binding.etReading.text.toString().isNullOrEmpty()){
                    AppUtils.showErrorAlert(
                        requireActivity(),
                        resources.getString(R.string.please_enter_odometer_reading)
                    )
                }
                else {
                    hideKeyBoard(requireContext(), requireView())
                    bookingDetail.endTrip(hashMap,binding.etReading.text.toString(),binding.etExtraMins.text.toString(),binding.etOtherCharges.text.toString())
                    dialog?.dismiss()
                }
            }
        }
        binding.ivOdometer.setOnClickListener {

            if(!opened){
                showAlertWithCancel(requireContext(),
                    "Zipzap Taxi Partner would like to access your camera and gallery to enable photos uploads. Your photos will only be use within the app and will not be shared. Please Allow to grant permission.","Allow","Deny",
                    {
                        saveIsDialogOpen(requireContext(),true)

                    },{

                    })
            }else if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted, proceed with camera-related tasks
                captureImage("odometer")
            } else {
                // Permission not granted, request camera permission
                AppUtils.showErrorAlert(requireActivity(),"Please give camera permission")
            }

        }
        binding.ivSelfie.setOnClickListener {
            if(!opened){
                showAlertWithCancel(requireContext(),
                    "Zipzap Taxi Partner would like to access your camera and gallery to enable photos uploads. Your photos will only be use within the app and will not be shared. Please Allow to grant permission.","Allow","Deny",
                    {
                        saveIsDialogOpen(requireContext(),true)

                    },{

                    })
            } else if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted, proceed with camera-related tasks
                captureImage("selfie")
            } else {
                // Permission not granted, request camera permission
                AppUtils.showErrorAlert(requireActivity(),
                    getString(R.string.please_give_camera_permission))
            }

        }

        binding.ivCross.setOnClickListener {
            dialog?.dismiss()
        }

    }

    private fun uploadDocApi(bodyimage: MultipartBody.Part) {
        viewModel.fileUploadApi(requireActivity(),true,map,bodyimage)
        viewModel.mResponse.observe(this, this)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is FileUploadResponse) {
                    val data: FileUploadResponse = value.data
                    if (data.code == AppConstant.success_code) {
                        hashMap[data.data.image_name] = data.data.image_path

                    }else{
                        AppUtils.showErrorAlert(requireActivity(), data.message)
                    }
                }
            }
            else -> {}
        }

    }

    /***
     * code to compress the image
     */
    private fun compressImage(imageFile: File) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val compressedImageFile = Compressor.compress(requireContext(), imageFile)
                withContext(Dispatchers.Main) {
                    val compressedImagePath = compressedImageFile.absolutePath

                    var bodyimage: MultipartBody.Part? = null

                    map["image_of"] = validationsClass.createPartFromString("odometer")
                    if (compressedImagePath != null) {

                        if(imgType=="odometer") {
                            mImagePath = compressedImagePath
                            bodyimage = prepareMultiPart("image", File(mImagePath))


                            if (rideType == "start") {
                                map["image_name"] =
                                    validationsClass.createPartFromString("odometer_start")
                            } else {
                                map["image_name"] =
                                    validationsClass.createPartFromString("odometer_end")
                            }

                        }else{
                            mSelfie = compressedImagePath
                            bodyimage = prepareMultiPart("image", File(mSelfie))

                            map["image_name"] = validationsClass.createPartFromString("driver_selfie")
                        }

                        uploadDocApi(bodyimage)
                        if(imgType=="odometer"){
                            Glide.with(requireContext()).load(compressedImagePath).error(R.drawable.placeholder).into(binding.ivOdometer)

                        }else{
                            Glide.with(requireContext()).load(compressedImagePath).error(R.drawable.placeholder).into(binding.ivSelfie)

                        }

                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle compression error
            }
        }
    }

    //------------------------Return Uri file to String Path ------------------//
    @SuppressLint("Recycle")
    private fun getAbsolutePath(uri: Uri): String {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf("_data")
            val cursor: Cursor?
            try {
                cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
                val columnIndex = cursor!!.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    return cursor.getString(columnIndex)
                }
            } catch (e: Exception) {
                // Eat it
                e.printStackTrace()
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path!!
        }
        return ""
    }

    private fun captureImage(type: String) {
        imgType=type
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        try {
            createImageFile(requireContext(), imageFileName, ".jpg")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val fileUri = FileProvider.getUriForFile(
            Objects.requireNonNull(requireContext()), "com.zipzaptaxi.live" + ".provider",
            mImageFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        imageCameraLauncher.launch(intent)
    }
    @Throws(IOException::class)
    fun createImageFile(context: Context, name: String, extension: String) {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        mImageFile = File.createTempFile(
            name,
            extension,
            storageDir
        )
    }
}