package com.app.twocommaquotes.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.text.HtmlCompat
import com.app.twocommaquotes.BaseApplication
import com.app.twocommaquotes.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject

//This class is a collection of common methods which are use in daily routine
class Utility @Inject constructor(){

//    companion object {
//        private var instance: Utility? = null
//
//        fun getInstance(): Utility {
//            if (instance == null) {
//                instance = Utility()
//            }
//            return instance as Utility
//        }
//    }

    @SuppressLint("HardwareIds")
    //get device token for different devices
    fun getDeviceToken(context: Context?): String {
        return Settings.Secure.getString(context!!.contentResolver, Settings.Secure.ANDROID_ID)
    }

    //this method will return 32 characters authenticate value while api calling
    fun getMD5EncryptedString(inputString: String): String {
        var mdEnc: MessageDigest? = null
        try {
            mdEnc = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        mdEnc!!.update(inputString.toByteArray(), 0, inputString.length)
        var md5 = BigInteger(1, mdEnc.digest()).toString(16)
        while (md5.length < 32) {
            md5 = "0$md5"
        }
        return md5
    }

    /**
     * The key hash value is used by Facebook as security check for login.
     * To get key hash value of your machine, write following code in onCreate method
     */
    fun getHashKey() {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            val info = BaseApplication.getInstance().packageManager.getPackageInfo(
                BaseApplication.getInstance().packageName, PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())

                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    /*This method is open your keyboard on click of edittext
   * @param pass your context
   * @Param pass your edittext id
   */
    fun launchKeyboard(mContext: Activity?, mEditText: EditText) {
        mEditText.postDelayed({
            val keyboard =
                mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.showSoftInput(mEditText, 0)
        }, 100)
    }


    /*This method is hide your keyboard for particular view
     * @Param pass your view id which you want to hide
     */
    private fun hideKeyboard(v: View) {
        val mgr = BaseApplication.getInstance()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(v.windowToken, 0)

    }

    /*This method is hide your keyboard on outside touch of screen
    * @Param pass your parent view id of activity of fragment
    */
    fun outSideTouchHideKeyboard(view: View) {
        // Set up touch listener for non-text box views to hideAnimateDialog keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { p0, p1 ->
                when (p1?.action) {
                    MotionEvent.ACTION_DOWN -> {}
                    MotionEvent.ACTION_UP -> p0?.performClick()
                    else -> {}
                }
                true
            }
        }

        // If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                outSideTouchHideKeyboard(innerView)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun hideKeyBoardWhenTouchOutside(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, _ ->
                hideKeyboard(v)
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                hideKeyBoardWhenTouchOutside(innerView)
            }
        }
    }

    /**
     * This method give the App versionCode
     *
     * @return (int) version :  it return app version code e.g. 1, 2, 3
     * return version - e.g. 1, 2, 3
     */
    fun getAppVersionCode(): Int {
        var version = 0
        try {
            val pInfo = BaseApplication.getInstance()
                .packageManager.getPackageInfo(BaseApplication.getInstance().packageName, 0)
            version = pInfo.versionCode
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return version
    }


    /**
     * This method return the Application version
     *
     * @return (String) version : it return app version
     * return version - e.g. 1, 2, 3
     */
    fun getAppVersion(): String {
        var version = ""
        try {
            val pInfo = BaseApplication.getInstance()
                .packageManager.getPackageInfo(BaseApplication.getInstance().packageName, 0)
            version = pInfo.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return version
    }

    fun getTextRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun getRequestBody(fileKey: String, value: String): MultipartBody.Part {
        return MultipartBody.Part.createFormData(fileKey, value)
    }

    fun getMultipartBody(fileKey: String, file: File): MultipartBody.Part {
        val fileReqBody: RequestBody = file.asRequestBody("*/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(fileKey, file.name, fileReqBody)
    }

    fun showNoInternetDialog(context: Context, tryAgainClick: View.OnClickListener) {
        val dialog = Dialog(context, R.style.AppTheme_Theme)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_no_internet, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        view.findViewById<AppCompatButton>(R.id.btnTryAgain)
            .setOnClickListener {
                if (BaseApplication.getInstance().isConnectionAvailable()) {
                    dialog.dismiss()
                    tryAgainClick.onClick(it)
                }
            }
        dialog.show()
    }


    fun loadImageUrl(context: Context, url: String, imageView: AppCompatImageView) {
        Glide.with(context)
            .asBitmap()
            .load(url).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop()
            .into(imageView)
            .onLoadFailed(context.resources.getDrawable(R.drawable.ic_black_yellow_flower))
    }

    fun loadThumbnailImageUrl(context: Context, url: String, webView: WebView) {

        webView.settings.loadWithOverviewMode = true
        webView.settings.javaScriptEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.webViewClient = WebViewClient()
        val url1 = "https://docs.google.com/gview?embedded=true&url=$url"
//            val url1 = "http://drive.google.com/viewerng/viewer?embedded=true&url="+url
//            val url1 = "https://docs.google.com/gview?embedded=true&url=http://vlpl.lrdevteam.com/storage/uploads/gst_document/22062720-corean-Learn-pdf.pdf"
        webView.loadUrl(url1)

    }

    // Function for convert html to string
    fun htmlToString(productDescription: String): String {
        var htmlString: Spanned? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            htmlString = HtmlCompat.fromHtml(productDescription, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            htmlString = Html.fromHtml(productDescription)
        }
        return htmlString.toString()
    }

}