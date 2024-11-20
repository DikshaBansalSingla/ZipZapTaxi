package com.zipzaptaxi.live.home.home

import BookingListResponse
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.adapter.HomeAdapter
import com.zipzaptaxi.live.cache.CacheConstants
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.cache.saveString
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentHomeBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.isVisible
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.viewmodel.BookingViewModel

class HomeFragment : Fragment(), Observer<RestObservable> {

    private val viewModel: BookingViewModel by lazy { ViewModelProvider(this)[BookingViewModel::class.java] }

    private val homeAdapter: HomeAdapter by lazy { HomeAdapter(requireContext()) }
    private var mContext: Context? = null
    private lateinit var locationSettingsLauncher: ActivityResultLauncher<IntentSenderRequest>

    private var arrayList = ArrayList<BookingListResponse.Data.Data>()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    private lateinit var mLayoutManager: LinearLayoutManager
    private var latitude = ""
    private var longitude = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Constant for requesting settings change
    private val REQUEST_CHECK_SETTINGS = 1001

    // Location permission launcher
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            checkLocationSettings()
        } else {
            showToast("Location permission not granted")
        }
    }

    // Camera permission launcher
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Check if the notification permission is required for Android 13 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermission()
            } else {
                // Directly proceed to getting location if notification permission is not needed
                getLastKnownLocation()
            }
        } else {
            showToast("Camera permission not granted")
        }
    }

    // Notification permission launcher for Android 13 and higher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLastKnownLocation()
        } else {
            showToast(getString(R.string.permission_to_show_notifications_is_not_granted))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        toolbarBinding = binding.appToolbar
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CacheConstants.Current = "home"
        setToolbar()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext ?: requireActivity())
        setAdapter()
        getBookingsApi()
        setOnClicks()
        requestAllPermissions()

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchData()
        }
        locationSettingsLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // User accepted the request to enable location settings
                getLastKnownLocation()
            } else {
                // User denied the request to enable location settings
                showToast("Location services are required for this feature")
            }
        }

    }

    private fun setOnClicks() {
        binding.tvWalletBal.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_walletFragment)
        }
        /* binding.tvActiveRides.setOnClickListener {
             findNavController().navigate(R.id.action_homeFragment_to_bookingsFragment)
         }*/
    }

    private fun requestAllPermissions() {
        // Request location permission
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            // Location permission is granted, now request camera permission
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        } else {
            // Camera permission already granted, now request notification permission
            requestNotificationPermission()
        }
    }

    private fun requestNotificationPermission() {
        // Request notification permission only for Android 13 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Proceed to get location if no notification permission is needed
            getLastKnownLocation()
        }
    }

    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).setMinUpdateIntervalMillis(5000).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Location settings are satisfied
            getLastKnownLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Create an IntentSenderRequest for the resolution
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    locationSettingsLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    showToast("Error opening settings: ${sendEx.message}")
                }
            } else {
                showToast("Location settings are not correct")
            }
        }
    }


    private fun fetchData() {
        getBookingsApi()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun getBookingsApi() {
        viewModel.bookingListApi(
            requireActivity(),
            true,
            getUser(requireContext()).user_type.toString()
        )
        viewModel.mResponse.observe(viewLifecycleOwner, this)
    }

    private fun setAdapter() {
        mLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvBookingList.layoutManager = mLayoutManager
        binding.rvBookingList.adapter = homeAdapter
        homeAdapter.list = arrayList

        homeAdapter.onItemClick = {
            val bundle = Bundle()
            bundle.putInt("id", arrayList[it].id)
            findNavController().navigate(R.id.action_homeFragment_to_bookingDetail, bundle)
        }

        homeAdapter.onButtonClick = {
            val bundle = Bundle()
            bundle.putInt("id", arrayList[it].id)
            findNavController().navigate(R.id.action_homeFragment_to_bookingDetail, bundle)
        }
    }

    private fun setToolbar() {
        toolbarBinding.tvCabFree.isVisible()
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        toolbarBinding.toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }
        toolbarBinding.tvCabFree.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cabFreeActivity)
        }
        toolbarBinding.toolbarTitle.text = getString(R.string.app_name)
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                mContext ?: requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
                saveString(requireContext(), "Lat", latitude)
                saveString(requireContext(), "Lang", longitude)
                showToast("Location is: Latitude: $latitude, Longitude: $longitude")
            } else {
                requestLocationUpdates()
            }
        }.addOnFailureListener { e ->
            Log.e("Location", "Failed to get location: ${e.message}", e)
        }
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).setMinUpdateIntervalMillis(5000).build()

        if (ActivityCompat.checkSelfPermission(
                mContext ?: requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                    saveString(requireContext(), "Lat", latitude)
                    saveString(requireContext(), "Lang", longitude)
                    showToast("Location updated: Latitude: $latitude, Longitude: $longitude")
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }, Looper.getMainLooper())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                getLastKnownLocation()
            } else {
                showToast("Location services are required for this feature")
            }
        }
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is BookingListResponse) {
                    val data: BookingListResponse = value.data
                    if (data.code == AppConstant.success_code) {
                        binding.tvWalletBal.text = "Wallet: ₹ " + data.data.wallet_balance
                        arrayList.clear()
                        if (data.data.data_list.isNullOrEmpty()) {
                            binding.rvBookingList.isGone()
                            binding.tvNoData.isVisible()
                        } else {
                            binding.rvBookingList.isVisible()
                            binding.tvNoData.isGone()
                            arrayList.addAll(data.data.data_list)
                            homeAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            Status.ERROR -> {
                showToast(value.data as String? ?: value.error.toString())
            }
            Status.LOADING -> {  }

            else ->{}
        }
    }
}

