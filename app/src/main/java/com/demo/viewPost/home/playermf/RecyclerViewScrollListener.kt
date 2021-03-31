package com.demo.viewPost.home.playermf

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
abstract class RecyclerViewScrollListener : RecyclerView.OnScrollListener() {
    private var firstVisibleItem = 0
    private var visibleItemCount = 0


    @Volatile
    private var mEnabled = true
    private var mPreLoadCount = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (mEnabled) {
            val manager = recyclerView.layoutManager
            require(manager is LinearLayoutManager) { "Expected recyclerview to have linear layout manager" }
            val mLayoutManager = manager
            visibleItemCount = mLayoutManager.childCount
            firstVisibleItem = mLayoutManager.findFirstCompletelyVisibleItemPosition()
            onItemIsFirstVisibleItem(
                firstVisibleItem,
                if (dy > 0) firstVisibleItem - 1 else firstVisibleItem + 1
            )
        }
    }

    /**
     * Called when end of scroll is reached.
     *
     * @param recyclerView - related recycler view.
     */
    abstract fun onItemIsFirstVisibleItem(index: Int, lastItemIndex: Int)

    fun disableScrollListener() {
        mEnabled = false
    }

    fun enableScrollListener() {
        mEnabled = true
    }

    fun setPreLoadCount(mPreLoadCount: Int) {
        this.mPreLoadCount = mPreLoadCount
    }
}