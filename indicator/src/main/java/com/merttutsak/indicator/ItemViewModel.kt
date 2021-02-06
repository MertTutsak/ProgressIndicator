package com.merttutsak.indicator

import android.icu.util.TimeUnit
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.merttutsak.indicator.countdownprogress.CountdownProgress
import io.reactivex.rxjava3.disposables.Disposable

data class ItemViewModel(
    var time: Int,
    var margin: Float,
    val layout: RelativeLayout,
    val onEndProgress: () -> Unit
) {

    private val progress: CountdownProgress = layout.findViewById(R.id.progress)

    init {
        val params = layout.layoutParams as FrameLayout.LayoutParams
        params.setMargins((margin).toInt(), 0, (margin).toInt(), 0)
        layout.layoutParams = params
        layout.requestLayout()

        progress.setDuration((time * 1000).toLong())
        progress.onCompleteListener = {
            if (isCurrent) {
                onEndProgress()
                progress.reset()
            }
        }
    }

    private var isCurrent: Boolean = false
        set(value) {
            field = value
            if (field) {
                startProgress()
            } else {
                stopProgress()
            }
        }

    fun isCurrent(): Boolean = isCurrent

    fun dot() {
        isCurrent = false
    }

    fun progress() {
        isCurrent = true
    }

    private fun startProgress() {
        progress.reset()
        progress.isVisible = true
        progress.play()
    }

    private fun stopProgress() {
        progress.isGone = true
        progress.reset()
    }

}