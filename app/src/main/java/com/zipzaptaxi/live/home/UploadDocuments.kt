package com.zipzaptaxi.live.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.ActivityUploadDocumentsBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.model.FileUploadResponse
import com.zipzaptaxi.live.utils.ImagePickerUtility
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.prepareMultiPart
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.viewmodel.AuthViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class UploadDocuments : AppCompatActivity() {
    var mAadharFPath = ""
    var mAadharBPath = ""
    var mPanFPath = ""
    var mPanBPath = ""
    var mDlFrontPath = ""
    var mDlBackPath = ""
    var mCertPath = ""

    lateinit var mValidationClass:ValidationsClass
    var jsonArray = JSONArray()

    private lateinit var binding: ActivityUploadDocumentsBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding

    private val viewModel: AuthViewModel
            by lazy { ViewModelProvider(this)[AuthViewModel::class.java] }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUploadDocumentsBinding.inflate(layoutInflater)
        toolbarBinding= binding.appToolbar
        setContentView(binding.root)

        mValidationClass= ValidationsClass.getInstance()
        setToolbar()
        setOnClicks()
        binding.btnNext.setOnClickListener {
            val intent = Intent(this, CarDetailActivity::class.java)
            startActivity(intent)        }
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

        toolbarBinding.toolbar.setNavigationOnClickListener {
            MainActivity().openCloseDrawer()
        }
        toolbarBinding.toolbarTitle.text= getString(R.string.documents)
    }

    private fun setOnClicks() {

//        binding.ivAadhaarFront.setOnClickListener {
//
//            getImage(this, 0)
//        }
//        binding.ivAadhaarBack.setOnClickListener {
//
//            getImage(this, 1)
//        }
//        binding.ivPanFront.setOnClickListener {
//
//            getImage(this, 2)
//        }
//        binding.ivPanBack.setOnClickListener {
//
//            getImage(this, 3)
//        }
//        binding.ivDLFront.setOnClickListener {
//
//            getImage(this, 4)
//        }
//        binding.ivDLBack.setOnClickListener {
//
//            getImage(this, 5)
//        }
//        binding.ivCert.setOnClickListener {
//
//            getImage(this, 6)
//        }


    }

}