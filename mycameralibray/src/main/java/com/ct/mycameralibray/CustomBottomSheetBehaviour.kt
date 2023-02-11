package com.ct.mycameralibray

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class CustomBottomSheetBehaviour <V : View> : BottomSheetBehavior<V> {
    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: V,
        ev: MotionEvent
    ): Boolean {
        return false
    }
}