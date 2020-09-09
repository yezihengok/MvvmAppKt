package com.example.commlib.weight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.example.commlib.utils.DensityUtil

/**
 *
 * Created by zz on 2018/1/3.
 * 圆角ImageView
 */
class RoundImageView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(mContext, attrs, defStyleAttr) {
    var width: Float = 0f
    var height: Float = 0f
    private val mRadiusPx: Int = DensityUtil.dip2px(5f)
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        width = getWidth().toFloat()
        height = getHeight().toFloat()
    }

    override fun onDraw(canvas: Canvas) {

        //这里的目的是将画布设置成一个顶部边缘是圆角的矩形
        if (width > mRadiusPx && height > mRadiusPx) {
            val path: Path = Path()
            path.moveTo(mRadiusPx.toFloat(), 0f)
            path.lineTo(width - mRadiusPx, 0f)
            path.quadTo(width, 0f, width, mRadiusPx.toFloat())
            path.lineTo(width, height - mRadiusPx)
            path.quadTo(width, height, width - mRadiusPx, height)
            path.lineTo(mRadiusPx.toFloat(), height)
            path.quadTo(0f, height, 0f, height - mRadiusPx)
            path.lineTo(0f, mRadiusPx.toFloat())
            path.quadTo(0f, 0f, mRadiusPx.toFloat(), 0f)
            canvas.clipPath(path)
        }
        super.onDraw(canvas)
    }

    init {
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }
}