package com.example.commlib.utils.glide

import android.content.Context
import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import java.security.MessageDigest

class GlideRoundTransform @JvmOverloads constructor(context: Context, radius: Int = 10) :
    CenterCrop() {
    private val radius: Float
    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        //glide4.0+
        val transform = super.transform(pool, toTransform, outWidth, outHeight)
        return roundCrop(pool, transform)!!
        //glide3.0
        //return roundCrop(pool, toTransform);
    }

    private fun roundCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) return null
        var result: Bitmap? = pool[source.width, source.height, Bitmap.Config.ARGB_8888]
        if (result == null) {
            result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(result!!)
        val paint = Paint()
        paint.shader =
            BitmapShader(source,Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        val rectF = RectF(
            0f, 0f, source.width.toFloat(), source.height
                .toFloat()
        )
        canvas.drawRoundRect(rectF, radius, radius, paint)
        return result
    }

    val id: String
        get() = javaClass.name + Math.round(radius)

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}

    companion object {
        fun pxTodp(context: Context, px: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (px / scale + 0.5f).toInt()
        }

        fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }

    init {
        this.radius = dip2px(context, radius.toFloat()).toFloat()
        //this.radius = radius;
    }
}