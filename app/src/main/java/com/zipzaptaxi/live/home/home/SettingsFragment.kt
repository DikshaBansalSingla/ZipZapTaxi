package com.zipzaptaxi.live.home.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.auth.LoginActivity
import com.zipzaptaxi.live.cache.CacheConstants
import com.zipzaptaxi.live.cache.clearAllData
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentSettingsBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.showCustomAlertWithCancel
import com.zipzaptaxi.live.viewmodel.AuthViewModel

class SettingsFragment : Fragment(), Observer<RestObservable> {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding


    private val authViewModel: AuthViewModel by lazy {
        AuthViewModel()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        toolbarBinding = binding.appToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CacheConstants.Current = "settings"
        setToolbar()
        setOnClicks()
        //getSupportData()
    }

    private fun setOnClicks() {
        binding.relTermMain.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "1")
            findNavController().navigate(R.id.action_settingsFragment_to_termsCondFragment,bundle)
        }
        binding.relMyPolicy.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "2")
            findNavController().navigate(R.id.action_settingsFragment_to_termsCondFragment,bundle)

        }
        binding.relMyBank.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_bankDetailFragment)
        }
        binding.relLogoutApp.setOnClickListener {
            showCustomAlertWithCancel(requireContext(),
                resources.getString(R.string.are_you_sure_you_want_to_logout),
                getString(R.string.ok),
                getString(R.string.cancel), {
                    authViewModel.logoutAppApi(requireActivity(), true)
                    authViewModel.mResponse.observe(viewLifecycleOwner, this)
                }, {})
        }
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }

        toolbarBinding.toolbarTitle.text = getString(R.string.settings)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is BaseResponseModel) {
                    val data: BaseResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        val googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)
                        signOut(googleSignInClient)
                        revokeAccess(googleSignInClient)
                        clearAllData(requireContext())
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        (activity as MainActivity).finishAffinity()
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

    private fun revokeAccess(googleSignInClient: GoogleSignInClient?) {

        googleSignInClient?.revokeAccess()
            ?.addOnCompleteListener(requireActivity()) {
                // ...
            }
    }

    private fun signOut(googleSignInClient: GoogleSignInClient) {
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            // ...
        }
    }
}