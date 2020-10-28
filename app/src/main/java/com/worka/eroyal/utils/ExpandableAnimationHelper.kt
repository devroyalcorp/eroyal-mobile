package com.worka.eroyal.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-11.
 */
class AnimationHelper {
    private var heightView = 0
    private var animation: Animation? = null
    private var duration: Float = 0f
    private var durationPerDP = 2

    constructor(durationPerDP: Int) {
        this.durationPerDP = durationPerDP
    }

    fun expandLayout(view: View, measureHeight: Int = view.measuredHeight) {
        view.measure(view.width, view.height)
        heightView = measureHeight
        view.layoutParams.height = 1
        view.visibility = View.VISIBLE
        animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val newHeight: Int
                if (view.measuredHeight <= heightView) {
                    newHeight = (1 + (heightView - 1) * interpolatedTime).toInt()
                    view.layoutParams.height = newHeight
                    view.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        startAnimation(view)
    }

    fun collapseLayout(view: View) {
        heightView = view.measuredHeight
        animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, transformation: Transformation) {
                val tmpInterpolatedTime = interpolatedTime.toInt()
                if (tmpInterpolatedTime == 1) view.visibility = View.GONE else {
                    view.layoutParams.height = (heightView - (heightView * interpolatedTime)).toInt()
                    view.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        startAnimation(view)
    }

    private fun startAnimation(view: View) {
        duration = ((heightView / view.context.resources.displayMetrics.density).toInt() * durationPerDP).toFloat()
        animation?.duration = duration.toLong()
        view.startAnimation(animation)
    }

    fun getDuration(): Long {
        return duration.toLong()
    }
}

