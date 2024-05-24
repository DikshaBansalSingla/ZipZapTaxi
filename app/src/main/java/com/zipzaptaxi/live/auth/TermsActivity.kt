package com.zipzaptaxi.live.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.FragmentTermsCondBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.model.TermsPrivacyResponseModel
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.viewmodel.OthersViewModel

class TermsActivity : AppCompatActivity(), Observer<RestObservable> {

    lateinit var binding: FragmentTermsCondBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding


    private val viewModel: OthersViewModel by lazy {
        OthersViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentTermsCondBinding.inflate(layoutInflater)
        toolbarBinding = binding.appToolbar

        setContentView(binding.root)
        setToolbar()
        getData()
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            finish()
        }

        toolbarBinding.toolbarTitle.text = getString(R.string.terms_and_conditions)

    }

    private fun getData() {
        viewModel.getTermsDataApi(this, true)
        viewModel.mResponse.observe(this, this)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is TermsPrivacyResponseModel) {
                    val data: TermsPrivacyResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        binding.tvContent.text = HtmlCompat.fromHtml(
                            data.data.terms_and_conditions,
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )

                    } else {
                        AppUtils.showErrorAlert(this, data.message)
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