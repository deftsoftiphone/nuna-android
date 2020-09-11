package com.demo.dashboard_search

import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


internal class StaggeredScrollListener(private val refreshList: RefreshList) :
    RecyclerView.OnScrollListener() {
    private var isLoading = false
    private var hasMorePages = true
    private var pageNumber = 0
    private var isRefreshing = false
    private var pastVisibleItems = 0
    override fun onScrolled(
        recyclerView: RecyclerView,
        dx: Int,
        dy: Int
    ) {
        super.onScrolled(recyclerView, dx, dy)
        val manager =
            recyclerView.layoutManager as StaggeredGridLayoutManager?
        val visibleItemCount = manager!!.childCount
        val totalItemCount = manager.itemCount
        val firstVisibleItems = manager.findFirstVisibleItemPositions(null)
        if (firstVisibleItems != null && firstVisibleItems.size > 0) {
            pastVisibleItems = firstVisibleItems[0]
        }
        if (visibleItemCount + pastVisibleItems >= totalItemCount && !isLoading) {
            isLoading = true
            if (hasMorePages && !isRefreshing) {
                isRefreshing = true
                Handler().postDelayed({ refreshList.onRefresh(pageNumber) }, 200)
            }
        } else {
            isLoading = false
        }
    }

    fun noMorePages() {
        hasMorePages = false
    }

    fun notifyMorePages() {
        isRefreshing = false
        pageNumber += 1
    }

    interface RefreshList {
        fun onRefresh(pageNumber: Int)
    }

}