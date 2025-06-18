package com.zipzaptaxi.live.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.zipzaptaxi.live.cache.CacheConstants
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.isVisible

import java.util.ArrayList

class WalletFragment : Fragment(), Observer<RestObservable> {

    private lateinit var binding: FragmentWalletBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding

    private val adapter:BookingHistoryAdapter by lazy {
        BookingHistoryAdapter(requireContext())
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
        CacheConstants.Current = "wallet"
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
        viewModel.getWalletDataApi(requireActivity(),true, getUser(requireContext()).user_type.toString())
        viewModel.mResponse.observe(viewLifecycleOwner,this)
    }

    private fun setAdapter() {
        binding.rvBookings.adapter= adapter
        adapter.list= arrayList
        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putInt("id", arrayList[it].booking_id)
            findNavController().navigate(R.id.action_walletFragment_to_bookingDetail,bundle)
        }
    }

    private fun setOnClicks() {
        binding.tvPaidAmt.setOnClickListener{
            findNavController().navigate(R.id.action_walletFragment_to_paidAmtActivity2)
        }

        binding.tvReqMoney.setOnClickListener {
            findNavController().navigate(R.id.action_walletFragment_to_tripAmtFragment)
        }

        if(getUser(requireContext()).user_type!="driver"){
            binding.tvAddMoney.setOnClickListener {
                findNavController().navigate(R.id.action_walletFragment_to_addMoneyActivity)
            }
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
                        if (binding.tvTotalBal.visibility == View.GONE && binding.tvNote.visibility == View.GONE && binding.tvAddMoney.visibility == View.GONE) {
                            val layoutParams = binding.tvReqMoney.layoutParams as ConstraintLayout.LayoutParams
                            layoutParams.topToBottom = R.id.tvPaidAmt
                            binding.tvReqMoney.layoutParams = layoutParams
                        } else {
                            val layoutParams = binding.tvReqMoney.layoutParams as ConstraintLayout.LayoutParams
                            layoutParams.topToBottom = R.id.tvTotalBal
                            binding.tvReqMoney.layoutParams = layoutParams
                        }

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
        if(getUser(requireContext()).user_type.toString()=="vendor"){
//            binding.tvNote.isVisible()
            binding.tvBlank.isGone()
            binding.tvVendorAmt.text="Total Earnings"+"\n₹" +data.vendor_amount
            binding.tvCollectAmt.text="Collect Amount" +"\n₹" +data.collect_amount
            binding.tvPaidAmt.text="Paid" +"\n₹" +data.paid
            binding.tvPenalty.text="Penalty"+"\n₹" +data.panelty
            binding.tvTotalBal.text="Balance"+"\n₹" + data.balance
            binding.tvNote.text="Note*: "+"₹"+data.note_balance+" is non refundable but will remain in your wallet"
            arrayList.clear()
            if(data.bookings.size!=0){
                arrayList.addAll(data.bookings)
                adapter.notifyDataSetChanged()
            }
        }else{
            binding.tvVendorAmt.text="Total Earnings"+"\n₹ " +data.vendor_amount
            binding.tvCollectAmt.text="Referral Amount" +"\n₹ " +data.referal_amount
            binding.tvPaidAmt.text="Penalty" +"\n₹ " +data.panelty
            binding.tvPenalty.text="Balance"+"\n₹ " +data.balance
           // binding.tvTotalBal.text="Balance"+"\n₹ " + data.balance
            binding.tvTotalBal.isGone()
            binding.tvNote.isGone()
            binding.tvBlank.isVisible()
            binding.tvAddMoney.isGone()
            arrayList.clear()
            if(data.bookings.size!=0){
                arrayList.addAll(data.bookings)
                adapter.notifyDataSetChanged()
            }
        }


    }
}