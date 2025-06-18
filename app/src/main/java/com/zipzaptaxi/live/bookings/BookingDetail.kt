package com.zipzaptaxi.live.bookings

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.adapter.CategoryCommercialAdapter
import com.zipzaptaxi.live.adapter.CategoryDriversAdapter
import com.zipzaptaxi.live.cache.CacheConstants
import com.zipzaptaxi.live.cache.getIsDialogOpen
import com.zipzaptaxi.live.cache.getSaveString
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.ActivityBookingDetailBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.home.CancelBottomSheet
import com.zipzaptaxi.live.home.home.GetOtpFragment
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.BookingDetailResponse
import com.zipzaptaxi.live.model.CabsList
import com.zipzaptaxi.live.model.DriverList
import com.zipzaptaxi.live.model.EndRideResponseModel
import com.zipzaptaxi.live.model.EnterOtpResModel
import com.zipzaptaxi.live.model.StartRideResponseModel
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.isVisible
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.utils.showCompleteAlert
import com.zipzaptaxi.live.utils.showCustomAlert
import com.zipzaptaxi.live.utils.showCustomAlertWithCancel
import com.zipzaptaxi.live.viewmodel.BookingViewModel

class BookingDetail : Fragment(), Observer<RestObservable> {

    lateinit var binding: ActivityBookingDetailBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    private var booking_id = 0

    val viewModel: BookingViewModel by lazy {
        BookingViewModel()
    }
    private var latitude = ""
    private var longitude = ""
    var status: String = "active"

    private var accept= 0
    var on_the_way= 0
    var opened= false

    private val listC: ArrayList<CabsList> = ArrayList()

    var cab_id = "0"
    private val listD: ArrayList<DriverList> = ArrayList()

    var driver_id = "0"


    private var getOtpFragment: GetOtpFragment? = null
    private var cancelBottomSheet: CancelBottomSheet? = null

    var jsonData: BookingDetailResponse.Data? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for requireActivity() fragment
        binding = ActivityBookingDetailBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CacheConstants.Current = "bookingDetail"
        opened= getIsDialogOpen(requireContext())

        setToolbar()
        val bundle = arguments
        booking_id = bundle?.getInt("id")!!
        getData(booking_id)
        setSpinners()

        latitude= getSaveString(requireContext(), "Lat").toString()
        longitude= getSaveString(requireContext(), "Lang").toString()

      //  showToast("Location is:   Latitude: $latitude, Longitude: $longitude")


    }

    private fun setSpinners() {
        binding.spCabs.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    (view as? TextView)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    val cabs = jsonData!!.cabs[binding.spCabs.selectedItemPosition - 1]
                    cab_id = cabs.id.toString()
                } else {
                    (view as? TextView)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
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
                    (view as? TextView)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    val cabs = jsonData!!.drivers[binding.spDrivers.selectedItemPosition - 1]
                    driver_id = cabs.id.toString()
                } else {
                    (view as? TextView)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun setOnClicks() {
        Log.e("=======",jsonData?.booking_status.toString())
        binding.btnBook.setOnClickListener {
            if(jsonData?.documents==0){
                showCustomAlert(requireActivity(),
                    getString(R.string.please_upload_all_the_documents), getString(R.string.ok)) {
                    findNavController().navigate(R.id.action_bookingDetail_to_uploadDocumentsFragment)
                }

            }else if(jsonData?.documents==1){
                showCustomAlert(requireActivity(),
                    getString(R.string.your_documents_verification_is_pending),  getString(R.string.ok)) {

                }
            }else if(jsonData?.documents==3){
                showCustomAlert(requireActivity(),
                    getString(R.string.your_documents_are_rejected_kindly_upload_your_documents_again),  getString(R.string.ok)) {
                    findNavController().navigate(R.id.action_bookingDetail_to_uploadDocumentsFragment)
                }
            }else if(jsonData?.home_city==0){
                showCustomAlert(requireActivity(),
                    getString(R.string.please_add_home_city),  getString(R.string.ok)) {
                    findNavController().navigate(R.id.action_bookingDetail_to_addHomeCityFragment)
                }
            }

            /***
             * Cases to handle the status of booking
             */

            else if(jsonData?.booking_status == "active"){
                if(jsonData?.wallet_balance?.toInt()!! < jsonData?.min_balance?.toInt()!! && jsonData?.status=="active") {

                    Log.e("======", jsonData?.wallet_balance.toString() + jsonData?.min_balance)
                    showCustomAlert(
                        requireActivity(),
                        getString(R.string.low_balance_please_add_money_into_your_wallet),
                        getString(R.string.ok)
                    ) {
                        findNavController().navigate(R.id.action_bookingDetail_to_addMoneyActivity)
                    }

                } else if (jsonData?.status == "active") {
                if (cab_id == "0" || driver_id == "0") {
                    showCustomAlert(requireContext(),
                        getString(R.string.please_add_cab_or_driver_before_booking),  getString(R.string.ok)) {}
                } else {
                    accept= 1
                    val hashMap = HashMap<String, String>()
                    hashMap["booking_id"] = booking_id.toString()
                    hashMap["driver_id"] = driver_id
                    hashMap["cab_id"] = cab_id
                    hashMap["status"] = status
                    hashMap["user_type"] = getUser(requireContext()).user_type.toString()
                    viewModel.assignBookingApi(requireActivity(), true, hashMap)
                }
            } else if (jsonData?.booking_status == "active" ) {
                if(on_the_way==0){
                    showCustomAlert(requireContext(),
                        getString(R.string.you_cannot_start_your_ride_before_2_hours),
                        getString(R.string.ok)
                    ) {}
                }else {
                    showCustomAlertWithCancel(requireContext(),
                        getString(R.string.are_you_sure_you_going_to_pick_customer),
                        getString(R.string.ok),
                        "Cancel", {
                            val hashMap = HashMap<String, String>()
                            hashMap["booking_id"] = booking_id.toString()
                            hashMap["driver_id"] = driver_id
                            hashMap["cab_id"] = cab_id
                            hashMap["status"] = "on_the_way"
                            hashMap["user_type"] = getUser(requireContext()).user_type.toString()
                            viewModel.assignBookingApi(requireActivity(), true, hashMap)
                        }, {})
                }
            } }else if (jsonData?.booking_status == "on_the_way") {
                showCustomAlertWithCancel(requireContext(),
                    getString(R.string.are_you_sure_you_have_arrived_the_location),
                    getString(R.string.ok), "Cancel", {
                        val hashMap = HashMap<String, String>()
                        hashMap["booking_id"] = booking_id.toString()
                        hashMap["driver_id"] = driver_id
                        hashMap["cab_id"] = cab_id
                        hashMap["user_type"] = getUser(requireContext()).user_type.toString()
                        hashMap["status"] = "started"
                        viewModel.assignBookingApi(requireActivity(), true, hashMap)
                    }, {})


            } else if (jsonData?.booking_status == "started" && jsonData?.assigned_to_me == 1) {
                sendOtp()
            } else if (jsonData?.status == "assigned" && jsonData?.booking_status == "in_progress") {
                endTripDialog()
            }
        }

        binding.btnCancel.setOnClickListener {

            if(jsonData?.extra_km_driven==0){
                requireActivity().runOnUiThread {
                    cancelBottomSheet =
                        jsonData?.delete_message?.let { it1 ->
                            CancelBottomSheet(this,
                                it1
                            )
                        }
                    cancelBottomSheet?.show(requireActivity().supportFragmentManager, "dialog")
                }
            }else{
                val message= "If you cancel this ride then you have to pay ₹ "+jsonData?.penalty+"as a penalty"
                showCustomAlertWithCancel(requireContext(), message,
                    "OK", "Cancel", {
                        cancelRide()
                    },{})
            }
        }
    }
     fun cancelRide() {
         viewModel.cancelRideApi(requireActivity(), true, booking_id,getUser(requireContext()).user_type.toString())
     }

    private fun endTripDialog() {
        showCustomAlertWithCancel(requireContext(),
            getString(R.string.are_you_sure_you_want_to_end_this_trip),
            getString(R.string.ok), "Cancel", {
                requireActivity().runOnUiThread {
                    getOtpFragment = GetOtpFragment(this, "end", "1234",opened)
                    getOtpFragment?.show(requireActivity().supportFragmentManager, "dialog")
                }
            }, {})
    }

    fun endTrip(hashMap: HashMap<String, String>, metereading: String,extraMin:String,otherCharges:String) {

        hashMap["id"] = booking_id.toString()
        hashMap["lat"] = latitude
        hashMap["lang"] = longitude
        hashMap["odometer_end_reading"] = metereading
        hashMap["extra_min"] = extraMin
        hashMap["other_charges"] = otherCharges
        hashMap["user_type"] = getUser(requireContext()).user_type.toString()
        viewModel.endRideApi(requireActivity(), true, hashMap)
    }

    private fun sendOtp() {
        viewModel.enterOtpApi(requireActivity(), true, booking_id,getUser(requireContext()).user_type.toString())
    }

    private fun getData(booking_id: Int) {
        viewModel.bookingDetailApi(requireActivity(), true, booking_id, getUser(requireContext()).user_type!!)
        viewModel.mResponse.observe(viewLifecycleOwner, this)

    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
                activity?.onBackPressed()
        // findNavController().navigate(R.id.action_bookingDetail_to_homeFragment)
        }
        toolbarBinding.toolbarTitle.text = getString(R.string.booking_detail)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is BookingDetailResponse) {
                    val data: BookingDetailResponse = value.data
                    if (data.code == AppConstant.success_code) {
                        jsonData = data.data
                        setData(data.data)


                    } else {
                        AppUtils.showErrorAlert(requireActivity(), data.message)
                    }
                } else if (value.data is BaseResponseModel) {
                    val data: BaseResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        AppUtils.showSuccessAlert(requireActivity(), data.message, onBackPressed = false)
                        if(accept==1){
                            findNavController().navigate(R.id.action_bookingDetail_to_bookingsFragment)
                        }else{
                            getData(booking_id)
                        }
                    } else {
                        AppUtils.showErrorAlert(requireActivity(), data.message)
                    }
                } else if (value.data is EnterOtpResModel) {
                    val data: EnterOtpResModel = value.data
                    if (data.code == AppConstant.success_code) {
                        requireActivity().runOnUiThread {
                            getOtpFragment = GetOtpFragment(
                                this,
                                "start",
                                data.data.otp.toString(),
                                opened
                            )
                            getOtpFragment?.show(requireActivity().supportFragmentManager, "dialog")
                        }
                    }

                } else if (value.data is StartRideResponseModel) {
                    val data: StartRideResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        AppUtils.showSuccessAlert(
                            requireActivity(), data.message,
                            onBackPressed = true
                        )
                    } else {
                        AppUtils.showErrorAlert(requireActivity(), data.message)
                    }
                } else if (value.data is EndRideResponseModel) {
                    val data: EndRideResponseModel = value.data
                    if (data.code == AppConstant.success_code) {

                        showCompleteAlert(requireContext(),data.data, getString(R.string.ok)){
                            findNavController().navigate(R.id.action_bookingDetail_to_homeFragment)
                        }

                        /*if(jsonData?.penalty==0){
                            showCustomAlert(requireContext(),"Your trip has been completed!! Please collect ₹ "+jsonData?.price+"amount","OK") {
                                findNavController().navigate(R.id.action_bookingDetail_to_homeFragment)
                            }
                        }else{
                            val message= "Your trip has been completed!! You have ride "+jsonData?.extra_km_driven+
                                    "KM extra so your total amount is ₹ "+jsonData?.total_price_with_extra_km
                            showCustomAlert(requireContext(),message,"OK") {
                                findNavController().navigate(R.id.action_bookingDetail_to_homeFragment)
                            }
                        }*/

                    } else {
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

    private fun setData(data: BookingDetailResponse.Data) {

        setOnClicks()
        status = data.booking_status

        binding.tvTripType.text = data.trip
        binding.tvTripId.text = data.booking_unique_id
        binding.tvTripFrom.text = data.source
        binding.tvTripTo.text = data.destination
        binding.tvCarModel.text = data.cab_model
        binding.tvTripCost.text = data.price
        binding.tvVendorAmount.text = "₹" + data.vendor_amount
        binding.tvOtherCharges?.text = "₹" + data.other_charges
        binding.tvBalAmount.text = "₹" + data.balance
        binding.tvPenalty.text = "₹" + data.penalty
        binding.tvTotalPrice?.text = "₹" + data.total_price_with_extra_km

        binding.tvExtraKm?.text = "₹" + data.extra_km_price
        binding.tvExtraMin?.text = "₹" + data.extra_min_price
        binding.txtExtraKm?.text ="Extra Km Driven("+data.extra_km_driven +"):"
        binding.txtExtraMin?.text ="Extra Minutes("+data.extra_min +"):"
        binding.tvPickDate.text = data.ride_date
        binding.tvPickTime.text = data.ride_time
        binding.tvRetDate.text = data.ride_end_date
        binding.tvRetTime.text = data.ride_end_time
        binding.tvPass.text = data.passengers
        binding.tvNightDrop.text = data.night_drop
        //  binding.tvPickupCharges.text = data.pickup_charges
        binding.tvStateToll.text = data.state_tax
        binding.tvTollTax.text = data.toll_tax
        binding.tvAirportCharge.text = data.airport_charge
        binding.tvName.text = """Name: ${data.name}"""
        binding.tvEmail.text = """Email: ${data.email}"""
        binding.tvPhone.text = "Phone: " + data.mobile
        binding.tvPickUpLoc.text = """PickUp From: ${data.pickLoc}"""
        binding.tvDropUpLoc.text = """Drop At: ${data.dropLoc}"""

        Glide.with(this).load(data.cab_image).into(binding.ivCar)

        binding.tvRoofTop.text = data.roofTop


        binding.tvCabType.text = data.car_type
        binding.tvNightPickUp.text = data.night_pick
        binding.tvParking.text = data.parking
        if (data.other_comments.isNullOrEmpty()) {
            binding.txtNote.isGone()
            binding.tvNote.isGone()
        } else {
            binding.txtNote.isVisible()
            binding.tvNote.isVisible()
            binding.tvNote.text = data.other_comments

        }

        listC.clear()
        listC.add(CabsList(0, "0", "Select Cab"))
        if (jsonData?.cabs?.isNotEmpty()!!) {
            for (i in 0 until jsonData?.cabs!!.size) {
                listC.add(CabsList(
                    jsonData?.cabs!![i].id, jsonData?.cabs!![i].number,
                    jsonData?.cabs!![i].vehicle_model
                )
                )
            }
        }

        val cabListt = CategoryCommercialAdapter(requireActivity(), "Select Cab", listC)
        binding.spCabs.adapter = cabListt


        listD.clear()
        listD.add(DriverList(0, "Select Driver", ""))
        if (jsonData?.drivers?.isNotEmpty()!!) {
            for (i in 0 until jsonData?.drivers!!.size) {
                listD.add(DriverList(
                    jsonData?.drivers!![i].id, jsonData?.drivers!![i].name,
                    jsonData?.drivers!![i].phone
                )
                )
            }
        }

        val driverListt = CategoryDriversAdapter(requireActivity(), "Select Driver", listD)
        binding.spDrivers.adapter = driverListt


        if (!jsonData?.assigned_cab.isNullOrEmpty()) {
            val obj = listC.find { it.id.toString() == jsonData?.assigned_cab }
            binding.spCabs.setSelection(listC.indexOf(obj))
        }
        if (!jsonData?.assigned_driver.isNullOrEmpty()) {
            val obj1 = listD.find { it.id.toString() == jsonData?.assigned_driver }
            binding.spDrivers.setSelection(listD.indexOf(obj1))
        }

        if (data.cabs.size == 0 && data.drivers.size == 0) {
            showCustomAlert(
                requireContext(),
                "You don't have any cab or driver related to this booking.",
                getString(R.string.ok)
            ) {}
        } else if (data.drivers.size == 0 && data.cabs.size != 0) {
            showCustomAlert(
                requireContext(),
                "You have no driver related to this booking. Please add new driver",
                getString(R.string.ok)
            ) {
                findNavController().navigate(R.id.action_bookingDetail_to_vehicleListFragment)
            }
        }
        if (data.cabs.size == 0 && data.drivers.size != 0) {
            showCustomAlert(
                requireContext(),
                "You have no cab related to this booking. Please add new cab",
                getString(R.string.ok)
            ) {
                findNavController().navigate(R.id.action_bookingDetail_to_carDetailFragment)
            }
        }
        if (data.booking_status == "active" && data.assigned_to_me == 1) {
            on_the_way= data.on_the_way_hide
            if(data.on_the_way_hide==0){
                binding.btnBook.text = getString(R.string.on_the_way)
                binding.spCabs.isEnabled = false
                binding.spDrivers.isEnabled = false
                binding.btnBook.isClickable= false
            }else{
                binding.btnBook.text = getString(R.string.on_the_way)
                binding.spCabs.isEnabled = false
                binding.spDrivers.isEnabled = false
            }

        } else if (data.booking_status == "on_the_way" && data.assigned_to_me == 1) {
            binding.btnBook.text = getString(R.string.arrived)
            binding.spCabs.isEnabled = false
            binding.spDrivers.isEnabled = false
        } else if (data.booking_status == "started" && data.assigned_to_me == 1) {
            binding.btnBook.text = getString(R.string.enter_otp)
            binding.spCabs.isEnabled = false
            binding.spDrivers.isEnabled = false
        } else if (data.booking_status == "assigned" && data.assigned_to_me == 0 && data.assigned_to_other == 1) {
            binding.btnBook.isGone()
            binding.tvCusDetails.isGone()
            binding.tvName.isGone()
            binding.tvEmail.isGone()
            binding.tvPhone.isGone()
            binding.tvPickUpLoc.isGone()
            binding.tvDropUpLoc.isGone()
            binding.spCabs.isEnabled = false
            binding.spDrivers.isEnabled = false
        } else if (data.booking_status == "cancelled") {
            binding.cvAmount.isVisible()
            binding.btnBook.isGone()
            binding.tvCusDetails.isGone()
            binding.tvName.isGone()
            binding.tvEmail.isGone()
            binding.tvPhone.isGone()
            binding.tvPickUpLoc.isGone()
            binding.tvDropUpLoc.isGone()
            binding.spCabs.isEnabled = false
            binding.spDrivers.isEnabled = false
        } else if (data.status == "assigned" && data.booking_status == "in_progress") {
            binding.btnBook.text = getString(R.string.end_trip)
            binding.spCabs.isEnabled = false
            binding.spDrivers.isEnabled = false
        } else if (data.booking_status == "completed") {
            binding.cvAmount.isVisible()
            binding.btnBook.isGone()
            binding.spCabs.isClickable = false
            binding.spCabs.isEnabled = false
            binding.spDrivers.isEnabled = false
        }

        if (data.can_cancel == "no") {
            binding.btnCancel.isGone()
        } else {
            binding.btnCancel.isVisible()
        }

        if (data.trip == "Round Trip") {
            binding.rlReturn.isVisible()
            binding.rlReturnTime.isVisible()
            binding.tvStops.isVisible()
            val arrayList = ArrayList<String>()
            for (i in 0 until data.stops.size) {
                arrayList.add(data.stops[i].value)
            }
            val stops = TextUtils.join(", ", arrayList)
            binding.tvStops.text = stops

        } else {
            binding.rlReturn.isGone()
            binding.rlReturnTime.isGone()
            binding.tvStops.isGone()
        }

        if (data.night_pick_charges == "0") {
            binding.tvNightCharge.isGone()
            binding.txtNightCharge.isGone()
            binding.ivNightCharge.isGone()
        } else {
            binding.tvNightCharge.isVisible()
            binding.txtNightCharge.isVisible()
            binding.ivNightCharge.isVisible()
            binding.tvNightCharge.text = data.night_pick_charges
        }
        if (data.night_drop_charges == "0") {
            binding.tvNightDropCharge.isGone()
            binding.txtNightDropCharge.isGone()
            binding.ivNightDropCharge.isGone()
        } else {
            binding.tvNightDropCharge.isVisible()
            binding.txtNightDropCharge.isVisible()
            binding.ivNightDropCharge.isVisible()
            binding.tvNightDropCharge.text = data.night_drop_charges
        }
    }

    fun startRide(pinview: String, hashMap: HashMap<String, String>, metereading: String) {
        hashMap["id"] = booking_id.toString()
        hashMap["otp"] = pinview
        hashMap["lat"] = latitude
        hashMap["lang"] = longitude
        hashMap["odometer_start_reading"] = metereading
        hashMap["user_type"] = getUser(requireContext()).user_type.toString()
        viewModel.startRideApi(requireActivity(), true, hashMap)
    }
}