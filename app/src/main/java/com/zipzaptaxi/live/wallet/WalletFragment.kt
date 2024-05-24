package com.zipzaptaxi.live.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.adapter.BookingHistoryAdapter
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentWalletBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.model.GetWalletModel
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.viewmodel.WalletViewModel
import androidx.navigation.fragment.findNavController

import java.util.ArrayList

class WalletFragment : Fragment(), Observer<RestObservable> {

    private lateinit var binding: FragmentWalletBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding

    private val adapter:BookingHistoryAdapter by lazy {
        BookingHistoryAdapter()
    }

    private val viewModel: WalletViewModel by lazy {
        WalletViewModel()
    }

    private val arrayList= ArrayList<GetWalletModel.Data.Booking>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWalletBinding.inflate(inflater,container,false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setOnClicks()
        setAdapter()

        //getWalletData()
    }

    override fun onResume() {
        super.onResume()
        getWalletData()
    }

    private fun getWalletData() {
        viewModel.getWalletDataApi(requireActivity(),true)
        viewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    private fun setAdapter() {
        binding.rvBookings.adapter= adapter
        adapter.list= arrayList
    }

    private fun setOnClicks() {
        binding.tvPaidAmt.setOnClickListener{
            findNavController().navigate(R.id.action_walletFragment_to_paidAmtActivity2)
        }

        binding.tvAddMoney.setOnClickListener {
            findNavController().navigate(R.id.action_walletFragment_to_addMoneyActivity)
        }
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }

        toolbarBinding.toolbarTitle.text= getString(R.string.wallet)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is GetWalletModel) {
                    val data: GetWalletModel = value.data
                    if (data.code == AppConstant.success_code) {
                            setData(data.data)

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

    private fun setData(data: GetWalletModel.Data) {
        binding.tvVendorAmt.text="Vendor Amount"+"\n₹" +data.vendor_amount
        binding.tvCollectAmt.text="Collect Amount" +"\n₹" +data.collect_amount
        binding.tvPaidAmt.text="Paid" +"\n₹" +data.paid
        binding.tvPenalty.text="Penalty"+"\n₹" +data.panelty
        binding.tvTotalBal.text="Balance"+"\n₹" + data.balance
        binding.tvNote.text="Note*: "+data.balance+" is non refundable but will remain in your wallet"
        arrayList.clear()
        if(data.bookings.size!=0){
            arrayList.addAll(data.bookings)
            adapter.notifyDataSetChanged()
        }

    }
}