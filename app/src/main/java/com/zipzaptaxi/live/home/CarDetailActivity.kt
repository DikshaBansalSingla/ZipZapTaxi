package com.zipzaptaxi.live.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.ActivityCarDetailsBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.home.VehicleListFragment
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.VehicleDetailResponse
import com.zipzaptaxi.live.model.VehicleListResponse
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.isVisible
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.getDate
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.viewmodel.VehicleViewModel

class CarDetailActivity : AppCompatActivity(), Observer<RestObservable> {

    val viewModel: VehicleViewModel by lazy {
        VehicleViewModel()
    }
    var id = 0
    var status = ""
    var from = ""
    private lateinit var binding: ActivityCarDetailsBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    private lateinit var mValidationClass: ValidationsClass
    private var jsonData: VehicleListResponse.Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarDetailsBinding.inflate(layoutInflater)
        toolbarBinding = binding.appToolbar
        setContentView(binding.root)
        mValidationClass = ValidationsClass.getInstance()

        setToolbar()
        val bundle= intent.extras
        id = bundle?.getInt("id")!!
        status = bundle.getString("status")!!
        from = bundle.getString("from")!!
        setAdapter()
        getVehicleDetail(id)
        setOnClicks()
    }

    private fun setAdapter() {
        // Inside onCreate or wherever appropriate
        val carColors: Array<String> = resources.getStringArray(R.array.Select_car_color)
        val carColorsAdapter = ArrayAdapter(this, R.layout.spiner_layout_text, carColors)
        carColorsAdapter.setDropDownViewResource(R.layout.spiner_layout_text)
        binding.spCabsColor.adapter = carColorsAdapter

        val carTypes: Array<String> = resources.getStringArray(R.array.Select_car_type)
        val carTypesAdapter = ArrayAdapter(this, R.layout.spiner_layout_text, carTypes)
        carTypesAdapter.setDropDownViewResource(R.layout.spiner_layout_text)
        binding.spCabType.adapter = carTypesAdapter

    }

    private fun getVehicleDetail(id: Int) {
        viewModel.vehicleDetailApi(this, true, id)
        viewModel.mResponse.observe(this, this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClicks() {

        if (status == "pending") {
            binding.etInsEndDate.isEnabled = false
            binding.etWPermitEndDate.isEnabled = false
            binding.etFitEndDate.isEnabled = false
            binding.etPucEndDate.isEnabled = false
            binding.etTaxDate.isEnabled = false
            binding.etRegEndDate.isEnabled = false
        } else {
            binding.etInsEndDate.isEnabled = true
            binding.etWPermitEndDate.isEnabled = true
            binding.etFitEndDate.isEnabled = true
            binding.etPucEndDate.isEnabled = true
            binding.etTaxDate.isEnabled = true
            binding.etRegEndDate.isEnabled = true
        }
        binding.spCabsColor.isClickable = false
        binding.spCabsColor.isEnabled = false
        binding.spCabType.isClickable = false
        binding.spCabType.isEnabled = false
        binding.etVehicleNo.isEnabled = false

        binding.etModel.isEnabled = false
        binding.etYear.isEnabled = false
        binding.etPass.isEnabled = false
        binding.etName.isEnabled = false
        binding.etSirName.isEnabled = false

        binding.cbIsCng.isClickable = false
        binding.cbIsPetrol.isClickable = false
        binding.cbIsDiesel.isClickable = false
        binding.cbIsElectric.isClickable = false
        binding.cbIsRoofTop.isClickable = false
        binding.ivRcFront.isClickable = false
        binding.ivRcBack.isClickable = false
        binding.ivCarLeft.isClickable = false
        binding.ivCarRight.isClickable = false
        binding.ivLicFront.isClickable = false
        binding.ivLicBack.isClickable = false

        if (status != "pending") {
            binding.etTaxDate.setOnTouchListener { _, _ ->
                Log.d("Touch", "EditText clicked")
                getDate(binding.etTaxDate, this)
                return@setOnTouchListener false // Consume the event

            }
            binding.etInsEndDate.setOnTouchListener { _, _ ->
                getDate(binding.etInsEndDate, this)
                return@setOnTouchListener false // Consume the event

            }
            binding.etWPermitEndDate.setOnTouchListener { _, _ ->
                getDate(binding.etWPermitEndDate, this)
                return@setOnTouchListener false // Consume the event

            }
            binding.etFitEndDate.setOnTouchListener { _, _ ->
                getDate(binding.etFitEndDate, this)
                return@setOnTouchListener false // Consume the event

            }
            binding.etPucEndDate.setOnTouchListener { _, _ ->
                getDate(binding.etPucEndDate, this)
                return@setOnTouchListener false // Consume the event

            }

        }


        binding.ivLicFront.setOnClickListener {
            openImagePopUp(jsonData?.car_front_image, this)
        }
        binding.ivLicBack.setOnClickListener {
            openImagePopUp(jsonData?.car_back_image, this)

        }

        if (jsonData?.car_left_image.isNullOrEmpty()) {
            binding.tvCarLeft.isGone()
            binding.ivCarLeft.isGone()

        } else {
            binding.ivCarLeft.setOnClickListener {
                openImagePopUp(jsonData?.car_left_image, this)
            }
        }
        if (jsonData?.car_right_image.isNullOrEmpty()) {
            binding.tvCarRight.isGone()
            binding.ivCarRight.isGone()
        } else {
            binding.ivCarRight.setOnClickListener {
                openImagePopUp(jsonData?.car_right_image, this)
            }
        }

        binding.ivRcFront.setOnClickListener {
            openImagePopUp(jsonData?.rc_front_image, this)

        }
        binding.ivRcBack.setOnClickListener {
            openImagePopUp(jsonData?.rc_back_image, this)

        }
        binding.ivInsCert.setOnClickListener {
            openImagePopUp(jsonData?.insurance_image, this)

        }
        binding.ivWhiteCert.setOnClickListener {
            openImagePopUp(jsonData?.permit_image, this)

        }

        binding.ivFitCert.setOnClickListener {
            openImagePopUp(jsonData?.fitness_image, this)

        }
        binding.ivPucCert.setOnClickListener {
            openImagePopUp(jsonData?.pollution_image, this)

        }

        binding.btnSubmit.setOnClickListener {
            val finalMap = HashMap<String, String>()
            if (isValid()) {

                finalMap["number_plate"] = binding.etVehicleNo.text.toString()
                finalMap["id"] = id.toString()

                finalMap["tax_paid_until_date"] = binding.etTaxDate.text.toString()
                finalMap["insurance_expiry_date"] = binding.etInsEndDate.text.toString()
                finalMap["permit_expiry_date"] = binding.etWPermitEndDate.text.toString()
                finalMap["fitness_expiry_date"] = binding.etFitEndDate.text.toString()
                finalMap["pollution_expiry_date"] = binding.etPucEndDate.text.toString()

                viewModel.addUpdateVehicleApi(this, true, finalMap)
                viewModel.mResponse.observe(this, this)

            }
        }
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {

            this.onBackPressed()
        }
        toolbarBinding.toolbarTitle.text = getString(R.string.car_detail)

    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is VehicleDetailResponse) {
                    val data: VehicleDetailResponse = value.data
                    if (data.code == AppConstant.success_code) {
                        jsonData = data.data
                        setData(data.data)
                    }
                } else if (value.data is BaseResponseModel) {
                    val data: BaseResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        AppUtils.showSuccessAlert(
                            this,
                            getString(R.string.car_updated_successfully)
                        )
                        finish()


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

    private fun navigateToFragment() {
        val fragment = VehicleListFragment() // Instantiate your fragment
        val container = findViewById<ConstraintLayout>(R.id.llMain) // Replace with the ID of your container
        container.removeAllViews() // Remove any existing views from the container
        container.addView(fragment.view) // Add the fragment's view to the container
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

    private fun setData(data: VehicleListResponse.Data) {
        if(from=="edit"){
            if (status == "pending") {
                binding.btnSubmit.isGone()
            } else {
                binding.btnSubmit.isVisible()
                binding.btnSubmit.text = getString(R.string.update)
            }

        }else{
            binding.btnSubmit.isGone()
        }


        binding.etVehicleNo.setText(data.number_plate)
        //binding.etVehicleColor.setText(data.color)
        binding.etModel.setText(data.vehicle_model)
        binding.etYear.setText(data.vehicle_year)
        binding.etPass.setText(data.passenger)
        //  binding.etBags.setText(data.bags)
        binding.etName.setText(data.vehicle_owner_name)
        binding.etSirName.setText(data.vehicle_owner_surname)
        binding.etRegEndDate.setText(data.registration_end_date)
        binding.etTaxDate.setText(data.tax_paid_until_date)
        binding.etInsEndDate.setText(data.insurance_expiry_date)
        binding.etWPermitEndDate.setText(data.permit_expiry_date)
        binding.etFitEndDate.setText(data.fitness_expiry_date)
        binding.etPucEndDate.setText(data.pollution_expiry_date)
        val carColors: Array<String> = resources.getStringArray(R.array.Select_car_color)
        for (i in carColors.indices) {
            if (data.color.equals(carColors[i], ignoreCase = true)) {
                binding.spCabsColor.setSelection(i)
                break
            }
        }

        val carTypes: Array<String> = resources.getStringArray(R.array.Select_car_type)
        for (i in carTypes.indices) {
            if (data.cab_type.equals(carTypes[i], ignoreCase = true)) {
                binding.spCabType.setSelection(i)
                break
            }
        }

        binding.cbIsCng.isChecked = data.is_cng != "0"
        binding.cbIsDiesel.isChecked = data.is_diesel != "0"
        binding.cbIsElectric.isChecked = data.is_electric != "0"
        binding.cbIsRoofTop.isChecked = data.has_roof_top != "0"

        Glide.with(this).load(data.car_front_image).into(binding.ivLicFront)
        Glide.with(this).load(data.car_back_image).into(binding.ivLicBack)
        Glide.with(this).load(data.car_left_image).into(binding.ivCarLeft)
        Glide.with(this).load(data.car_right_image).into(binding.ivCarRight)
        Glide.with(this).load(data.rc_front_image).into(binding.ivRcFront)
        Glide.with(this).load(data.rc_back_image).into(binding.ivRcBack)
        Glide.with(this).load(data.insurance_image).into(binding.ivInsCert)
        Glide.with(this).load(data.permit_image).into(binding.ivWhiteCert)
        Glide.with(this).load(data.fitness_image).into(binding.ivFitCert)
        Glide.with(this).load(data.pollution_image).into(binding.ivPucCert)

    }

    private fun isValid(): Boolean {
        var check = false
        if (!mValidationClass.isNetworkConnected)
            AppUtils.showErrorAlert(this, resources.getString(R.string.no_internet))
        else if (mValidationClass.checkStringNull(binding.etTaxDate.text.toString().trim()))
            AppUtils.showErrorAlert(this, getString(R.string.please_enter_tax_paid_date))
        else if (mValidationClass.checkStringNull(binding.etInsEndDate.text.toString().trim()))
            AppUtils.showErrorAlert(this, getString(R.string.please_enter_insurance_expiry_date))
        else if (mValidationClass.checkStringNull(binding.etWPermitEndDate.text.toString().trim()))
            AppUtils.showErrorAlert(this, getString(R.string.please_enter_permit_expiry_date))
        else if (mValidationClass.checkStringNull(binding.etFitEndDate.text.toString().trim()))
            AppUtils.showErrorAlert(this, getString(R.string.please_enter_fitness_expiry_date))
        else if (mValidationClass.checkStringNull(binding.etPucEndDate.text.toString().trim()))
            AppUtils.showErrorAlert(this, getString(R.string.please_enter_pollution_expiry_date))
        else
            check = true
        return check
    }
}
