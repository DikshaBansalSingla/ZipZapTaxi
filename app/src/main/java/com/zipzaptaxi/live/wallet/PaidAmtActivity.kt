package com.zipzaptaxi.live.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.adapter.PaidAmtAdapter
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.ActivityPaidAmountBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.DriverListResponse
import com.zipzaptaxi.live.model.GetTransactionsModel
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.isVisible
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.viewmodel.WalletViewModel

class PaidAmtActivity : Fragment(), Observer<RestObservable> {

    private lateinit var binding: ActivityPaidAmountBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    private val adapter:PaidAmtAdapter by lazy {
        PaidAmtAdapter()
    }

    val arrayList= ArrayList<GetTransactionsModel.Data>()

    val viewModel:WalletViewModel by lazy {
        WalletViewModel()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ActivityPaidAmountBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setAdapter()
        getData()
    }

    private fun getData() {
        viewModel.getTransactionsApi(requireActivity(),true)
        viewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    private fun setAdapter() {
        binding.rvPaidAmt.adapter= adapter
        adapter.list= arrayList
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {

            findNavController().navigate(R.id.action_paidAmtActivity2_to_walletFragment)
        }
        toolbarBinding.toolbarTitle.text= getString(R.string.paid_amt)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is GetTransactionsModel) {
                    val data: GetTransactionsModel = value.data
                    if (data.code == AppConstant.success_code) {
                        arrayList.clear()
                        if(data.data.isNullOrEmpty()){
                            binding.rvPaidAmt.isGone()
                            binding.tvNoData.isVisible()
                        }
                        else {
                            binding.rvPaidAmt.isVisible()
                            binding.tvNoData.isGone()

                            arrayList.addAll(data.data)
                            adapter.notifyDataSetChanged()
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
}