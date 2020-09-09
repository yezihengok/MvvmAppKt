package com.example.commlib.utils

import android.content.Context
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import java.util.*
import java.util.regex.Pattern

object StringUtil {
    //private static Context context;
    /**
     * 验证
     * @param s
     * @return
     */
	@JvmStatic
	fun isEmpty(s: String?): Boolean {
        return s == null || s.length == 0 || s == "null"
    }

    @JvmStatic
	fun isNoEmpty(s: String?): Boolean {
        val bool = s == null || s.length == 0 || s == "null"
        return !bool
    }

    /**
     * 验证邮箱的合法性
     * ^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$
     * @param email
     * @return
     */
    fun isValidEmail(email: String?): Boolean {
        if (isEmpty(email)) return false
        val pattern_ = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$"
        val pattern = Pattern.compile(pattern_)
        val mat = pattern.matcher(email)
        return mat.matches()
    }

    /**
     * 判断日期格式是否正确
     *
     * @param sDate
     * @return
     */
    fun isValidDate(sDate: String?): Boolean {
        val datePattern1 = "\\d{4}-\\d{2}-\\d{2}"
        val datePattern2 = ("^((\\d{2}(([02468][048])|([13579][26]))"
                + "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|"
                + "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?"
                + "((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?("
                + "(((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?"
                + "((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))")
        if (sDate != null) {
            var pattern = Pattern.compile(datePattern1)
            var match = pattern.matcher(sDate)
            return if (match.matches()) {
                pattern = Pattern.compile(datePattern2)
                match = pattern.matcher(sDate)
                match.matches()
            } else {
                false
            }
        }
        return false
    }

    /**
     * 正则表达式判断字符串是数字，可以为正数，可以为负数，可含有小数点，不能含有字符。
     */
	@JvmStatic
	fun isNumeric(values: String?): Boolean {
        if (isEmpty(values)) {
            return false
        }
        val pattern = Pattern.compile("-?[0-9]*.?[0-9]*")
        val isNum = pattern.matcher(values)
        return if (!isNum.matches()) {
            false
        } else true
    }

    /**
     * 检查指定的字符串列表是否不为空。
     */
    fun areNotEmpty(vararg values: String?): Boolean {
        var result = true
        if (values == null || values.size == 0) {
            result = false
        } else {
            for (value in values) {
                result = result and !isEmpty(value)
            }
        }
        return result
    }

    /**
     * 把通用字符编码的字符串转化为汉字编码。
     */
    fun unicodeToChinese(unicode: String): String {
        val out = StringBuilder()
        if (!isEmpty(unicode)) {
            for (i in 0 until unicode.length) {
                out.append(unicode[i])
            }
        }
        return out.toString()
    }

    /**
     * 验证姓名是中文
     */
    fun isValidUserName(name: String): Boolean {
        var name = name
        return if (isEmpty(name)) {
            false
        } else {

            // ^[_a-zA-Z0-9+\.\u4e00-\u9fa5]{2,6}$
            name = String(name.toByteArray()) // 用GBK编码
            // final String pattern_ = "^[_a-zA-Z0-9+\\.\u4e00-\u9fa5]{2,6}$";
            val pattern_ = "^[\u4e00-\u9fa5]{2,6}$"
            val pattern = Pattern.compile(pattern_)
            val mat = pattern.matcher(name)
            mat.matches()
        }
    }

    /**
     * 验证密码为字母、数字、下划线两者及以上8-20个字符
     * ^(?![0-9]+$)(?![a-zA-Z]+$)(?![_]+$)\\w{8,20}$
     * (?!^(\\d+|[a-zA-Z]+|[_]+)$)^[\\w]{8,20}$
     */
    fun checkPassword(password: String?): Boolean {
        var tag = false
        tag = if (isEmpty(password)) {
            false
        } else {
            val pattern_ = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![_]+$)\\w{8,20}$"
            val pattern = Pattern.compile(pattern_)
            val mat = pattern.matcher(password)
            return mat.matches()
        }
        return tag
    }

    /**
     * 验证密码为英文加数字
     */
    fun checkPasswordVar(password: String?): Boolean {
        var tag = false
        tag = if (isEmpty(password)) {
            false
        } else {
            val pattern_ = "^[A-Za-z0-9]{6,16}$"
            val pattern = Pattern.compile(pattern_)
            val mat = pattern.matcher(password)
            return mat.matches()
        }
        return tag
    }

    fun isValidMobile(mobile: String?): Boolean {
        if (isEmpty(mobile)) return false
        //TODO:oyj 2015-4-23 手机验证放开13-19开头
        val pattern = Pattern.compile("^(1[3-9])\\d{9}$")
        //		Pattern pattern = Pattern.compile("^((13[0-9])|(14[7])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        val matcher = pattern.matcher(mobile)
        return matcher.matches()
    }

    fun isValidQQ(qq: String?): Boolean {
        if (isEmpty(qq)) return false
        // Pattern pattern=Pattern.compile("^[13,14,15,18,19]\\d{9}$");
        val pattern = Pattern.compile("^[1-9]\\d{3,}$")
        val matcher = pattern.matcher(qq)
        return matcher.matches()
    }

    fun isValidAmount(amount: String?): Boolean {
        if (isEmpty(amount)) return false
        val pattern = Pattern.compile("^((\\d{1,})|([0-9]+\\.[0-9]{1,2}))$")
        val matcher = pattern.matcher(amount)
        return matcher.matches()
    }

    fun isValidNumber(url: String?): Boolean {
        if (isEmpty(url)) return false
        val pattern = Pattern.compile("^\\d{1,10}$")
        val matcher = pattern.matcher(url)
        return matcher.matches()
    }

    /**
     * 文本框中过滤特殊字符
     * [~@#$%^&*_+<>@#¥%]
     * < > % ' " $ = (后台限制的字符)
     */
    fun StringFilter(content: String?): String {
        return if (isEmpty(content)) {
            ""
        } else {
            val pattern_ = "[<>%'\"$=&]"
            val pattern = Pattern.compile(pattern_)
            val mat = pattern.matcher(content)
            mat.replaceAll("*").trim { it <= ' ' }
        }
    }

    /**
     * 文本框中过滤特殊字符
     * [~@#$%^&*_+<>@#¥%]
     * < > % ' " $ = (后台限制的字符)
     */
    fun StringFilters(content: String?): Boolean {
        val regEx = "[`~!@#$%^&*()+=|{}':;,\\[\\]<>/?！￥…（）—【】‘；：”“’。，、？]"
        val p = Pattern.compile(regEx)
        val m = p.matcher(content)
        return if (m.find()) true else false
    }

    /**
     * 文本框中过滤特殊字符用空取代
     * [~@#$%^&*_+<>@#¥%]
     * < > % ' " $ = (后台限制的字符)
     * @param content
     * @return
     */
    fun StringFilter2(content: String): String {
        return if (isEmpty(content)) {
            ""
        } else {
            val speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
            val pattern = Pattern.compile(speChat)
            val matcher = pattern.matcher(content)
            if (matcher.find()) {
                //JLog.e("hasLawlessStr=ggggggg ");
                ""
            } else content
        }
    }

    /**
     * 验证文本框中是否包含特殊字符
     * [~@#$%^&*_+<>@#¥%]
     * < > % ' " $ = (后台限制的字符)
     */
    fun hasLawlessStr(content: String?): Boolean {
        return if (isEmpty(content)) {
            false
        } else {
            val pattern_ = "[<>%'\"$=&]"
            val pattern = Pattern.compile(pattern_)
            val mat = pattern.matcher(content)
            mat.find()
        }
    }

    /**
     * 验证是否 不包含指定的字符 < > / \ %
     */
    fun isContains(name: String?): Boolean {
        val bo: Boolean
        // ^[_a-zA-Z0-9+\.\u4e00-\u9fa5]{2,6}$
        // name = new String(name.getBytes());// 用GBK编码
        // final String pattern_ = "^[_a-zA-Z0-9+\\.\u4e00-\u9fa5]{2,6}$";
        val pattern_ = "^(?:(?!(<|>|/|\\\\|%)).)*$"
        val pattern = Pattern.compile(pattern_)
        val mat = pattern.matcher(name)
        bo = mat.matches()
        return bo
    }

    /**
     * 验证是否境外手机
     * (^\+?\d{1,3}?(\(\d{2,5})\))(\d{3,19})(-(\d{4,8}))?$
     */
    @Deprecated("") //并不准建议弃用
    fun isValidMobileForeign(content: String?): Boolean {
        return if (isEmpty(content)) {
            false
        } else {
            val pattern_ = "(^\\+?\\d{1,3}?(\\(\\d{2,5})\\))(\\d{3,19})(-(\\d{4,8}))?$"
            val pattern = Pattern.compile(pattern_)
            val mat = pattern.matcher(content)
            mat.matches()
        }
    }

    /**
     * 验证港澳台身份证
     */
    fun checkForeignIdCard(idCard: String?): Boolean {
        var tag = false
        tag = if (isEmpty(idCard)) {
            false
        } else {
            val pattern_ = "^[A-Za-z0-9()]{6,25}$"
            val pattern = Pattern.compile(pattern_)
            val mat = pattern.matcher(idCard)
            return mat.matches()
        }
        return tag
    }

    /**
     * 验证密身份证
     */
    fun checkIdCard(idCard: String?): Boolean {
        var tag = false
        tag = if (isEmpty(idCard)) {
            false
        } else {
            val pattern_ = "^[0-9xX]{15,18}$"
            val pattern = Pattern.compile(pattern_)
            val mat = pattern.matcher(idCard)
            return mat.matches()
        }
        return tag
    }

    /**
     * 领取红包卡号校验
     */
    fun checkReceiveCard(card: String?): Boolean {
        var tag = false
        tag = if (isEmpty(card)) {
            false
        } else {
            val pattern_ = "^[0-9]{9,10}$"
            val pattern = Pattern.compile(pattern_)
            val mat = pattern.matcher(card)
            return mat.matches()
        }
        return tag
    }

    /**
     * 将String转换为Long
     *
     * @param str
     * @return
     */
    fun parserLong(str: String?): Long {
        var str = str
        if (str == null || str.trim { it <= ' ' }.also { str = it }.isEmpty()) {
            return 0
        }
        try {
            return str!!.toLong()
        } catch (e: Exception) {
        }
        return 0
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @return
     */
    fun subZeroAndDot(input: String): String {
        var input = input
        if (input.indexOf(".") > 0) {
            input = input.replace("0+?$".toRegex(), "") // 去掉多余的0
            input = input.replace("[.]$".toRegex(), "") // 如最后一位是.则去掉
        }
        return input
    }

    /**
     * 11位电话号码3 4 4显示
     * @param editText
     */
    fun showPhoneNo(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var inputStr = ""
                var newStr = s.toString()
                newStr = newStr.replace(" ", "")
                var index = 0
                if (index + 3 < newStr.length) {
                    inputStr += newStr.substring(index, index + 3) + " "
                    index += 3
                }
                while (index + 4 < newStr.length) {
                    inputStr += newStr.substring(index, index + 4) + " "
                    index += 4
                }
                inputStr += newStr.substring(index, newStr.length)
                editText.setText(inputStr)
                editText.setSelection(inputStr.length)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    /**
     * 手机号 3 4 4 ..显示
     * @param TextView
     * @param newStr
     */
    fun showPhoneNo(TextView: TextView, newStr: String) {
        var newStr = newStr
        if (isNoEmpty(newStr)) {
            var inputStr = ""
            newStr = newStr.replace(" ", "")
            var index = 0
            if (index + 3 < newStr.length) {
                inputStr += newStr.substring(index, index + 3) + " "
                index += 3
            }
            while (index + 4 < newStr.length) {
                inputStr += newStr.substring(index, index + 4) + " "
                index += 4
            }
            inputStr += newStr.substring(index, newStr.length)
            TextView.text = inputStr
        }
    }

    /**
     *
     */
    fun showCardNo(editText: TextView, newStr: String) {
        var newStr = newStr
        var inputStr = ""
        newStr = newStr.replace(" ", "")
        var index = 0
        //        if((index + 3)< newStr.length()){
//            inputStr += (newStr.substring(index, index + 3)+ " ");
//            index += 3;
//        }
        while (index + 4 < newStr.length) {
            inputStr += newStr.substring(index, index + 4) + " "
            index += 4
        }
        inputStr += newStr.substring(index, newStr.length)
        editText.text = inputStr
    }

    /**
     * 过滤+86(邮箱除外)，中间空格
     * @param idNumber
     * @return
     */
    fun filterIdNum(idNumber: String): String {
        var idNumber = idNumber
        if (idNumber.contains("+86") && !idNumber.contains("@")) {
            idNumber = idNumber.replace("+86", "")
        }
        if (idNumber.contains("(") && !idNumber.contains("@")) {
            idNumber = idNumber.replace("(", "")
        }
        if (idNumber.contains(")") && !idNumber.contains("@")) {
            idNumber = idNumber.replace(")", "")
        }
        if (idNumber.contains(" ")) {
            idNumber = idNumber.replace(" ", "")
        }
        return idNumber
    }

    /**
     * 字符串打码显示
     * @param str
     * @param starNum 打码*个数 4个为手机号打码，10个为身份证或者银行卡号
     * @return
     */
    fun hideString(str: String, starNum: Int): String {
        var str = str
        if (isNoEmpty(str) && str.length > 4) {
            val tempStr = str.substring(3, str.length - 4)
            if (starNum == 4) {
                str = str.replace(tempStr, "****")
            } else if (starNum == 10) {
                str = str.replace(tempStr, "**********")
            }
        }
        return str
    }

    /**
     * 判断list 是否为空
     * @param list
     * @param <T>
     * @return
    </T> */
    fun <T> isListNotNull(list: List<T>?): Boolean {
        return list != null && list.isNotEmpty()
    }

    /**
     * 判断list 是否为空
     * @param list
     * @param <T>
     * @return
    </T> */
    fun <T> isListNull(list: List<T>?): Boolean {
        return !(list != null && list.isNotEmpty())
    }

    /**
     * Android 手机唯一标识
     * @param context
     * @return
     */
    fun getPhoneSign(context: Context): String {
        val deviceId = StringBuilder()
        // 渠道标志
//        deviceId.append("a");
        try {
            //IMEI（imei）
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val imei = tm.deviceId
            if (!TextUtils.isEmpty(imei)) {
//                deviceId.append("imei");
                deviceId.append(imei)
                return deviceId.toString()
            }
            //序列号（sn）
            val sn = tm.simSerialNumber
            if (!TextUtils.isEmpty(sn)) {
//                deviceId.append("sn");
                deviceId.append(sn)
                return deviceId.toString()
            }
            //如果上面都没有， 则生成一个id：随机码
            val uuid = getUUID(context)
            if (!TextUtils.isEmpty(uuid)) {
//                deviceId.append("id");
                deviceId.append(uuid)
                return deviceId.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            //            deviceId.append("id").append(getUUID());
            deviceId.append(getUUID(context))
        }
        return deviceId.toString()
    }

    /**
     * UUID
     */
    private var uuid: String? = null
    fun getUUID(context: Context): String? {
        val mShare = context.getSharedPreferences("uuid", Context.MODE_PRIVATE)
        if (mShare != null) {
            uuid = mShare.getString("uuid", "")
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString()
            mShare!!.edit().putString("uuid", uuid).commit()
        }
        return uuid
    }

    /**
     * 冒号分割处理
     * @param str 待处理字符串
     * @return 冒号分割后的字符串
     */
	@JvmStatic
	fun colonSplit(str: String): String {
        return if (!isEmpty(str)) {
            str.replace("(?<=[0-9A-F]{2})[0-9A-F]{2}".toRegex(), ":$0")
        } else str
    }
}