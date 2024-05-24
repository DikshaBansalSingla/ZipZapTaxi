package com.zipzaptaxi.live.home.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.ActivityCarDetailsBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.FileUploadResponse
import com.zipzaptaxi.live.utils.ImagePickerFragment
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.prepareMultiPart
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.getCurrentMaxDate
import com.zipzaptaxi.live.utils.getDate
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.viewmodel.AuthViewModel
import com.zipzaptaxi.live.viewmodel.VehicleViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class CarDetailFragment : ImagePickerFragment(), Observer<RestObservable>,
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityCarDetailsBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding


    lateinit var mValidationClass: ValidationsClass
    private val hashMap = HashMap<String, RequestBody>()

    private val finalMap = HashMap<String, String>()
    private val viewModel: AuthViewModel
            by lazy { ViewModelProvider(this)[AuthViewModel::class.java] }
    private val vehicleViewModel: VehicleViewModel by lazy {
        VehicleViewModel()
    }

    private var mLicFrontPath = ""
    private var mLicBackPath = ""
    private var mCarLeftPath = ""
    private var mCarRightPath = ""
    private var mRcFrontPath = ""
    private var mRcBackPath = ""
    private var mInsPath = ""
    private var mPermitPath = ""
    private var mFitPath = ""
    private var mPucPath = ""
    private var mCabColor = ""
    private var mCabType = ""


    override fun selectedImage(imagePath: String?, code: Int) {
        var bodyimage: MultipartBody.Part? = null

        hashMap["image_of"] = mValidationClass.createPartFromString("car")
        if (imagePath != null && code == 0) {
            mLicFrontPath = imagePath
            bodyimage = prepareMultiPart("image", File(mLicFrontPath))

            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivLicFront)
            hashMap["image_name"] = mValidationClass.createPartFromString("car_front_image")
            uploadDocApi(bodyimage)
        } else if (imagePath != null && code == 1) {
            mLicBackPath = imagePath
            bodyimage = prepareMultiPart("image", File(mLicBackPath))
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivLicBack)

            hashMap["image_name"] = mValidationClass.createPartFromString("car_back_image")
            uploadDocApi(bodyimage)
        } else if (imagePath != null && code == 2) {
            mCarLeftPath = imagePath
            bodyimage = prepareMultiPart("image", File(mCarLeftPath))
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivCarLeft)
            hashMap["image_name"] = mValidationClass.createPartFromString("car_left_image")
            uploadDocApi(bodyimage)
        } else if (imagePath != null && code == 3) {
            mCarRightPath = imagePath
            bodyimage = prepareMultiPart("image", File(mCarRightPath))
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivCarRight)
            hashMap["image_name"] = mValidationClass.createPartFromString("car_right_image")
            uploadDocApi(bodyimage)
        } else if (imagePath != null && code == 4) {
            mRcFrontPath = imagePath
            bodyimage = prepareMultiPart("image", File(mRcFrontPath))
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivRcFront)
            hashMap["image_name"] = mValidationClass.createPartFromString("rc_front_image")
            uploadDocApi(bodyimage)
        } else if (imagePath != null && code == 5) {
            mRcBackPath = imagePath
            bodyimage = prepareMultiPart("image", File(mRcBackPath))
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivRcBack)
            hashMap["image_name"] = mValidationClass.createPartFromString("rc_back_image")
            uploadDocApi(bodyimage)
        } else if (imagePath != null && code == 6) {
            mInsPath = imagePath
            bodyimage = prepareMultiPart("image", File(mInsPath))
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivInsCert)
            hashMap["image_name"] = mValidationClass.createPartFromString("insurance_image")
            uploadDocApi(bodyimage)
        } else if (imagePath != null && code == 7) {
            mPermitPath = imagePath
            bodyimage = prepareMultiPart("image", File(mPermitPath))
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivWhiteCert)
            hashMap["image_name"] = mValidationClass.createPartFromString("permit_image")
            uploadDocApi(bodyimage)
        } else if (imagePath != null && code == 8) {
            mFitPath = imagePath
            bodyimage = prepareMultiPart("image", File(mFitPath))
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivFitCert)
            hashMap["image_name"] = mValidationClass.createPartFromString("fitness_image")
            uploadDocApi(bodyimage)
        } else if (imagePath != null && code == 9) {
            mPucPath = imagePath
            bodyimage = prepareMultiPart("image", File(mPucPath))
            Glide.with(this).load(imagePath).error(R.drawable.placeholder)
                .into(binding.ivPucCert)
            hashMap["image_name"] = mValidationClass.createPartFromString("pollution_image")
            uploadDocApi(bodyimage)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ActivityCarDetailsBinding.inflate(inflater, container, false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mValidationClass = ValidationsClass.getInstance()
        setToolbar()
        setOnClicks()

        val carColor = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Select_car_color,
            R.layout.spiner_layout_text
        )
        carColor.setDropDownViewResource(R.layout.spiner_layout_text)
        binding.spCabsColor.adapter = carColor

        val cabType = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Select_car_type,
            R.layout.spiner_layout_text
        )
        carColor.setDropDownViewResource(R.layout.spiner_layout_text)
        binding.spCabType.adapter = cabType
        binding.spCabsColor.onItemSelectedListener = this
        binding.spCabType.onItemSelectedListener = this

        setupCheckboxes()
    }

    private fun setupCheckboxes() {
        // Checkbox listeners
        binding.cbIsCng.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbIsDiesel.isChecked = false
                binding.cbIsElectric.isChecked = false
            }
        }

        binding.cbIsDiesel.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbIsCng.isChecked = false
                binding.cbIsElectric.isChecked = false
            }
        }

        binding.cbIsElectric.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbIsCng.isChecked = false
                binding.cbIsDiesel.isChecked = false
            }
        }
    }

    private fun uploadDocApi(bodyimage: MultipartBody.Part) {
        viewModel.fileUploadApi(requireActivity(), false, hashMap, bodyimage)
        viewModel.mResponse.observe(viewLifecycleOwner, this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClicks() {
        binding.etRegEndDate.setOnTouchListener { _, _ ->
            Log.d("Touch", "EditText clicked")
            getDate(binding.etRegEndDate, requireContext())
            return@setOnTouchListener false // Consume the _
        }
        binding.etRegStartDate.setOnTouchListener { _, _ ->
            Log.d("Touch", "EditText clicked")
            getCurrentMaxDate(binding.etRegStartDate, requireContext())
            return@setOnTouchListener false // Consume the _
        }

        binding.etTaxDate.setOnTouchListener { _, _ ->
            Log.d("Touch", "EditText clicked")
            getDate(binding.etTaxDate, requireContext())
            return@setOnTouchListener false // Consume the _

        }
        binding.etInsEndDate.setOnTouchListener { view, _ ->
            Log.d("Touch", "EditText clicked")
            getDate(binding.etInsEndDate, requireContext())
            return@setOnTouchListener false // Consume the _

        }
        binding.etWPermitEndDate.setOnTouchListener { view, _ ->
            Log.d("Touch", "EditText clicked")
            getDate(binding.etWPermitEndDate, requireContext())
            return@setOnTouchListener false // Consume the _

        }
        binding.etFitEndDate.setOnTouchListener { view, _ ->
            Log.d("Touch", "EditText clicked")
            getDate(binding.etFitEndDate, requireContext())
            return@setOnTouchListener false // Consume the _

        }
        binding.etPucEndDate.setOnTouchListener { view, _ ->
            Log.d("Touch", "EditText clicked")
            getDate(binding.etPucEndDate, requireContext())
            return@setOnTouchListener false // Consume the _

        }
        /* binding.etRegEndDate.setOnClickListener {
             getDate(binding.etRegEndDate, requireContext())
         }
         binding.etTaxDate.setOnClickListener {
             getDate(binding.etTaxDate, requireContext())
         }
         binding.etInsEndDate.setOnClickListener {
             getDate(binding.etInsEndDate, requireContext())
         }
         binding.etWPermitEndDate.setOnClickListener {
             getDate(binding.etWPermitEndDate, requireContext())
         }

         binding.etFitEndDate.setOnClickListener {
             getDate(binding.etFitEndDate, requireContext())
         }
         binding.etPucEndDate.setOnClickListener {
             getDate(binding.etPucEndDate, requireContext())
         }*/
        binding.ivLicFront.setOnClickListener {

            getImage(requireActivity(), 0, false)
        }
        binding.ivLicBack.setOnClickListener {
            getImage(requireActivity(), 1, false)
        }
        binding.ivCarLeft.setOnClickListener {
            getImage(requireActivity(), 2, false)
        }
        binding.ivCarRight.setOnClickListener {
            getImage(requireActivity(), 3, false)
        }
        binding.ivRcFront.setOnClickListener {
            getImage(requireActivity(), 4, false)
        }
        binding.ivRcBack.setOnClickListener {
            getImage(requireActivity(), 5, false)
        }
        binding.ivInsCert.setOnClickListener {
            getImage(requireActivity(), 6, false)
        }
        binding.ivWhiteCert.setOnClickListener {
            getImage(requireActivity(), 7, false)
        }
        binding.ivFitCert.setOnClickListener {
            getImage(requireActivity(), 8, false)
        }
        binding.ivPucCert.setOnClickListener {
            getImage(requireActivity(), 9, false)
        }
        binding.btnSubmit.setOnClickListener {
            if (isValid()) {
                finalMap["color"] = mCabColor
                finalMap["number_plate"] = binding.etVehicleNo.text.toString()
                finalMap["vehicle_model"] = binding.etModel.text.toString()
                finalMap["vehicle_year"] = binding.etYear.text.toString()
                finalMap["vehicle_owner_name"] = binding.etName.text.toString()
                finalMap["vehicle_owner_surname"] = binding.etSirName.text.toString()
                finalMap["registration_end_date"] = binding.etRegEndDate.text.toString()
                finalMap["registration_start_date"] = binding.etRegStartDate.text.toString()
                finalMap["tax_paid_until_date"] = binding.etTaxDate.text.toString()
                finalMap["insurance_expiry_date"] = binding.etInsEndDate.text.toString()
                finalMap["permit_expiry_date"] = binding.etWPermitEndDate.text.toString()
                finalMap["fitness_expiry_date"] = binding.etFitEndDate.text.toString()
                finalMap["pollution_expiry_date"] = binding.etPucEndDate.text.toString()
                finalMap["passenger"] = binding.etPass.text.toString()
                finalMap["cab_type"] = mCabType
                //finalMap["bags"] = binding.etBags.text.toString()
                if (binding.cbIsCng.isChecked) {

                    finalMap["is_cng"] = "1"
                } else {
                    finalMap["is_cng"] = "0"
                }

                if (binding.cbIsDiesel.isChecked) {

                    finalMap["is_diesel"] = "1"
                } else {
                    finalMap["is_diesel"] = "0"
                }

                if (binding.cbIsElectric.isChecked) {
                    binding.cbIsCng.isChecked = false
                    binding.cbIsDiesel.isChecked = false
                    finalMap["is_electric"] = "1"
                } else {
                    finalMap["is_electric"] = "0"
                }

                if (binding.cbIsRoofTop.isChecked) {
                    finalMap["has_roof_top"] = "1"
                } else {
                    finalMap["has_roof_top"] = "0"
                }

                vehicleViewModel.addUpdateVehicleApi(requireActivity(), true, finalMap)
                vehicleViewModel.mResponse.observe(viewLifecycleOwner, this)

            }
        }
    }


    private fun isValid(): Boolean {
        var check = false
        if (!mValidationClass.isNetworkConnected)
            AppUtils.showErrorAlert(requireActivity(), resources.getString(R.string.no_internet))
        else if (mValidationClass.checkStringNull(binding.etVehicleNo.text.toString()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter vehicle no")
        else if (binding.spCabsColor.selectedItemPosition == 0)
            AppUtils.showErrorAlert(requireActivity(), "Please enter vehicle color")
        else if (mValidationClass.checkStringNull(binding.etModel.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter vehicle Model")
        else if (mValidationClass.checkStringNull(binding.etYear.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter vehicle Year")
        else if (mValidationClass.checkStringNull(binding.etPass.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter Passengers")
        else if (binding.spCabType.selectedItemPosition == 0)
            AppUtils.showErrorAlert(requireActivity(), "Please select cab type")
        else if (mValidationClass.checkStringNull(binding.etName.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter vehicle Owner Name")
        else if (mValidationClass.checkStringNull(binding.etSirName.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter Owner Surname")
        else if (mValidationClass.checkStringNull(binding.etRegStartDate.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter Registration Start date")
        else if (mValidationClass.checkStringNull(binding.etRegEndDate.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter Registration end date")
        else if (mValidationClass.checkStringNull(binding.etTaxDate.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter Tax Paid date")
        else if (mValidationClass.checkStringNull(binding.etInsEndDate.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter Insurance Expiry date")
        else if (mValidationClass.checkStringNull(binding.etWPermitEndDate.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter Permit Expiry date")
        else if (mValidationClass.checkStringNull(binding.etFitEndDate.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter Fitness Expiry date")
        else if (mValidationClass.checkStringNull(binding.etPucEndDate.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter Pollution Expiry date")
        else if (mLicFrontPath.isNullOrEmpty() || mLicBackPath.isNullOrEmpty() ||
            mRcFrontPath.isNullOrEmpty() || mRcBackPath.isNullOrEmpty()
            || mInsPath.isNullOrEmpty() || mPermitPath.isNullOrEmpty()
            || mFitPath.isNullOrEmpty() || mPucPath.isNullOrEmpty()
        )
            AppUtils.showErrorAlert(
                requireActivity(),
                resources.getString(R.string.upload_documents)
            )
        else
            check = true
        return check
    }


    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {

            (requireActivity()).supportFragmentManager.popBackStackImmediate()
        }
        toolbarBinding.toolbarTitle.text = getString(R.string.car_detail)

    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is FileUploadResponse) {
                    val data: FileUploadResponse = value.data
                    if (data.code == AppConstant.success_code) {

                        finalMap[data.data.image_name] = data.data.image_path

                    } else {
                        AppUtils.showErrorAlert(requireActivity(), data.message)
                    }
                } else if (value.data is BaseResponseModel) {
                    val data: BaseResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        AppUtils.showSuccessAlert(
                            requireActivity(),
                            "Car added Successfully"
                        )

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

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.d("SpinnerSelection", "Selected position: $p2")
        if (p0?.id == R.id.spCabsColor) {
            val array = this.resources.getStringArray(R.array.Select_car_color)
            mCabColor = array[p2]
            Log.d("SpinnerSelection", "Cabs Color selected: $mCabColor")
        } else if (p0?.id == R.id.spCabType) {
            val array = this.resources.getStringArray(R.array.Select_car_type)
            mCabType = array[p2]
            Log.d("SpinnerSelection", "Cab Type selected: $mCabType")
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}