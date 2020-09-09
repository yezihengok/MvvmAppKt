package com.example.commlib.rx

import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import com.blankj.ALog
import com.example.commlib.R
import com.example.commlib.utils.CommUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Rxjava2.x实现轮询定时器.
 * 2019年04月15日09:07:59
 * @author yzh
 */
object RxTimerUtil {
    /**
     * 作用1、避免重复执行相同name定时器2,计时结束后取消订阅
     */
    private val mDisposableMap: MutableMap<String, Disposable> = HashMap()

    /**
     * x秒后执行next操作
     */
    fun timer(seconds: Long, name: String, next: IRxNext?) {
        if (mDisposableMap.containsKey(name)) {
            ALog.e(TextUtils.concat("已经有定时器【", name, "】在执行了，本次重复定时器不在执行").toString())
            return
        }
        Observable.timer(seconds, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(disposable: Disposable) {
                    mDisposableMap[name] = disposable
                }

                override fun onNext(number: Long) {
                    next?.doNext(number, name)
                }

                override fun onError(e: Throwable) {
                    //取消订阅
                    cancel(name)
                }

                override fun onComplete() {
                    //取消订阅
                    cancel(name)
                }
            })
    }

    /**
     * 每隔milliseconds秒后执行next操作
     * @param milliseconds
     * @param name 给当前定时器命名
     * @param next
     */
    fun interval(milliseconds: Long, name: String, next: IRxNext?) {
        if (mDisposableMap.containsKey(name)) {
            ALog.e(TextUtils.concat("已经有定时器【", name, "】在执行了，本次重复定时器不在执行").toString())
            return
        }
        Observable.interval(milliseconds, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(disposable: Disposable) {
                    mDisposableMap[name] = disposable
                }

                override fun onNext(number: Long) {
                    next?.doNext(number, name)
                }

                override fun onError(e: Throwable) {
                    cancel(name)
                    ALog.e("---onError---")
                }

                override fun onComplete() {
                    cancel(name)
                }
            })
    }

    /**
     * 每隔xx后执行next操作
     */
    @JvmStatic
    fun interval(milliseconds: Long, unit: TimeUnit?, name: String, next: IRxNext?) {
        if (mDisposableMap.containsKey(name)) {
            ALog.e(TextUtils.concat("已经有定时器【", name, "】在执行了，本次重复定时器不在执行").toString())
            return
        }
        Observable.interval(milliseconds, unit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(disposable: Disposable) {
                    mDisposableMap[name] = disposable
                }

                override fun onNext(number: Long) {
                    next?.doNext(number, name)
                }

                override fun onError(e: Throwable) {
                    cancel(name)
                }

                override fun onComplete() {
                    cancel(name)
                }
            })
    }

    /**
     * 每隔xx后执行next操作
     */
    fun countDownTimer(seconds: Long, name: String, tv: TextView) {
        if (mDisposableMap.containsKey(name)) {
            ALog.e(TextUtils.concat("已经有定时器【", name, "】在执行了，本次重复定时器不在执行").toString())
            return
        }
        Observable.interval(0, 1, TimeUnit.SECONDS)
            .take(seconds + 1)
            .map { aLong -> seconds - aLong }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()) //ui线程中进行控件更新
            .doOnSubscribe {
                tv.isEnabled = false
                tv.setTextColor(Color.BLACK)
            }.subscribe(object : Observer<Long> {
                override fun onSubscribe(disposable: Disposable) {
                    mDisposableMap[name] = disposable
                }

                override fun onNext(num: Long) {
                    tv.text = "剩余" + num + "秒"
                }

                override fun onError(e: Throwable) {
                    cancel(name)
                }

                override fun onComplete() {
                    //回复原来初始状态
                    tv.isEnabled = true
                    tv.text = "发送验证码"
                    cancel(name)
                }
            })
    }

    /**
     * 每隔xx后执行next操作
     */
    fun countDownTimer(seconds: Long, name: String, tv: TextView?, iTimer: ITimer) {
        if (mDisposableMap.containsKey(name)) {
            ALog.e(TextUtils.concat("已经有定时器【", name, "】在执行了，本次重复定时器不在执行").toString())
            return
        }
        Observable.interval(0, 1, TimeUnit.SECONDS)
            .take(seconds + 1)
            .map { aLong -> seconds - aLong }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()) //ui线程中进行控件更新
            .doOnSubscribe {
                if (tv != null) {
                    tv.visibility = View.VISIBLE
                    CommUtils.setTextColor(tv, R.color.color_write)
                }
            }
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(disposable: Disposable) {
                    mDisposableMap[name] = disposable
                }

                override fun onNext(num: Long) {
                    //tv.setText("剩余" + num + "秒");
                    if (tv != null) {
                        if (num <= 3) {
                            CommUtils.setTextColor(tv, R.color.color_red)
                        }
                        tv.text = num.toString()
                    }
                    iTimer.doNext(num, num == 0L)
                }

                override fun onError(e: Throwable) {
                    cancel(name)
                }

                override fun onComplete() {
                    //回复原来初始状态
                    // tv.setEnabled(true);
                    // tv.setText("发送验证码");
                    cancel(name)
                    if (tv != null) {
                        tv.visibility = View.GONE
                    }
                }
            })
    }

    /**
     * 取消订阅
     */
    @JvmStatic
    fun cancel(timerName: String) {
        val mDisposable = mDisposableMap[timerName] as Disposable?
        if (mDisposable != null) {
            mDisposableMap.remove(timerName)
            if (!mDisposable.isDisposed) {
                mDisposable.dispose()
                Log.i("RxTimerUtil", "---Rx定时器【$timerName】取消---")
            }
        }
    }

    interface IRxNext {
        fun doNext(number: Long, timerName: String?)
    }

    interface ITimer {
        fun doNext(number: Long, complete: Boolean)
    }
}