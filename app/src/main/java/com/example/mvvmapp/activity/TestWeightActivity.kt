package com.example.mvvmapp.activity

import android.os.Build
import androidx.annotation.RequiresApi
import com.blankj.ALog
import com.example.commlib.base.mvvm.BaseActivity
import com.example.commlib.base.mvvm.BaseViewModel
import com.example.commlib.rx.RxTimerUtil.IRxNext
import com.example.commlib.rx.RxTimerUtil.cancel
import com.example.commlib.rx.RxTimerUtil.interval
import com.example.commlib.utils.animations.Other.pulseAnimator
import com.example.commlib.utils.animations.RxAnimation
import com.example.commlib.weight.CountDownView
import com.example.commlib.weight.CountDownView.OnCountDownFinishListener
import com.example.commlib.weight.IProgressBar
import com.example.commlib.weight.IProgressBar.IProgressBarTextGenerator
import com.example.commlib.weight.SuperTextView
import com.example.commlib.weight.SuperTextView.OnDynamicListener
import com.example.mvvmapp.R
import com.example.mvvmapp.databinding.ActivityTestWeightBinding
import java.util.concurrent.TimeUnit

/**
 * @anthor yzh
 * @time 2019/11/30 15:07
 */
class TestWeightActivity constructor() : BaseActivity<ActivityTestWeightBinding?, BaseViewModel?>() {
    var num: Int = 0
    override val layoutId: Int
         get() {
            return R.layout.activity_test_weight
        }

    //    @Override
    //    protected BaseMvvmViewModel initMVVMViewModel() {
    //        return null;
    //    }
    public override fun initViewObservable() {}
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun initView() {
        mBinding?.mCircleProgressBar?.mIProgressBarTextGenerator =
            object : IProgressBarTextGenerator {
                override fun generateText(
                    progressBar: IProgressBar?,
                    value: Int,
                    maxValue: Int
                ): String? {
                    if (value == maxValue) {
                        mBinding?.mCircleProgressBar?.setProgress(0)
                        cancel("TestWeightActivity")
                    }
                    return "${100 * value / maxValue}%"
                }
            }

        //倒计时工具类
        interval(100, TimeUnit.MILLISECONDS, "TestWeightActivity", object : IRxNext {
            override fun doNext(number: Long, timerName: String?) {
                mBinding?.mCircleProgressBar?.setProgress(num++)
            }
        })
        mBinding?.mCountDownView?.startCountDown()

        mBinding?.mCountDownView?.setAddCountDownListener(object : OnCountDownFinishListener {
            override fun countDownFinished() {
                mBinding?.mCountDownView?.startCountDown()
            }
        })
        mBinding?.mLineWaveVoiceView?.startRecord()

        //示例动画工具类使用
        RxAnimation.get.setAnimation(pulseAnimator(mBinding?.mCountDownView, 2))
                .setDuration(1000)
                .start()
        val content = "Android仿酷狗动感歌词（支持翻译和音译歌词）显示效果\nhttps://www.jianshu.com/p/9e7111db7b41"
        mBinding?.mSuperTextView?.setDynamicText(content)
        mBinding?.mSuperTextView?.setDynamicStyle(SuperTextView.DynamicStyle.CHANGE_COLOR)
        mBinding?.mSuperTextView?.setDurationByToalTime(8 * 1000.toLong())
        mBinding?.mSuperTextView?.start()
        mBinding?.mSuperTextView?.setOnDynamicListener(object : OnDynamicListener {
            override fun onChange(position: Int, total: Int) {
                val lineCount: Int = mBinding?.mSuperTextView?.lineCount?:0
                val lineHeight: Int = mBinding?.mSuperTextView?.lineHeight?:0
                ALog.i(String.format("总行数：%s,行高度：%s", lineCount, lineHeight))
            }

            override fun onCompile() {
                mBinding?.mSuperTextView?.setDynamicStyle(SuperTextView.DynamicStyle.TYPEWRITING)
                mBinding?.mSuperTextView?.start()
            }
        })
    }
}