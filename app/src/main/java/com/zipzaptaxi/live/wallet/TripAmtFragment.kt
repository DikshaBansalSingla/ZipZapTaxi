package com.zipzaptaxi.live.wallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.adapter.DriverListAdapter
import com.zipzaptaxi.live.adapter.TransactionsAdapter
import com.zipzaptaxi.live.cache.CacheConstants
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentTripAmtBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.DriverListResponse
import com.zipzaptaxi.live.model.TdsDataModel
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.isVisible
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.viewmodel.WalletViewModel

class TripAmtFragment : Fragment(), Observer<RestObservable> {

    private lateinit var binding: FragmentTripAmtBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    val viewModel: WalletViewModel by lazy {
        WalletViewModel()
    }

    val arrayList = ArrayList<TdsDataModel.Data.Transaction>()

    private val transactionsAdapter: TransactionsAdapter by lazy {
        TransactionsAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTripAmtBinding.inflate(inflater, container, false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CacheConstants.Current = "tripAmt"
        setToolbar()
        setAdapter()
        getData()
        setClicks()

    }

    private fun setAdapter() {
        binding.rvTransactions.adapter = transactionsAdapter
        transactionsAdapter.list = arrayList
    }

    private fun getData() {
        viewModel.getTdsDataApi(requireActivity(), true)
        viewModel.mResponse.observe(viewLifecycleOwner, this)
    }

    private fun setClicks() {
        binding.btnSentReq.setOnClickListener {
            val map= HashMap<String,String>()
            map["user_type"]= getUser(requireContext()).user_type.toString()
            map["User_id"]= getUser(requireContext()).id.toString()
            viewModel.sendRequestApi(requireActivity(),true,map)
            viewModel.mResponse.observe(viewLifecycleOwner,this)
        }
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_tripAmtFragment_to_walletFragment)
        }
        toolbarBinding.toolbarTitle.text = getString(R.string.trip_amount)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is TdsDataModel) {
                    val data: TdsDataModel = value.data
                    if (data.code == AppConstant.success_code) {
                        setData(data.data)
                    }
                }else if(value.data is BaseResponseModel){
                    val data: BaseResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        AppUtils.showSuccessAlert(requireActivity(),data.message)
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

    private fun setData(data: TdsDataModel.Data) {
        binding.tvTds.text = data.tds_deducted
        binding.tvTdsApply.text = data.tds_applicable
        binding.tvAvailBal.text = data.available_balance
        binding.tvSecurity.text = data.security
        binding.tvWithDrawBal.text = data.withdrable_amount
        arrayList.clear()

        if (data.transactions.size == 0) {
            binding.rvTransactions.isGone()
            binding.tvNoData.isVisible()
        } else {
            binding.rvTransactions.isVisible()
            binding.tvNoData.isGone()

            arrayList.addAll(data.transactions)
            transactionsAdapter.notifyDataSetChanged()
        }
    }

}