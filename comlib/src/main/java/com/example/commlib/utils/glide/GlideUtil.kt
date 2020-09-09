package com.example.commlib.utils.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.commlib.R
import com.example.commlib.utils.DensityUtil

/**
 * @author yzh
 * @date 2019/04/29
 */
object GlideUtil {
    //    DiskCacheStrategy.NONE： 表示不缓存任何内容。
    //    DiskCacheStrategy.DATA： 表示只缓存原始图片。
    //    DiskCacheStrategy.RESOURCE： 表示只缓存转换过后的图片。
    //    DiskCacheStrategy.ALL ： 表示既缓存原始图片，也缓存转换过后的图片。
    //    DiskCacheStrategy.AUTOMATIC： 表示让Glide根据图片资源智能地选择使用哪一种缓存策略（默认选项）。
    /**
     * 将gif图转换为静态图
     */
    fun displayasBitmap(url: String?, imageView: ImageView) {
        Glide.with(imageView.context)
            .asBitmap()
            .load(url)
            .placeholder(R.drawable.shape_bg_loading)
            .error(R.drawable.shape_bg_loading) //                .skipMemoryCache(true) //跳过内存缓存
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(imageView)
    }

    /**
     * 一般加载
     * @param url
     * @param type  加载失败默认图类型
     */
    @JvmStatic
    @BindingAdapter("displayImg", "displayType")
    fun displayImg(imageView: ImageView, url: String?, type: Int) {
        Glide.with(imageView.context)
            .load(url)
            .centerCrop()
            .placeholder(getDefaultPic(type))
            .error(getDefaultPic(type))
            .into(imageView)
    }

    /**
     * 加载圆角图片(实现加载的图片和默认加载失败（占位图）都处理成指定圆角效果)
     * @param url
     * @param type  加载失败默认图类型
     * @param radius 圆角度数
     */
    @JvmStatic
    @BindingAdapter(value = ["displayRound", "displayType", "radius"], requireAll = true)
    fun displayRoundImg(imageView: ImageView, url: String?, type: Int, radius: Int) {
        //这种加载成功才会显示圆角
//        Glide.with(imageView.getContext())
//                .load(url)
//                .centerCrop()
//                .apply(RequestOptions.bitmapTransform(new RoundedCorners(radius)))
//                .transition(DrawableTransitionOptions.withCrossFade(500))
//                .placeholder(getDefaultPic(type))
//                .error(getDefaultPic(type))
//                .into(imageView);
        val placeholderId = getDefaultPic(type)
        val errorId = getDefaultPic(type)
        val transform: Transformation<Bitmap> = GlideRoundTransform(imageView.context, radius)
        //Transformation<Bitmap> transform=new RoundedCorners(radius);
        Glide.with(imageView.context).load(url)
            .apply(
                RequestOptions()
                    .placeholder(placeholderId)
                    .error(errorId)
                    .centerCrop()
                    .transform(transform)
            ) //    .transition(DrawableTransitionOptions.withCrossFade(500))
            .thumbnail(loadTransform(imageView.context, placeholderId, transform))
            .thumbnail(loadTransform(imageView.context, errorId, transform))
            .into(imageView)
    }

    private fun loadTransform(
        context: Context, @DrawableRes placeholderId: Int, transform: Transformation<Bitmap>
    ): RequestBuilder<Drawable> {
        return Glide.with(context)
            .load(placeholderId)
            .apply(
                RequestOptions().centerCrop()
                    .transform(transform)
            )
    }

    /**
     * 加载圆形图,暂时用到显示头像(实现加载的图片和默认加载失败（占位图）都处理成指定圆形效果)
     */
    @JvmStatic
    @BindingAdapter(value = ["displayCircle", "displayType"], requireAll = true)
    fun displayCircle(imageView: ImageView, url: String?, type: Int) {

//        Glide.with(imageView.getContext())
//                .load(url)
//                .centerCrop()
//                .apply(RequestOptions.circleCropTransform())
//                .transition(DrawableTransitionOptions.withCrossFade(500))
//                .placeholder(getDefaultPic(type))
//                .error(getDefaultPic(type))
//                .into(imageView);
        val placeholderId = getDefaultPic(type)
        val errorId = getDefaultPic(type)
        val transform: Transformation<Bitmap> = GlideCircleTransform()
        Glide.with(imageView.context).load(url)
            .apply(
                RequestOptions()
                    .placeholder(placeholderId)
                    .error(errorId)
                    .centerCrop()
                    .transform(transform)
            ) //               .transition(DrawableTransitionOptions.withCrossFade(500))
            .thumbnail(loadTransform(imageView.context, placeholderId, transform))
            .thumbnail(loadTransform(imageView.context, errorId, transform))
            .into(imageView)
    }

    private fun getDefaultPic(type: Int): Int {
        when (type) {
            0 -> return R.drawable.shape_bg_loading
            4 -> return R.drawable.no_banner
            else -> {
            }
        }
        return R.drawable.shape_bg_loading
    }
    /**
     * 显示高斯模糊效果（电影详情页）
     */
    //    @BindingAdapter("android:displayGaussian")
    //    private static void displayGaussian(ImageView imageView,String url) {
    // "23":模糊度；"4":图片缩放4倍后再进行模糊
    //        Glide.with(imageView.getContext())
    //                .load(url)
    //                .transition(DrawableTransitionOptions.withCrossFade())
    //                .error(R.drawable.stackblur_default)
    //                .placeholder(R.drawable.stackblur_default)
    //                .transition(DrawableTransitionOptions.withCrossFade(500))
    //                .transform(new BlurTransformation(50, 8))
    //                .into(imageView);
    //   }
    /**
     * 加载固定宽高图片
     */
    @JvmStatic
    @BindingAdapter("android:imageUrl", "android:imageWidth", "android:imageHeight")
    fun imageUrl(imageView: ImageView, url: String?, imageWidthDp: Int, imageHeightDp: Int) {
        Glide.with(imageView.context)
            .load(url)
            .override(
                DensityUtil.dip2px(imageWidthDp.toFloat()),
                DensityUtil.dip2px(imageHeightDp.toFloat())
            )
            .placeholder(getDefaultPic(4))
            .centerCrop()
            .error(getDefaultPic(0))
            .into(imageView)

        //         .apply(bitmapTransform(new CircleCrop()))
//                .transform(new GlideCircleTransform())
//                .transform(new RoundedCorners(20))
//                .transform(new CenterCrop(), new RoundedCorners(20))
    }
}