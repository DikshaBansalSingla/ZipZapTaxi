package com.zipzaptaxi.live.home.home

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.adapter.HomeCityAdapter
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentAddHomeCityBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.model.AddDeleteCityModel
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.viewmodel.OthersViewModel
import java.util.ArrayList

class AddHomeCityFragment : Fragment(), Observer<RestObservable> {


    private val savedCityList = mutableListOf<AddDeleteCityModel.Data>()
    private val newCityList = mutableListOf<AddDeleteCityModel.Data>()

    private val homeCityAdapter:HomeCityAdapter by lazy {
        HomeCityAdapter()
    }

    private val viewModel: OthersViewModel by lazy {
        OthersViewModel()
    }
    private val locationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            val location = place.address ?: ""
            newCityList.add(AddDeleteCityModel.Data(location, "", 0, "", 0))
            updateAdapter()
        } else {
            println(resources.getString(R.string.something_went_wrong_while_getting_locations))
        }
    }
    lateinit var binding: FragmentAddHomeCityBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddHomeCityBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.map_key))
        }
        setToolbar()
        setOnClicks()
        setAdapter()
        getHomeCityList()

    }

    private fun getHomeCityList() {
        viewModel.getCitiesListApi(requireActivity(),true)
        viewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    private fun setAdapter() {
        binding.rvCities.adapter = homeCityAdapter
        binding.rvCities.layoutManager = LinearLayoutManager(requireContext())
        homeCityAdapter.onDeleteClick ={
            if (homeCityAdapter.list[it].id == 0) {
                newCityList.removeAt(it - savedCityList.size)
            } else {
                deleteFromList(savedCityList[it].id)
            }
            updateAdapter()
        }
    }
    private fun updateAdapter() {
        homeCityAdapter.updateData(savedCityList + newCityList)
    }

    private fun deleteFromList(id: Int) {
        val map= HashMap<String,Any>()
        map["ids"]= id
        map["delete"]= 1
        viewModel.addDeleteHomeCityApi(requireActivity(),true,map)
    }

    private fun setOnClicks() {
        binding.etCity.setOnClickListener {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.ADDRESS
            )
            val countryList = ArrayList<String>()
                countryList.add("IN")

            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountries(countryList)
                .build(requireContext())
            locationLauncher.launch(intent)
        }

        binding.btnSave.setOnClickListener {
            val newCitiesString = newCityList.joinToString(", ") { it.city }
            if (newCitiesString.isNotEmpty()) {
                val map = HashMap<String, Any>()
                map["city"] = newCitiesString
                map["delete"] = 0
                map["user_type"] = getUser(requireContext()).user_type!!
                viewModel.addDeleteHomeCityApi(requireActivity(), true, map)
            }

        }
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }
        toolbarBinding.toolbarTitle.text= getString(R.string.add_home_city)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is AddDeleteCityModel) {
                    val data = value.data.data
                    savedCityList.clear()
                    savedCityList.addAll(data)
                    newCityList.clear()
                    updateAdapter()
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