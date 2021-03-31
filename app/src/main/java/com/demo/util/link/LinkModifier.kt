package com.demo.util.link

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import com.demo.viewPost.utils.CustomTypefaceSpan
import java.util.*
import java.util.regex.Pattern

class LinkModifier(private val mViewType: ViewType) {
    enum class ViewType {
        TEXT_VIEW, EDIT_TEXT
    }

    var links: List<Link> = ArrayList()
    private var mFoundLinks: MutableList<Link> = ArrayList()
    var text: String? = null
    var spannable: Spannable? = null
    val foundLinks: List<Link>
        get() = mFoundLinks

    fun setFoundLinks(mFoundLinks: MutableList<Link>) {
        this.mFoundLinks = mFoundLinks
    }

    fun addLinkToSpan(link: Link) {
        if (spannable == null) {
            spannable = SpannableString.valueOf(text)
        }
        addLinkToSpan(spannable, link)
    }

    private fun addLinkToSpan(s: Spannable?, link: Link) {
        val pattern = Pattern.compile(Pattern.quote(link.text))
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            val start = matcher.start()
            if (start >= 0) {
                val end = start + link.text?.length!!
                applyLink(link, Range(start, end), s, link.typeFace!!)
            }
        }
    }

    private fun applyLink(link: Link, range: Range, text: Spannable?, typeface: Typeface) {
        val linkSpan = ClickableLinkSpan(link, range)
        text!!.setSpan(linkSpan, range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val styleSpan = StyleSpan(link.textStyle.ordinal)
        text.setSpan(styleSpan, range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val fontSpan: TypefaceSpan = CustomTypefaceSpan("", typeface)
        text.setSpan(
            fontSpan,
            range.start, range.end,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    private fun removePreviousSpans() {
        val toRemoveSpans = spannable!!.getSpans(
            0, spannable!!.length,
            ClickableSpan::class.java
        )
        for (toRemoveSpan in toRemoveSpans) {
            spannable!!.removeSpan(toRemoveSpan)
        }
        val toRemoveStyleSpans = spannable!!.getSpans(
            0, spannable!!.length,
            StyleSpan::class.java
        )
        for (toRemoveStyleSpan in toRemoveStyleSpans) {
            spannable!!.removeSpan(toRemoveStyleSpan)
        }

        val toRemoveTypeSpans =
            spannable!!.getSpans(0, spannable!!.length, TypefaceSpan::class.java)
        for (toRemoveTypeSpan in toRemoveTypeSpans) {
            spannable!!.removeSpan(toRemoveTypeSpan)
        }
    }

    fun convertPatternsToLinks() {
        mFoundLinks.clear()
        mFoundLinks.addAll(links)
        var size = mFoundLinks.size
        var i = 0
        while (i < size) {
            if (mFoundLinks[i].pattern != null) {
                addLinksFromPattern(mFoundLinks[i])
                mFoundLinks.removeAt(i)
                size--
            } else {
                i++
            }
        }
    }

    private fun addLinksFromPattern(linkWithPattern: Link) {
        val pattern = linkWithPattern.pattern
        val m = pattern?.matcher(text)
        while (m?.find()!!) {
            val link = Link(linkWithPattern).setText(m.group())
            mFoundLinks.add(link)
        }
    }

    private fun validateTextLinks() {
        var size = mFoundLinks.size
        var i = 0
        while (i < size) {
            if (mFoundLinks[i].pattern == null) {
                addLinksFromText(mFoundLinks[i])
                mFoundLinks.removeAt(i)
                size--
            } else {
                i++
            }
        }
    }

    private fun addLinksFromText(link: Link) {
        val pattern = Pattern.compile(Pattern.quote(link.text))
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            mFoundLinks.add(link)
        }
    }

    fun build() {
        if (mViewType == ViewType.EDIT_TEXT) {
            text = spannable.toString()
            removePreviousSpans()
        } else {
            spannable = null
        }
        convertPatternsToLinks()
        validateTextLinks()
        for (link in mFoundLinks) {
            addLinkToSpan(link)
        }
    }
}
