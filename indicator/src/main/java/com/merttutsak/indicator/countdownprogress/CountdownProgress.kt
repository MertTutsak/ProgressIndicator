package com.merttutsak.indicator.countdownprogress

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.merttutsak.indicator.R

class CountdownProgress @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val KEY_SUPERSTATE = "cpv_superstate"

        private const val KEY_CURRENT_TIME = "cpv_currenttime"

        private const val DEFAULT_DURATION = 2000L
    }

    // The underlying progressbar widget
    private lateinit var progressBar: ProgressBar

    // The animator used to update the progressbar widget
    private lateinit var progressAnimator: ObjectAnimator

    // The listener to trigger after countdown is finished
    var onCompleteListener: (()-> Unit)? = null

    // Saves the currently paused time for the animation. Used to resume pre-19
    private var currentPlayTime: Long = 0L

    // If the animation was canceled.
    private var wasCanceled: Boolean = false

    init {
        LayoutInflater.from(context).inflate(R.layout.count_down_layout, this, true)

        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.max = Integer.MAX_VALUE

        progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, progressBar.max)

        progressAnimator.let {
            it.interpolator = LinearInterpolator()
            it.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    wasCanceled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (onCompleteListener != null && !wasCanceled) {
                        onCompleteListener?.invoke()
                    }
                }
            })
        }

        setDuration(DEFAULT_DURATION)

    }

    fun play() {
        wasCanceled = false
        if (progressAnimator.isPaused) {
            progressAnimator.resume()
        } else if (!progressAnimator.isRunning) {
            progressAnimator.start()
        }
    }

    fun pause() {
        if (!progressAnimator.isPaused && progressAnimator.isRunning) {
            // Paused animators still count as running
            currentPlayTime = progressAnimator.currentPlayTime
        }
        progressAnimator.pause()
    }

    fun reset() {
        currentPlayTime = 0
        progressAnimator.currentPlayTime = currentPlayTime
        progressAnimator.cancel()
    }

    fun setDuration(duration: Long) {
        progressAnimator.duration = duration
    }


    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(KEY_SUPERSTATE, super.onSaveInstanceState())
        bundle.putLong(KEY_CURRENT_TIME, currentPlayTime)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val bundle = state
            currentPlayTime = bundle.getLong(KEY_CURRENT_TIME, 0)
            progressAnimator.currentPlayTime = currentPlayTime
            super.onRestoreInstanceState(bundle.getParcelable(KEY_SUPERSTATE))
        } else {
            super.onRestoreInstanceState(state)
        }
    }
}