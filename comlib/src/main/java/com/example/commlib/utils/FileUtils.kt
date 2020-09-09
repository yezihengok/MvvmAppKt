package com.example.commlib.utils

import com.blankj.ALog
import com.example.commlib.utils.encrypt.MD5Utils
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.util.*

/**
 * detail: 文件操作工具类
 * @author Ttt
 */
object FileUtils {
    // 日志 TAG
    private val TAG = FileUtils::class.java.simpleName

    // 换行字符串
    private val NEW_LINE_STR = System.getProperty("line.separator")

    /**
     * 获取文件
     * @param filePath 文件路径
     * @return 文件 [File]
     */
    fun getFile(filePath: String?): File? {
        return getFileByPath(filePath)
    }

    /**
     * 获取文件
     * @param filePath 文件路径
     * @param fileName 文件名
     * @return 文件 [File]
     */
    fun getFile(filePath: String?, fileName: String?): File? {
        return if (filePath != null && fileName != null) File(filePath, fileName) else null
    }

    /**
     * 获取文件
     * @param filePath 文件路径
     * @return 文件 [File]
     */
    @JvmStatic
    fun getFileByPath(filePath: String?): File? {
        return if (filePath != null) File(filePath) else null
    }

    /**
     * 获取路径, 并且进行创建目录
     * @param filePath 保存目录
     * @param fileName 文件名
     * @return 文件 [File]
     */
    fun getFileCreateFolder(filePath: String?, fileName: String?): File? {
        // 防止不存在目录文件, 自动创建
        createFolder(filePath)
        // 返回处理过后的 File
        return getFile(filePath, fileName)
    }

    /**
     * 获取路径, 并且进行创建目录
     * @param filePath 保存目录
     * @param fileName 文件名
     * @return 文件 [File]
     */
    fun getFilePathCreateFolder(filePath: String?, fileName: String?): String? {
        // 防止不存在目录文件, 自动创建
        createFolder(filePath)
        // 返回处理过后的 File
        val file = getFile(filePath, fileName)
        // 返回文件路径
        return getAbsolutePath(file)
    }

    /**
     * 判断某个文件夹是否创建, 未创建则创建 ( 纯路径 - 无文件名 )
     * @param dirPath 文件夹路径 ( 无文件名字. 后缀 )
     * @return `true` success, `false` fail
     */
    fun createFolder(dirPath: String?): Boolean {
        return createFolder(getFileByPath(dirPath))
    }

    /**
     * 判断某个文件夹是否创建, 未创建则创建 ( 纯路径 - 无文件名 )
     * @param file 文件夹路径 ( 无文件名字. 后缀 )
     * @return `true` success, `false` fail
     */
    fun createFolder(file: File?): Boolean {
        if (file != null) {
            try {
                // 当这个文件夹不存在的时候则创建文件夹
                return if (!file.exists()) {
                    // 允许创建多级目录
                    file.mkdirs()
                } else true
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "createFolder")
            }
        }
        return false
    }

    /**
     * 创建文件夹目录 - 可以传入文件名
     * @param filePath 文件路径 + 文件名
     * @return `true` success, `false` fail
     */
    fun createFolderByPath(filePath: String?): Boolean {
        return createFolderByPath(getFileByPath(filePath))
    }

    /**
     * 创建文件夹目录 - 可以传入文件名
     * @param file 文件
     * @return `true` success, `false` fail
     */
    fun createFolderByPath(file: File?): Boolean {
        // 创建文件夹 - 如果失败才创建
        if (file != null) {
            if (file.exists()) {
                return true
            } else if (!file.parentFile.mkdirs()) {
                return createFolder(file.parent)
            }
        }
        return false
    }

    /**
     * 创建多个文件夹, 如果不存在则创建
     * @param filePaths 文件路径数组
     * @return `true` success, `false` fail
     */
    fun createFolderByPaths(vararg filePaths: String?): Boolean {
        if (filePaths != null && filePaths.size != 0) {
            var i = 0
            val len = filePaths.size
            while (i < len) {
                createFolder(filePaths[i])
                i++
            }
            return true
        }
        return false
    }

    /**
     * 创建多个文件夹, 如果不存在则创建
     * @param files 文件数组
     * @return `true` success, `false` fail
     */
    fun createFolderByPaths(vararg files: File?): Boolean {
        if (files != null && files.size != 0) {
            var i = 0
            val len = files.size
            while (i < len) {
                createFolder(files[i])
                i++
            }
            return true
        }
        return false
    }
    // =
    /**
     * 判断目录是否存在, 不存在则判断是否创建成功
     * @param dirPath 目录路径
     * @return `true` 存在或创建成功, `false` 不存在或创建失败
     */
    fun createOrExistsDir(dirPath: String?): Boolean {
        return createOrExistsDir(getFileByPath(dirPath))
    }

    /**
     * 判断目录是否存在, 不存在则判断是否创建成功
     * @param file 文件
     * @return `true` 存在或创建成功, `false` 不存在或创建失败
     */
    fun createOrExistsDir(file: File?): Boolean {
        // 如果存在, 是目录则返回 true, 是文件则返回 false, 不存在则返回是否创建成功
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    /**
     * 判断文件是否存在, 不存在则判断是否创建成功
     * @param filePath 文件路径
     * @return `true` 存在或创建成功, `false` 不存在或创建失败
     */
    fun createOrExistsFile(filePath: String?): Boolean {
        return createOrExistsFile(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在, 不存在则判断是否创建成功
     * @param file 文件
     * @return `true` 存在或创建成功, `false` 不存在或创建失败
     */
    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        // 如果存在, 是文件则返回 true, 是目录则返回 false
        if (file.exists()) return file.isFile
        // 判断文件是否存在, 不存在则直接返回
        return if (!createOrExistsDir(file.parentFile)) false else try {
            // 存在, 则返回新的路径
            file.createNewFile()
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "createOrExistsFile")
            false
        }
    }

    /**
     * 判断文件是否存在, 存在则在创建之前删除
     * @param filePath 文件路径
     * @return `true` 创建成功, `false` 创建失败
     */
    fun createFileByDeleteOldFile(filePath: String?): Boolean {
        return createFileByDeleteOldFile(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在, 存在则在创建之前删除
     * @param file 文件
     * @return `true` 创建成功, `false` 创建失败
     */
    fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        // 文件存在并且删除失败返回 false
        if (file.exists() && !file.delete()) return false
        // 创建目录失败返回 false
        return if (!createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            ALog.eTag(TAG, e, "createFileByDeleteOldFile")
            false
        }
    }

    /**
     * 获取文件路径
     * @param file 文件
     * @return 文件路径
     */
    fun getPath(file: File?): String? {
        return file?.path
    }

    /**
     * 获取文件绝对路径
     * @param file 文件
     * @return 文件绝对路径
     */
    fun getAbsolutePath(file: File?): String? {
        return file?.absolutePath
    }
    // =
    /**
     * 获取文件名
     * @param file 文件
     * @return 文件名
     */
    fun getName(file: File?): String? {
        return file?.name
    }

    /**
     * 获取文件名
     * @param filePath 文件路径
     * @return 文件名
     */
    fun getName(filePath: String?): String {
        return getName(filePath, "")
    }

    /**
     * 获取文件名
     * @param filePath   文件路径
     * @param defaultStr 默认字符串
     * @return 文件名, 如果文件路径为 null 时, 返回默认字符串
     */
    fun getName(filePath: String?, defaultStr: String): String {
        return if (CommUtils.isEmpty(filePath)) defaultStr else File(filePath).name
    }

    /**
     * 获取文件后缀名 ( 无 "." 单独后缀 )
     * @param file 文件
     * @return 文件后缀名 ( 无 "." 单独后缀 )
     */
    fun getFileSuffix(file: File?): String? {
        return getFileSuffix(getAbsolutePath(file))
    }

    /**
     * 获取文件后缀名 ( 无 "." 单独后缀 )
     * @param filePath 文件路径或文件名
     * @return 文件后缀名 ( 无 "." 单独后缀 )
     */
    fun getFileSuffix(filePath: String?): String? {
        // 获取最后的索引
        var lastIndexOf=0
        // 判断是否存在
        if (filePath != null && filePath.lastIndexOf('.').also { lastIndexOf = it } != -1) {
            val result = filePath.substring(lastIndexOf)
            return if (result.startsWith(".")) {
                result.substring(1)
            } else result
        }
        return null
    }

    /**
     * 获取文件名 ( 无后缀 )
     * @param file 文件
     * @return 文件名 ( 无后缀 )
     */
    fun getFileNotSuffix(file: File?): String? {
        return getFileNotSuffix(getName(file))
    }

    /**
     * 获取文件名 ( 无后缀 )
     * @param filePath 文件路径
     * @return 文件名 ( 无后缀 )
     */
    fun getFileNotSuffixToPath(filePath: String?): String? {
        return getFileNotSuffix(getName(filePath))
    }

    /**
     * 获取文件名 ( 无后缀 )
     * @param fileName 文件名
     * @return 文件名 ( 无后缀 )
     */
    fun getFileNotSuffix(fileName: String?): String? {
        return if (fileName != null) {
            if (fileName.lastIndexOf('.') != -1) {
                fileName.substring(0, fileName.lastIndexOf('.'))
            } else {
                fileName
            }
        } else null
    }

    /**
     * 获取路径中的不带拓展名的文件名
     * @param file 文件
     * @return 不带拓展名的文件名
     */
    fun getFileNameNoExtension(file: File?): String? {
        return if (file == null) null else getFileNameNoExtension(file.path)
    }

    /**
     * 获取路径中的不带拓展名的文件名
     * @param filePath 文件路径
     * @return 不带拓展名的文件名
     */
    fun getFileNameNoExtension(filePath: String): String {
        if (CommUtils.isEmpty(filePath)) return filePath
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        if (lastSep == -1) {
            return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
        }
        return if (lastPoi == -1 || lastSep > lastPoi) {
            filePath.substring(lastSep + 1)
        } else filePath.substring(lastSep + 1, lastPoi)
    }

    /**
     * 获取路径中的文件拓展名
     * @param file 文件
     * @return 文件拓展名
     */
    fun getFileExtension(file: File?): String? {
        return if (file == null) null else getFileExtension(file.path)
    }

    /**
     * 获取路径中的文件拓展名
     * @param filePath 文件路径
     * @return 文件拓展名
     */
    fun getFileExtension(filePath: String): String {
        if (CommUtils.isEmpty(filePath)) return filePath
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
    }
    // =
    /**
     * 检查是否存在某个文件
     * @param file 文件
     * @return `true` yes, `false` no
     */
    fun isFileExists(file: File?): Boolean {
        return file != null && file.exists()
    }

    /**
     * 检查是否存在某个文件
     * @param filePath 文件路径
     * @return `true` yes, `false` no
     */
    fun isFileExists(filePath: String?): Boolean {
        return isFileExists(getFileByPath(filePath))
    }

    /**
     * 检查是否存在某个文件
     * @param filePath 文件路径
     * @param fileName 文件名
     * @return `true` yes, `false` no
     */
    fun isFileExists(filePath: String?, fileName: String?): Boolean {
        return filePath != null && fileName != null && File(filePath, fileName).exists()
    }

    /**
     * 判断是否文件
     * @param filePath 文件路径
     * @return `true` yes, `false` no
     */
    fun isFile(filePath: String?): Boolean {
        return isFile(getFileByPath(filePath))
    }

    /**
     * 判断是否文件
     * @param file 文件
     * @return `true` yes, `false` no
     */
    fun isFile(file: File?): Boolean {
        return file != null && file.exists() && file.isFile
    }

    /**
     * 判断是否文件夹
     * @param filePath 文件路径
     * @return `true` yes, `false` no
     */
    fun isDirectory(filePath: String?): Boolean {
        return isDirectory(getFileByPath(filePath))
    }

    /**
     * 判断是否文件夹
     * @param file 文件
     * @return `true` yes, `false` no
     */
    fun isDirectory(file: File?): Boolean {
        return file != null && file.exists() && file.isDirectory
    }

    /**
     * 判断是否隐藏文件
     * @param filePath 文件路径
     * @return `true` yes, `false` no
     */
    fun isHidden(filePath: String?): Boolean {
        return isHidden(getFileByPath(filePath))
    }

    /**
     * 判断是否隐藏文件
     * @param file 文件
     * @return `true` yes, `false` no
     */
    fun isHidden(file: File?): Boolean {
        return file != null && file.exists() && file.isHidden
    }
    // =
    /**
     * 获取文件最后修改的毫秒时间戳
     * @param filePath 文件路径
     * @return 文件最后修改的毫秒时间戳
     */
    fun getFileLastModified(filePath: String?): Long {
        return getFileLastModified(getFileByPath(filePath))
    }

    /**
     * 获取文件最后修改的毫秒时间戳
     * @param file 文件
     * @return 文件最后修改的毫秒时间戳
     */
    fun getFileLastModified(file: File?): Long {
        return file?.lastModified() ?: 0L
    }

    /**
     * 获取文件编码格式
     * @param filePath 文件路径
     * @return 文件编码格式
     */
    fun getFileCharsetSimple(filePath: String?): String? {
        return getFileCharsetSimple(getFileByPath(filePath))
    }

    /**
     * 获取文件编码格式
     * @param file 文件
     * @return 文件编码格式
     */
    fun getFileCharsetSimple(file: File?): String? {
        if (!isFileExists(file)) return null
        var pos = 0
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            pos = (`is`.read() shl 8) + `is`.read()
        } catch (e: IOException) {
            ALog.eTag(TAG, e, "getFileCharsetSimple")
        } finally {
            CloseUtils.closeIOQuietly(`is`)
        }
        return when (pos) {
            0xefbb -> "UTF-8"
            0xfffe -> "Unicode"
            0xfeff -> "UTF-16BE"
            else -> "GBK"
        }
    }

    /**
     * 获取文件行数
     * @param filePath 文件路径
     * @return 文件行数
     */
    fun getFileLines(filePath: String?): Int {
        return getFileLines(getFileByPath(filePath))
    }

    /**
     * 获取文件行数 ( 比 readLine 要快很多 )
     * @param file 文件
     * @return 文件行数
     */
    fun getFileLines(file: File?): Int {
        if (!isFileExists(file)) return 0
        var lineCount = 1
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            val buffer = ByteArray(1024)
            var readChars: Int
            if (NEW_LINE_STR!!.endsWith("\n")) {
                while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\n'.toByte()) ++lineCount
                    }
                }
            } else {
                while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\r'.toByte()) ++lineCount
                    }
                }
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getFileLines")
        } finally {
            CloseUtils.closeIOQuietly(`is`)
        }
        return lineCount
    }

//    public static int getFileLines(final File file) {
//        if (!isFileExists(file)) return 0;
//        int lineCount = 1;
//        InputStream is = null;
//        try {
//            is = new BufferedInputStream(new FileInputStream(file));
//            byte[] buffer = new byte[1024];
//            int readChars;
//            if (NEW_LINE_STR.endsWith("\n")) {
//                while ((readChars = is.read(buffer, 0, 1024)) != -1) {
//                    for (int i = 0; i < readChars; ++i) {
//                        if (buffer[i] == '\n') ++lineCount;
//                    }
//                }
//            } else {
//                while ((readChars = is.read(buffer, 0, 1024)) != -1) {
//                    for (int i = 0; i < readChars; ++i) {
//                        if (buffer[i] == '\r') ++lineCount;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            ALog.eTag(TAG, e, "getFileLines");
//        } finally {
//            CloseUtils.closeIOQuietly(is);
//        }
//        return lineCount;
//    }


    // =
    /**
     * 获取文件大小
     * @param filePath 文件路径
     * @return 文件大小
     */
    fun getFileSize(filePath: String?): String {
        return getFileSize(getFileByPath(filePath))
    }

    /**
     * 获取文件大小
     * @param file 文件
     * @return 文件大小
     */
    fun getFileSize(file: File?): String {
        return formatByteMemorySize(getFileLength(file).toDouble())
    }

    /**
     * 获取目录大小
     * @param dirPath 目录路径
     * @return 文件大小
     */
    fun getDirSize(dirPath: String?): String {
        return getDirSize(getFileByPath(dirPath))
    }

    /**
     * 获取目录大小
     * @param dir 目录
     * @return 文件大小
     */
    fun getDirSize(dir: File?): String {
        return formatByteMemorySize(getDirLength(dir).toDouble())
    }

    /**
     * 获取文件大小
     * @param filePath 文件路径
     * @return 文件大小
     */
    fun getFileLength(filePath: String?): Long {
        return getFileLength(getFileByPath(filePath))
    }

    /**
     * 获取文件大小
     * @param file 文件
     * @return 文件大小
     */
    fun getFileLength(file: File?): Long {
        return file?.length() ?: 0L
    }

    /**
     * 获取目录全部文件大小
     * @param dirPath 目录路径
     * @return 目录全部文件大小
     */
    fun getDirLength(dirPath: String?): Long {
        return getDirLength(getFileByPath(dirPath))
    }

    /**
     * 获取目录全部文件大小
     * @param dir 目录
     * @return 目录全部文件大小
     */
    fun getDirLength(dir: File?): Long {
        if (!isDirectory(dir)) return 0
        var len: Long = 0
        val files = dir!!.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                len += if (file.isDirectory) {
                    getDirLength(file)
                } else {
                    file.length()
                }
            }
        }
        return len
    }

    /**
     * 获取文件大小 - 网络资源
     * @param httpUri 文件网络链接
     * @return 文件大小
     */
    fun getFileLengthNetwork(httpUri: String): Long {
        if (CommUtils.isEmpty(httpUri)) return 0L
        // 判断是否网络资源
        val isHttpRes =
            httpUri.toLowerCase().startsWith("http:") || httpUri.toLowerCase().startsWith("https:")
        if (isHttpRes) {
            try {
                val conn = URL(httpUri).openConnection() as HttpURLConnection
                conn.setRequestProperty("Accept-Encoding", "identity")
                conn.connect()
                return if (conn.responseCode == 200) {
                    conn.contentLength.toLong()
                } else 0L
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getFileLengthNetwork")
            }
        }
        return 0L
    }

    /**
     * 获取路径中的文件名
     * @param file 文件
     * @return 文件名
     */
    fun getFileName(file: File?): String? {
        return if (file == null) null else getFileName(file.path)
    }

    /**
     * 获取路径中的文件名
     * @param filePath 文件路径
     * @return 文件名
     */
    fun getFileName(filePath: String): String {
        if (CommUtils.isEmpty(filePath)) return filePath
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
    }

    /**
     * 获取路径中的最长目录地址
     * @param file 文件
     * @return 最长目录地址
     */
    fun getDirName(file: File?): String? {
        return if (file == null) null else getDirName(file.path)
    }

    /**
     * 获取全路径中的最长目录地址
     * @param filePath 文件路径
     * @return 最长目录地址
     */
    fun getDirName(filePath: String): String {
        if (CommUtils.isEmpty(filePath)) return filePath
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
    }
    // =
    /**
     * 重命名文件 - 同个目录下, 修改文件名
     * @param filePath    文件路径
     * @param newFileName 文件新名称
     * @return `true` yes, `false` no
     */
    fun rename(filePath: String?, newFileName: String): Boolean {
        return rename(getFileByPath(filePath), newFileName)
    }

    /**
     * 重命名文件 - 同个目录下, 修改文件名
     * @param file        文件
     * @param newFileName 文件新名称
     * @return `true` yes, `false` no
     */
    fun rename(file: File?, newFileName: String): Boolean {
        // 文件为空返回 false
        if (file == null) return false
        // 文件不存在返回 false
        if (!file.exists()) return false
        // 如果文件名没有改变返回 true
        if (newFileName == file.name) return true
        // 拼接新的文件路径
        val newFile = File(file.parent + File.separator + newFileName)
        // 如果重命名的文件已存在返回 false
        return !newFile.exists() && file.renameTo(newFile)
    }
    // ================
    // = 文件大小处理 =
    // ================
    /**
     * 传入文件路径, 返回对应的文件大小
     * @param filePath 文件路径
     * @return 文件大小转换字符串
     */
    fun formatFileSize(filePath: String?): String {
        val file = getFileByPath(filePath)

        return formatFileSize(file?.length()?.toDouble() ?: 0.toDouble())
    }



    /**
     * 传入文件路径, 返回对应的文件大小
     * @param file 文件
     * @return 文件大小转换字符串
     */
    fun formatFileSize(file: File?): String {
        return formatFileSize(file?.length()?.toDouble() ?: 0.toDouble())
    }

    /**
     * 传入对应的文件大小, 返回转换后文件大小
     * @param fileSize 文件大小
     * @return 文件大小转换字符串
     */
    fun formatFileSize(fileSize: Double): String {
        // 转换文件大小
        val df = DecimalFormat("#.00")
        val fileSizeStr: String
        fileSizeStr = when {
            fileSize <= 0 -> {
                "0B"
            }
            fileSize < 1024 -> {
                df.format(fileSize) + "B"
            }
            fileSize < 1048576 -> {
                df.format(fileSize / 1024) + "KB"
            }
            fileSize < 1073741824 -> {
                df.format(fileSize / 1048576) + "MB"
            }
            fileSize < 1099511627776.0 -> {
                df.format(fileSize / 1073741824) + "GB"
            }
            else -> {
                df.format(fileSize / 1099511627776.0) + "TB"
            }
        }
        return fileSizeStr
    }

    /**
     * 字节数转合适内存大小 保留 3 位小数 (%.位数f)
     * @param byteSize 字节数
     * @return 合适内存大小字符串
     */
    fun formatByteMemorySize(byteSize: Double): String {
        return formatByteMemorySize(3, byteSize)
    }

    /**
     * 字节数转合适内存大小 保留 number 位小数 (%.位数f)
     * @param number   保留小数位数
     * @param byteSize 字节数
     * @return 合适内存大小字符串
     */
    fun formatByteMemorySize(number: Int, byteSize: Double): String {
        return if (byteSize < 0.0) {
            "0B"
        } else if (byteSize < 1024.0) {
            String.format("%." + number + "fB", byteSize)
        } else if (byteSize < 1048576.0) {
            String.format("%." + number + "fKB", byteSize / 1024.0)
        } else if (byteSize < 1073741824.0) {
            String.format("%." + number + "fMB", byteSize / 1048576.0)
        } else if (byteSize < 1099511627776.0) {
            String.format("%." + number + "fGB", byteSize / 1073741824.0)
        } else {
            String.format("%." + number + "fTB", byteSize / 1099511627776.0)
        }
    }
    // ============
    // = 文件操作 =
    // ============
    /**
     * 删除文件
     * @param filePath 文件路径
     * @return `true` success, `false` fail
     */
    fun deleteFile(filePath: String?): Boolean {
        return deleteFile(getFileByPath(filePath))
    }

    /**
     * 删除文件
     * @param file 文件
     * @return `true` success, `false` fail
     */
    fun deleteFile(file: File?): Boolean {
        // 文件存在, 并且不是目录文件, 则直接删除
        return if (file != null && file.exists() && !file.isDirectory) {
            file.delete()
        } else false
    }

    /**
     * 删除多个文件
     * @param filePaths 文件路径数组
     * @return `true` success, `false` fail
     */
    fun deleteFiles(vararg filePaths: String?): Boolean {
        if (filePaths != null && filePaths.size != 0) {
            var i = 0
            val len = filePaths.size
            while (i < len) {
                deleteFile(filePaths[i])
                i++
            }
            return true
        }
        return false
    }

    /**
     * 删除多个文件
     * @param files 文件数组
     * @return `true` success, `false` fail
     */
    fun deleteFiles(vararg files: File?): Boolean {
        if (files != null && files.size != 0) {
            var i = 0
            val len = files.size
            while (i < len) {
                deleteFile(files[i])
                i++
            }
            return true
        }
        return false
    }
    // =
    /**
     * 删除文件夹
     * @param filePath 文件路径
     * @return `true` success, `false` fail
     */
    fun deleteFolder(filePath: String?): Boolean {
        return deleteFolder(getFileByPath(filePath))
    }

    /**
     * 删除文件夹
     * @param file 文件
     * @return `true` success, `false` fail
     */
    fun deleteFolder(file: File?): Boolean {
        if (file != null) {
            try {
                // 文件存在, 并且不是目录文件, 则直接删除
                if (file.exists()) {
                    return if (file.isDirectory) { // 属于文件目录
                        val files = file.listFiles()
                        if (null == files || files.size == 0) {
                            file.delete()
                        }
                        for (f in files!!) {
                            deleteFolder(f.path)
                        }
                        file.delete()
                    } else { // 属于文件
                        deleteFile(file)
                    }
                }
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "deleteFolder")
            }
        }
        return false
    }

    /**
     * 保存文件
     * @param filePath 文件路径
     * @param data     待存储数据
     * @return `true` success, `false` fail
     */
    fun saveFile(filePath: String?, data: ByteArray?): Boolean {
        return saveFile(getFile(filePath), data)
    }

    /**
     * 保存文件
     * @param file 文件
     * @param data 待存储数据
     * @return `true` success, `false` fail
     */
    fun saveFile(file: File?, data: ByteArray?): Boolean {
        if (file != null && data != null) {
            try {
                // 防止文件夹没创建
                createFolder(getDirName(file))
                // 写入文件
                val fos = FileOutputStream(file)
                val bos = BufferedOutputStream(fos)
                bos.write(data)
                bos.close()
                fos.close()
                return true
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "saveFile")
            }
        }
        return false
    }

    /**
     * 追加文件
     * @param filePath 文件路径
     * @param data     待追加数据
     * @return `true` success, `false` fail
     */
    fun appendFile(filePath: String?, data: ByteArray?): Boolean {
        return appendFile(getFile(filePath), data)
    }

    /**
     * 追加文件
     * <pre>
     * 如果未创建文件, 则会创建并写入数据 ( 等同 [.saveFile] )
     * 如果已创建文件, 则在结尾追加数据
    </pre> *
     * @param file 文件
     * @param data 待追加数据
     * @return `true` success, `false` fail
     */
    fun appendFile(file: File?, data: ByteArray?): Boolean {
        try {
            // 防止文件夹没创建
            createFolder(getDirName(file))
            // 写入文件
            val fos = FileOutputStream(file, true)
            val bos = BufferedOutputStream(fos)
            bos.write(data)
            bos.close()
            fos.close()
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "appendFile")
        }
        return false
    }
    // =
    /**
     * 读取文件
     * @param filePath 文件路径
     * @return 文件内容 byte[]
     */
    fun readFileBytes(filePath: String?): ByteArray? {
        return readFileBytes(getFileByPath(filePath))
    }

    /**
     * 读取文件
     * @param file 文件
     * @return 文件内容 byte[]
     */
    fun readFileBytes(file: File?): ByteArray? {
        if (file != null && file.exists()) {
            try {
                val fis = FileInputStream(file)
                val length = fis.available()
                val buffer = ByteArray(length)
                fis.read(buffer)
                fis.close()
                return buffer
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "readFileBytes")
            }
        }
        return null
    }

    /**
     * 读取文件
     * @param inputStream [InputStream]
     * @return 文件内容 byte[]
     */
    fun readFileBytes(inputStream: InputStream?): ByteArray? {
        if (inputStream != null) {
            try {
                val length = inputStream.available()
                val buffer = ByteArray(length)
                inputStream.read(buffer)
                inputStream.close()
                return buffer
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "readFileBytes")
            }
        }
        return null
    }

    /**
     * 读取文件
     * @param filePath 文件路径
     * @return 文件内容字符串
     */
    fun readFile(filePath: String?): String? {
        return readFile(getFileByPath(filePath))
    }

    /**
     * 读取文件
     * @param file 文件
     * @return 文件内容字符串
     */
    fun readFile(file: File?): String? {
        if (file != null && file.exists()) {
            try {
                return readFile(FileInputStream(file))
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "readFile")
            }
        }
        return null
    }
    /**
     * 读取文件
     * @param inputStream [InputStream] new FileInputStream(path)
     * @param encode      编码格式
     * @return 文件内容字符串
     */
    /**
     * 读取文件
     * @param inputStream [InputStream] new FileInputStream(path)
     * @return 文件内容字符串
     */
    @JvmOverloads
    fun readFile(inputStream: InputStream?, encode: String? = null): String? {
        if (inputStream != null) {
            try {
                val isr: InputStreamReader? = null
                if (encode != null) {
                    InputStreamReader(inputStream, encode)
                } else {
                    InputStreamReader(inputStream)
                }
                val br = BufferedReader(isr)
                val builder = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    builder.append(line)
                }
                isr!!.close()
                br.close()
                return builder.toString()
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "readFile")
            }
        }
        return null
    }
    // =
    /**
     * 复制单个文件
     * @param inputStream  文件流 ( 被复制 )
     * @param destFilePath 目标文件地址
     * @param overlay      如果目标文件存在, 是否覆盖
     * @return `true` success, `false` fail
     */
    fun copyFile(inputStream: InputStream?, destFilePath: String?, overlay: Boolean): Boolean {
        if (inputStream == null || destFilePath == null) {
            return false
        }
        val destFile = File(destFilePath)
        // 如果属于文件夹则跳过
        if (destFile.isDirectory) {
            return false
        }
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            if (overlay) {
                // 删除已经存在的目标文件, 无论目标文件是目录还是单个文件
                destFile.delete()
            } else { // 如果文件存在, 但是不覆盖, 则返回 false 表示失败
                return false
            }
        } else {
            // 如果目标文件所在目录不存在, 则创建目录
            if (!destFile.parentFile.exists()) {
                // 目标文件所在目录不存在
                if (!destFile.parentFile.mkdirs()) {
                    // 复制文件失败: 创建目标文件所在目录失败
                    return false
                }
            }
        }
        // 复制文件
        var byteread = 0 // 读取的字节数
        val `is`: InputStream = inputStream
        var os: OutputStream? = null
        return try {
            os = FileOutputStream(destFile)
            val buffer = ByteArray(1024)
            while (`is`.read(buffer).also { byteread = it } != -1) {
                os.write(buffer, 0, byteread)
            }
            true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "copyFile")
            false
        } finally {
            CloseUtils.closeIOQuietly(os, `is`)
        }
    }

    /**
     * 复制单个文件
     * @param srcFilePath  待复制的文件地址
     * @param destFilePath 目标文件地址
     * @param overlay      如果目标文件存在, 是否覆盖
     * @return `true` success, `false` fail
     */
    fun copyFile(srcFilePath: String?, destFilePath: String?, overlay: Boolean): Boolean {
        if (srcFilePath == null || destFilePath == null) {
            return false
        }
        val srcFile = File(srcFilePath)
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            return false
        } else if (!srcFile.isFile) { // srcFile.isDirectory();
            return false
        }
        // 判断目标文件是否存在
        val destFile = File(destFilePath)
        // 如果属于文件夹则跳过
        if (destFile.isDirectory) {
            return false
        }
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            if (overlay) {
                // 删除已经存在的目标文件, 无论目标文件是目录还是单个文件
                File(destFilePath).delete()
            } else { // 如果文件存在, 但是不覆盖, 则返回 false 表示失败
                return false
            }
        } else {
            // 如果目标文件所在目录不存在, 则创建目录
            if (!destFile.parentFile.exists()) {
                // 目标文件所在目录不存在
                if (!destFile.parentFile.mkdirs()) {
                    // 复制文件失败: 创建目标文件所在目录失败
                    return false
                }
            }
        }
        // 复制文件
        var byteread = 0 // 读取的字节数
        var `is`: InputStream? = null
        var os: OutputStream? = null
        return try {
            `is` = FileInputStream(srcFile)
            os = FileOutputStream(destFile)
            val buffer = ByteArray(1024)
            while (`is`.read(buffer).also { byteread = it } != -1) {
                os.write(buffer, 0, byteread)
            }
            true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "copyFile")
            false
        } finally {
            CloseUtils.closeIOQuietly(os, `is`)
        }
    }

    /**
     * 复制文件夹
     * @param srcFolderPath  待复制的文件夹地址
     * @param destFolderPath 目标文件夹地址
     * @param overlay        如果目标文件存在, 是否覆盖
     * @return `true` success, `false` fail
     */
    fun copyFolder(srcFolderPath: String?, destFolderPath: String?, overlay: Boolean): Boolean {
        return copyFolder(srcFolderPath, destFolderPath, srcFolderPath, overlay)
    }

    /**
     * 复制文件夹
     * @param srcFolderPath  待复制的文件夹地址
     * @param destFolderPath 目标文件夹地址
     * @param sourcePath     源文件地址 ( 用于保递归留记录 )
     * @param overlay        如果目标文件存在, 是否覆盖
     * @return `true` success, `false` fail
     */
    private fun copyFolder(
        srcFolderPath: String?,
        destFolderPath: String?,
        sourcePath: String?,
        overlay: Boolean
    ): Boolean {
        if (srcFolderPath == null || destFolderPath == null || sourcePath == null) {
            return false
        }
        val srcFile = File(srcFolderPath)
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            return false
        } else if (!srcFile.isDirectory) { // 不属于文件夹则跳过
            return false
        }
        // 判断目标文件是否存在
        val destFile = File(destFolderPath)
        // 如果文件夹没创建, 则创建
        if (!destFile.exists()) {
            // 允许创建多级目录
            destFile.mkdirs()
        }
        // 判断是否属于文件夹
        if (!destFile.isDirectory) { // 不属于文件夹则跳过
            return false
        }
        // 判断是否存在
        if (srcFile.exists()) {
            // 获取文件路径
            val files = srcFile.listFiles()
            // 防止不存在文件
            if (files != null && files.size != 0) {
                // 进行遍历
                for (file in files) {
                    // 文件存在才进行处理
                    if (file.exists()) {
                        // 属于文件夹
                        if (file.isDirectory) {
                            copyFolder(file.absolutePath, destFolderPath, sourcePath, overlay)
                        } else { // 属于文件
                            // 复制的文件地址
                            val filePath = file.absolutePath
                            // 获取源文件地址 - 并且进行判断
                            var dealSource = File(sourcePath).absolutePath
                            // 属于最前才进行处理
                            if (filePath.indexOf(dealSource) == 0) {
                                // 获取处理后的地址
                                dealSource = filePath.substring(dealSource.length)
                                // 获取需要复制保存的地址
                                val savePath = File(destFolderPath, dealSource).absolutePath
                                // 进行复制文件
                                val isResult = copyFile(filePath, savePath, overlay)
                            }
                        }
                    }
                }
            }
        }
        return true
    }
    // =
    /**
     * 移动 ( 剪切 ) 文件
     * @param srcFilePath  待移动的文件地址
     * @param destFilePath 目标文件地址
     * @param overlay      如果目标文件存在, 是否覆盖
     * @return `true` success, `false` fail
     */
    fun moveFile(srcFilePath: String?, destFilePath: String?, overlay: Boolean): Boolean {
        // 复制文件
        return if (copyFile(srcFilePath, destFilePath, overlay)) {
            // 删除文件
            deleteFile(srcFilePath)
        } else false
    }

    /**
     * 移动 ( 剪切 ) 文件夹
     * @param srcFilePath  待移动的文件夹地址
     * @param destFilePath 目标文件夹地址
     * @param overlay      如果目标文件存在, 是否覆盖
     * @return `true` success, `false` fail
     */
    fun moveFolder(srcFilePath: String?, destFilePath: String?, overlay: Boolean): Boolean {
        // 复制文件夹
        return if (copyFolder(srcFilePath, destFilePath, overlay)) {
            // 删除文件夹
            deleteFolder(srcFilePath)
        } else false
    }

    /**
     * 复制或移动目录
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param listener    是否覆盖监听器
     * @param isMove      是否移动
     * @return `true` 复制或移动成功, `false` 复制或移动失败
     */
    fun copyOrMoveDir(
        srcDirPath: String?,
        destDirPath: String?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        return copyOrMoveDir(
            getFileByPath(srcDirPath),
            getFileByPath(destDirPath),
            listener,
            isMove
        )
    }

    /**
     * 复制或移动目录
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param listener 是否覆盖监听器
     * @param isMove   是否移动
     * @return `true` 复制或移动成功, `false` 复制或移动失败
     */
    fun copyOrMoveDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        if (srcDir == null || destDir == null || listener == null) return false
        // 为防止以上这种情况出现出现误判, 须分别在后面加个路径分隔符
        val srcPath = srcDir.path + File.separator
        val destPath = destDir.path + File.separator
        if (destPath.contains(srcPath)) return false
        // 源文件不存在或者不是目录则返回 false
        if (!srcDir.exists() || !srcDir.isDirectory) return false
        if (destDir.exists()) {
            if (listener.onReplace()) { // 需要覆盖则删除旧目录
                if (!deleteAllInDir(destDir)) { // 删除文件失败的话返回 false
                    return false
                }
            } else { // 不需要覆盖直接返回即可 true
                return true
            }
        }
        // 目标目录不存在返回 false
        if (!createOrExistsDir(destDir)) return false
        val files = srcDir.listFiles()
        for (file in files) {
            val oneDestFile = File(destPath + file.name)
            if (file.isFile) {
                // 如果操作失败返回 false
                if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) return false
            } else if (file.isDirectory) {
                // 如果操作失败返回 false
                if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) return false
            }
        }
        return !isMove || deleteDir(srcDir)
    }

    /**
     * 复制或移动文件
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param listener     是否覆盖监听器
     * @param isMove       是否移动
     * @return `true` 复制或移动成功, `false` 复制或移动失败
     */
    fun copyOrMoveFile(
        srcFilePath: String?,
        destFilePath: String?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        return copyOrMoveFile(
            getFileByPath(srcFilePath),
            getFileByPath(destFilePath),
            listener,
            isMove
        )
    }

    /**
     * 复制或移动文件
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 是否覆盖监听器
     * @param isMove   是否移动
     * @return `true` 复制或移动成功, `false` 复制或移动失败
     */
    fun copyOrMoveFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        if (srcFile == null || destFile == null || listener == null) return false
        // 如果源文件和目标文件相同则返回 false
        if (srcFile == destFile) return false
        // 源文件不存在或者不是文件则返回 false
        if (!srcFile.exists() || !srcFile.isFile) return false
        if (destFile.exists()) { // 目标文件存在
            if (listener.onReplace()) { // 需要覆盖则删除旧文件
                if (!destFile.delete()) { // 删除文件失败的话返回 false
                    return false
                }
            } else { // 不需要覆盖直接返回即可 true
                return true
            }
        }
        // 目标目录不存在返回 false
        return if (!createOrExistsDir(destFile.parentFile)) false else try {
            FileIOUtils.writeFileFromIS(
                destFile,
                FileInputStream(srcFile),
                false
            ) && !(isMove && !deleteFile(srcFile))
        } catch (e: FileNotFoundException) {
            ALog.eTag(TAG, e, "copyOrMoveFile")
            false
        }
    }

    /**
     * 复制目录
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param listener    是否覆盖监听器
     * @return `true` 复制成功, `false` 复制失败
     */
    fun copyDir(srcDirPath: String?, destDirPath: String?, listener: OnReplaceListener?): Boolean {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener)
    }

    /**
     * 复制目录
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param listener 是否覆盖监听器
     * @return `true` 复制成功, `false` 复制失败
     */
    fun copyDir(srcDir: File?, destDir: File?, listener: OnReplaceListener?): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, false)
    }

    /**
     * 复制文件
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param listener     是否覆盖监听器
     * @return `true` 复制成功, `false` 复制失败
     */
    fun copyFile(
        srcFilePath: String?,
        destFilePath: String?,
        listener: OnReplaceListener?
    ): Boolean {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener)
    }

    /**
     * 复制文件
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 是否覆盖监听器
     * @return `true` 复制成功, `false` 复制失败
     */
    fun copyFile(srcFile: File?, destFile: File?, listener: OnReplaceListener?): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, false)
    }

    /**
     * 移动目录
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param listener    是否覆盖监听器
     * @return `true` 移动成功, `false` 移动失败
     */
    fun moveDir(srcDirPath: String?, destDirPath: String?, listener: OnReplaceListener?): Boolean {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener)
    }

    /**
     * 移动目录
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param listener 是否覆盖监听器
     * @return `true` 移动成功, `false` 移动失败
     */
    fun moveDir(srcDir: File?, destDir: File?, listener: OnReplaceListener?): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, true)
    }

    /**
     * 移动文件
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param listener     是否覆盖监听器
     * @return `true` 移动成功, `false` 移动失败
     */
    fun moveFile(
        srcFilePath: String?,
        destFilePath: String?,
        listener: OnReplaceListener?
    ): Boolean {
        return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener)
    }

    /**
     * 移动文件
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 是否覆盖监听器
     * @return `true` 移动成功, `false` 移动失败
     */
    fun moveFile(srcFile: File?, destFile: File?, listener: OnReplaceListener?): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, true)
    }

    /**
     * 删除目录
     * @param dirPath 目录路径
     * @return `true` 删除成功, `false` 删除失败
     */
    fun deleteDir(dirPath: String?): Boolean {
        return deleteDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录
     * @param dir 目录
     * @return `true` 删除成功, `false` 删除失败
     */
    fun deleteDir(dir: File?): Boolean {
        if (dir == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (file.isFile) {
                    if (!file.delete()) return false
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) return false
                }
            }
        }
        return dir.delete()
    }

    /**
     * 删除目录下所有东西
     * @param dirPath 目录路径
     * @return `true` 删除成功, `false` 删除失败
     */
    fun deleteAllInDir(dirPath: String?): Boolean {
        return deleteAllInDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录下所有东西
     * @param dir 目录
     * @return `true` 删除成功, `false` 删除失败
     */
    fun deleteAllInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir, FileFilter { true })
    }

    /**
     * 删除目录下所有文件
     * @param dirPath 目录路径
     * @return `true` 删除成功, `false` 删除失败
     */
    fun deleteFilesInDir(dirPath: String?): Boolean {
        return deleteFilesInDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录下所有文件
     * @param dir 目录
     * @return `true` 删除成功, `false` 删除失败
     */
    fun deleteFilesInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir, FileFilter { pathname -> pathname.isFile })
    }

    /**
     * 删除目录下所有过滤的文件
     * @param dirPath 目录路径
     * @param filter  过滤器
     * @return `true` 删除成功, `false` 删除失败
     */
    fun deleteFilesInDirWithFilter(dirPath: String?, filter: FileFilter?): Boolean {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter)
    }

    /**
     * 删除目录下所有过滤的文件
     * @param dir    目录
     * @param filter 过滤器
     * @return `true` 删除成功, `false` 删除失败
     */
    fun deleteFilesInDirWithFilter(dir: File?, filter: FileFilter?): Boolean {
        if (filter == null) return false
        // dir is null then return false
        if (dir == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (filter.accept(file)) {
                    if (file.isFile) {
                        if (!file.delete()) return false
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
        }
        return true
    }
    /**
     * 获取目录下所有文件
     * @param dirPath     目录路径
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    /**
     * 获取目录下所有文件 - 不递归进子目录
     * @param dirPath 目录路径
     * @return 文件链表
     */
    @JvmOverloads
    fun listFilesInDir(dirPath: String?, isRecursive: Boolean = false): List<File>? {
        return listFilesInDir(getFileByPath(dirPath), isRecursive)
    }
    /**
     * 获取目录下所有文件
     * @param dir         目录
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    /**
     * 获取目录下所有文件 - 不递归进子目录
     * @param dir 目录
     * @return 文件链表
     */
    @JvmOverloads
    fun listFilesInDir(dir: File?, isRecursive: Boolean = false): List<File>? {
        return listFilesInDirWithFilter(dir, FileFilter { true }, isRecursive)
    }

    /**
     * 获取目录下所有过滤的文件 - 不递归进子目录
     * @param dirPath 目录路径
     * @param filter  过滤器
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dirPath: String?, filter: FileFilter?): List<File>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, false)
    }

    /**
     * 获取目录下所有过滤的文件
     * @param dirPath     目录路径
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(
        dirPath: String?,
        filter: FileFilter?,
        isRecursive: Boolean
    ): List<File>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive)
    }
    /**
     * 获取目录下所有过滤的文件
     * @param dir         目录
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    /**
     * 获取目录下所有过滤的文件 - 不递归进子目录
     * @param dir    目录
     * @param filter 过滤器
     * @return 文件链表
     */
    @JvmOverloads
    fun listFilesInDirWithFilter(
        dir: File?,
        filter: FileFilter?,
        isRecursive: Boolean = false
    ): List<File>? {
        if (!isDirectory(dir) || filter == null) return null
        val list: MutableList<File> = ArrayList()
        val files = dir!!.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (filter.accept(file)) {
                    list.add(file)
                }
                if (isRecursive && file.isDirectory) {
                    list.addAll(listFilesInDirWithFilter(file, filter, true)!!)
                }
            }
        }
        return list
    }
    /**
     * 获取目录下所有文件
     * @param dirPath     目录路径
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    // =
    /**
     * 获取目录下所有文件 - 不递归进子目录
     * @param dirPath 目录路径
     * @return 文件链表
     */
    @JvmOverloads
    fun listFilesInDirBean(dirPath: String?, isRecursive: Boolean = false): List<FileList>? {
        return listFilesInDirBean(getFileByPath(dirPath), isRecursive)
    }
    /**
     * 获取目录下所有文件
     * @param dir         目录
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    /**
     * 获取目录下所有文件 - 不递归进子目录
     * @param dir 目录
     * @return 文件链表
     */
    @JvmOverloads
    fun listFilesInDirBean(dir: File?, isRecursive: Boolean = false): List<FileList>? {
        return listFilesInDirWithFilterBean(dir, FileFilter { true }, isRecursive)
    }

    /**
     * 获取目录下所有过滤的文件 - 不递归进子目录
     * @param dirPath 目录路径
     * @param filter  过滤器
     * @return 文件链表
     */
    fun listFilesInDirWithFilterBean(dirPath: String?, filter: FileFilter?): List<FileList>? {
        return listFilesInDirWithFilterBean(getFileByPath(dirPath), filter, false)
    }

    /**
     * 获取目录下所有过滤的文件
     * @param dirPath     目录路径
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDirWithFilterBean(
        dirPath: String?,
        filter: FileFilter?,
        isRecursive: Boolean
    ): List<FileList>? {
        return listFilesInDirWithFilterBean(getFileByPath(dirPath), filter, isRecursive)
    }
    /**
     * 获取目录下所有过滤的文件
     * @param dir         目录
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    /**
     * 获取目录下所有过滤的文件 - 不递归进子目录
     * @param dir    目录
     * @param filter 过滤器
     * @return 文件链表
     */
    @JvmOverloads
    fun listFilesInDirWithFilterBean(
        dir: File?,
        filter: FileFilter?,
        isRecursive: Boolean = false
    ): List<FileList>? {
        if (!isDirectory(dir) || filter == null) return null
        val list: MutableList<FileList> = ArrayList()
        val files = dir!!.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (filter.accept(file)) {
                    var fileList: FileList
                    fileList = if (isRecursive && file.isDirectory) {
                        val subs = listFilesInDirWithFilterBean(file, filter, true)
                        FileList(file, subs)
                    } else {
                        FileList(file)
                    }
                    list.add(fileList)
                }
            }
        }
        return list
    }

    // ================
    // = 图片类型判断 =
    // ================
    // 图片格式
    private val IMAGE_FORMATS = arrayOf(".PNG", ".JPG", ".JPEG", ".BMP", ".GIF", ".WEBP")

    /**
     * 根据文件名判断文件是否为图片
     * @param file 文件
     * @return `true` yes, `false` no
     */
    fun isImageFormats(file: File?): Boolean {
        return file != null && isImageFormats(file.path, IMAGE_FORMATS)
    }

    /**
     * 根据文件名判断文件是否为图片
     * @param filePath 文件路径
     * @return `true` yes, `false` no
     */
    fun isImageFormats(filePath: String?): Boolean {
        return isImageFormats(filePath, IMAGE_FORMATS)
    }

    /**
     * 根据文件名判断文件是否为图片
     * @param filePath    文件路径
     * @param fileFormats 文件格式
     * @return `true` yes, `false` no
     */
    fun isImageFormats(filePath: String?, fileFormats: Array<String>?): Boolean {
        return isFileFormats(filePath, fileFormats)
    }

    // ================
    // = 音频类型判断 =
    // ================
    // 音频格式
    private val AUDIO_FORMATS = arrayOf(".MP3", ".AAC", ".OGG", ".WMA", ".APE", ".FLAC", ".RA")

    /**
     * 根据文件名判断文件是否为音频
     * @param file 文件
     * @return `true` yes, `false` no
     */
    fun isAudioFormats(file: File?): Boolean {
        return file != null && isAudioFormats(file.path, AUDIO_FORMATS)
    }

    /**
     * 根据文件名判断文件是否为音频
     * @param filePath 文件路径
     * @return `true` yes, `false` no
     */
    fun isAudioFormats(filePath: String?): Boolean {
        return isAudioFormats(filePath, AUDIO_FORMATS)
    }

    /**
     * 根据文件名判断文件是否为音频
     * @param filePath    文件路径
     * @param fileFormats 文件格式
     * @return `true` yes, `false` no
     */
    fun isAudioFormats(filePath: String?, fileFormats: Array<String>?): Boolean {
        return isFileFormats(filePath, fileFormats)
    }

    // ================
    // = 视频类型判断 =
    // ================
    // 视频格式
    private val VIDEO_FORMATS = arrayOf(
        ".MP4",
        ".AVI",
        ".MOV",
        ".ASF",
        ".MPG",
        ".MPEG",
        ".WMV",
        ".RM",
        ".RMVB",
        ".3GP",
        ".MKV"
    )

    /**
     * 根据文件名判断文件是否为视频
     * @param file 文件
     * @return `true` yes, `false` no
     */
    fun isVideoFormats(file: File?): Boolean {
        return file != null && isVideoFormats(file.path, VIDEO_FORMATS)
    }

    /**
     * 根据文件名判断文件是否为视频
     * @param filePath 文件路径
     * @return `true` yes, `false` no
     */
    fun isVideoFormats(filePath: String?): Boolean {
        return isVideoFormats(filePath, VIDEO_FORMATS)
    }

    /**
     * 根据文件名判断文件是否为视频
     * @param filePath    文件路径
     * @param fileFormats 文件格式
     * @return `true` yes, `false` no
     */
    fun isVideoFormats(filePath: String?, fileFormats: Array<String>?): Boolean {
        return isFileFormats(filePath, fileFormats)
    }
    // =
    /**
     * 根据文件名判断文件是否为指定格式
     * @param file        文件
     * @param fileFormats 文件格式
     * @return `true` yes, `false` no
     */
    fun isFileFormats(file: File?, fileFormats: Array<String>?): Boolean {
        return file != null && isFileFormats(file.path, fileFormats)
    }

    /**
     * 根据文件名判断文件是否为指定格式
     * @param filePath    文件路径
     * @param fileFormats 文件格式
     * @return `true` yes, `false` no
     */
    fun isFileFormats(filePath: String?, fileFormats: Array<String>?): Boolean {
        if (filePath == null || fileFormats == null || fileFormats.size == 0) return false
        val path = filePath.toUpperCase()
        for (format in fileFormats) {
            if (format != null) {
                if (path.endsWith(format.toUpperCase())) {
                    return true
                }
            }
        }
        return false
    }
    // ============
    // = MD5Utils =
    // ============
    /**
     * 获取文件 MD5 值
     * @param filePath 文件路径
     * @return 文件 MD5 值
     */
    fun getFileMD5(filePath: String?): ByteArray {
        return MD5Utils.getFileMD5(filePath)
    }

    /**
     * 获取文件 MD5 值
     * @param filePath 文件路径
     * @return 文件 MD5 值转十六进制字符串
     */
    fun getFileMD5ToHexString(filePath: String?): String {
        return MD5Utils.getFileMD5ToHexString(filePath)
    }

    /**
     * 获取文件 MD5 值
     * @param file 文件
     * @return 文件 MD5 值转十六进制字符串
     */
    fun getFileMD5ToHexString(file: File?): String {
        return MD5Utils.getFileMD5ToHexString(file)
    }

    /**
     * 获取文件 MD5 值
     * @param file 文件
     * @return 文件 MD5 值 byte[]
     */
    fun getFileMD5(file: File?): ByteArray {
        return MD5Utils.getFileMD5(file)
    }
    // =
    /**
     * detail: 覆盖 / 替换事件
     * @author Ttt
     */
    interface OnReplaceListener {
        /**
         * 是否覆盖 / 替换文件
         * @return `true` yes, `false` no
         */
        fun onReplace(): Boolean
    }
    // =
    /**
     * detail: 文件列表
     * @author Ttt
     */
    class FileList

    /**
     * 构造函数
     * @param file 当前文件夹
     */ @JvmOverloads constructor(
        /**
         * 获取当前文件夹
         * @return [File]
         */
        // 当前文件夹
        val file: File,
        /**
         * 获取文件夹内子文件列表
         * @return [ArrayList]
         */
        // 文件夹内子文件列表
        val subFiles: List<FileList>? = ArrayList(0)
    ) {

        // =
        /**
         * 构造函数
         * @param file  当前文件夹
         * @param subFiles 文件夹内子文件列表
         */
    }
}