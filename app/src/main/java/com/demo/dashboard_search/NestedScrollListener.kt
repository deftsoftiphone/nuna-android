package com.demo.dashboard_search

import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


abstract class NestedScrollListener : NestedScrollView.OnScrollChangeListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private var visibleThreshold = 8
    // The current offset index of data you have loaded
    private var currentPage = 1
    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0
    // True if we are still waiting for the last set of data to load.
    private var loading = true
    // Sets the starting page index
    private val startingPageIndex = 0

    private var mLayoutManager: RecyclerView.LayoutManager

    private var recyclerView : RecyclerView? = null

    constructor(layoutManager: LinearLayoutManager) {
        this.mLayoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager) {
        this.mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    constructor(layoutManager: StaggeredGridLayoutManager, rv : RecyclerView) {
        this.mLayoutManager = layoutManager
        this.recyclerView = rv
        visibleThreshold *= layoutManager.spanCount
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    private fun getFirstVisibleItem(firstVisibleItemPositions: IntArray): Int {
        var minSize = 0
        for (i in firstVisibleItemPositions.indices) {
            if (i == 0) {
                minSize = firstVisibleItemPositions[i]
            } else if (firstVisibleItemPositions[i] < minSize) {
                minSize = firstVisibleItemPositions[i]
            }
        }
        return minSize
    }

    override fun onScrollChange(
        scrollView: NestedScrollView?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        if (!(scrollY > oldScrollY) || scrollView == null) {
            return
        }


        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        val totalItemCount = mLayoutManager.itemCount

        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too

        if (!loading) {
            if (recyclerView != null) {
                if ((scrollY >= (recyclerView?.measuredHeight?:0 - scrollView.measuredHeight))) {
                    currentPage++
                    onLoadMore()
                    loading = true
                }
            }
        }
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.


    // Call this method whenever performing new searches
    fun resetState() {
        this.currentPage = this.startingPageIndex
        this.previousTotalItemCount = 0
        this.loading = true
    }

    // Defines the process for actually loading more data based on page
    abstract fun onLoadMore()
}