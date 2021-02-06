package com.merttutsak.indicator.utils.extension

import android.util.Log
import com.merttutsak.indicator.utils.SchedulerProvider
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

fun delay(delay: Long, timeUnit: TimeUnit = TimeUnit.SECONDS, f: () -> Unit): Disposable {
    return Observable.timer(delay, timeUnit)
        .compose(SchedulerProvider.ioToMainObservableScheduler())
        .doOnError {
            Log.e("TimerError",it.localizedMessage?:"")
        }
        .subscribe {
            f()
        }
}