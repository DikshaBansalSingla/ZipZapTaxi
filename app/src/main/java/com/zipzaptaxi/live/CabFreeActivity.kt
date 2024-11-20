package com.zipzaptaxi.live

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.zipzaptaxi.live.adapter.CategoryCommercialAdapter
import com.zipzaptaxi.live.adapter.CategoryDriversAdapter
import com.zipzaptaxi.live.adapter.TimeAdapter
import com.zipzaptaxi.live.cache.CacheConstants
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.ActivityCabFreeBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.CabFreeData
import com.zipzaptaxi.live.model.CabsList
import com.zipzaptaxi.live.model.DriverList
import com.zipzaptaxi.live.model.TimeList
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.extensionfunctions.toast
import com.zipzaptaxi.live.utils.getCurrentMinDate
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.viewmodel.CabFreeViewModel

class CabFreeActivity : Fragment(), Observer<RestObservable> {

    val viewModel:CabFreeViewModel by lazy {
        CabFreeViewModel()
    }
    lateinit var mValidationClass:ValidationsClass
    private val listC: ArrayList<CabsList> = ArrayList()

    private val listD: ArrayList<DriverList> = ArrayList()
    private val listT: ArrayList<TimeList> = ArrayList()

    lateinit var binding: ActivityCabFreeBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding

    var cab_id="0"
    var driver_id="0"
    var time_id="0"
    private var click=0

    var jsonData: CabFreeData.Data? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ActivityCabFreeBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CacheConstants.Current = "cabFree"
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.map_key))
        }
        mValidationClass= ValidationsClass.getInstance()
        setToolbar()
        setOnClicks()
        getData()

        binding.spCabs.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    (view as? TextView)?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.black
                        )
                    )
                    val cabs = jsonData!!.cabs[binding.spCabs.selectedItemPosition - 1]
                    cab_id = cabs.id.toString()
                } else {
                    (view as? TextView)?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.black
                        )
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.spDrivers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    (view as? TextView)?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.black
                        )
                    )
                    val cabs = jsonData!!.drivers[binding.spDrivers.selectedItemPosition - 1]
                    driver_id = cabs.id.toString()
                } else {
                    (view as? TextView)?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.black
                        )
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.spTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    (view as? TextView)?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.black
                        )
                    )
                    val time = jsonData!!.time[binding.spTime.selectedItemPosition - 1]
                     time_id= time
                } else {
                    (view as? TextView)?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.black
                        )
                    )
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun getData() {
        viewModel.getCabFreeApi(requireActivity(),true)
        viewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClicks() {

        binding.etFromDate.setOnTouchListener { _, event ->
            Log.d("Touch", "EditText clicked")
            getCurrentMinDate(binding.etFromDate, requireContext())
            return@setOnTouchListener false // Consume the event
        }

        binding.etToDate.setOnTouchListener { view, event ->
            Log.d("Touch", "EditText clicked")
            getCurrentMinDate(binding.etToDate, requireContext())
            return@setOnTouchListener false // Consume the event

        }
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ADDRESS
        )
        binding.etFrom.setOnClickListener {
            click=0
            val countryList : ArrayList<String> = ArrayList()
            countryList.add("IN")
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountries(countryList)
                .build(requireContext())
            locationLauncher.launch(intent)
        }
        binding.etCityTo.setOnClickListener {
            click=1
            val countryList : ArrayList<String> = ArrayList()
            countryList.add("IN")
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountries(countryList)
                .build(requireContext())
            locationLauncher.launch(intent)
        }

        binding.btnSubmit.setOnClickListener {
            if(isValid()){
                val data= HashMap<String,String>()
                data["driver_id"]=driver_id
                data["cab_id"]=cab_id
                data["from"]=binding.etFrom.text.toString()
                data["to"]=binding.etCityTo.text.toString()
                data["from_date"]=binding.etFromDate.text.toString()
                data["to_date"]=binding.etFromDate.text.toString()
                data["time"]=time_id
                data["amount"]=binding.etAmount.text.toString()

                viewModel.postCabFreeApi(requireActivity(),true,data)
                viewModel.mResponse.observe(viewLifecycleOwner, this)
            }
        }
    }

    private fun isValid(): Boolean {
        var check = false
        if (!mValidationClass.isNetworkConnected)
            AppUtils.showErrorAlert(requireActivity(), resources.getString(R.string.no_internet))
        else if (cab_id == "0")
            AppUtils.showErrorAlert(requireActivity(), "Please select cab")
        else if (driver_id == "0")
            AppUtils.showErrorAlert(requireActivity(), "Please select driver")
        else if (mValidationClass.checkStringNull(binding.etFrom.text.toString()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter City")
        else if (mValidationClass.checkStringNull(binding.etCityTo.text.toString()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter city")
        else if (mValidationClass.checkStringNull(binding.etFromDate.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter date")
        else if (mValidationClass.checkStringNull(binding.etToDate.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter date")
        else if (time_id == "0")
            AppUtils.showErrorAlert(requireActivity(), "Please select time")
        else if (mValidationClass.checkStringNull(binding.etAmount.text.toString().trim()))
            AppUtils.showErrorAlert(requireActivity(), "Please enter amount")

        else
           check = true
        return check
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        toolbarBinding.toolbarTitle.text= getString(R.string.cab_free)

    }

    private val locationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(result.data!!)

            Log.d("Place: ", place.name!!.toString() + " " + place.address)
            Log.d(ContentValues.TAG, "locationGet: " + place.name)

            Log.d(ContentValues.TAG, "locationGet: ${place.plusCode}")

            if(click==0){
                binding.etFrom.setText(place.address)
            }else{
                binding.etCityTo.setText(place.address)
            }

        } else {
            print(resources?.getString(R.string.something_went_wrong_while_getting_locations)!!)
        }
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is CabFreeData) {
                    val data: CabFreeData = value.data
                    if (data.code == AppConstant.success_code) {
                        jsonData= data.data
                        setData(data.data)
                    }else{
                        AppUtils.showErrorAlert(requireActivity(), data.message)
                    }
                }else if (value.data is BaseResponseModel) {
                    val data: BaseResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        toast(data.message)
                     //  AppUtils.showSuccessAlert(requireActivity(),data.message)
                        findNavController().navigate(R.id.action_cabFreeActivity_to_homeFragment)

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

    private fun setData(data: CabFreeData.Data) {
        listC.clear()
        listC.add(CabsList(0, "0", "Select Cab"))
        if (data.cabs.isNotEmpty()) {
            for (i in 0 until data.cabs.size) {
                listC.add(
                    CabsList(
                        data.cabs[i].id, data.cabs[i].number,
                        data.cabs[i].vehicle_model
                    )
                )
            }
        }

        val cabListt = CategoryCommercialAdapter(requireActivity(), "Select Cab", listC)
        binding.spCabs.adapter = cabListt

        listD.clear()
        listD.add(DriverList(0, "Select Driver", ""))
        if (data.drivers.isNotEmpty()) {
            for (i in 0 until data.drivers.size) {
                listD.add(
                    DriverList(
                        data.drivers[i].id, data.drivers[i].name,
                        data.drivers[i].phone
                    )
                )
            }
        }

        val driverListt = CategoryDriversAdapter(requireActivity(), "Select Driver", listD)
        binding.spDrivers.adapter = driverListt

        val cabListT = CategoryCommercialAdapter(requireActivity(), "Select Cab", listC)
        binding.spCabs.adapter = cabListT

        listT.clear()
        listT.add(TimeList("Select Time"))
        if (data.time.isNotEmpty()) {
            for (i in 0 until data.time.size) {
                listT.add(TimeList (data.time[i].toString())
                )
            }
        }

        val timeList = TimeAdapter(requireActivity(), "Select Time", listT)
        binding.spTime.adapter = timeList
    }

}