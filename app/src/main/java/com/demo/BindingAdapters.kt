package com.demo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.demo.base.MainApplication
import com.demo.model.response.baseResponse.PostCategory
import com.demo.util.Prefs
import com.demo.util.RoundedCornersTransformation
import com.demo.util.Util
import com.demo.util.toDP
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target


object BindingAdapters {

    @JvmStatic
    @BindingAdapter("app:currentLanguageCategoryName")
    fun currentLanguageCategoryName(view: TextView, category: PostCategory) {
        try {
            println(" category = [${category}]")
            val name = when (Prefs.init().selectedLangId) {
                1 -> category.hindi!!
                2 -> category.categoryName!!
                3 -> category.marathi!!
                4 -> category.bengali!!
                5 -> category.tamil!!
                6 -> category.telugu!!
                7 -> category.gujrati!!
                8 -> category.kannada!!
                else -> "Category"
            }
            view.text = name
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    @JvmStatic
    @BindingAdapter("app:currentLanguageMuebertCategory")
    fun currentLanguageMuebertCategory(view: TextView, category: String?) {
        view.context.apply {
            category?.let {
                val name = when (it) {
                    getString(R.string.key_chillout) -> getString(R.string.chillout)
                    getString(R.string.key_workout) -> getString(R.string.workout)
                    getString(R.string.key_cinematic) -> getString(R.string.cinematic)
                    getString(R.string.key_meditate) -> getString(R.string.meditate)
                    getString(R.string.key_dance) -> getString(R.string.dance)
                    getString(R.string.key_pop) -> getString(R.string.pop)
                    getString(R.string.key_house) -> getString(R.string.house)
                    getString(R.string.key_classic) -> getString(R.string.classic)
                    else -> "Category"
                }

                view.text = name
            }
        }
    }

    @JvmStatic
    @BindingAdapter("app:currentLanguageMuebertTrack")
    fun currentLanguageMuebertTrack(view: TextView, track: String?) {
        view.context.apply {
            track?.let {
                val name = when (it) {
                    //Chill out
                    getString(R.string.key_downtempo) -> getString(R.string.downtempo)
                    getString(R.string.key_chill) -> getString(R.string.chill)
                    getString(R.string.key_psychill) -> getString(R.string.psychill)
                    getString(R.string.key_chillout) -> getString(R.string.chillout)
                    getString(R.string.key_lofi) -> getString(R.string.lofi)
                    getString(R.string.key_chill_hop) -> getString(R.string.chill_hop)
                    getString(R.string.key_atmosphere) -> getString(R.string.atmosphere)
                    getString(R.string.key_ambient) -> getString(R.string.ambient)
                    getString(R.string.key_dark_ambient) -> getString(R.string.dark_ambient)
                    getString(R.string.key_chillgressive) -> getString(R.string.chillgressive)
                    getString(R.string.key_lounge) -> getString(R.string.lounge)
                    getString(R.string.key_chillrave) -> getString(R.string.chillrave)

                    //Workout
                    getString(R.string.key_fitness_90) -> getString(R.string.fitness_90)
                    getString(R.string.key_energy_100) -> getString(R.string.energy_100)
                    getString(R.string.key_cardio_120) -> getString(R.string.cardio_120)
                    getString(R.string.key_cardio_130) -> getString(R.string.cardio_130)
                    getString(R.string.key_run_130) -> getString(R.string.run_130)
                    getString(R.string.key_run_140) -> getString(R.string.run_140)
                    getString(R.string.key_run_160) -> getString(R.string.run_160)
                    getString(R.string.key_run_180) -> getString(R.string.run_180)

                    //Cinemetic
                    getString(R.string.key_pumped) -> getString(R.string.pumped)
                    getString(R.string.key_happy) -> getString(R.string.happy)
                    getString(R.string.key_optimistic) -> getString(R.string.optimistic)
                    getString(R.string.key_sad) -> getString(R.string.sad)
                    getString(R.string.key_serious) -> getString(R.string.serious)
                    getString(R.string.key_beautiful) -> getString(R.string.beautiful)
                    getString(R.string.key_romantic) -> getString(R.string.romantic)
                    getString(R.string.key_peaceful) -> getString(R.string.peaceful)
                    getString(R.string.key_dreamy) -> getString(R.string.dreamy)
                    getString(R.string.key_epic) -> getString(R.string.epic)
                    getString(R.string.key_dramatic) -> getString(R.string.dramatic)
                    getString(R.string.key_groovy) -> getString(R.string.groovy)
                    getString(R.string.key_spooky) -> getString(R.string.spooky)
                    getString(R.string.key_upbeat) -> getString(R.string.upbeat)
                    getString(R.string.key_friends) -> getString(R.string.friends)
                    getString(R.string.key_night) -> getString(R.string.night)
                    getString(R.string.key_extreme) -> getString(R.string.extreme)

                    //Meditate
                    getString(R.string.key_calm) -> getString(R.string.calm)
                    getString(R.string.key_gentle) -> getString(R.string.gentle)
                    getString(R.string.key_sentimental) -> getString(R.string.sentimental)
                    getString(R.string.key_motivation) -> getString(R.string.motivation)
                    getString(R.string.key_minimal_120) -> getString(R.string.minimal_120)
                    getString(R.string.key_minimal_170) -> getString(R.string.minimal_170)
                    getString(R.string.key_ambient) -> getString(R.string.ambient)
                    getString(R.string.key_piano) -> getString(R.string.piano)
                    getString(R.string.key_lullaby) -> getString(R.string.lullaby)
                    getString(R.string.key_meditation) -> getString(R.string.meditation)
                    getString(R.string.key_om) -> getString(R.string.om)
                    getString(R.string.key_zen) -> getString(R.string.zen)
                    getString(R.string.key_chillout) -> getString(R.string.chillout)
                    getString(R.string.key_ethnic_108) -> getString(R.string.ethnic_108)
                    getString(R.string.key_summer) -> getString(R.string.summer)
                    getString(R.string.key_travel) -> getString(R.string.travel)

                    //Dance
                    getString(R.string.key_edm) -> getString(R.string.edm)
                    getString(R.string.key_electronica) -> getString(R.string.electronica)
                    getString(R.string.key_idm) -> getString(R.string.idm)
                    getString(R.string.key_braindance) -> getString(R.string.braindance)
                    getString(R.string.key_synthwave) -> getString(R.string.synthwave)
                    getString(R.string.key_trance) -> getString(R.string.trance)
                    getString(R.string.key_uplifting_trance) -> getString(R.string.uplifting_trance)
                    getString(R.string.key_psytrance) -> getString(R.string.psytrance)
                    getString(R.string.key_techno) -> getString(R.string.techno)
                    getString(R.string.key_dubtechno) -> getString(R.string.dubtechno)
                    getString(R.string.key_hardtechno) -> getString(R.string.hardtechno)
                    getString(R.string.key_bassline) -> getString(R.string.bassline)
                    getString(R.string.key_trap) -> getString(R.string.trap)
                    getString(R.string.key_drumnbass) -> getString(R.string.drumnbass)
                    getString(R.string.key_liquidfunk) -> getString(R.string.liquidfunk)
                    getString(R.string.key_neurofunk) -> getString(R.string.neurofunk)
                    getString(R.string.key_microfunk) -> getString(R.string.microfunk)
                    getString(R.string.key_dub) -> getString(R.string.dub)
                    getString(R.string.key_glitch_hop) -> getString(R.string.glitch_hop)
                    getString(R.string.key_breakbeat) -> getString(R.string.breakbeat)
                    getString(R.string.key_acid_jazz) -> getString(R.string.acid_jazz)
                    getString(R.string.key_electro_funk) -> getString(R.string.electro_funk)
                    getString(R.string.key_funk) -> getString(R.string.funk)
                    getString(R.string.key_nu_disco) -> getString(R.string.nu_disco)
                    getString(R.string.key_indie_dance) -> getString(R.string.indie_dance)
                    getString(R.string.key_white_noise) -> getString(R.string.white_noise)
                    getString(R.string.key_pink_noise) -> getString(R.string.pink_noise)
                    getString(R.string.key_brown_noise) -> getString(R.string.brown_noise)

                    //Pop
                    getString(R.string.key_pop) -> getString(R.string.pop)
                    getString(R.string.key_future_pop) -> getString(R.string.future_pop)
                    getString(R.string.key_country_pop) -> getString(R.string.country_pop)
                    getString(R.string.key_latin_pop) -> getString(R.string.latin_pop)
                    getString(R.string.key_indie_pop) -> getString(R.string.indie_pop)
                    getString(R.string.key_indie_rock) -> getString(R.string.indie_rock)
                    getString(R.string.key_post_rock) -> getString(R.string.post_rock)
                    getString(R.string.key_hiphop) -> getString(R.string.hiphop)

                    //House
                    getString(R.string.key_tropical_house) -> getString(R.string.tropical_house)
                    getString(R.string.key_house) -> getString(R.string.house)
                    getString(R.string.key_deep_house) -> getString(R.string.deep_house)
                    getString(R.string.key_minimal_house) -> getString(R.string.minimal_house)
                    getString(R.string.key_bassline_house) -> getString(R.string.bassline_house)
                    getString(R.string.key_disco_house) -> getString(R.string.disco_house)
                    getString(R.string.key_melodic_house) -> getString(R.string.melodic_house)
                    getString(R.string.key_tropical_house) -> getString(R.string.tropical_house)

                    //Classic
                    getString(R.string.key_classical) -> getString(R.string.classical)
                    getString(R.string.key_neo_classic) -> getString(R.string.neo_classic)
                    getString(R.string.key_world_music) -> getString(R.string.world_music)
                    getString(R.string.key_folk) -> getString(R.string.folk)
                    getString(R.string.key_acoustic) -> getString(R.string.acoustic)
                    getString(R.string.key_reggaeton) -> getString(R.string.reggaeton)
                    getString(R.string.key_slow_ballad) -> getString(R.string.slow_ballad)

                    else -> getString(R.string.action_add_music_track)
                }
                view.text = name
            }
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @JvmStatic
    @BindingAdapter("app:currentTrackIcon")
    fun currentTrackIcon(view: ImageView, trackCategory: String?) {
        view.context.apply {
            trackCategory?.let {
                val icon = when (it) {
                    getString(R.string.key_chillout) -> MainApplication.get()
                        .getDrawable(R.drawable.moods_selector_bg)
                    getString(R.string.key_workout) -> MainApplication.get()
                        .getDrawable(R.drawable.focus_selector_bg)
                    getString(R.string.key_cinematic) -> MainApplication.get()
                        .getDrawable(R.drawable.sleep_selector_bg)
                    getString(R.string.key_meditate) -> MainApplication.get()
                        .getDrawable(R.drawable.calm_selector_bg)
                    getString(R.string.key_dance) -> MainApplication.get()
                        .getDrawable(R.drawable.chill_selector_bg)
                    getString(R.string.key_pop) -> MainApplication.get()
                        .getDrawable(R.drawable.sport_selector_bg)
                    getString(R.string.key_house) -> MainApplication.get()
                        .getDrawable(R.drawable.genres_selector_bg)
                    getString(R.string.key_classic) -> MainApplication.get()
                        .getDrawable(R.drawable.classical_selector_bg)
                    else -> null
                }
                view.setImageDrawable(icon)
            }
        }
    }


    /*  @JvmStatic
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
      }*/


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
//            Picasso.get().load(R.drawable.cate_bitmap).transform(transformation).into(view)
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
            Picasso.get().load(url).placeholder(R.drawable.profile).resize(400, 400)
                .centerCrop()
                .into(view)
    }

    @JvmStatic
    @BindingAdapter(value = ["roundedImageUrl", "defaultImage"], requireAll = false)
    fun loadImageAsRoundedCorners(view: ImageView, url: String?, defaultImage: Int?) {
/*
        val transformation: Transformation = RoundedTransformationBuilder()
            .borderColor(Color.TRANSPARENT)
            .borderWidthDp(0f)
            .cornerRadiusDp(0f)
            .oval(false)
            .build()

        if (url.isNullOrEmpty()) {
            if (defaultImage == 0 || defaultImage == null) {
                Picasso.get().load(R.drawable.ic_placeholder)*//*.transform(transformation)*//*
                    .into(
                        view
                    )
            } else {
                Picasso.get().load(defaultImage)*//*.transform(transformation)*//*.into(view)
            }
            return
        }
        Picasso.get().load(url).transform(RoundedCornersTransformation(30, 15)).into(view)*/
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

/*
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
*/

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
    @BindingAdapter("bind:loadUserImage")
    fun loadUserImage(view: ImageView, imageUrl: String?) {
        Glide.with(view.context)
            .apply {
                this.setDefaultRequestOptions(Util.profileImageRequestOptions())
            }
            .load(imageUrl)
            .placeholder(R.drawable.ic_placeholder_user_small)
            .error(R.drawable.ic_placeholder_user_small)
            .thumbnail(0.10f)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    view.setImageDrawable(resource)
                    return true
                }
            })
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("bind:likedByUser")
    fun likedByUser(view: ImageView, likedByUser: Boolean) {
        view.setImageResource(R.drawable.ic_not_selected_heart)
    }

    @JvmStatic
    @BindingAdapter("bind:loadImage")
    fun loadFullImage(view: ImageView, imageUrl: String?) {
        val context = view.context

/*
        Glide.with(view.context)
                .apply {
                    this.setDefaultRequestOptions(Util.postRequestOptions())
                }
                .load(imageUrl)
                .placeholder(R.drawable.dashboard_item_bg)
                .error(R.drawable.dashboard_item_bg)
                .thumbnail(0.35f)
                .onlyRetrieveFromCache(true)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                    ): Boolean {
                        view.setImageDrawable(resource)
                        return true
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
               .into(view)*/


        Glide.with(view.context)
            .apply {
                this.setDefaultRequestOptions(Util.postRequestOptions())
            }
            .load(imageUrl)
            .thumbnail(0.5f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("bind:postThumbnail")
    fun postThumbnail(view: ImageView, imageUrl: String?) {
        val context = view.context
        Glide.with(view.context)
            .apply {
                this.setDefaultRequestOptions(Util.postThumbnailOptions())
            }
            .load(imageUrl)
            .thumbnail(0.5f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }

    @JvmStatic
//    @BindingAdapter(value = ["app:fontAccordingToLanguage", "app: boldTheFont"], requireAll = false)
    @BindingAdapter(
        "fontAccordingToLanguage",
        "styleBold",
        "styleItalic",
        "padding",
        requireAll = false
    )
    fun fontAccordingToLanguage(
        view: TextView,
        type: String?,
        bold: Boolean?,
        italic: Boolean?,
        padding: Int = -1
    ) {
        val context = view.context
        when (Prefs.init().selectedLangId) {
            1, 3, 4, 5, 6, 7, 8 -> {//Hindi   Marathi    Bengali    Tamil    Telugu     Gujurati     Kannada
                if (context.getString(R.string.key_bold_font) == type) {
                    if (italic != null && italic)
                        view.setTF(R.font.nirmala_b, Typeface.BOLD_ITALIC)
                    else view.setTF(R.font.nirmala_b, Typeface.BOLD)
                } else {
                    if (italic != null && italic)
                        view.setTF(R.font.nirmala, Typeface.ITALIC)
                    else view.setTF(R.font.nirmala)
                }

                val scale: Float = context.resources.displayMetrics.density
                if (padding > -1) {
                    val size = (if (padding > -1) padding else 6 * scale + 0.5f).toInt()
                    view.apply {
                        setPadding(view.paddingStart, size, view.paddingEnd, size)
//                        setPadding(0, 0, 0, 0)
                    }
                }
            }
            2 -> { //English
                if (context.getString(R.string.key_bold_font) == type) {
                    if (bold != null && bold) {
                        if (italic != null && italic)
                            view.setTF(R.font.gotham_pro_medium, Typeface.BOLD_ITALIC)
                        else view.setTF(R.font.gotham_pro_medium, Typeface.BOLD)
                    } else {
                        if (italic != null && italic)
                            view.setTF(R.font.gotham_pro_medium, Typeface.ITALIC)
                        else view.setTF(R.font.gotham_pro_medium)
                    }
                } else {
                    if (italic != null && italic)
                        view.setTF(R.font.gotham_pro, Typeface.ITALIC)
                    else view.setTF(R.font.gotham_pro)
                }

                /*  if (verticalPaddingToEnglish > -1)
                      view.setPadding(
                          valueInDp(context, view.left),
                          valueInDp(context, view.top + verticalPaddingToEnglish),
                          valueInDp(context, view.right),
                          valueInDp(context, view.bottom + verticalPaddingToEnglish)
                      )*/
            }
        }
    }

    private fun valueInDp(context: Context, value: Int): Int {
        return if (value > -1) {
            context.toDP(value)
        } else {
            0
        }
    }

    private fun TextView.setTF(fontId: Int) {
        typeface = ResourcesCompat.getFont(context, fontId)
    }

    private fun TextView.setTF(fontId: Int, typefaceType: Int) {
        setTypeface(ResourcesCompat.getFont(context, fontId), typefaceType)
    }
}
