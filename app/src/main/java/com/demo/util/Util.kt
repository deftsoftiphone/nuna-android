package com.demo.util

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.InputFilter
import android.text.Selection
import android.text.Spanned
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.base.MainApplication
import com.demo.model.response.baseResponse.Category
import com.google.android.material.tabs.TabLayout
import org.json.JSONObject
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


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

fun <T> List<T>.toArrayList(): ArrayList<T> {
    val temp = ArrayList<T>()
    temp.addAll(this)
    return temp
}

fun Category.fetchCurrentLanguageCategoryName(): String {
    return when (Prefs.init().selectedLang.languageIntId) {
        1 -> this.categoryNameHindi!!
        2 -> this.categoryName!!
        3 -> this.categoryNameMarathi!!
        4 -> this.categoryNameBengali!!
        5 -> this.categoryNameTamil!!
        6 -> this.categoryNameTelugu!!
        7 -> this.categoryNameGujrati!!
        8 -> this.categoryNameKannada!!
        else -> "Category"
    }
}

operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
    val value = this.value ?: arrayListOf()
    value.addAll(values)
    this.value = value
}

class Util {

    companion object {


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
                Log.e("ConvTimeE", e.message)
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
                Log.e("ConvTimeE", e.message)
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
