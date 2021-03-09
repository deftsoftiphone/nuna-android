package com.demo.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.InputFilter
import android.text.Selection
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.demo.R
import com.demo.base.MainApplication
import com.demo.model.response.baseResponse.Category
import com.demo.model.response.baseResponse.HashTag
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.model.response.baseResponse.PostCategory
import com.demo.muebert.modal.CategoriesItem
import com.demo.muebert.modal.ChannelsItem
import com.demo.viewPost.services.VideoPreLoadingService
import com.demo.viewPost.utils.ConstantObjects
import com.google.android.material.tabs.TabLayout
import kotlinx.android.parcel.RawValue
import org.json.JSONObject
import java.io.*
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

fun String.mediaUrlAccordingToInternetSpeed(speed: Double): String {
    try {
        var url = this.subSequence(0, this.length - 8)
        val extension = ".m3u8"
        url = if (speed >= 1024)
            "${url}720"
        else "${url}540"
        return "$url$extension"
    } catch (e: Exception) {
        e.printStackTrace()
        return this
    }
}

fun String.toMp4Url(): String {
    return try {
        if (this.contains(".m3u8")) {
            val url = this.subSequence(0, this.length - 9)
            val newUrl = "${url}.mp4"
            return newUrl.replace("HLS", "MP4")
        } else {
            this
        }
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}

fun getBitmapFromURL(src: String?): Bitmap? {
    return try {
        val url = URL(src)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream
        BitmapFactory.decodeStream(input)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun containsIllegalCharacters(displayName: String): Boolean {
    val nameLength = displayName.length
    for (i in 0 until nameLength) {
        val hs = displayName[i]
        if (0xd800 <= hs.toInt() && hs.toInt() <= 0xdbff) {
            val ls = if (i + 1 < nameLength) displayName[i + 1] else displayName[i]
            val uc = (hs.toInt() - 0xd800) * 0x400 + (ls.toInt() - 0xdc00) + 0x10000
            if (0x1d000 <= uc && uc <= 0x1f77f) {
                return true
            }
        } else if (Character.isHighSurrogate(hs)) {
            val ls = displayName[i + 1]
            if (ls.toInt() == 0x20e3) {
                return true
            }
        } else {
            // non surrogate
            if (0x2100 <= hs.toInt() && hs.toInt() <= 0x27ff) {
                return true
            } else if (0x2B05 <= hs.toInt() && hs.toInt() <= 0x2b07) {
                return true
            } else if (0x2934 <= hs.toInt() && hs.toInt() <= 0x2935) {
                return true
            } else if (0x3297 <= hs.toInt() && hs.toInt() <= 0x3299) {
                return true
            } else if (hs.toInt() == 0xa9 || hs.toInt() == 0xae || hs.toInt() == 0x303d || hs.toInt() == 0x3030 || hs.toInt() == 0x2b55 || hs.toInt() == 0x2b1c || hs.toInt() == 0x2b1b || hs.toInt() == 0x2b50) {
                return true
            }
        }
    }
    return false
}

fun View.hideView() {
    animate()
        .alpha(0.0f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                setVisibility(View.GONE)
            }
        })
}

fun View.showView() {
    animate()
        .alpha(1.0f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                setVisibility(View.VISIBLE)
            }
        })
}

fun Float.roundOff(): Float? {
    val df = DecimalFormat("#.#")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toFloat()
}

fun Double.roundOff(): Double? {
    val df = DecimalFormat("#.#")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toDouble()
}

fun runWithDelay(delay: Long, run: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        run()
    }, delay)
}

fun compressBitmap(
    file: String?,
    width: Int,
    height: Int,
    maxSizeBytes: Int
): ByteArray? {
    val bmpFactoryOptions: BitmapFactory.Options = BitmapFactory.Options()
    bmpFactoryOptions.inJustDecodeBounds = true
    val bitmap: Bitmap
    val heightRatio = Math.ceil((bmpFactoryOptions.outHeight / height.toFloat()).toDouble()).toInt()
    val widthRatio = Math.ceil((bmpFactoryOptions.outWidth / width.toFloat()).toDouble()).toInt()
    if (heightRatio > 1 || widthRatio > 1) {
        if (heightRatio > widthRatio) {
            bmpFactoryOptions.inSampleSize = heightRatio
        } else {
            bmpFactoryOptions.inSampleSize = widthRatio
        }
    }
    bmpFactoryOptions.inJustDecodeBounds = false
    bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions)
    val stream = ByteArrayOutputStream()
    var currSize: Int
    var currQuality = 100
    do {
        bitmap.compress(Bitmap.CompressFormat.JPEG, currQuality, stream)
        currSize = stream.toByteArray().size
        // limit quality by 5 percent every time
        currQuality -= 20
    } while (currSize >= maxSizeBytes)
    return stream.toByteArray()
}

fun compressImage(filePath: String, fileName: String): String? {
    var scaledBitmap: Bitmap? = null
    val options: BitmapFactory.Options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
    options.inJustDecodeBounds = true
    var bmp = BitmapFactory.decodeFile(filePath, options)
    var actualHeight = options.outHeight
    var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
    val maxHeight = 612.0f
    val maxWidth = 383.0f
    var imgRatio = actualWidth / actualHeight.toFloat()
    val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
    if (actualHeight > maxHeight || actualWidth > maxWidth) {
        if (imgRatio < maxRatio) {
            imgRatio = maxHeight / actualHeight
            actualWidth = (imgRatio * actualWidth).toInt()
            actualHeight = maxHeight.toInt()
        } else if (imgRatio > maxRatio) {
            imgRatio = maxWidth / actualWidth
            actualHeight = (imgRatio * actualHeight).toInt()
            actualWidth = maxWidth.toInt()
        } else {
            actualHeight = maxHeight.toInt()
            actualWidth = maxWidth.toInt()
        }
    }

//      setting inSampleSize value allows to load a scaled down version of the original image
    options.inSampleSize = 4
//    options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
    options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
    options.inPurgeable = true
    options.inInputShareable = true
    options.inTempStorage = ByteArray(16 * 1024)
    try {
//          load the bitmap from its path
        bmp = BitmapFactory.decodeFile(filePath, options)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
    }
    try {
        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
    }
    val ratioX = actualWidth / options.outWidth.toFloat()
    val ratioY = actualHeight / options.outHeight.toFloat()
    val middleX = actualWidth / 2.0f
    val middleY = actualHeight / 2.0f
    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
    val canvas = Canvas(scaledBitmap!!)
    canvas.setMatrix(scaleMatrix)
    canvas.drawBitmap(
        bmp,
        middleX - bmp.width / 2,
        middleY - bmp.height / 2,
        Paint(Paint.FILTER_BITMAP_FLAG)
    )

//      check the rotation of the image and display it properly
    val exif: ExifInterface
    try {
        exif = ExifInterface(filePath)
        val orientation: Int = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, 0
        )
        Log.d("EXIF", "Exif: $orientation")
        val matrix = Matrix()
        if (orientation == 6) {
            matrix.postRotate(90F)
            Log.d("EXIF", "Exif: $orientation")
        } else if (orientation == 3) {
            matrix.postRotate(180F)
            Log.d("EXIF", "Exif: $orientation")
        } else if (orientation == 8) {
            matrix.postRotate(270F)
            Log.d("EXIF", "Exif: $orientation")
        }
        scaledBitmap = Bitmap.createBitmap(
            scaledBitmap, 0, 0,
            scaledBitmap.width, scaledBitmap.height, matrix,
            true
        )
    } catch (e: IOException) {
        e.printStackTrace()
    }
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(fileName)

//          write the compressed bitmap at the destination specified by filename.
        scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, out)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    return fileName
}


fun Context.startPreLoadingService(posts: ArrayList<PostAssociated>) {
    val urls = ArrayList<String>()
    posts.forEach { post ->
        post.medias?.let { mds ->
            if (mds.isNotEmpty()) {
                val filtered = mds.filter { m -> m.isImage == false }
                if (!filtered.isNullOrEmpty())
                    filtered[0].mediaUrl?.let { it1 -> urls.add(it1) }
            }
        }
    }

    val preloadingServiceIntent = Intent(this, VideoPreLoadingService::class.java)
    preloadingServiceIntent.putStringArrayListExtra(ConstantObjects.VIDEO_LIST, urls)
    startService(preloadingServiceIntent)
}

fun Context.stopPreLoadingService() {
    val preloadingServiceIntent = Intent(this, VideoPreLoadingService::class.java)
    stopService(preloadingServiceIntent)
}

fun generatePostFileName(): String {
    return "Shawkeen_post_${System.currentTimeMillis()}"
}

fun TabLayout.getTextViewFromTabItem(index: Int): TextView {
    val rLayout = this.getTabAt(index)
    return rLayout?.view?.getChildAt(1) as TextView
}

fun Context.toDP(size: Float): Int {
    val scale: Float = resources.displayMetrics.density
    return (size * scale + 0.5f).toInt()
}

/*fun Context.toDP(size: Int): Int {
    val scale: Float = resources.displayMetrics.density
    return (size * scale + 0.5f).toInt()
}*/

fun ArrayList<CategoriesItem>.parseCategories(): ArrayList<CategoriesItem> {
    val result = ArrayList<CategoriesItem>()
    this.forEach { category ->
        val cat = category
        cat.icon = getIcon(cat.name)
        if (cat.channels == null) cat.channels = ArrayList()

        category.groups?.forEach { group ->
            group.channels?.let {
                val temp = ArrayList<ChannelsItem>()
                it.forEach { channel ->
                    val c = channel
                    c.groupName = group.name
                    c.groupId = group.groupId
                    c.categoryId = category.categoryId
                    c.categoryName = category.name
                    temp.add(c)
                }
                cat.channels!!.addAll(temp)
            }
        }
        cat.groups = ArrayList()
        result.add(cat)
    }
    return getNewList(result)
}

fun getNewList(oldList: java.util.ArrayList<CategoriesItem>): ArrayList<CategoriesItem> {
    val result = ArrayList<CategoriesItem>()
    for (i in 0..7) {
        when (i) {
            0 -> {
                val cat = CategoriesItem()
                cat.apply {
                    name = "Chillout"
                    icon = getIcon(name)
                    channels = getChannelsByName(
                        listOf(
                            "Downtempo",
                            "Chill",
                            "Psychill",
                            "Chillout",
                            "Lofi",
                            "Chill Hop",
                            "Atmosphere",
                            "Ambient",
                            "Darkambient",
                            "Chillgressive",
                            "Lounge",
                            "Chillrave"
                        ), oldList
                    )
                }
                result.add(cat)
            }
            1 -> {
                val cat = CategoriesItem()
                cat.apply {
                    name = "Workout"
                    icon = getIcon(name)
                    channels = getChannelsByName(
                        listOf(
                            "Fitness 90",
                            "Energy 100",
                            "Cardio 120",
                            "Cardio 130",
                            "Run 130",
                            "Run 140",
                            "Run 160",
                            "Run 180"
                        ), oldList
                    )
                }
                result.add(cat)
            }
            2 -> {
                val cat = CategoriesItem()
                cat.apply {
                    name = "Cinemetic"
                    icon = getIcon(name)
                    channels = getChannelsByName(
                        listOf(
                            "Pumped",
                            "Happy",
                            "Optimistic",
                            "Sad",
                            "Serious",
                            "Beautiful",
                            "Romantic",
                            "Peaceful",
                            "Dreamy",
                            "Epic",
                            "Dramatic",
                            "Groovy",
                            "Spooky",
                            "Upbeat",
                            "Friends",
                            "Night",
                            "Extreme"
                        ), oldList
                    )
                }
                result.add(cat)
            }
            3 -> {
                val cat = CategoriesItem()
                cat.apply {
                    name = "Meditate"
                    icon = getIcon(name)
                    channels = getChannelsByName(
                        listOf(
                            "Calm",
                            "Gentle",
                            "Sentimental",
                            "Motivation",
                            "Minimal 120",
                            "Minimal 170",
                            "Ambient",
                            "Piano",
                            "Lullaby",
                            "Meditation",
                            "Om",
                            "Zen",
                            "Chillout",
                            "Ethnic 108",
                            "Summer",
                            "Travel"
                        ), oldList
                    )
                }
                result.add(cat)
            }
            4 -> {
                val cat = CategoriesItem()
                cat.apply {
                    name = "Dance"
                    icon = getIcon(name)
                    channels = getChannelsByName(
                        listOf(
                            "EDM",
                            "Electronica",
                            "IDM",
                            "Braindance",
                            "Synthwave",
                            "Trance",
                            "Uplifting Trance",
                            "Psytrance",
                            "Techno",
                            "Dubtechno",
                            "Hardtechno",
                            "Bassline",
                            "Trap",
                            "Drumnbass",
                            "Liquidfunk",
                            "Neurofunk",
                            "Microfunk",
                            "Dub",
                            "Glitch Hop",
                            "Breakbeat",
                            "Acid Jazz",
                            "Electro Funk",
                            "Funk",
                            "Nu Disco",
                            "Indie Dance",
                            "White Noise",
                            "Pink Noise",
                            "Brown Noise"
                        ), oldList
                    )
                }
                result.add(cat)
            }
            5 -> {
                val cat = CategoriesItem()
                cat.apply {
                    name = "Pop"
                    icon = getIcon(name)
                    channels = getChannelsByName(
                        listOf(
                            "Pop",
                            "Future Pop",
                            "Country Pop",
                            "Latin Pop",
                            "Indie Pop",
                            "Indie Rock",
                            "Post-Rock",
                            "Hiphop"
                        ), oldList
                    )

                }
                result.add(cat)
            }
            6 -> {
                val cat = CategoriesItem()
                cat.apply {
                    name = "House"
                    icon = getIcon(name)
                    channels = getChannelsByName(
                        listOf(
                            "Tropical House",
                            "House",
                            "Deep House",
                            "Minimal House",
                            "Bassline House",
                            "Disco House",
                            "Melodic House",
                            "Tropical House"
                        ), oldList
                    )
                }
                result.add(cat)
            }
            7 -> {
                val cat = CategoriesItem()
                cat.apply {
                    name = "Classic"
                    icon = getIcon(name)
                    channels = getChannelsByName(
                        listOf(
                            "Classical",
                            "Neo-classic",
                            "World Music",
                            "Folk",
                            "Acoustic",
                            "Reggaeton",
                            "Slow Ballad"
                        ), oldList
                    )
                }
                result.add(cat)
            }
        }
    }
    return result
}

fun getChannelsByName(
    desiredChannels: List<String>,
    categoriesList: java.util.ArrayList<CategoriesItem>
): java.util.ArrayList<ChannelsItem>? {
    val channelList = ArrayList<ChannelsItem>()
    for (i in desiredChannels.indices) {
        for (j in categoriesList.indices) {
            for (k in categoriesList[j].channels!!.indices) {
                if (desiredChannels[i] == categoriesList[j].channels!![k].name) {
                    val c = categoriesList[j].channels!![k]
                    channelList.add(categoriesList[j].channels!![k])
                    break
                }
            }
        }
    }
    return channelList
}

@SuppressLint("UseCompatLoadingForDrawables")
fun getIcon(name: String?): @RawValue Drawable? {
    return when (name) {
        "Chillout" -> MainApplication.get().getDrawable(R.drawable.moods_selector_bg)
        "Workout" -> MainApplication.get().getDrawable(R.drawable.focus_selector_bg)
        "Cinemetic" -> MainApplication.get().getDrawable(R.drawable.sleep_selector_bg)
        "Meditate" -> MainApplication.get().getDrawable(R.drawable.calm_selector_bg)
        "Dance" -> MainApplication.get().getDrawable(R.drawable.chill_selector_bg)
        "Pop" -> MainApplication.get().getDrawable(R.drawable.sport_selector_bg)
        "House" -> MainApplication.get().getDrawable(R.drawable.genres_selector_bg)
        "Classic" -> MainApplication.get().getDrawable(R.drawable.classical_selector_bg)
        else -> null
    }
}

fun String.getFileName(): String {
    if (TextUtils.isEmpty(this))
        return ""

    return this.split("/").last()
}


fun generateFileName(path: String): String {
    return System.currentTimeMillis().toString() + path.substring(path.lastIndexOf("."))
}

fun Context.getMIMEType(uri: Uri): String? {
    var mimeType: String? = null
    mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        val cr: ContentResolver = contentResolver
        cr.getType(uri)
    } else {
        val fileExtension: String = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileExtension.toLowerCase(Locale.getDefault())
        )
    }
    return mimeType
}

fun <T> toJsonObject(vararg pairs: Pair<String, T>): JSONObject {
    val parentObject = JSONObject()
    pairs.forEach { pair ->
        parentObject.put(pair.first, pair.second)
    }
    return parentObject
}

fun RecyclerView.setupScroller(offset: Int, allLoaded: Boolean, onResult: () -> Unit) {
    setOnScrollChangeListener { _, _, _, _, _ ->
        val v = getChildAt(childCount - 1)
        val d = v.bottom - (height + scrollY)
        if (d == 0 && !allLoaded) {
            onResult()
        }
    }
}

fun ArrayList<Category>.removeCategoriesWithNoPost(categoryName: String): ArrayList<Category> {
    val categories = ArrayList<Category>()
    this.forEach {
        if (!it.postAssociated.isNullOrEmpty() || it.categoryName == categoryName)
            categories.add(it)
    }
    return categories
}

fun View.setMargins(
    l: Int,
    t: Int,
    r: Int,
    b: Int
) {
    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        val p =
            this.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(l, t, r, b)
        this.requestLayout()
    }
}

fun Context.updateLanguage() {
    this.selectLanguage(Prefs.init().selectedLangId)
}

fun Context.selectLanguage(languageId: Int?) {
    val lang = when (languageId ?: Prefs.init().selectedLangId) {
        1 -> "hi"
        2 -> "en"
        3 -> "mr"
        4 -> "bn"
        5 -> "ta"
        6 -> "te"
        7 -> "gu"
        8 -> "kn"
        else -> ""
    }

    this.setApplicationLanguage(lang)
}


fun Context.setApplicationLanguage(newLanguage: String) {
    val activityRes = resources
    val activityConf = activityRes.configuration
    val newLocale = Locale(newLanguage)
    activityConf.setLocale(newLocale)
    activityRes.updateConfiguration(activityConf, activityRes.displayMetrics)
    val applicationRes = resources
    val applicationConf = applicationRes.configuration
    applicationConf.setLocale(newLocale)
    applicationRes.updateConfiguration(
        applicationConf,
        applicationRes.displayMetrics
    )
}

fun EditText.cursorAtEnd() {
    Selection.setSelection(this.text, this.text!!.length)
}

fun <T> List<T>.listToArrayList(): ArrayList<T> {
    val temp = ArrayList<T>()
    temp.addAll(this)
    return temp
}

fun <T> MutableList<T>.mutableToArrayList(): ArrayList<T> {
    val temp = ArrayList<T>()
    temp.addAll(this)
    return temp
}

fun PostCategory.fetchCurrentLanguageCategoryName(): String {
    return when (Prefs.init().selectedLang.languageIntId) {
        1 -> this.hindi!!
        2 -> this.categoryName!!
        3 -> this.marathi!!
        4 -> this.bengali!!
        5 -> this.tamil!!
        6 -> this.telugu!!
        7 -> this.gujrati!!
        8 -> this.kannada!!
        else -> "Category"
    }
}

fun Context.clearNotification() {
    val notificationManger = getSystemService(this, NotificationManager::class.java)
    notificationManger?.cancelAll()
}


operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
    val value = (this.value ?: ArrayList()).apply {
        addAll(values)
    }
    this.value = value
}

fun <T> MutableLiveData<HashSet<T>>.addToSet(values: List<T>) {
    val value = (this.value ?: HashSet()).apply {
        addAll(values)
    }
    this.value = value
}

fun <T> MutableLiveData<ArrayList<T>?>.nullAdd(values: List<T>) {
    val value = (this.value ?: ArrayList()).apply {
        addAll(values)
    }
    this.value = value
}

fun HashSet<HashTag>.toArrayList(): ArrayList<HashTag> {
    return ArrayList(this.sortedBy { s -> s.noOfPosts })
}

@SuppressLint("HardwareIds")
fun Context.getUniqueToken(): String {
    return Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ANDROID_ID
    )
}

fun TabLayout.setTabsHeight() {
    if (Prefs.init().selectedLangId == 2)
        layoutParams.height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            30f,
            resources.displayMetrics
        ).roundToInt()
    else
        layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
}

fun Context.toDP(value: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources?.displayMetrics
    ).toInt()
}


class Util {

    companion object {

        fun profileImageRequestOptions(): RequestOptions {
            return RequestOptions().also {
//                it.placeholder(R.drawable.placeholder_image)
                it.placeholder(R.drawable.ic_placeholder_user_small)
                it.error(R.drawable.ic_placeholder_user_small)
                it.override(200, 200)
                it.diskCacheStrategy(DiskCacheStrategy.ALL)
                it.format(DecodeFormat.PREFER_RGB_565)
            }
        }

        fun postRequestOptions(): RequestOptions {
            return RequestOptions().also {
                it.placeholder(R.drawable.dashboard_item_bg)
                it.error(R.drawable.dashboard_item_bg)
//                it.override(350, 600)
                it.override(600, 800)
                it.diskCacheStrategy(DiskCacheStrategy.ALL)
                it.format(DecodeFormat.PREFER_RGB_565)
            }
        }

        fun postThumbnailOptions(): RequestOptions {
            return RequestOptions().also {
//                it.override(350, 600)
                it.override(600, 800)
                it.diskCacheStrategy(DiskCacheStrategy.ALL)
                it.format(DecodeFormat.PREFER_RGB_565)
            }
        }

        fun pxToDp(context: Context, sizeInDp: Int): Int {
            val scale: Float = context.resources.displayMetrics.density
            return (sizeInDp * scale + 0.5f).toInt()
        }

        fun checkIfHasNetwork(): Boolean {
            val connectivityManager =
                MainApplication.get()
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        fun Context.hasInternet(): Boolean {
            if (!checkIfHasNetwork()) {
                Validator.showMessage(getString(R.string.connectErr))
                return false
            }
            return true
        }

        fun isPackageExist(context: Context, target: String): Boolean {
            return context.packageManager.getInstalledApplications(0)
                .find { info -> info.packageName == target } != null
        }


        fun indexOfDiff(str1: String?, str2: String?): Int {
            if (str1 === str2) {
                return -1
            }
            if (str1 == null || str2 == null) {
                return 0
            }
            var i = 0
            while (i < str1.length && i < str2.length) {
                if (str1[i] != str2[i]) {
                    break
                }
                ++i
            }
            return if (i < str2.length || i < str1.length) {
                i
            } else -1
        }


        fun log(msg: String) {
            Log.d("APP_LOGS", msg)
        }


        fun reduceMarginsInTabs(tabLayout: TabLayout, marginOffset: Int) {
            val tabStrip: View = tabLayout.getChildAt(0)
            if (tabStrip is ViewGroup) {
                val tabStripGroup: ViewGroup = tabStrip as ViewGroup
                for (i in 0 until (tabStrip as ViewGroup).getChildCount()) {
                    val tabView: View = tabStripGroup.getChildAt(i)
                    if (tabView.layoutParams is ViewGroup.MarginLayoutParams) {
                        (tabView.layoutParams as ViewGroup.MarginLayoutParams).leftMargin =
                            marginOffset
                        (tabView.layoutParams as ViewGroup.MarginLayoutParams).rightMargin =
                            marginOffset
                    }
                }
                tabLayout.requestLayout()
            }
        }

        fun getFilter(limit: Int, spaceEnable: Boolean): Array<InputFilter> {
            val inputFilter = InputFilter(fun(
                source: CharSequence,
                start: Int,
                end: Int,
                _: Spanned,
                _: Int,
                _: Int
            ): String? {
                for (index in start until end) {
                    val type = Character.getType(source[index])
                    if (spaceEnable) {
                        if (type == Character.SURROGATE.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                            return ""
                        }
                    } else {
                        if (type == Character.SURROGATE.toInt() || type != Character.LOWERCASE_LETTER.toInt()
                            || type == Character.NON_SPACING_MARK.toInt() || Character.isWhitespace(
                                source[index]
                            )
                        ) {
                            return ""
                        }
                    }
                }
                return null
            })
            return arrayOf(inputFilter, InputFilter.LengthFilter(limit))
        }


        fun userNameFilter(limit: Int): Array<InputFilter> {
            val inputFilter = InputFilter(fun(
                source: CharSequence,
                start: Int,
                end: Int,
                _: Spanned,
                _: Int,
                _: Int
            ): String? {
                return source.toString().toLowerCase()

            })
            return arrayOf(inputFilter, InputFilter.LengthFilter(limit))

        }


        fun toast(s: String) {
            Toast.makeText(MainApplication.get().getContext(), s, Toast.LENGTH_SHORT).show()
        }

        fun getUriFromFilePath(path: String): Uri {
            return Uri.fromFile(File(path))
        }

        fun areAllPermissionsAccepted(grantResults: IntArray): Boolean {
            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }

        fun getUriStringFromPath(path: String): String {
            return "file://$path"
        }

        fun isAPdf(path: String): Boolean {
            val extension = path.substring(path.lastIndexOf("."))
            return extension.equals(".pdf", ignoreCase = true)
        }

        fun contextFrom(v: View): Context {
            return v.context
        }

        fun getDrawable(id: Int): Drawable {
            return ContextCompat.getDrawable(MainApplication.get(), id)!!
        }

        fun getColor(id: Int): Int {
            return ContextCompat.getColor(MainApplication.get(), id)
        }

        fun getUrlListFromString(str: String): List<String> {
            var data: String = str
            if (str[str.lastIndex] == ',') {
                data = str.substring(0, str.lastIndex)
            }
            return data.split(",").toList()
        }


        var serverdateFormat = "yyyy-M-dd HH:mm:ss"

        fun convertServerDateToUserTimeZone(serverDate: String): String {
            var ourdate: String
            try {
                val formatter = SimpleDateFormat(serverdateFormat, Locale.UK)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value = formatter.parse(serverDate)
                //val timeZone = TimeZone.getTimeZone("Asia/Kolkata")
                val timeZone = TimeZone.getDefault()
                val dateFormatter =
                    SimpleDateFormat(serverdateFormat, Locale.UK) //this format changeable
                dateFormatter.timeZone = timeZone
                ourdate = dateFormatter.format(value)

                //Log.d("OurDate", OurDate);
            } catch (e: Exception) {
                ourdate = "0000-00-00 00:00:00"
            }



            return ourdate
        }

        fun updateStatusBarColor(
            color: String,
            acti: FragmentActivity
        ) {// Color must be in hexadecimal fromat
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = acti?.window
                window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window?.setStatusBarColor(Color.parseColor(color))
            }


        }

        fun updateStatusBarColor(
            islight: Boolean,
            color: String,
            acti: FragmentActivity
        ) {// Color must be in hexadecimal fromat
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = acti?.window
                window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window?.setStatusBarColor(Color.parseColor(color))

                if (islight) {
                    val view = window.getDecorView()
                    view.setSystemUiVisibility(view.getSystemUiVisibility() or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                } else {
                    val view = window.getDecorView()
                    view.setSystemUiVisibility(view.getSystemUiVisibility() and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
                }
            }


        }

        fun convertDateOnly(time: String): String? {
            val inputPattern = "yyyy-M-dd HH:mm:ss"
            val outputPattern = "dd MMM yyyy"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)

            var date: Date? = null
            var str: String? = null

            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return str
        }

        fun covertTimeToText(data: String, context: Context): String? {

            var dataDate: String = convertServerDateToUserTimeZone(data)

            var convTime: String? = null

            val prefix = ""
            val suffix = "ago"

            try {
                val dateFormat = SimpleDateFormat("yyyy-M-dd HH:mm:ss")
                val pasTime: Date = dateFormat.parse(dataDate)

                val nowTime = Date()

                val dateDiff: Long = nowTime.time - pasTime.time

                val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
                val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
                val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
                val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)

                if (second < 60) {
                    convTime = second.toString() + context.getString(R.string.second_ago)
                } else if (minute < 60) {
                    //Minutes
                    convTime = minute.toString() + context.getString(R.string.minute_ago)
                } else if (hour < 24) {
                    convTime = hour.toString() + context.getString(R.string.hour_ago)
                } else if (day >= 7) {
                    if (day > 360) {
                        //Years
                        convTime = (day / 30).toString() + "y " + suffix
                    } /*else if (day > 30) {
                    //Months
                    convTime = (day / 360).toString() + " Months " + suffix
                } */ else {
                        //Week
                        convTime = (day / 7).toString() + context.getString(R.string.week_ago)
                    }
                } else if (day < 7) {
                    //Days
                    convTime = day.toString() + context.getString(R.string.day_ago)
                }

            } catch (e: ParseException) {
                e.printStackTrace()
                e.message?.let { Log.e("ConvTimeE", it) }
            }

            return convTime
        }


        fun covertTimeToText(data: String): String? {

            var dataDate: String = convertServerDateToUserTimeZone(data)

            var convTime: String? = null

            val prefix = ""
            val suffix = "ago"

            try {
                val dateFormat = SimpleDateFormat("yyyy-M-dd HH:mm:ss")
                val pasTime: Date = dateFormat.parse(dataDate)

                val nowTime = Date()

                val dateDiff: Long = nowTime.time - pasTime.time

                val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
                val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
                val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
                val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)

                if (second < 60) {
                    convTime = second.toString() + "s " + suffix
                } else if (minute < 60) {
                    //Minutes
                    convTime = minute.toString() + "m " + suffix
                } else if (hour < 24) {
                    convTime = hour.toString() + "h " + suffix
                } else if (day >= 7) {
                    if (day > 360) {
                        //Years
                        convTime = (day / 30).toString() + "y " + suffix
                    } /*else if (day > 30) {
                    //Months
                    convTime = (day / 360).toString() + " Months " + suffix
                } */ else {
                        //Week
                        convTime = (day / 7).toString() + "w " + suffix
                    }
                } else if (day < 7) {
                    //Days
                    convTime = day.toString() + "d " + suffix
                }

            } catch (e: ParseException) {
                e.printStackTrace()
                e.message?.let { Log.e("ConvTimeE", it) }
            }

            return convTime
        }

        fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
            val output =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)

            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.getWidth(), bitmap.getHeight())
            val rectF = RectF(rect)
            val roundPx = pixels.toFloat()

            paint.setAntiAlias(true)
            canvas.drawARGB(0, 0, 0, 0)
            paint.setColor(color)
            canvas.drawRoundRect(rectF, 100F, 100F, paint)

            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
            canvas.drawBitmap(bitmap, rect, rect, paint)

            return output
        }
    }
}
