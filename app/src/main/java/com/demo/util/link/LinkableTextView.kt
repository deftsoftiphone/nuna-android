package com.demo.util.link

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import java.util.*

class LinkableTextView : androidx.appcompat.widget.AppCompatTextView {
    private val mLinks: MutableList<Link> = ArrayList()
    private var mLinkModifier: LinkModifier? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        movementMethod = LinkMovementMethod.getInstance()
        mLinkModifier = LinkModifier(LinkModifier.ViewType.TEXT_VIEW)
    }

    fun setText(text: String?): LinkableTextView {
        mLinkModifier!!.text = text
        return this
    }

    fun addLink(link: Link): LinkableTextView {
        mLinks.add(link)
        mLinkModifier!!.links = mLinks
        return this
    }

    fun addLinks(links: List<Link>?): LinkableTextView {
        mLinks.addAll(links!!)
        mLinkModifier!!.links = mLinks
        return this
    }

    val foundLinks: List<Link>
        get() = mLinkModifier!!.foundLinks

    fun build(): LinkableTextView {
        mLinkModifier!!.build()
        text = mLinkModifier!!.spannable
        return this
    }
}
