package com.example.commlib.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.commlib.weight.banner.loader.ImageLoader

class GlideImageLoader : ImageLoader() {
    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
        Glide.with(context.applicationContext)
            .load(path)
            .into(imageView)
    } /*  @Override
    public ImageView createImageView(Context context) {
        //圆角
        return new RoundImageView(context);
    }*/
}