package com.demo.view_others_profile

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout


class AppBarLayoutNoEmptyScrollBehavior(mAppBarLayout: Any, mRecyclerView: Any) : CoordinatorLayout.Behavior<View>() {


    var mAppBarLayout: AppBarLayout? = null
    var mRecyclerView: RecyclerView? = null
    fun AppBarLayoutNoEmptyScrollBehavior(
        appBarLayout: AppBarLayout?,
        recyclerView: RecyclerView?
    ) {
        mAppBarLayout = appBarLayout
        mRecyclerView = recyclerView
    }

    fun isRecylerViewScrollable(recyclerView: RecyclerView?): Boolean {
        var recyclerViewHeight: Int =
            recyclerView!!.getHeight() // Height includes RecyclerView plus AppBarLayout at same level
        val appCompatHeight =
            if (mAppBarLayout != null) mAppBarLayout!!.getHeight() else 0
        recyclerViewHeight -= appCompatHeight
        return recyclerView.computeVerticalScrollRange() > recyclerViewHeight
    }


    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int
    ): Boolean {
        return if (isRecylerViewScrollable(mRecyclerView)) {
            super.onStartNestedScroll(
                coordinatorLayout,
                child,
                directTargetChild,
                target,
                axes
            )
        } else false    }


}
