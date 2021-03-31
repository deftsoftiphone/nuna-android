package com.demo.util.link

import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull


class ClickableLinkSpan(private val mLink: Link, private val mRange: Range) :
    ClickableSpan() {
    override fun onClick(widget: View) {
        val tv = widget as TextView
        val s = tv.text as Spanned
        if (mLink.clickListener != null) {
            mLink.clickListener!!.onClick(s.subSequence(mRange.start, mRange.end).toString())
        }
    }

    override fun updateDrawState(@NonNull ds: TextPaint) {
        if (mLink.textColor != 0) {
            ds.color = mLink.textColor
        }
        ds.isUnderlineText = mLink.isUnderlined
    }
}
