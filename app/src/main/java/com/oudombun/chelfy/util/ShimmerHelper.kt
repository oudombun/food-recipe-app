package com.oudombun.chelfy.util

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AlphaAnimation

object ShimmerHelper {

    fun startShimmer(view: View) {
        val shimmer = AlphaAnimation(0.3f, 1.0f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }
        view.startAnimation(shimmer)
    }

    fun stopShimmer(view: View) {
        view.clearAnimation()
    }

    fun applyShimmerToViews(vararg views: View) {
        views.forEach { startShimmer(it) }
    }
}