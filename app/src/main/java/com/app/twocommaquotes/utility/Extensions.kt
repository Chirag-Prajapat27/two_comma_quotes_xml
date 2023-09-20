package com.app.twocommaquotes.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.*
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.util.TypedValue
import android.view.*
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import android.webkit.WebView
import android.widget.*
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.app.twocommaquotes.R
import timber.log.Timber
import java.io.BufferedReader
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/*inline fun <reified T : Any> String?.toKotlinObject(): T {
    return BaseApplication.getInstance().mGsonProvider.provideGson().fromJson(this, T::class.java)
}*/

/**
 * @param clickablePart Clickable string, a part of complete text.
 * @param onClickListener Higher order function to pass event to calling classes
 */
fun SpannableString.addClickListener(clickablePart: String, onClickListener: () -> Unit): SpannableString {
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) = onClickListener.invoke()
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = ds.linkColor
            ds.isUnderlineText = false
        }
    }
    val clickablePartStart = indexOf(clickablePart)
    setSpan(
        clickableSpan,
        clickablePartStart,
        clickablePartStart + clickablePart.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return this
}

/**
 * @param spannableString Spannable String being set in TextView
 * @method Set textView link color, highlight color and sets spannable string.
 */
fun TextView?.setSpannableText(spannableString: SpannableString) {
    this?.setLinkTextColor(ContextCompat.getColor(this.context, R.color.lightBlue))
    this?.highlightColor = Color.TRANSPARENT // prevent TextView change background when highlight
    this?.movementMethod = LinkMovementMethod.getInstance()
    this?.setText(spannableString, TextView.BufferType.SPANNABLE)
}

/**
 * @param heightInPixels Height of the dropdown in pixels
 * @method Set a custom height to dropdown
 */
fun Spinner?.limitSpinnerDropDownHeight(heightInPixels: Int) {
    try {
        val popup = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        // Get private mPopup member variable and try cast to ListPopupWindow
        val popupWindow = popup.get(this) as android.widget.ListPopupWindow

        // Set popupWindow height to heightInPixels
        popupWindow.height = heightInPixels
    } catch (e: NoClassDefFoundError) {
        // silently fail...
    } catch (e: ClassCastException) {
    } catch (e: NoSuchFieldException) {
    } catch (e: IllegalAccessException) {
    }
}

fun startTimer(countDownInMillis: Long, totalTimeInMillis: Long, onClickListener: (Boolean) -> Unit): CountDownTimer {
    return object : CountDownTimer(totalTimeInMillis, countDownInMillis) {
        override fun onFinish() {
            onClickListener.invoke(true)
        }

        override fun onTick(millis: Long) {
            onClickListener.invoke(false)
        }
    }
}

fun View?.showView() {
    this?.visibility = View.VISIBLE
}

fun View?.hideView() {
    this?.visibility = View.GONE
}

fun View?.makeViewInvisible() {
    this?.visibility = View.INVISIBLE
}

@Suppress("UNCHECKED_CAST")
inline fun <A, B, R> Pair<A?, B?>.letNotNull(transform: (A, B) -> R): R? =
        when (null) {
            first, second -> null
            else -> transform(first as A, second as B)
        }

@Suppress("UNCHECKED_CAST")
inline fun <A, B, C, R> Triple<A?, B?, C?>.letNotNull(transform: (A, B, C) -> R): R? =
        when (null) {
            first, second, third -> null
            else -> transform(first as A, second as B, third as C)
        }

inline fun <A, B, C, R> Triple<A?, B?, C?>.letNotNullOrEmpty(transform: (A, B, C) -> R): R? =
        when (null) {
            first, second, third -> null
            else -> {
                when ("") {
                    first, second, third -> null
                    else -> transform(first as A, second as B, third as C)
                }

            }
        }

fun TextView.setErrorMessage(message: String? = null) {
    message?.let {
        this.visibility = View.VISIBLE
        this.text = message
    } ?: kotlin.run {
        this.visibility = View.GONE
    }
}

fun TextView.setSafeText(message: String? = null) {
    setErrorMessage(message)
}

fun Fragment.getDrawable(drawable: Int): Drawable? {
    val context = this.context
    context?.let {
        return AppCompatResources.getDrawable(it, drawable)
    }
    return null
}

/**
 * addFragment
 * @param fragment = fragment
 * @param frameId = containerId
 * @param tag = tag
 */
fun AppCompatActivity.addFragment(fragment: Fragment?, frameId: Int, tag: String? = null, isAddToBackStack: Boolean = false) {
    supportFragmentManager.inTransaction({
        fragment?.let {
            add(frameId, fragment, tag)
        }
    }, isAddToBackStack)
}

/*fun BaseFragment.addFragment(fragment: Fragment?, frameId: Int, tag: String? = null, isAddToBackStack: Boolean = false) {
    childFragmentManager.inTransaction({
        fragment?.let {
            add(frameId, fragment, tag)
        }
    }, isAddToBackStack)
}*/

/**
 * removeFragment
 * @param fragment = fragment
 * @param frameId = containerId
 * @param tag = tag
 */
fun AppCompatActivity.replaceFragment(fragment: Fragment?, frameId: Int, tag: String? = null, isAddToBackStack: Boolean = false) {
    supportFragmentManager.inTransaction({
        fragment?.let {
            replace(frameId, it, tag)
        }
    }, isAddToBackStack)
}

/**
 * beginTransaction , addFunction and commit
 * @param function = function which is of type FragmentTransaction
 */
inline fun FragmentManager.inTransaction(function: FragmentTransaction.() -> FragmentTransaction?, isAddToBackStack: Boolean = true) {
    val transaction = beginTransaction().function()
    if (isAddToBackStack) {
        transaction?.addToBackStack(null)
    }
    transaction?.commitAllowingStateLoss()
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun String.getNameInitials(): String {
    val result = StringBuilder()
    val stringArray = split(" ")
    var counter = 0
    for (element in stringArray) {
        val charArray = element.toCharArray()
        if (charArray.isNotEmpty()) {
            ++counter
            result.append(charArray[0].toUpperCase())
        }
        if (counter == 2) break
    }
    return result.toString()
}

/**
 *  gives callbacks whenever any changes are recorded in the given EditText
 */
fun EditText.textChangeListener(onTextChange: (s: CharSequence?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChange.invoke(s)
        }

    })
}

fun ViewPager.onPageChangeListener(onPageSelected: (currentPosition: Int) -> Unit) {
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(currentPosition: Int) {
            onPageSelected.invoke(currentPosition)
        }

        override fun onPageScrollStateChanged(p0: Int) {

        }

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

        }
    })
}

fun TextView.setCustomText(str: String?) {
    text = if (str.isNullOrBlank()) resources.getString(R.string.dash) else str
}

fun TextView.setTextColor(color: String?) {
    try {
        color?.let {
            setTextColor(Color.parseColor(color))
        }
    } catch (e: java.lang.Exception) {
    }
}

/**
 * @method changes the status bar theme to LIGHT_STATUS_BAR with soft keyboard open
 */
fun AppCompatActivity.setLightStatusBarWithSoftInput() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.let {
            // Default behavior is that if navigation bar is hidden, the system will "steal" touches
            // and show it again upon user's touch. We just want the user to be able to show the
            // navigation bar by swipe, touches are handled by custom code -> change system bar behavior.
            // Alternative to deprecated SYSTEM_UI_FLAG_IMMERSIVE.
            it.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            // make navigation bar translucent (alternative to deprecated
            // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            // - do this already in hideSystemUI() so that the bar
            // is translucent if user swipes it up
            // Finally, hide the system bars, alternative to View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            // and SYSTEM_UI_FLAG_FULLSCREEN.
            //it.hide(WindowInsets.Type.statusBars())
        }

    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }
    }
}

fun AppCompatActivity.setStatusBarWithCustomColour(color: Int? = null) {
    color?.let {
        window.statusBarColor = it
    } ?: kotlin.run {
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.let {
            it.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            it.show(WindowInsets.Type.statusBars())
        }

    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}

fun AppCompatActivity.showOrHideSoftInputKeyboard(show: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.let {
            if (show) {
                it.show(WindowInsets.Type.ime())
            } else {
                it.hide(WindowInsets.Type.ime())
            }
        }
    }
}


/**
 * @method changes the status bar theme to LIGHT_STATUS_BAR without soft keyboard open
 */
//fun AppCompatActivity.setLightStatusBarWithoutSoftInput() {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        val wic = window.decorView.windowInsetsController
////        wic?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS)
////        wic?.hide(WindowInsets.Type.ime())
////        window.statusBarColor = Color.WHITE
////    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
////        window.statusBarColor = Color.WHITE
//    }
//}


/**
 * Extension function to line by line in BufferedReader
 */
val BufferedReader.lines: Iterator<String?>
    get() = object : Iterator<String?> {
        var line = this@lines.readLine()

        override fun next(): String? {
            if (line == null)
                throw NoSuchElementException()
            val result = line!!

            line = this@lines.readLine()
            return result
        }

        override fun hasNext() = line != null

    }

fun Context.showOOPSToast() {
    Toast.makeText(this, getString(R.string.str_something_went_wrong), Toast.LENGTH_SHORT).show()
}

fun Double.getFormattedDoubleString() =
        if (this.rem(1) == 0.0)
            String.format("%d", this.toLong())
        else
            String.format("%d", this.toLong())


/**
 * <html><body><h4>Description</h4><font color='#868686'>Shabu-shabu is a Japanese cooking style where dinners cook thin slices of meat quickly in boiling broth and then dip them in sauce.</font><h4>How to Redeem</h4><font color='#868686'>1. Integer egestas scelerisque felis id auctor. Praesent sit amet volutpatsem.daksdkajdskajdkajkjskldjakldjlakdj<br>2.Aliquam varius, ipsum eget elementum     rutrum, tortor ligula vehicula elit<br>3. Tortor ligula vehicula elit, elementum eugiat quam diam vel nisi. Donec pretium.</font></body></html>
 * eg: tv.text = message.getHtmlSpannable()
 */
fun String.getHtmlSpannable(): Spanned? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(this)
        }

/* Convert string value to dot separated */
fun String.formatNumberWithDots(exceptionListener: () -> Unit = {}): String {
    return try {
        val lastIndex = this.lastIndexOf('.')
        val finalString = if (lastIndex != -1) this.substring(0, lastIndex) else this
        val output = DecimalFormat("#,###,###").format(finalString.toLong())
        output.replace(",", ".")
    } catch (numberFormatException: NumberFormatException) {
        exceptionListener.invoke()
        Timber.e("NumberFormatException in formatNumberWithDots %s ", numberFormatException.message)
        this
    }
}

/**
 * returns a bitmap with background and getpluslogo above it
 */
fun Context?.getLogoWithBackgroundBitmap(@ColorRes backgroundColorId: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    this?.let { paint.color = ContextCompat.getColor(it, backgroundColorId) }
    paint.style = Paint.Style.FILL
    canvas.drawPaint(paint)
    this?.let {
        val getPlusLogoBitmap = getLogoBitmap()
        canvas.drawBitmap(getPlusLogoBitmap, (canvas.width - getPlusLogoBitmap.width) / 2f, (canvas.height - getPlusLogoBitmap.height) / 2f, null)
    }
    return bitmap
}

/**
 * returns a bitmap of logo
 */
fun Context.getLogoBitmap(): Bitmap {
    val iconBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground)
    return Bitmap.createScaledBitmap(iconBitmap, 100, 100, false)
}

/**
 * IN cae you are using this in the MainThread and you are sure that it's not going to be used in different threads,
 * then you can avoid all of this overhead to make it Thread Safe and just use it like this:
 * check [LazyThreadSafetyMode.NONE]
 */
fun <T> nonSafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

/**
 * check [LazyThreadSafetyMode.PUBLICATION]
 */
fun <T> nonDeadlockLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.PUBLICATION, initializer)

/* Format the yyyy-MM-dd format string date into dd MMMM yyyy string date*/
fun String.dateFormatter(): String {

    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
    try {
        return outputDateFormat.format(inputDateFormat.parse(this))
    } catch (e: ParseException) {
        Timber.e("ParseException in dateFormatter %s", e.message)
        return ""
    }
}

fun Context.pxToDp(px: Float): Float {
    return px / resources.displayMetrics.density
}

fun Context.dpToPx(dp: Float): Float {
    return dp * resources.displayMetrics.density
}


fun ImageView.loadFromAsset(url: String?) {
    url?.let {
        val inputStream = context.assets.open(url)
        val drawable = Drawable.createFromStream(inputStream, null)
        setImageDrawable(drawable)
    }
}


/*Checks the password complexity and returns the result*/
fun String.checkPasswordComplexity(): Boolean {
    val combination = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}\$"
    val pattern = Pattern.compile(combination)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

fun String.isValidNumber(): Boolean {
    val combination = "[0-9]+"
    val pattern = Pattern.compile(combination)
    val matcher = pattern.matcher(this)
    return isNotBlank() && matcher.matches()
}

fun String.capitalizeFirstLetter(): String {
    return if (this.length > 1)
        this.substring(0, 1).toUpperCase() + this.substring(1).toLowerCase()
    else toUpperCase()
}

fun EditText.hideDrawableEnd() {
    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
}

fun Activity.finishActivity() {
    if (!isFinishing) finish()
}

fun String.dateStringFormatter(): String {

    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val outputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    return try {
        outputDateFormat.format(inputDateFormat.parse(this))
    } catch (e: ParseException) {
        Timber.e("ParseException in dateFormatter %s", e.message)
        ""
    }
}

fun String.convertStringToDate(): Date {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return try {
        inputDateFormat.parse(this)
    } catch (e: ParseException) {
        Timber.e("ParseException in dateFormatter %s", e.message)
        Date()
    }
}

fun String.convertStringToDateFormat(): Date {
    val inputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    return try {
        inputDateFormat.parse(this)
    } catch (e: ParseException) {
        Timber.e("ParseException in dateFormatter %s", e.message)
        Date()
    }
}

fun String.convertYYYYMMDDTStringToDateFormat(): Date {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    return try {
        inputDateFormat.parse(this)
    } catch (e: ParseException) {
        Timber.e("ParseException in dateFormatter %s", e.message)
        Date()
    }
}

fun String.isValidEmail(): Boolean {
    return this.let {
        it.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(it.trim()).matches()
    }
}

fun String.isValidPhone(): Boolean {
    return this.let {
        it.isNotBlank() && Patterns.PHONE.matcher(it).matches()
    }
}

fun EditText.disableEditText() {
    isCursorVisible = false
    isClickable = false
    isFocusable = false
}


fun View.setViewWidthHeight(width: Int, height: Int) {
    val params = layoutParams
    params.width = width
    params.height = height
    layoutParams = params
}

fun View.getViewHeight(): Int {
    this.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
    return this.measuredHeight
}

/*fun Activity.setCookie(message: String, titleId: Int = R.string.success, backgroundColorId: Int = R.color._8cc63f,
                       drawable: Int = R.drawable.ic_img_toast_success, cookieDismissListener: (() -> Unit) = {}) {
    CookieBar.dismiss(this)
    (this as BaseActivity).disableBackPress(true)
    CookieBar.build(this)
            .setTitle(titleId)
            .setMessage(message)
            .setBackgroundColor(backgroundColorId)
            .setIcon(drawable)
            .setDuration(5000L)
            .setCookiePosition(CookieBar.TOP)
            .show()
    Handler(Looper.getMainLooper()).postDelayed({
        disableBackPress(false)
        cookieDismissListener.invoke()
    }, 2000L)

}*/

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun <T> Context.launchActivity(activity: Class<T>, bundle: Bundle? = null) {
    val intent = Intent(this, activity)
    bundle?.let { intent.putExtras(it) }
    startActivity(intent)
}

fun <T> Activity.launchActivityForResult(activity: Class<T>, requestCode: Int, bundle: Bundle? = null) {
    val intent = Intent(this, activity)
    bundle?.let { intent.putExtras(it) }
    startActivityForResult(intent, requestCode)
}

fun View.disableBackPress(isDisabled: Boolean) {
    this.requestFocus()
    this.isFocusableInTouchMode = isDisabled
    this.setOnKeyListener { _, keyCode, keyEvent ->
        if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
            isDisabled
        } else false
    }
}

fun Context.launchBrowser(url: String?) {
    try {
        if (url?.isNotBlank() == true) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    } catch (e: Exception) {
        Timber.e(e)
    }
}

fun String?.defaultString(): String = this ?: ""

fun Int?.defaultInt(): Int = this ?: 0

fun String.removeSubString(subString: String): String {
    return replace(subString, "")
}

fun ByteArray.convertByteArrayToBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, size, BitmapFactory.Options().apply {
        inScaled = false
    })
}

fun String.changeDecimalToThreeDot(): String {
    return this.substringBefore('.').formatNumberWithDots()
}

fun String.removePhonePrefix(): String {
    return when {
        startsWith("91") -> substring(2)
        startsWith("+91 ") -> substring(4)
        startsWith("+91") -> substring(3)
        startsWith("0") -> substring(1)
        else -> this
    }
}

fun View.customShapeBackground(backgroundColor: String?) {
    val shape = GradientDrawable()
    shape.shape = GradientDrawable.RECTANGLE
    shape.cornerRadii = floatArrayOf(32f, 32f, 32f, 32f, 32f, 32f, 32f, 32f)
    shape.setColor(Color.parseColor(backgroundColor ?: "#F06292"))
//    shape.setColor(Color.parseColor(String.format("#%06X", 0xFFFFFF and backgroundColor)))
    background = shape
}

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun Bitmap.rotateImage(degrees: Float): Bitmap? {
    return try {
        if (degrees == 0F) return this
        val matrix = Matrix()
        matrix.postRotate(degrees)
        val scaledBitmap = Bitmap.createScaledBitmap(this, width, height, true)
        Bitmap.createBitmap(scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix, true)
    } catch (e: Exception) {
        Timber.e(e)
        this
    }
}

fun <T> MutableList<T>.clearAndAdd(items: List<T>) {
    clear()
    addAll(items)
}

inline fun <T : Fragment> T.withArgs(
        argsBuilder: Bundle.() -> Unit
): T = this.apply {
    arguments = Bundle().apply(argsBuilder)
}

fun EditText.setonCurrencyChangeListener() {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null && s.toString().isNotBlank()) {
                removeTextChangedListener(this)
                val cleanString: String = s.replace("""[.]""".toRegex(), "")
                val parsed = cleanString.toDouble()
                val formatted = parsed.getFormattedDoubleString().formatNumberWithDots { }
                setText(formatted)
                setSelection(formatted.length)
                addTextChangedListener(this)
            }
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}


fun WebView.loadHtmlStringContent(htmlContent: String) {
    settings.javaScriptEnabled = true
    loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
}

fun String.getLastPathParam() = Uri.parse(this).lastPathSegment.orEmpty()


fun String.checkForAlphabet(): Boolean {
    forEach {
        if(!it.isLetter()) return false
    }
    return true
}

fun TextView.setHtmlString(str: String) {
    this.text = HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY)
}


// Extension function only meant for this Xml = item_tier_tooltip_detail
/*fun Balloon.setTooltipUi(headingTextString: String? = null , descriptionTextString: String? = null , font: Int = R.font.poppins_semibold, isHtml: Boolean = false, headingTextSize: Float? = null) {
    val typeface = ResourcesCompat.getFont(this.getContentView().context, font)
    val headingText = this.getContentView()
        .findViewById<TextView>(R.id.tv_tier_tooltip_detail_heading)
    if(headingTextString != null) {
        headingText.showView()
        headingText.text  = headingTextString
    } else {
        headingText.hideView()
    }
    headingTextSize?.let {
        headingText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, headingTextSize
        )
    }
    headingText.typeface = typeface
    val descriptionText = this.getContentView()
        .findViewById<TextView>(R.id.tv_tier_tooltip_detail_description)
    if(descriptionTextString != null) {
        descriptionText.showView()
        if (!isHtml)
            descriptionText.text = descriptionTextString
        else
            descriptionText.setHtmlString(descriptionTextString)
    } else {
        descriptionText.hideView()
    }
}*/

    fun TextView.changeFont(style: Int?, textSize: Float?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var typeface: Typeface? = null
            style?.let {
                typeface = ResourcesCompat.getFont(this.context, style)
            }
            this.typeface = typeface
            textSize?.let {
            this.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, textSize
            )
        }
        }
    }
