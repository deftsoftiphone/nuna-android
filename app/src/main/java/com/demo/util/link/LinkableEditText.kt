package com.demo.util.link

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.widget.EditText
import java.util.*

@SuppressLint("AppCompatCustomView")
class LinkableEditText : EditText, TextWatcher {
    private val mLinks: MutableList<Link> = ArrayList()
    private var mLinkModifier: LinkModifier? = null
    private var mOnTextChangedListener: OnTextChangedListener? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(this)
        movementMethod = LinkMovementMethod.getInstance()
        mLinkModifier = LinkModifier(LinkModifier.ViewType.EDIT_TEXT)
    }

    fun addLink(link: Link): LinkableEditText {
        mLinks.add(link)
        mLinkModifier!!.links = mLinks
        return this
    }

    fun addLinks(links: List<Link>?): LinkableEditText {
        mLinks.addAll(links!!)
        mLinkModifier!!.links = mLinks
        return this
    }

    val foundLinks: List<Link>
        get() = mLinkModifier!!.foundLinks

    fun setTextChangedListener(listener: OnTextChangedListener?): LinkableEditText {
        mOnTextChangedListener = listener
        return this
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (mOnTextChangedListener != null) {
            mOnTextChangedListener!!.onTextChanged(text, start, lengthBefore, lengthAfter)
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (mOnTextChangedListener != null) {
            mOnTextChangedListener!!.beforeTextChanged(s, start, count, after)
        }
    }

    override fun afterTextChanged(s: Editable) {
        mLinkModifier!!.spannable = s
        mLinkModifier!!.build()
        if (mOnTextChangedListener != null) {
            mOnTextChangedListener!!.afterTextChanged(s)
        }
    }

    interface OnTextChangedListener {
        fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int)
        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
        fun afterTextChanged(s: Editable?)
    }
}
