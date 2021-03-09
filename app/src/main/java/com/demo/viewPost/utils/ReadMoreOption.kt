package com.demo.viewPost.utils

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.demo.R
import com.demo.util.Prefs
import com.demo.util.containsIllegalCharacters
import com.demo.viewPost.utils.hashtag.ClickableForegroundColorSpan
import java.util.regex.Pattern

/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
class ReadMoreOption private constructor(builder: Builder) :
    ClickableForegroundColorSpan.OnHashTagClickListener {

    data class HashTagLength(
        val startIndex: Int,
        val endIndex: Int
    )

    private var mTextView: TextView? = null

    // required
    private val context: Context

    // optional
    private val textLength: Int
    private val textLengthType: Int
    private val moreLabel: String
    private val lessLabel: String
    private val moreLabelColor: Int
    private val lessLabelColor: Int
    private val labelUnderLine: Boolean
    private val expandAnimation: Boolean
    private var mOnHashTagClickListener: OnHashTagClickListener? = null
    private var onExpandTextviewListener: onExpandTextView? = null
    private var hashtagListIndex = mutableListOf<HashTagLength>()

    fun addReadMoreTo(
        textView: TextView,
        text: CharSequence,
        listener: OnHashTagClickListener?,
        expandTextView: onExpandTextView
    ) {
        mOnHashTagClickListener = listener
        onExpandTextviewListener = expandTextView
        mTextView = textView

        if (textLengthType == TYPE_CHARACTER) {
            if (text.length <= textLength) {
                mTextView!!.text = text
                setColorsToAllHashTags(mTextView!!.text)
                return
            }
        } else {
            // If TYPE_LINE
            mTextView!!.setLines(textLength)
            mTextView!!.text = text
            setColorsToAllHashTags(mTextView!!.text)
        }

        mTextView!!.post(Runnable {
            var textLengthNew = textLength
            if (textLengthType == TYPE_LINE) {
                if (mTextView!!.layout.lineCount <= textLength) {
                    mTextView!!.setText(text)
                    return@Runnable
                }
                val lp = mTextView!!.layoutParams as ViewGroup.MarginLayoutParams
                val subString = text.toString().substring(
                    mTextView!!.layout.getLineStart(0),
                    mTextView!!.layout.getLineEnd(textLength - 1)
                )
                textLengthNew = subString.length - (moreLabel.length + 4 + lp.rightMargin / 6)
            }
            val spannableStringBuilder = SpannableStringBuilder(text.subSequence(0, textLengthNew))
                .append("...")
                .append(moreLabel)
            val ss = SpannableString.valueOf(spannableStringBuilder)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    addReadLess(text)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = labelUnderLine
                    ds.color = moreLabelColor
                }
            }
            //ss.setSpan(RelativeSizeSpan(1.2f), ss.length - moreLabel.length, ss.length, 0)
            val robotoBold =
                if (Prefs.init().selectedLang.languageIntId == 2) Typeface.createFromAsset(
                    context.assets, "fonts/GothamPro-Bold.ttf"
                ) else ResourcesCompat.getFont(context, R.font.nirmala_b)

            val robotoBoldSpan: TypefaceSpan = CustomTypefaceSpan("", robotoBold!!)
            ss.setSpan(
                robotoBoldSpan,
                ss.length - moreLabel.length,
                ss.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            ss.setSpan(
                clickableSpan,
                ss.length - moreLabel.length,
                ss.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (expandAnimation) {
                val layoutTransition = LayoutTransition()
                layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                (mTextView!!.parent as ViewGroup).layoutTransition = layoutTransition
            }
            mTextView!!.text = ss
            mTextView!!.movementMethod = LinkMovementMethod.getInstance()
            setColorsToAllHashTags(mTextView!!.text)
            onExpandTextviewListener!!.onExpand(false)
        })
    }


    private fun addReadLess(text: CharSequence) {
        mTextView!!.maxLines = Int.MAX_VALUE
        val spannableStringBuilder = SpannableStringBuilder(text)
            .append(lessLabel)
        val ss = SpannableString.valueOf(spannableStringBuilder)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                Handler().post {
                    addReadMoreTo(
                        mTextView!!,
                        text,
                        listener = mOnHashTagClickListener,
                        expandTextView = onExpandTextviewListener!!
                    )
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = labelUnderLine
                ds.color = lessLabelColor
            }
        }
        // ss.setSpan(RelativeSizeSpan(1.2f), ss.length - lessLabel.length, ss.length, 0)
        val robotoBold =
            if (Prefs.init().selectedLang.languageIntId == 2) Typeface.createFromAsset(
                context.assets, "fonts/GothamPro-Bold.ttf"
            ) else ResourcesCompat.getFont(context, R.font.nirmala_b)

        val robotoBoldSpan: TypefaceSpan = CustomTypefaceSpan("", robotoBold!!)
        ss.setSpan(
            robotoBoldSpan,
            ss.length - lessLabel.length,
            ss.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        ss.setSpan(
            clickableSpan,
            ss.length - lessLabel.length,
            ss.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mTextView!!.setText(ss)
        mTextView!!.movementMethod = LinkMovementMethod.getInstance()
        setColorsToAllHashTags(mTextView!!.text)
        onExpandTextviewListener!!.onExpand(true)
    }

    class Builder(  // required
        val context: Context
    ) {
        // optional
        var textLength = 80
        var textLengthType = TYPE_CHARACTER
        var moreLabel = " ${context.getString(R.string.more)}..."
        var lessLabel = " ${context.getString(R.string.see_less)}"
        var moreLabelColor = Color.parseColor("#FFFFFF")
        var lessLabelColor = Color.parseColor("#FFFFFF")
        var labelUnderLine = false
        var expandAnimation = false

        /**
         * @param length can be no. of line OR no. of characters - default is 100 character
         * @param textLengthType ReadMoreOption.TYPE_LINE for no. of line OR
         * ReadMoreOption.TYPE_CHARACTER for no. of character
         * - default is ReadMoreOption.TYPE_CHARACTER
         * @return Builder obj
         */
        fun textLength(length: Int, textLengthType: Int): Builder {
            textLength = length
            this.textLengthType = textLengthType
            return this
        }

        fun moreLabel(moreLabel: String): Builder {
            this.moreLabel = moreLabel
            return this
        }

        fun lessLabel(lessLabel: String): Builder {
            this.lessLabel = lessLabel
            return this
        }

        fun moreLabelColor(moreLabelColor: Int): Builder {
            this.moreLabelColor = moreLabelColor
            return this
        }

        fun lessLabelColor(lessLabelColor: Int): Builder {
            this.lessLabelColor = lessLabelColor
            return this
        }

        fun labelUnderLine(labelUnderLine: Boolean): Builder {
            this.labelUnderLine = labelUnderLine
            return this
        }

        /**
         * @param expandAnimation either true to enable animation on expand or false to disable animation
         * - default is false
         * @return Builder obj
         */
        fun expandAnimation(expandAnimation: Boolean): Builder {
            this.expandAnimation = expandAnimation
            return this
        }

        fun build(): ReadMoreOption {
            return ReadMoreOption(this)
        }
    }

    companion object {
        private val TAG = ReadMoreOption::class.java.simpleName
        const val TYPE_LINE = 1
        const val TYPE_CHARACTER = 2
    }

    init {
        context = builder.context
        textLength = builder.textLength
        textLengthType = builder.textLengthType
        moreLabel = builder.moreLabel
        lessLabel = builder.lessLabel
        moreLabelColor = builder.moreLabelColor
        lessLabelColor = builder.lessLabelColor
        labelUnderLine = builder.labelUnderLine
        expandAnimation = builder.expandAnimation
    }

    interface OnHashTagClickListener {
        fun onHashTagClicked(hashTag: String?)
    }

    interface onExpandTextView {
        fun onExpand(isExpanded: Boolean?)
    }

    private fun setColorsToAllHashTags(text: CharSequence) {
        var startIndexOfNextHashSign: Int
        var index = 0
        while (index < text.length - 1) {
            val sign = text[index]
            var nextNotLetterDigitCharIndex =
                index + 1 // we assume it is next. if if was not changed by findNextValidHashTagChar then index will be incremented by 1
            if (sign == '#') {
                startIndexOfNextHashSign = index
                nextNotLetterDigitCharIndex =
                    findNextValidHashTagChar(text, startIndexOfNextHashSign)
                setColorForHashTagToTheEnd(startIndexOfNextHashSign, nextNotLetterDigitCharIndex)
            }
            index = nextNotLetterDigitCharIndex
        }
    }

    val regex =
        Pattern.compile("[^\u0A80-\u0AFF\u0980-\u09FF\u0C00-\u0C7F\u0B80-\u0BFF\u0C80-\u0CFF\u0900-\u097Fa-zA-Z0-9#_]")

    private fun findNextValidHashTagChar(text: CharSequence, start: Int): Int {
        var nonLetterDigitCharIndex = -1 // skip first sign '#"
        for (index in start + 1 until text.length) {
            val sign = text[index]
            val isValidSign =
                sign.toString().equals("#",ignoreCase = true) || Character.isSpaceChar(sign)
                        || containsIllegalCharacters(sign.toString()) || Character.isWhitespace(sign)
                        || regex.matcher(sign.toString()).find()
            if (isValidSign) {
                nonLetterDigitCharIndex = index
                break
            }
        }
        if (nonLetterDigitCharIndex == -1) {
            // we didn't find non-letter. We are at the end of text
            nonLetterDigitCharIndex = text.length
        }
        return nonLetterDigitCharIndex
    }

    private fun setColorForHashTagToTheEnd(startIndex: Int, nextNotLetterDigitCharIndex: Int) {
        val text = mTextView!!.text as Spannable
        var span: CharacterStyle? = null
        if (mOnHashTagClickListener != null) {
            span =
                ClickableForegroundColorSpan(ContextCompat.getColor(context, R.color.white), this)
        }
        val robotoBold = if (Prefs.init().selectedLangId == 2) Typeface.createFromAsset(
            context.assets, "fonts/GothamPro-Bold.ttf"
        ) else ResourcesCompat.getFont(context, R.font.nirmala_b)
        val robotoBoldSpan: TypefaceSpan = CustomTypefaceSpan("", robotoBold!!)
        val textAppearanceSpan = TextAppearanceSpan(context, R.style.hastagStyle)


        text.setSpan(
            textAppearanceSpan,
            startIndex,
            nextNotLetterDigitCharIndex,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        text.setSpan(
            robotoBoldSpan,
            startIndex,
            nextNotLetterDigitCharIndex,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        text.setSpan(
            span,
            startIndex,
            nextNotLetterDigitCharIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    override fun onHashTagClicked(hashTag: String?) {
        mOnHashTagClickListener!!.onHashTagClicked(hashTag)
    }

}