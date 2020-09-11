package com.demo

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.demo.util.RoundedCornersTransformation
import com.demo.util.Util
import com.demo.util.aws.AmazonUtil
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.squareup.picasso.Transformation
import java.util.regex.Matcher
import java.util.regex.Pattern

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("bind:spanHashTag")
    fun spanHashTag(view: EditText, text: String?) {
//        val text = view.text
        if (!TextUtils.isEmpty(text)) {
            val sb = SpannableStringBuilder(text)
            val p: Pattern = Pattern.compile("(#\\w+)")
            val m: Matcher = p.matcher(text!!)
            while (m.find()) {
                //String word = m.group();
                //String word1 = notes.substring(m.start(), m.end());
                sb.setSpan(
                    ForegroundColorSpan(Color.rgb(255, 0, 0)),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )

                sb.setSpan(
                    StyleSpan(Typeface.BOLD), m.start(),
                    m.end(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
            println("view = [${view}], text = [${text}]")
            view.text = sb
        }
    }


    @JvmStatic
    @BindingAdapter(value = ["imageUrl", "defaultImage"], requireAll = false)
    fun loadImage(view: ImageView, url: String?, defaultImage: Int?) {
        if (url.isNullOrEmpty()) {
            if (defaultImage == 0 || defaultImage == null) {
                Picasso.get().load(R.drawable.ic_placeholder).into(view)
            } else {
                Picasso.get().load(defaultImage).into(view)
            }
            return
        }
        Picasso.get().load(url).into(view)
    }

    @JvmStatic
    @BindingAdapter(value = ["loadRoundedImage", "defaultImage"], requireAll = false)
    fun loadRoundedImage(view: ImageView, url: String?, defaultImage: Int?) {
        if (url.isNullOrEmpty()) {
            if (defaultImage == 0 || defaultImage == null) {
                Picasso.get().load(R.drawable.ic_placeholder).into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        view.setImageBitmap(Util.getRoundedCornerBitmap(bitmap!!, 100))
                    }

                })
            } else {
                Picasso.get().load(defaultImage).into(view)
            }
            return
        }
        Picasso.get().load(url).into(view)
    }

    @JvmStatic
    @BindingAdapter(value = ["imageUrlNew", "defaultImage"], requireAll = false)
    fun loadImageNew(view: ImageView, url: String?, defaultImage: Int?) {

        val transformation = RoundedCornersTransformation(20, 0)

        if (url.isNullOrEmpty()) {
            Picasso.get().load(R.drawable.cate_bitmap).transform(transformation).into(
                view
            )
        } else
            Picasso.get().load(url).transform(transformation)
                .into(view)
    }

    @JvmStatic
    @BindingAdapter(value = ["imageUrlUpdate", "defaultImage"], requireAll = false)
    fun loadImageUpdate(view: ImageView, url: String?, defaultImage: Int?) {

        if (url.isNullOrEmpty()) {
            Picasso.get().load(R.drawable.profile)/*.transform(transformation)*/.into(
                view
            )
        } else
            Picasso.get().load(url).placeholder(R.drawable.profile).resize(400, 400).centerCrop()
                .into(view)
    }

    @JvmStatic
    @BindingAdapter(value = ["roundedImageUrl", "defaultImage"], requireAll = false)
    fun loadImageAsRoundedCorners(view: ImageView, url: String?, defaultImage: Int?) {

        val transformation: Transformation = RoundedTransformationBuilder()
            .borderColor(Color.TRANSPARENT)
            .borderWidthDp(0f)
            .cornerRadiusDp(0f)
            .oval(false)
            .build()

        if (url.isNullOrEmpty()) {
            if (defaultImage == 0 || defaultImage == null) {
                Picasso.get().load(R.drawable.ic_placeholder)/*.transform(transformation)*/.into(
                    view
                )
            } else {
                Picasso.get().load(defaultImage)/*.transform(transformation)*/.into(view)
            }
            return
        }
        Picasso.get().load(url).transform(RoundedCornersTransformation(30, 15)).into(view)
    }

    @JvmStatic
    @BindingAdapter(value = ["imageUrlList", "defaultImage"], requireAll = false)
    fun loadImageFomList(view: ImageView, urlList: String?, defaultImage: Int?) {
        val url: String
        val urlArr = urlList?.split(",")

        if (urlList?.isNotEmpty() == true && urlArr?.get(0)?.isNotBlank() == true) {
            url = urlArr[0]
            Picasso.get().load(url).into(view)

        } else if (defaultImage != null && defaultImage != 0) {
            Picasso.get().load(defaultImage).into(view)

        } else {
            Picasso.get().load(R.drawable.ic_placeholder).into(view)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["roundedImageUrlList", "defaultImage"], requireAll = false)
    fun loadRoundedImageFomList(view: ImageView, urlList: String?, defaultImage: Int?) {
        var url: String
        val urlArr = urlList?.split(",")


        val transformation = RoundedCornersTransformation(12, 0)


        if (urlList?.isNotEmpty() == true && urlArr?.get(0)?.isNotBlank() == true) {
            url = urlArr[0]


            if (url.startsWith("htt")) {

            } else {
                var strurl: String
                strurl = AmazonUtil.S3_MEDIA_PATH + url + ".jpg";
                url = strurl
            }

            var thumb = url.replace(
                url.split("/")[url.split("/").size - 1],
                ""
            ) + "thumb/" + url.split("/")[url.split("/").size - 1]



            Picasso.get().load(thumb).transform(transformation).resize(200, 200).centerCrop()
                .into(view)

        } else if (defaultImage != null && defaultImage != 0) {
            Picasso.get().load(defaultImage).transform(transformation).into(view)
        } else {
            Picasso.get().load(R.drawable.profile).transform(transformation).into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("visibleErrIfListEmpty")
    fun visibleErrIfListEmpty(view: View, list: List<Any>?) {
        if (list.isNullOrEmpty()) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("htmlText")
    fun loadHtmlText(view: TextView, text: String?) {
        if (text == null) return

        var textstr: String
        textstr = text

        textstr = textstr.replace("|", "<br>")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.text = Html.fromHtml(textstr, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            view.text = Html.fromHtml(textstr)
        }
    }

    @JvmStatic
    @BindingAdapter("dateFormated")
    fun loadDate(view: TextView, text: String?) {
        if (text == null) return

        view.text = Util.covertTimeToText(text)

    }


    @JvmStatic
    @BindingAdapter("dateFormatedOnly")
    fun loadDateOnly(view: TextView, text: String?) {
        if (text == null) return
        view.text = Util.convertDateOnly(text)

    }

    @JvmStatic
    @BindingAdapter("updateMessage")
    fun loadUpdateMessage(view: TextView, text: String?) {
        if (text == null) return
        if (text == "like") {
            view.text = Util.covertTimeToText(text)
        } else if (text == "comment") {
            view.text = Util.covertTimeToText(text)
        } else
            view.text = Util.covertTimeToText(text)

    }

    @JvmStatic
    @BindingAdapter("bind:customFont")
    fun customFont(textView: TextView, fontName: String) {
        textView.typeface = Typeface.createFromAsset(textView.context.assets, "fonts/$fontName")
    }

    @BindingAdapter("app:toTitleCaseWithHash")
    @JvmStatic
    fun toTitleCaseWithHash(view: TextView, text: String) {
        if (!TextUtils.isEmpty(text)) {
            var space = true
            val builder = StringBuilder(text)
            val len = builder.length
            for (i in 0 until len) {
                val c = builder[i]
                if (space) {
                    if (!Character.isWhitespace(c)) {
                        // Convert to title case and switch out of whitespace mode.
                        builder.setCharAt(i, Character.toTitleCase(c))
                        space = false
                    }
                } else if (Character.isWhitespace(c)) {
                    space = true
                } else {
                    builder.setCharAt(i, Character.toLowerCase(c))
                }
            }
            view.text = "#$builder"
        }
    }

    @BindingAdapter("app:toTitleCase")
    @JvmStatic
    fun toTitleCase(view: TextView, text: String?) {
        text?.let {
            var space = true
            val builder = StringBuilder(text)
            val len = builder.length
            for (i in 0 until len) {
                val c = builder[i]
                if (space) {
                    if (!Character.isWhitespace(c)) {
                        // Convert to title case and switch out of whitespace mode.
                        builder.setCharAt(i, Character.toTitleCase(c))
                        space = false
                    }
                } else if (Character.isWhitespace(c)) {
                    space = true
                } else {
                    builder.setCharAt(i, Character.toLowerCase(c))
                }
            }
            view.text = builder.toString()
        }
    }


    @JvmStatic
    @BindingAdapter(value = ["bind:imageUrl1", "bind:defaultImage1"], requireAll = false)
    fun loadImagae(view: ImageView, imageUrl: String?, defaultImage: Int?) {
        Picasso.get().load(imageUrl).placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image).into(view)
    }

    @JvmStatic
    @BindingAdapter("bind:loadImage")
    fun loadFullImage(view: ImageView, imageUrl: String?) {
        Glide.with(view.context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_post_placeholder)
            .error(R.drawable.ic_post_placeholder).into(view)

//        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_post_placeholder)
//            .error(R.drawable.ic_post_placeholder).into(view)
    }

}