package com.merttutsak.indicator

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.merttutsak.indicator.utils.extension.delay
import io.reactivex.rxjava3.disposables.Disposable

data class ItemViewModel(
    var time: Int,
    var margin: Float,
    val layout: RelativeLayout,
    val onEndProgress: () -> Unit
) {

    var timer: Disposable? = null
    private val dot: ImageView = layout.findViewById(R.id.dot)
    private val progress: ImageView = layout.findViewById(R.id.progress)

    init {
        val params = layout.layoutParams as FrameLayout.LayoutParams
        params.setMargins((margin).toInt(), 0, (margin).toInt(), 0)
        layout.layoutParams = params
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
        dot.isGone = true
        progress.isVisible = true
        startTimer()
    }

    private fun stopProgress() {
        stopTimer()
        //todo stop progress
        progress.isGone = true
        dot.isVisible = true
    }

    private fun startTimer(){
        stopTimer()
        timer = delay(time.toLong()) {
            if (isCurrent) {
                stopTimer()
                onEndProgress()
            }
        }
    }

    private fun stopTimer(){
        timer?.dispose()
        timer?.let { timer = null }
    }
}