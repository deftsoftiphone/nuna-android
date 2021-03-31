package com.demo.util.link

import android.graphics.Typeface
import java.util.regex.Pattern

class Link {
    enum class TextStyle {
        NORMAL, BOLD, ITALIC, BOLD_ITALIC
    }

    var text: String?
    var pattern: Pattern?
    var textColor = 0
    var textStyle = TextStyle.NORMAL
    var isUnderlined = true
    var clickListener: OnClickListener? = null
    var typeFace: Typeface? = null

    constructor(link: Link) {
        text = link.text
        pattern = link.pattern
        clickListener = link.clickListener
        textColor = link.textColor
        textStyle = link.textStyle
        isUnderlined = link.isUnderlined
        typeFace = link.typeFace
    }

    constructor(text: String?) {
        this.text = text
        pattern = null
    }

    constructor(pattern: Pattern?) {
        this.pattern = pattern
        text = null
    }

    fun setText(text: String?): Link {
        this.text = text
        return this
    }

    fun setPattern(pattern: Pattern?): Link {
        this.pattern = pattern
        return this
    }

    fun setTextColor(textColor: Int): Link {
        this.textColor = textColor
        return this
    }

    fun setTextStyle(textStyle: TextStyle): Link {
        this.textStyle = textStyle
        return this
    }

    fun setUnderlined(underlined: Boolean): Link {
        isUnderlined = underlined
        return this
    }

    fun setClickListener(clickListener: OnClickListener?): Link {
        this.clickListener = clickListener
        return this
    }



    interface OnClickListener {
        fun onClick(text: String?)
    }
}
