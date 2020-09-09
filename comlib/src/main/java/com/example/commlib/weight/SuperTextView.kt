package com.example.commlib.weight

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.example.commlib.R
import com.example.commlib.weight.SuperTextView.DynamicStyle

/**
 * 逐渐变色的TextView
 * Created by yzh. 2019-11-28
 * 基于 @author Jenly 修改 [Jenly](mailto:jenly1314@gmail.com)
 */
@SuppressLint("AppCompatCustomView")
class SuperTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): TextView(context, attrs, defStyleAttr) {
    private var mDuration: Int = 200
    private var mIsStart: Boolean = false
    fun isStart(): Boolean {
        return mIsStart
    }

    private var mText: CharSequence?=null
    private var mPosition: Int = 0
    private var mSelectedColor: Int = -0xff01
    private var mOnDynamicListener: OnDynamicListener? = null
    private var mDynamicStyle: DynamicStyle = DynamicStyle.NORMAL

    enum class DynamicStyle(private val mValue: Int) {
        NORMAL(0), TYPEWRITING(1), CHANGE_COLOR(2);

        companion object {
            fun getFromInt(value: Int): DynamicStyle {
                for (style: DynamicStyle in values()) {
                    if (style.mValue == value) {
                        return style
                    }
                }
                return NORMAL
            }
        }

    }

    init {
        init(context,attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if(attrs!=null){
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SuperTextView)
            mText = a.getText(R.styleable.SuperTextView_dynamicText)
            mDuration = a.getInt(R.styleable.SuperTextView_duration, mDuration)
            mSelectedColor = a.getColor(R.styleable.SuperTextView_selectedColor, mSelectedColor)
            mDynamicStyle = DynamicStyle.getFromInt(a.getInt(R.styleable.SuperTextView_dynamicStyle, 0))
            a.recycle()
        }
    }

    fun start() {
        if (mIsStart) {
            return
        }
        if (TextUtils.isEmpty(mText)) { //如果动态文本为空、则取getText()的文本内容
            mText = text
        }
        mPosition = 0
        if (!TextUtils.isEmpty(mText)) {
            mIsStart = true
            post(mRunnable)
        } else {
            mIsStart = false
            mOnDynamicListener?.onCompile()
        }
    }

    /**
     * 停止
     */
    fun stop() {
        mIsStart = false
        mPosition = 0
        removeCallbacks(mRunnable)
    }

    /**
     * 暂停
     */
    fun push() {
        mIsStart = false
        removeCallbacks(mRunnable)
    }

    fun reStart() {
        mIsStart = true
        postDelayed(mRunnable, mDuration.toLong())
    }

     val mRunnable: Runnable = object : Runnable {
         override fun run() {
            when {
                DynamicStyle.TYPEWRITING == mDynamicStyle -> {
                    text = mText?.subSequence(0, mPosition)
                }
                DynamicStyle.CHANGE_COLOR == mDynamicStyle -> {
                    setChangeColorText(mPosition)
                }
                else -> {
                    text = mText
                    mIsStart = false
                    if (mOnDynamicListener != null) {
                        mOnDynamicListener?.onCompile()
                    }
                    return
                }
            }

             val length:Int= if(mText==null)0 else mText?.length ?: 0

            if (mPosition < length) {
                if (mOnDynamicListener != null) {
                    mOnDynamicListener?.onChange(mPosition,length)
                }
                mPosition++
               // postDelayed(mRunnable, mDuration.toLong())
                reStart()
            } else {
                if (mOnDynamicListener != null) {
                    mOnDynamicListener?.onChange(mPosition,length)
                }
                mIsStart = false
                if (mOnDynamicListener != null) {
                    mOnDynamicListener?.onCompile()
                }
            }
        }
    }

    private fun setChangeColorText(position: Int) {
        val spannableString: SpannableString = SpannableString(mText)
        spannableString.setSpan(ForegroundColorSpan(mSelectedColor), 0, position, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        text = spannableString
    }

    fun setDynamicText(@StringRes resId: Int) {
        mText = resources.getText(resId)
    }

    fun setDynamicText(text: CharSequence?) {
        if (text != null) {
            mText = text
        }
    }

    fun getDynamicText(): CharSequence? {
        return mText
    }

    fun setDuration(duration: Int) {
        mDuration = duration
    }

    /***
     * 根据指定播放完的总时间，计算出mDuration
     * @param time 毫秒
     */
    fun setDurationByToalTime(time: Long) {
        val length:Int= if(mText==null)0 else mText?.length ?: 0

        Log.i("SuperTextView", "总时长===$time")
        mDuration = (time / length).toInt()
        Log.d("SuperTextView", "mDuration===$mDuration")
    }

    fun getDuration(): Int {
        return mDuration
    }

    fun getDynamicStyle(): DynamicStyle {
        return mDynamicStyle
    }

    fun setDynamicStyle(dynamicStyle: DynamicStyle) {
        mDynamicStyle = dynamicStyle
    }

    fun setOnDynamicListener(onDynamicListener: OnDynamicListener?) {
        mOnDynamicListener = onDynamicListener
    }

    fun setSelectedColor(selectedColor: Int) {
        mSelectedColor = selectedColor
    }

    fun setSelectedColorResource(@ColorRes resId: Int) {
        mSelectedColor = ContextCompat.getColor(context, resId)
    }

    interface OnDynamicListener {
        fun onChange(position: Int, total: Int)
        fun onCompile()
    }
}