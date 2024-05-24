package com.zipzaptaxi.live.home.home

import BookingListResponse
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.zipzaptaxi.live.CabFreeActivity
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.adapter.HomeAdapter
import com.zipzaptaxi.live.bookings.BookingDetail
import com.zipzaptaxi.live.cache.saveString
import com.zipzaptaxi.live.cache.saveToken
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
import com.zipzaptaxi.live.wallet.WalletFragment


class HomeFragment : Fragment(), Observer<RestObservable> {

    private val viewModel: BookingViewModel
            by lazy { ViewModelProvider(this)[BookingViewModel::class.java] }

    private val homeAdapter: HomeAdapter by lazy {
        HomeAdapter(requireContext())
    }
    private var arrayList = ArrayList<BookingListResponse.Data>()

    private lateinit var binding: FragmentHomeBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    private lateinit var mLayoutManager: LinearLayoutManager
    private var lastBackPressTime = 0L
    private var latitude = ""
    private var longitude = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            requestCameraPermission()
        } else {
            // Permission denied, handle accordingly
            // You can show a message or request the permission again
        }
    }

    // Camera permission launcher
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Camera permission granted, proceed with notification permission only if SDK version is 13 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermission()
            } else {
                // SDK version is lower than 13, proceed with necessary operations
                getLastKnownLocation()
            }
        } else {
            // Permission denied, handle accordingly
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
                getLastKnownLocation()
            // FCM SDK (and your app) can post notifications.
        } else {
            showToast(getString(R.string.permission_to_show_notifications_is_not_granted))
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setAdapter()
        getBookingsApi()
        setOnClicks()
        requestAllPermissions()

        binding.swipeRefreshLayout.setOnRefreshListener { // Implement your refresh action here
            // For example, fetch new data from server
            fetchData()
        }
    }

    private fun requestAllPermissions() {
        // Request location permission
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            // Location permission is already granted or denied, proceed with camera permission
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        // Request camera permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        } else {
            // Camera permission is already granted or denied, proceed with notification permission
            requestNotificationPermission()
        }
    }

    private fun requestNotificationPermission() {
        // Request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // All permissions have been requested, proceed with the necessary operations
            getLastKnownLocation()
        }
    }


    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(

                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            getLastKnownLocation()
        }
    }

   /* private fun requestCameraPermission() {
        // Request camera permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }*/

    private fun fetchData() {
        // Perform your data fetching operation here
        // This method will be called when user swipes to refresh
        // After completing the operation, make sure to call setRefreshing(false) to stop the refreshing animation
        getBookingsApi()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(ContentValues.TAG, "askNotificationPermission: .. has permission")
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Log.d(ContentValues.TAG, "askNotificationPermission: .. show rationale")
                Log.e(ContentValues.TAG, "Permissions Denied")
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    R.string.notification_rationale,
                    Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.ok) {
                        // Request permission
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            102
                        )
                    }.show()
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setOnClicks() {
        binding.tvWalletBal.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_walletFragment)
        }
        binding.tvActiveRides.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_bookingsFragment)
        }
    }

    private fun getBookingsApi() {
        viewModel.bookingListApi(requireActivity(), true)
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
            findNavController().navigate(R.id.action_homeFragment_to_bookingDetail,bundle)
        }

        homeAdapter.onButtonClick = {
            val bundle = Bundle()
            bundle.putInt("id", arrayList[it].id)
            findNavController().navigate(R.id.action_homeFragment_to_bookingDetail,bundle)
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


    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is BookingListResponse) {
                    val data: BookingListResponse = value.data
                    if (data.code == AppConstant.success_code) {
                        binding.tvWalletBal.text= "Wallet: ₹ "+data.data[0].wallet_balance.toString()
                        arrayList.clear()
                        if (data.data.isNullOrEmpty()) {
                            binding.rvBookingList.isGone()
                            binding.tvNoData.isVisible()
                        } else {
                            binding.rvBookingList.isVisible()
                            binding.tvNoData.isGone()

                            arrayList.addAll(data.data)
                            homeAdapter.notifyDataSetChanged()
                        }
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

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                location?.let {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                    saveString(requireContext(),"Lat",latitude)
                    saveString(requireContext(),"Lang",longitude)
                    // Do something with latitude and longitude
                    Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                }
            }
            .addOnFailureListener { e ->
                // Failed to get location
                Log.e("Location", "Failed to get location: ${e.message}", e)
            }
    }

}