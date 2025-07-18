package com.app.twocommaquotes

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.app.twocommaquotes.adapter.QuoteAdapter
import com.app.twocommaquotes.api.loadingmanage.NetworkResult
import com.app.twocommaquotes.customclasses.CustomDialog
import com.app.twocommaquotes.databinding.ActivityMainBinding
import com.app.twocommaquotes.model.QuoteModelNew
import com.app.twocommaquotes.permission.PermissionCheck
import com.app.twocommaquotes.permission.PermissionHandler
import com.app.twocommaquotes.utility.*
import com.app.twocommaquotes.viewmodels.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var viewBinding: ActivityMainBinding
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: QuoteAdapter

    @Inject
    lateinit var utility: Utility

    override fun onCreate(instance: Bundle?, viewBinding: ActivityMainBinding) {
        this.viewBinding = viewBinding

        viewBinding.clRoot.background = this.getDrawable(R.drawable.ic_black_iphone_bg)

        viewModel.getQuotesNormal()
        getQuotes()

        viewBinding.rvText.setHasFixedSize(true)
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(viewBinding.rvText)
        adapter = QuoteAdapter()

        viewBinding.ivShare.setOnClickListener {
            checkPermission()
        }
    }

    private fun getQuotes() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getQuotesResponseState.collect { data ->
                    when (data) {
                        is NetworkResult.Loading -> {
                            CustomDialog.getInstance().showDialog(this@MainActivity)
                        }

                        is NetworkResult.Success -> {
                            CustomDialog.getInstance().hide()
                            data.data.let { dataList ->
                                viewBinding.rvText.adapter = adapter
                                adapter.submitList(dataList)
                            }
                        }

                        is NetworkResult.Error -> {
                            CustomDialog.getInstance().hide()
                        }

                        is NetworkResult.ConnectionError -> {
                            CustomDialog.getInstance().hide()
                            Toast.makeText(
                                this@MainActivity,
                                "Please check your Internet connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }


    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    private fun screenshot(view: View, filename: String): File? {
        val date = Date()

        val format: CharSequence = DateFormat.format("yyyy-MM-dd_hh:mm:ss", date)
        try {

            val dirPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
//                 this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString() + "/TwoComma"
            } else {
                Environment.getExternalStorageDirectory().toString() + "/TwoComma"
            }

            val file = File(dirPath)
            if (!file.exists()) {
                val mkdir: Boolean = file.mkdir()
            }

            // File name
            val path = "$dirPath/$filename${format.toString().replace(":", ".")}.jpeg"
            Log.d("MyFile", path)
            view.isDrawingCacheEnabled = true
            val bitmap: Bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false
            val imageUrl = File(path)
            val outputStream = FileOutputStream(imageUrl)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return imageUrl

        } catch (io: FileNotFoundException) {
            io.printStackTrace()
            Log.d("MyFile", io.message.toString())
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("MyFile", e.message.toString())
        }
        return null
    }

    private fun checkPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayListOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayListOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        PermissionCheck.check(
            this, permissions,
            "Storage permission is necessary to store a image",
            object : PermissionHandler() {
                override fun onGranted() {
                    viewBinding.ivShare.makeViewInvisible()
                    Toast.makeText(
                        this@MainActivity,
                        "Save image in your gallery",
                        Toast.LENGTH_SHORT
                    ).show()
                    screenshot(window.decorView.rootView, "two_comma")
                    viewBinding.ivShare.showView()
                }

                override fun onDenied(
                    context: Context,
                    deniedPermissions: java.util.ArrayList<String>
                ) {
                    super.onPermissionDenied(context, deniedPermissions)
                }
            }
        )
    }
}
