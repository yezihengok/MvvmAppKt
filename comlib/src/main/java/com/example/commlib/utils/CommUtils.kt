//package com.example.commlib.utils
//
//import android.app.Activity
//import android.app.Dialog
//import android.content.Context
//import android.content.Intent
//import android.content.res.Resources
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.net.Uri
//import android.os.Build
//import android.os.Looper
//import android.text.TextUtils
//import android.util.Log
//import android.view.Gravity
//import android.view.View
//import android.view.ViewGroup
//import android.view.ViewGroup.MarginLayoutParams
//import android.view.ViewTreeObserver.OnGlobalLayoutListener
//import android.view.Window
//import android.widget.Button
//import android.widget.EditText
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.annotation.ArrayRes
//import androidx.annotation.StringRes
//import androidx.core.content.FileProvider
//import com.blankj.ALog
//import com.example.commlib.R
//import com.example.commlib.api.App.Companion.instance
//import com.example.commlib.listener.Listener
//import org.apache.commons.lang.StringUtils
//import org.json.JSONObject
//import java.io.File
//import java.util.*
//import java.util.regex.Pattern
//
///**
// * @Description: 公用的一些方法
// * @Author: yzh
// * @CreateDate: 2019/10/23 14:37
// */
//class CommUtils {
//
//    companion object {
//        private const val TAG = "CommUtils"
//
//        /**
//         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
//         * @param dpValue dp 值
//         * @return int 转换后的值
//         */
//        fun dip2px(dpValue: Float): Int {
//            val scale = instance.resources.displayMetrics.density
//            return (dpValue * scale + 0.5f).toInt()
//        }
//
//        /**
//         * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
//         * @param pxValue dp 值
//         * @return int 转换后的值
//         */
//        fun px2dip(pxValue: Float): Int {
//            val scale = instance.resources.displayMetrics.density
//            return (pxValue / scale + 0.5f).toInt()
//        }
//
//        /**
//         * 直接获取控件的宽、高
//         * @param view
//         * @return int[]
//         */
//        fun getViewHeight(view: View): Int {
//            val vto2 = view.viewTreeObserver
//            vto2.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                }
//            })
//            return view.height
//        }
//
//        /**
//         * 直接获取控件的宽、高
//         * @param view
//         * @return int[]
//         */
//        fun getViewWidth(view: View): Int {
//            val vto2 = view.viewTreeObserver
//            vto2.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                }
//            })
//            return view.width
//        }
//
//        val isMainThread: Unit
//            get() {
//                Log.e(
//                    TAG,
//                    "---是否在主线程：" + (Thread.currentThread() === Looper.getMainLooper().thread)
//                )
//            }
//
//        /**
//         * 判断list 是否为空
//         * @param list
//         * @param <T>
//         * @return
//        </T> */
//        fun <T> isListNotNull(list: List<T>?): Boolean {
//            return list != null && list.isNotEmpty()
//        }
//
//        /**
//         * 判断list 是否为空
//         * @param list
//         * @param <T>
//         * @return
//        </T> */
//        fun <T> isListNull(list: List<T>?): Boolean {
//            return !(list != null && list.isNotEmpty())
//        }
//
//        fun isEmpty(s: String?): Boolean {
//            return s == null || s.isEmpty() || s == "null"
//        }
//
//        fun isNoEmpty(s: String?): Boolean {
//            val bool = s == null || s.isEmpty() || s == "null"
//            return !bool
//        }
//
//
//        fun isJson(json: String?): Boolean {
//            try {
//                val jsonObject = JSONObject(json)
//            } catch (e: Exception) {
//                //Log.e(TAG,"该字符串不是json!\n"+json);
//                return false
//            }
//            return true
//        }
//
//        /**
//         * 设置某个View的margin
//         *
//         * @param view   需要设置的view
//         * @param isDp   需要设置的数值是否为DP
//         * @param left   左边距
//         * @param right  右边距
//         * @param top    上边距
//         * @param bottom 下边距
//         * @return
//         */
//        fun setViewMargin(
//            view: View?,
//            isDp: Boolean,
//            left: Int,
//            right: Int,
//            top: Int,
//            bottom: Int
//        ): ViewGroup.LayoutParams? {
//            if (view == null) {
//                return null
//            }
//            var leftPx = left
//            var rightPx = right
//            var topPx = top
//            var bottomPx = bottom
//            val params = view.layoutParams
//            var marginParams: MarginLayoutParams? = null
//            //获取view的margin设置参数
//            marginParams = if (params is MarginLayoutParams) {
//                params
//            } else {
//                //不存在时创建一个新的参数
//                MarginLayoutParams(params)
//            }
//
//            //根据DP与PX转换计算值
//            if (isDp) {
//                leftPx = dip2px(left.toFloat())
//                rightPx = dip2px(right.toFloat())
//                topPx = dip2px(top.toFloat())
//                bottomPx = dip2px(bottom.toFloat())
//            }
//            //设置margin
//            marginParams!!.setMargins(leftPx, topPx, rightPx, bottomPx)
//            view.layoutParams = marginParams
//            view.requestLayout()
//            return marginParams
//        }
//
//        /**
//         * 在代码中为TextView 设置color资源
//         *
//         * @param tv
//         * @param color 例如 R.color.oranges
//         */
//        fun setTextColor(tv: TextView, color: Int) {
//            val resource = tv.context.resources as Resources
//            //        ColorStateList csl = (ColorStateList) resource.getColorStateList(color);
////        if(csl!=null){
////        }
//            tv.setTextColor(resource.getColor(color))
//        }
//
//        /**editText设置文字后 光标设置为文字末尾 */
//        fun setTextSelectEnd(editText: EditText, str: String) {
//            if (editText != null) {
//                if (str != null) {
//                    editText.setText(str)
//                    editText.setSelection(editText.text.length)
//                }
//            }
//        }
//
//        /**
//         * 为textview 设值，避免空值情况
//         *
//         * @param tv
//         * @param str
//         */
//
//        fun setTextValues(tv: TextView?, str: String?) {
//            if (tv != null && !TextUtils.isEmpty(str)) {
//                tv.text = str
//            }
//        }
//
//
//        fun setTextValues(tv: TextView?, @StringRes id: Int) {
//            val str = tv!!.context.getString(id)
//            if (tv != null && !TextUtils.isEmpty(str)) {
//                tv.text = str
//            }
//        }
//
//
//        fun getStatusBarHeight(context: Context): Int {
//            var result = 0
//            val resourceId = context.resources.getIdentifier(
//                "status_bar_height", "dimen", "android"
//            )
//            if (resourceId > 0) {
//                result = context.resources.getDimensionPixelSize(resourceId)
//            }
//            if (result == 0) {
//                val scale = context.resources.displayMetrics.density
//                result = (26 * scale + 0.5f).toInt()
//            }
//            return result
//        }
//
//        /**
//         * 优化确认取消弹窗方法 2016-3-1 yzh update
//         *
//         * @param msg           弹框信息
//         * @param leftName      左边按钮名称
//         * @param rightName     右边按钮名称 (传null表示只显示一个按钮)
//         * @param leftlistener  左边按钮监听 (无需监听事件可传null)
//         * @param rightlistener 右边按钮监听 (无需监听事件可传null)
//         * @param color         设置按钮文字颜色  #FFFFFF (可传null取默认)
//         * @return
//         */
//
//        fun showDialog(
//            context: Context,
//            title: String?,
//            msg: String,
//            leftName: String?,
//            rightName: String?,
//            leftlistener: Listener?,
//            rightlistener: Listener?,
//            vararg color: String?
//        ): Dialog {
//            val showDialog = Dialog(context)
//            showDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
//            showDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
//            showDialog.window!!.setGravity(Gravity.CENTER)
//            showDialog.setContentView(R.layout.comm_show_dialogs)
//            showDialog.setCancelable(false)
//            if (!(context as Activity).isFinishing) {
//                showDialog.show()
//            }
//            val lyrame: LinearLayout
//            val btSure: Button
//            val btCancel: Button
//            val tvContent: TextView
//            lyrame = showDialog.findViewById(R.id.lyShow_Frame)
//            btSure = showDialog.findViewById(R.id.btShow_sure)
//            btCancel = showDialog.findViewById(R.id.btShow_cancle)
//            tvContent = showDialog.findViewById(R.id.tvShow_content)
//            lyrame.layoutParams.width = DensityUtil.getScreenWidth() - dip2px(100f)
//            tvContent.text = msg
//            if (msg.length >= 20) {
//                tvContent.gravity = Gravity.LEFT
//            } else {
//                tvContent.gravity = Gravity.CENTER
//            }
//            if (color != null && color.size > 0) {
//                btSure.setTextColor(Color.parseColor(color[0]))
//                if (color.size > 1) {
//                    btCancel.setTextColor(Color.parseColor(color[1]))
//                }
//            } else {
//                //默认颜色
//                setTextColor(btSure, R.color.ui_blue)
//                setTextColor(btCancel, R.color.ui_blue)
//            }
//            if (StringUtil.isNoEmpty(title)) {
//                val tvTitle = showDialog.findViewById<TextView>(R.id.tvTitle)
//                tvTitle.text = title
//            }
//            if (StringUtil.isEmpty(leftName)) {
//                btSure.visibility = View.GONE
//                showDialog.findViewById<View>(R.id.spit).visibility = View.GONE
//            } else {
//                btSure.text = leftName
//                btSure.setOnClickListener { v: View? ->
//                    showDialog.dismiss()
//                    leftlistener?.onResult()
//                }
//            }
//            if (StringUtil.isEmpty(rightName)) {
//                btCancel.visibility = View.GONE
//                showDialog.findViewById<View>(R.id.spit).visibility = View.GONE
//            } else {
//                btCancel.text = rightName
//                btCancel.setOnClickListener { v: View? ->
//                    showDialog.dismiss()
//                    rightlistener?.onResult()
//                }
//            }
//            if (StringUtils.isEmpty(rightName) || StringUtils.isEmpty(leftName)) {
//                showDialog.findViewById<View>(R.id.spit).visibility = View.GONE
//            }
//            return showDialog
//        }
//
//        /**
//         * 点击按钮后需要手动关闭的弹窗
//         *
//         * @param msg           弹框信息
//         * @param leftName      左边按钮名称
//         * @param rightName     右边按钮名称 (传null表示只显示一个按钮)
//         * @param leftlistener  左边按钮监听 (无需监听事件可传null)
//         * @param rightlistener 右边按钮监听 (无需监听事件可传null)
//         * @param color         设置按钮文字颜色  #FFFFFF (可传null取默认)
//         * @return
//         */
//        fun showDialogs(
//            context: Context,
//            title: String?,
//            msg: String,
//            leftName: String?,
//            rightName: String?,
//            leftlistener: Listener?,
//            rightlistener: Listener?,
//            vararg color: String?
//        ): TextView {
//            val showDialog = Dialog(context)
//            showDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
//            showDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
//            showDialog.window!!.setGravity(Gravity.CENTER)
//            showDialog.setContentView(R.layout.comm_show_dialogs)
//            showDialog.setCancelable(false)
//            if (!(context as Activity).isFinishing) {
//                showDialog.show()
//            }
//            val lyrame: LinearLayout
//            val btSure: Button
//            val btCancel: Button
//            val tvContent: TextView
//            lyrame = showDialog.findViewById(R.id.lyShow_Frame)
//            btSure = showDialog.findViewById(R.id.btShow_sure)
//            btCancel = showDialog.findViewById(R.id.btShow_cancle)
//            tvContent = showDialog.findViewById(R.id.tvShow_content)
//            lyrame.layoutParams.width = DensityUtil.getScreenWidth() - dip2px(100f)
//            tvContent.text = msg
//            if (msg.length >= 20) {
//                tvContent.gravity = Gravity.LEFT
//            } else {
//                tvContent.gravity = Gravity.CENTER
//            }
//            if (color.isNotEmpty()) {
//                btSure.setTextColor(Color.parseColor(color[0]))
//                if (color.size > 1) {
//                    btCancel.setTextColor(Color.parseColor(color[1]))
//                }
//            } else {
//                //默认颜色
//                setTextColor(btSure, R.color.ui_blue)
//                setTextColor(btCancel, R.color.ui_blue)
//            }
//            if (StringUtil.isNoEmpty(title)) {
//                val tvTitle = showDialog.findViewById<TextView>(R.id.tvTitle)
//                tvTitle.text = title
//            }
//            if (StringUtil.isEmpty(leftName)) {
//                btSure.visibility = View.GONE
//                showDialog.findViewById<View>(R.id.spit).visibility = View.GONE
//            } else {
//                btSure.text = leftName
//                btSure.setOnClickListener { v: View? ->
//                    // showDialog.dismiss();
//                    leftlistener?.onResult()
//                }
//            }
//            if (StringUtil.isEmpty(rightName)) {
//                btCancel.visibility = View.GONE
//                showDialog.findViewById<View>(R.id.spit).visibility = View.GONE
//            } else {
//                btCancel.text = rightName
//                btCancel.setOnClickListener { v: View? ->
//                    //showDialog.dismiss();
//                    rightlistener?.onResult()
//                }
//            }
//            if (StringUtils.isEmpty(rightName) || StringUtils.isEmpty(leftName)) {
//                showDialog.findViewById<View>(R.id.spit).visibility = View.GONE
//            }
//            return tvContent
//        }
//
//        /**
//         * 调往系统APK安装界面（适配7.0）
//         *
//         * @return
//         */
//        fun getInstallAppIntent(filePath: String?): Intent? {
//            //apk文件的本地路径
//            val apkfile = File(filePath)
//            if (!apkfile.exists()) {
//                return null
//            }
//            val intent = Intent(Intent.ACTION_VIEW)
//            val contentUri = getUriForFile(apkfile)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            }
//            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
//            return intent
//        }
//
//        /**
//         * 将文件转换成uri
//         *
//         * @return
//         */
//
//        fun getUriForFile(file: File?): Uri? {
//            var fileUri: Uri? = null
//            fileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                FileProvider.getUriForFile(
//                    instance,
//                    instance.packageName + ".fileprovider",
//                    file!!
//                )
//            } else {
//                Uri.fromFile(file)
//            }
//            return fileUri
//        }
//
//        /**
//         * 根据资源名称获取 资源id
//         * @param name
//         * @param type 资源类型  drawable、 raw
//         * @return
//         */
//        fun getResId(name: String?, type: String?): Int {
//            val r = instance.resources
//            var id = 0
//            try {
//                id = r.getIdentifier(name, type, instance.packageName)
//                //踩坑提示  如果是在插件化环境里 context.getPackageName() 获取的包名会变成宿主的包名，建议写死包名或者反射获取
//                ALog.v("BaseApplication.getInstance().getPackageName()==" + instance.packageName)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            return id
//        }
//
//        /**
//         * 正则表达式判断字符串是数字，可以为正数，可以为负数，可含有小数点，不能含有字符。
//         */
//
//        fun isNumeric(values: String?): Boolean {
//            if (isEmpty(values)) {
//                return false
//            }
//            //Pattern pattern = Pattern.compile("-?[0-9]*.?[0-9]*");
//            val pattern = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$")
//            val isNum = pattern.matcher(values)
//            return isNum.matches()
//        }
//
//        /**
//         * 根据资源名称获取 资源id （通过反射）
//         * @param imageName
//         * @param mipmap   资源类型  例如  R.mipmap.class
//         * @return
//         */
//        fun getResId(imageName: String?, mipmap: Class<*>): Int {
//            //Class mipmap = R.raw.class;
//            //Class mipmap = R.mipmap.class;
//            return try {
//                val field = mipmap.getField(imageName!!)
//                field.getInt(imageName)
//            } catch (e: NoSuchFieldException) { //如果没有在"mipmap"下找到imageName,将会返回0
//                0
//            } catch (e: IllegalAccessException) {
//                0
//            }
//        }
//
//        /**
//         * 从arrays.xml中读取数组
//         * @param resId
//         * @return
//         */
//
//        fun getArrays(@ArrayRes resId: Int): IntArray {
//            val array = instance.resources.obtainTypedArray(resId)
//            val len = array.length()
//            val intArray = IntArray(array.length())
//            for (i in 0 until len) {
//                intArray[i] = array.getResourceId(i, 0)
//            }
//            array.recycle()
//            return intArray
//        }
//
//
//
//        /**
//         * 正则表达式匹配两个指定字符串中间的内容 biru
//         * @param str
//         * @param rgex 例如  ： "abc(.*?)abc"  "(?<=\\{)(.+?)(?=\\})"
//         * @return
//         */
//        fun getSubUtil(str: String?, rgex: String?): List<String> {
//            val list: MutableList<String> = ArrayList()
//            val pattern = Pattern.compile(rgex) // 匹配的模式
//            val m = pattern.matcher(str)
//            while (m.find()) {
//                val i = 1
//                list.add(m.group(i))
//                println(m.group(i))
//                // i++;
//            }
//            return list
//        }
//
//        /**
//         * 正则表达式匹配两个指定字符串中间的内容 并去掉 可能包含的 "-" 符号 并且是数字
//         * @param str
//         * @param rgex 例如  ： "abc(.*?)abc"  "(?<=\\{)(.+?)(?=\\})"
//         * @return
//         */
//        fun getSubUtil1(str: String?, rgex: String?): List<String> {
//            val list: MutableList<String> = ArrayList()
//            val pattern = Pattern.compile(rgex) // 匹配的模式
//            val m = pattern.matcher(str)
//            while (m.find()) {
//                val i = 1
//                var s = m.group(i)
//                if (isNoEmpty(s)) {
//                    s = s.replace("-", "")
//                    if (isNumeric(s)) {
//                        list.add(s)
//                    }
//                }
//                println(m.group(i))
//                // i++;
//            }
//            return list
//        }
//
//        /**
//         * 正则表达式匹配两个指定字符串中间的内容 并且是中文数字
//         * @param str
//         * @param rgex 例如  ： "abc(.*?)abc"  "(?<=\\{)(.+?)(?=\\})"
//         * @return
//         */
//        fun getSubUtil2(str: String?, rgex: String?): List<String> {
//            val list: MutableList<String> = ArrayList()
//            val pattern = Pattern.compile(rgex) // 匹配的模式
//            val m = pattern.matcher(str)
//            while (m.find()) {
//                val i = 1
//                var s = m.group(i)
//                if (isNoEmpty(s)) {
//                    s = s.replace("-", "")
//                    if (isChineseNumber(s)) {
//                        list.add(s)
//                    }
//                }
//                println(m.group(i))
//                // i++;
//            }
//            return list
//        }
//
//        /**
//         * 正则表达式匹配两个指定字符串中间的内容 biru
//         * @param str
//         * @param rgex 例如  ： "abc(.*?)abc"  "(?<=\\{)(.+?)(?=\\})"
//         * @return
//         */
//        fun getSubUtil3(str: String?, rgex: String?, left: String, right: String): List<String> {
//            val list: MutableList<String> = ArrayList()
//            val pattern = Pattern.compile(rgex) // 匹配的模式
//            val m = pattern.matcher(str)
//            while (m.find()) {
//                val i = 1
//                list.add(left + m.group(i) + right) //在追加上 匹配的前后缀
//                println(m.group(i))
//                // i++;
//            }
//            return list
//        }
//
//        /**
//         * pan
//         * @return
//         */
//        fun isChineseNumber(s: String): Boolean {
//            val regex = "^[零一二三四五六七八九十百千万亿壹贰叁肆伍陆柒捌玖拾佰仟萬億]+$"
//            val p = Pattern.compile(regex)
//            val m = p.matcher(s)
//            if (m.matches()) {
//                println("$s 是汉字的数字")
//            }
//            return m.matches()
//        }
//
//        /**
//         * 将秒 格式化显示（例：4000秒 =  1:06:40）1小时6分钟40秒
//         * @return
//         */
//
//        fun showTimes(miss: Long): String {
//            val hh = if (miss / 3600 > 9) (miss / 3600).toString()  else "0${miss / 3600}"
//            val mm =
//                if (miss % 3600 / 60 > 9) (miss % 3600 / 60).toString()  else "0${miss % 3600 / 60}"
//            val ss =
//                if (miss % 3600 % 60 > 9) (miss % 3600 % 60).toString() else "0${miss % 3600 % 60}"
//            return hh + "小时" + mm + "分钟" + ss + "秒"
//        }
//
//        /**
//         * 将秒 格式化显示（例：4000秒 =  01:06:40）
//         * @return
//         */
//        // 将秒转化成小时分钟秒
//        fun showTime(miss: Long): String? {
//            val hh = if (miss / 3600 > 9) (miss / 3600).toString()  else "0${miss / 3600}"
//            val mm =
//                if (miss % 3600 / 60 > 9) (miss % 3600 / 60).toString()  else "0${miss % 3600 / 60}"
//            val ss =
//                if (miss % 3600 % 60 > 9) (miss % 3600 % 60).toString() else "0${miss % 3600 % 60}"
//            return if ("00" != hh) {
//                "$hh:$mm:$ss"
//            } else {
//                "$mm:$ss"
//            }
//        }
//
//    }
//
//
//
//}