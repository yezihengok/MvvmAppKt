package com.example.commlib.utils

import android.content.ContentResolver
import android.content.res.*
import android.content.res.Resources.Theme
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.text.TextUtils
import android.util.DisplayMetrics
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.blankj.ALog
import com.example.commlib.api.App
import java.io.*
import java.util.*

/**
 * detail: 资源文件工具类
 * @author Ttt
 */
object ResourceUtils {
    // 日志 TAG
    private val TAG = ResourceUtils::class.java.simpleName
    // ================
    // = 快捷获取方法 =
    // ================
    /**
     * 获取 Resources
     * @return [Resources]
     */
    @JvmStatic
    val resources: Resources?
        get() {
            try {
                return App.instance.resources
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getResources")
            }
            return null
        }

    /**
     * 获取 Resources.Theme
     * @return [Resources.Theme]
     */
    val theme: Theme?
        get() {
            try {
                return App.instance.theme
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getTheme")
            }
            return null
        }

    /**
     * 获取 AssetManager
     * @return [AssetManager]
     */
    val assets: AssetManager?
        get() {
            try {
                return App.instance.assets
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getAssets")
            }
            return null
        }

    /**
     * 获取 ContentResolver
     * @return [ContentResolver]
     */
    @JvmStatic
    val contentResolver: ContentResolver?
        get() {
            try {
                return App.instance.contentResolver
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getContentResolver")
            }
            return null
        }

    /**
     * 获取 DisplayMetrics
     * @return [DisplayMetrics]
     */
    val displayMetrics: DisplayMetrics?
        get() {
            try {
                return App.instance.resources.displayMetrics
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getDisplayMetrics")
            }
            return null
        }

    /**
     * 获取 Configuration
     * @return [Configuration]
     */
    @JvmStatic
    val configuration: Configuration?
        get() {
            try {
                return App.instance.resources.configuration
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getConfiguration")
            }
            return null
        }

    /**
     * 获取 ColorStateList
     * @param id resource identifier of a [ColorStateList]
     * @return [ColorStateList]
     */
    fun getColorStateList(@ColorRes id: Int): ColorStateList? {
        try {
            return ContextCompat.getColorStateList(App.instance, id)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getColorStateList")
        }
        return null
    }

    /**
     * 获取 String
     * @param id R.string.id
     * @return String
     */
    fun getString(@StringRes id: Int): String? {
        try {
            return App.instance.resources.getString(id)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getString")
        }
        return null
    }

    /**
     * 获取 String
     * @param id         R.string.id
     * @param formatArgs 格式化参数
     * @return String
     */
    fun getString(@StringRes id: Int, vararg formatArgs: Any?): String? {
        try {
            return App.instance.resources.getString(id, *formatArgs)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getString")
        }
        return null
    }

    /**
     * 获取 Color
     * @param colorId R.color.id
     * @return Color
     */
    fun getColor(@ColorRes colorId: Int): Int {
        try {
            return ContextCompat.getColor(App.instance, colorId)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getColor")
        }
        return -1
    }

    /**
     * 获取 Drawable
     * @param drawableId R.drawable.id
     * @return [Drawable]
     */
    fun getDrawable(@DrawableRes drawableId: Int): Drawable? {
        try {
            return ContextCompat.getDrawable(App.instance, drawableId)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getDrawable")
        }
        return null
    }

    /**
     * 获取 .9 Drawable
     * @param drawableId R.drawable.id
     * @return .9 [NinePatchDrawable]
     */
    fun getNinePatchDrawable(@DrawableRes drawableId: Int): NinePatchDrawable? {
        try {
            return getDrawable(drawableId) as NinePatchDrawable?
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getNinePatchDrawable")
        }
        return null
    }

    /**
     * 获取指定颜色 Drawable
     * @param color 颜色值
     * @return 指定颜色 Drawable
     */
    fun getColorDrawable(@ColorInt color: Int): ColorDrawable? {
        try {
            return ColorDrawable(color)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getColorDrawable")
        }
        return null
    }

    /**
     * 获取十六进制颜色值 Drawable
     * @param color 十六进制颜色值
     * @return 十六进制颜色值 Drawable
     */
    fun getColorDrawable(color: String?): ColorDrawable? {
        try {
            return ColorDrawable(Color.parseColor(color))
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getColorDrawable")
        }
        return null
    }

    /**
     * 获取 Bitmap
     * @param resId resource identifier
     * @return [Bitmap]
     */
    fun getBitmap(resId: Int): Bitmap? {
        try {
            return BitmapFactory.decodeResource(App.instance.resources, resId)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getBitmap")
        }
        return null
    }

    /**
     * 获取 Bitmap
     * @param resId   resource identifier
     * @param options [BitmapFactory.Options]
     * @return [Bitmap]
     */
    fun getBitmap(resId: Int, options: BitmapFactory.Options?): Bitmap? {
        try {
            return BitmapFactory.decodeResource(App.instance.resources, resId, options)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getBitmap")
        }
        return null
    }

    /**
     * 获取 Dimension
     * @param id resource identifier
     * @return Dimension
     */
    fun getDimension(@DimenRes id: Int): Float {
        try {
            return App.instance.resources.getDimension(id)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getDimension")
        }
        return 0f
    }

    /**
     * 获取 Boolean
     * @param id resource identifier
     * @return Boolean
     */
    fun getBoolean(@BoolRes id: Int): Boolean {
        try {
            return App.instance.resources.getBoolean(id)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getBoolean")
        }
        return false
    }

    /**
     * 获取 Integer
     * @param id resource identifier
     * @return Integer
     */
    fun getInteger(@IntegerRes id: Int): Int {
        try {
            return App.instance.resources.getInteger(id)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getInteger")
        }
        return -1
    }

    /**
     * 获取 Animation
     * @param id resource identifier
     * @return XmlResourceParser
     */
    fun getAnimation(@AnimatorRes @AnimRes id: Int): XmlResourceParser? {
        try {
            return App.instance.resources.getAnimation(id)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getAnimation")
        }
        return null
    }

    /**
     * 获取给定资源标识符的全名
     * @param id resource identifier
     * @return Integer
     */
    fun getResourceName(@AnyRes id: Int): String? {
        try {
            return App.instance.resources.getResourceName(id)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getResourceName")
        }
        return null
    }

    /**
     * 获取 int[]
     * @param id resource identifier
     * @return int[]
     */
    fun getIntArray(@ArrayRes id: Int): IntArray? {
        try {
            return App.instance.resources.getIntArray(id)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getIntArray")
        }
        return null
    }

    /**
     * 获取 String[]
     * @param id resource identifier
     * @return String[]
     */
    fun getStringArray(@ArrayRes id: Int): Array<String>? {
        try {
            return App.instance.resources.getStringArray(id)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getStringArray")
        }
        return null
    }

    /**
     * 获取 CharSequence[]
     * @param id resource identifier
     * @return CharSequence[]
     */
    fun getTextArray(@ArrayRes id: Int): Array<CharSequence>? {
        try {
            return App.instance.resources.getTextArray(id)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getTextArray")
        }
        return null
    }
    // =
    /**
     * 获取 layout id
     * @param resName layout xml fileName
     * @return layout id
     */
    fun getLayoutId(resName: String): Int {
        return getIdentifier(resName, "layout")
    }

    /**
     * 获取 drawable id
     * @param resName drawable name
     * @return drawable id
     */
    fun getDrawableId(resName: String): Int {
        return getIdentifier(resName, "drawable")
    }

    /**
     * 获取 mipmap id
     * @param resName mipmap name
     * @return mipmap id
     */
    fun getMipmapId(resName: String): Int {
        return getIdentifier(resName, "mipmap")
    }

    /**
     * 获取 menu id
     * @param resName menu name
     * @return menu id
     */
    fun getMenuId(resName: String): Int {
        return getIdentifier(resName, "menu")
    }

    /**
     * 获取 raw id
     * @param resName raw name
     * @return raw id
     */
    fun getRawId(resName: String): Int {
        return getIdentifier(resName, "raw")
    }

    /**
     * 获取 anim id
     * @param resName anim xml fileName
     * @return anim id
     */
    fun getAnimId(resName: String): Int {
        return getIdentifier(resName, "anim")
    }

    /**
     * 获取 color id
     * @param resName color name
     * @return color id
     */
    fun getColorId(resName: String): Int {
        return getIdentifier(resName, "color")
    }

    /**
     * 获取 dimen id
     * @param resName dimen name
     * @return dimen id
     */
    fun getDimenId(resName: String): Int {
        return getIdentifier(resName, "dimen")
    }

    /**
     * 获取 attr id
     * @param resName attr name
     * @return attr id
     */
    fun getAttrId(resName: String): Int {
        return getIdentifier(resName, "attr")
    }

    /**
     * 获取 style id
     * @param resName style name
     * @return style id
     */
    fun getStyleId(resName: String): Int {
        return getIdentifier(resName, "style")
    }

    /**
     * 获取 styleable id
     * @param resName styleable name
     * @return styleable id
     */
    fun getStyleableId(resName: String): Int {
        return getIdentifier(resName, "styleable")
    }

    /**
     * 获取 id
     * @param resName id name
     * @return id
     */
    fun getId(resName: String): Int {
        return getIdentifier(resName, "id")
    }

    /**
     * 获取 string id
     * @param resName string name
     * @return string id
     */
    fun getStringId(resName: String): Int {
        return getIdentifier(resName, "string")
    }

    /**
     * 获取 bool id
     * @param resName bool name
     * @return bool id
     */
    fun getBoolId(resName: String): Int {
        return getIdentifier(resName, "bool")
    }

    /**
     * 获取 integer id
     * @param resName integer name
     * @return integer id
     */
    fun getIntegerId(resName: String): Int {
        return getIdentifier(resName, "integer")
    }

    /**
     * 获取资源 id
     * @param resName 资源名
     * @param defType 资源类型
     * @return 资源 id
     */
    fun getIdentifier(resName: String, defType: String): Int {
        return getIdentifier(resName, defType, AppUtils.packageName)
    }

    /**
     * 获取资源 id
     * @param resName     资源名
     * @param defType     资源类型
     * @param packageName 应用包名
     * @return 资源 id
     */
    @JvmStatic
    fun getIdentifier(resName: String, defType: String, packageName: String): Int {
        try {
            return App.instance.resources.getIdentifier(resName, defType, packageName)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getIdentifier - $resName $defType: $packageName")
        }
        return 0
    }
    // =
    /**
     * 获取 AssetManager 指定资源 InputStream
     * @param fileName 文件名
     * @return [InputStream]
     */
    fun open(fileName: String?): InputStream? {
        try {
            return App.instance.assets.open(fileName!!)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "open")
        }
        return null
    }

    /**
     * 获取 AssetManager 指定资源 AssetFileDescriptor
     * @param fileName 文件名
     * @return [AssetFileDescriptor]
     */
    fun openFd(fileName: String?): AssetFileDescriptor? {
        try {
            return App.instance.assets.openFd(fileName!!)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openFd")
        }
        return null
    }

    /**
     * 获取 AssetManager 指定资源 AssetFileDescriptor
     * @param fileName 文件名
     * @return [AssetFileDescriptor]
     */
    fun openNonAssetFd(fileName: String?): AssetFileDescriptor? {
        try {
            return App.instance.assets.openNonAssetFd(fileName!!)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openNonAssetFd")
        }
        return null
    }

    /**
     * 获取对应资源 InputStream
     * @param id resource identifier
     * @return [InputStream]
     */
    fun openRawResource(@RawRes id: Int): InputStream? {
        try {
            return App.instance.resources.openRawResource(id)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openRawResource")
        }
        return null
    }

    /**
     * 获取对应资源 AssetFileDescriptor
     * @param id resource identifier
     * @return [AssetFileDescriptor]
     */
    fun openRawResourceFd(@RawRes id: Int): AssetFileDescriptor? {
        try {
            return App.instance.resources.openRawResourceFd(id)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openRawResourceFd")
        }
        return null
    }

    /**
     * 获取 Uri InputStream
     * <pre>
     * 主要用于获取到分享的 FileProvider Uri 存储起来 [FileIOUtils.writeFileFromIS]
    </pre> *
     * @param uri [Uri] FileProvider Uri、Content Uri、File Uri
     * @return Uri InputStream
     */
    fun openInputStream(uri: Uri?): InputStream? {
        if (uri == null) return null
        try {
            return contentResolver!!.openInputStream(uri)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openInputStream $uri")
        }
        return null
    }

    /**
     * 获取 Uri OutputStream
     * @param uri [Uri] FileProvider Uri、Content Uri、File Uri
     * @return Uri OutputStream
     */
    fun openOutputStream(uri: Uri?): OutputStream? {
        if (uri == null) return null
        try {
            return contentResolver!!.openOutputStream(uri)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openOutputStream $uri")
        }
        return null
    }

    /**
     * 获取 Uri OutputStream
     * @param uri  [Uri] FileProvider Uri、Content Uri、File Uri
     * @param mode 读写模式
     * @return Uri OutputStream
     */
    fun openOutputStream(uri: Uri?, mode: String): OutputStream? {
        if (uri == null) return null
        try {
            return contentResolver!!.openOutputStream(uri, mode)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openOutputStream mode: $mode, $uri")
        }
        return null
    }

    /**
     * 获取 Uri ParcelFileDescriptor
     * <pre>
     * 通过 new FileInputStream(openFileDescriptor().getFileDescriptor()) 进行文件操作
    </pre> *
     * @param uri  [Uri] FileProvider Uri、Content Uri、File Uri
     * @param mode 读写模式
     * @return Uri ParcelFileDescriptor
     */
    fun openFileDescriptor(uri: Uri?, mode: String): ParcelFileDescriptor? {
        if (uri == null || TextUtils.isEmpty(mode)) return null
        try {
            return contentResolver!!.openFileDescriptor(uri, mode)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openFileDescriptor mode: $mode, $uri")
        }
        return null
    }

    /**
     * 获取 Uri AssetFileDescriptor
     * <pre>
     * 通过 new FileInputStream(openAssetFileDescriptor().getFileDescriptor()) 进行文件操作
    </pre> *
     * @param uri  [Uri] FileProvider Uri、Content Uri、File Uri
     * @param mode 读写模式
     * @return Uri AssetFileDescriptor
     */
    fun openAssetFileDescriptor(uri: Uri?, mode: String): AssetFileDescriptor? {
        if (uri == null || TextUtils.isEmpty(mode)) return null
        try {
            return contentResolver!!.openAssetFileDescriptor(uri, mode)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openAssetFileDescriptor mode: $mode, $uri")
        }
        return null
    }
    // ================
    // = 读取资源文件 =
    // ================
    /**
     * 获取 Assets 资源文件数据
     * <pre>
     * 直接传入文件名、文件夹 / 文件名 等
     * 根目录 a.txt
     * 子目录 /www/a.html
    </pre> *
     * @param fileName 文件名
     * @return 文件 byte[] 数据
     */
    fun readBytesFromAssets(fileName: String?): ByteArray? {
        var `is`: InputStream? = null
        try {
            `is` = open(fileName)
            val length = `is`!!.available()
            val buffer = ByteArray(length)
            `is`.read(buffer)
            return buffer
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "readBytesFromAssets")
        } finally {
            CloseUtils.closeIOQuietly(`is`)
        }
        return null
    }

    /**
     * 获取 Assets 资源文件数据
     * @param fileName 文件名
     * @return 文件字符串内容
     */
    fun readStringFromAssets(fileName: String?): String? {
        try {
            val str=String(readBytesFromAssets(fileName)!!, Charsets.UTF_8)

            return str
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "readStringFromAssets")
        }
        return null
    }
    // =
    /**
     * 获取 Raw 资源文件数据
     * @param resId 资源 id
     * @return 文件 byte[] 数据
     */
    fun readBytesFromRaw(@RawRes resId: Int): ByteArray? {
        var `is`: InputStream? = null
        try {
            `is` = openRawResource(resId)
            val length = `is`!!.available()
            val buffer = ByteArray(length)
            `is`.read(buffer)
            return buffer
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "readBytesFromRaw")
        } finally {
            CloseUtils.closeIOQuietly(`is`)
        }
        return null
    }

    /**
     * 获取 Raw 资源文件数据
     * @param resId 资源 id
     * @return 文件字符串内容
     */
    fun readStringFromRaw(@RawRes resId: Int): String? {
        try {
            val  string=String(readBytesFromRaw(resId)!!, Charsets.UTF_8)
            return string
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "readStringFromRaw")
        }
        return null
    }
    // =
    /**
     * 获取 Assets 资源文件数据 ( 返回 List<String> 一行的全部内容属于一个索引 )
     * @param fileName 文件名
     * @return [<]
    </String> */
    fun geFileToListFromAssets(fileName: String?): List<String>? {
        var `is`: InputStream? = null
        var br: BufferedReader? = null
        try {
            `is` = open(fileName)
            br = BufferedReader(InputStreamReader(`is`))
            val lists: MutableList<String> = ArrayList()
            var line: String
            while (br.readLine().also { line = it } != null) {
                lists.add(line)
            }
            return lists
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "geFileToListFromAssets")
        } finally {
            CloseUtils.closeIOQuietly(`is`, br)
        }
        return null
    }

    /**
     * 获取 Raw 资源文件数据 ( 返回 List<String> 一行的全部内容属于一个索引 )
     * @param resId 资源 id
     * @return [<]
    </String> */
    fun geFileToListFromRaw(@RawRes resId: Int): List<String>? {
        var `is`: InputStream? = null
        var br: BufferedReader? = null
        try {
            `is` = openRawResource(resId)
            br = BufferedReader(InputStreamReader(`is`))
            val lists: MutableList<String> = ArrayList()
            var line: String
            while (br.readLine().also { line = it } != null) {
                lists.add(line)
            }
            return lists
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "geFileToListFromRaw")
        } finally {
            CloseUtils.closeIOQuietly(`is`, br)
        }
        return null
    }
    // =
    /**
     * 获取 Assets 资源文件数据并保存到本地
     * @param fileName 文件名
     * @param file     文件保存地址
     * @return `true` success, `false` fail
     */
    fun saveAssetsFormFile(fileName: String?, file: File?): Boolean {
        try {
            // 获取 Assets 文件
            val `is` = open(fileName)
            // 存入 SDCard
            val fos = FileOutputStream(file)
            // 设置数据缓冲
            val buffer = ByteArray(1024)
            // 创建输入输出流
            val baos = ByteArrayOutputStream()
            var len: Int
            while (`is`!!.read(buffer).also { len = it } != -1) {
                baos.write(buffer, 0, len)
            }
            // 保存数据
            val bytes = baos.toByteArray()
            // 写入保存的文件
            fos.write(bytes)
            // 关闭流
            CloseUtils.closeIOQuietly(baos, `is`)
            fos.flush()
            CloseUtils.closeIOQuietly(fos)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "saveAssetsFormFile")
        }
        return false
    }

    /**
     * 获取 Raw 资源文件数据并保存到本地
     * @param resId 资源 id
     * @param file  文件保存地址
     * @return `true` success, `false` fail
     */
    fun saveRawFormFile(@RawRes resId: Int, file: File?): Boolean {
        try {
            // 获取 raw 文件
            val `is` = openRawResource(resId)
            // 存入 SDCard
            val fos = FileOutputStream(file)
            // 设置数据缓冲
            val buffer = ByteArray(1024)
            // 创建输入输出流
            val baos = ByteArrayOutputStream()
            var len: Int
            while (`is`!!.read(buffer).also { len = it } != -1) {
                baos.write(buffer, 0, len)
            }
            // 保存数据
            val bytes = baos.toByteArray()
            // 写入保存的文件
            fos.write(bytes)
            // 关闭流
            CloseUtils.closeIOQuietly(baos, `is`)
            fos.flush()
            CloseUtils.closeIOQuietly(fos)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "saveRawFormFile")
        }
        return false
    }
}