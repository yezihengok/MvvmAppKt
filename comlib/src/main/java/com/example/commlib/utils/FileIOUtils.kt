package com.example.commlib.utils

import com.blankj.ALog
import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.util.*

/**
 * detail: 文件 (IO 流 ) 工具类
 * @author Ttt
 */
object FileIOUtils {
    // 日志 TAG
    private val TAG = FileIOUtils::class.java.simpleName

    // 换行符
    private val NEW_LINE_STR = System.getProperty("line.separator")

    // 缓存大小
    private var sBufferSize = 8192

    // 无数据读取
    const val EOF = -1

    /**
     * 设置缓冲区的大小, 默认大小等于 8192 字节
     * @param bufferSize 缓冲 Buffer 大小
     */
    fun setBufferSize(bufferSize: Int) {
        sBufferSize = bufferSize
    }

    /**
     * 获取输入流
     * @param filePath 文件路径
     * @return [FileInputStream]
     */
    fun getFileInputStream(filePath: String?): FileInputStream? {
        return getFileInputStream(FileUtils.getFile(filePath))
    }

    /**
     * 获取输入流
     * @param file 文件
     * @return [FileInputStream]
     */
    fun getFileInputStream(file: File?): FileInputStream? {
        if (file == null) return null
        try {
            return FileInputStream(file)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getFileInputStream")
        }
        return null
    }

    /**
     * 获取输出流
     * @param filePath 文件路径
     * @return [FileOutputStream]
     */
    fun getFileOutputStream(filePath: String?): FileOutputStream? {
        return getFileOutputStream(FileUtils.getFile(filePath))
    }

    /**
     * 获取输出流
     * @param filePath 文件路径
     * @param append   是否追加到结尾
     * @return [FileOutputStream]
     */
    fun getFileOutputStream(filePath: String?, append: Boolean): FileOutputStream? {
        return getFileOutputStream(FileUtils.getFile(filePath), append)
    }

    /**
     * 获取输出流
     * @param file 文件
     * @return [FileOutputStream]
     */
    fun getFileOutputStream(file: File?): FileOutputStream? {
        return getFileOutputStream(file, false)
    }

    /**
     * 获取输出流
     * @param file   文件
     * @param append 是否追加到结尾
     * @return [FileOutputStream]
     */
    fun getFileOutputStream(file: File?, append: Boolean): FileOutputStream? {
        if (file == null) return null
        try {
            return FileOutputStream(file, append)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getFileOutputStream")
        }
        return null
    }
    // =
    /**
     * 通过输入流写入文件
     * @param filePath    文件路径
     * @param inputStream [InputStream]
     * @return `true` success, `false` fail
     */
    fun writeFileFromIS(filePath: String?, inputStream: InputStream?): Boolean {
        return writeFileFromIS(FileUtils.getFileByPath(filePath), inputStream, false)
    }

    /**
     * 通过输入流写入文件
     * @param filePath    文件路径
     * @param inputStream [InputStream]
     * @param append      是否追加到结尾
     * @return `true` success, `false` fail
     */
    fun writeFileFromIS(filePath: String?, inputStream: InputStream?, append: Boolean): Boolean {
        return writeFileFromIS(FileUtils.getFileByPath(filePath), inputStream, append)
    }
    /**
     * 通过输入流写入文件
     * @param file        文件
     * @param inputStream [InputStream]
     * @param append      是否追加到结尾
     * @return `true` success, `false` fail
     */
    /**
     * 通过输入流写入文件
     * @param file        文件
     * @param inputStream [InputStream]
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun writeFileFromIS(file: File?, inputStream: InputStream?, append: Boolean = false): Boolean {
        if (inputStream == null || !FileUtils.createOrExistsFile(file)) return false
        var os: OutputStream? = null
        return try {
            os = BufferedOutputStream(FileOutputStream(file, append))
            val data = ByteArray(sBufferSize)
            var len: Int
            while (inputStream.read(data, 0, sBufferSize).also { len = it } != EOF) {
                os.write(data, 0, len)
            }
            true
        } catch (e: IOException) {
            ALog.eTag(TAG, e, "writeFileFromIS")
            false
        } finally {
            CloseUtils.closeIOQuietly(inputStream, os)
        }
    }

    /**
     * 通过字节流写入文件
     * @param filePath 文件路径
     * @param bytes    byte[]
     * @return `true` success, `false` fail
     */
    fun writeFileFromBytesByStream(filePath: String?, bytes: ByteArray?): Boolean {
        return writeFileFromBytesByStream(FileUtils.getFileByPath(filePath), bytes, false)
    }

    /**
     * 通过字节流写入文件
     * @param filePath 文件路径
     * @param bytes    byte[]
     * @param append   是否追加到结尾
     * @return `true` success, `false` fail
     */
    fun writeFileFromBytesByStream(filePath: String?, bytes: ByteArray?, append: Boolean): Boolean {
        return writeFileFromBytesByStream(FileUtils.getFileByPath(filePath), bytes, append)
    }
    /**
     * 通过字节流写入文件
     * @param file   文件
     * @param bytes  byte[]
     * @param append 是否追加到结尾
     * @return `true` success, `false` fail
     */
    /**
     * 通过字节流写入文件
     * @param file  文件
     * @param bytes byte[]
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun writeFileFromBytesByStream(
        file: File?,
        bytes: ByteArray?,
        append: Boolean = false
    ): Boolean {
        if (bytes == null || !FileUtils.createOrExistsFile(file)) return false
        var bos: BufferedOutputStream? = null
        return try {
            bos = BufferedOutputStream(FileOutputStream(file, append))
            bos.write(bytes)
            true
        } catch (e: IOException) {
            ALog.eTag(TAG, e, "writeFileFromBytesByStream")
            false
        } finally {
            CloseUtils.closeIOQuietly(bos)
        }
    }

    /**
     * 通过 FileChannel 把字节流写入文件
     * @param filePath 文件路径
     * @param bytes    byte[]
     * @param isForce  是否强制写入
     * @return `true` success, `false` fail
     */
    fun writeFileFromBytesByChannel(
        filePath: String?,
        bytes: ByteArray?,
        isForce: Boolean
    ): Boolean {
        return writeFileFromBytesByChannel(FileUtils.getFileByPath(filePath), bytes, false, isForce)
    }

    /**
     * 通过 FileChannel 把字节流写入文件
     * @param filePath 文件路径
     * @param bytes    byte[]
     * @param append   是否追加到结尾
     * @param isForce  是否强制写入
     * @return `true` success, `false` fail
     */
    fun writeFileFromBytesByChannel(
        filePath: String?,
        bytes: ByteArray?,
        append: Boolean,
        isForce: Boolean
    ): Boolean {
        return writeFileFromBytesByChannel(
            FileUtils.getFileByPath(filePath),
            bytes,
            append,
            isForce
        )
    }

    /**
     * 通过 FileChannel 把字节流写入文件
     * @param file    文件
     * @param bytes   byte[]
     * @param isForce 是否强制写入
     * @return `true` success, `false` fail
     */
    fun writeFileFromBytesByChannel(file: File?, bytes: ByteArray?, isForce: Boolean): Boolean {
        return writeFileFromBytesByChannel(file, bytes, false, isForce)
    }

    /**
     * 通过 FileChannel 把字节流写入文件
     * @param file    文件
     * @param bytes   byte[]
     * @param append  是否追加到结尾
     * @param isForce 是否强制写入
     * @return `true` success, `false` fail
     */
    fun writeFileFromBytesByChannel(
        file: File?,
        bytes: ByteArray?,
        append: Boolean,
        isForce: Boolean
    ): Boolean {
        if (bytes == null || !FileUtils.createOrExistsFile(file)) return false
        var fc: FileChannel? = null
        return try {
            fc = FileOutputStream(file, append).channel
            fc.position(fc.size())
            fc.write(ByteBuffer.wrap(bytes))
            if (isForce) fc.force(true)
            true
        } catch (e: IOException) {
            ALog.eTag(TAG, e, "writeFileFromBytesByChannel")
            false
        } finally {
            CloseUtils.closeIOQuietly(fc)
        }
    }

    /**
     * 通过 MappedByteBuffer 把字节流写入文件
     * @param filePath 文件路径
     * @param bytes    byte[]
     * @param isForce  是否强制写入
     * @return `true` success, `false` fail
     */
    fun writeFileFromBytesByMap(filePath: String?, bytes: ByteArray?, isForce: Boolean): Boolean {
        return writeFileFromBytesByMap(filePath, bytes, false, isForce)
    }

    /**
     * 通过 MappedByteBuffer 把字节流写入文件
     * @param filePath 文件路径
     * @param bytes    byte[]
     * @param append   是否追加到结尾
     * @param isForce  是否强制写入
     * @return `true` success, `false` fail
     */
    fun writeFileFromBytesByMap(
        filePath: String?,
        bytes: ByteArray?,
        append: Boolean,
        isForce: Boolean
    ): Boolean {
        return writeFileFromBytesByMap(FileUtils.getFileByPath(filePath), bytes, append, isForce)
    }

    /**
     * 通过 MappedByteBuffer 把字节流写入文件
     * @param file    文件
     * @param bytes   byte[]
     * @param isForce 是否强制写入
     * @return `true` success, `false` fail
     */
    fun writeFileFromBytesByMap(file: File?, bytes: ByteArray?, isForce: Boolean): Boolean {
        return writeFileFromBytesByMap(file, bytes, false, isForce)
    }

    /**
     * 通过 MappedByteBuffer 把字节流写入文件
     * @param file    文件
     * @param bytes   byte[]
     * @param append  是否追加到结尾
     * @param isForce 是否强制写入
     * @return `true` success, `false` fail
     */
    fun writeFileFromBytesByMap(
        file: File?,
        bytes: ByteArray?,
        append: Boolean,
        isForce: Boolean
    ): Boolean {
        if (bytes == null || !FileUtils.createOrExistsFile(file)) return false
        var fc: FileChannel? = null
        return try {
            fc = FileOutputStream(file, append).channel
            val mbb = fc.map(FileChannel.MapMode.READ_WRITE, fc.size(), bytes.size.toLong())
            mbb.put(bytes)
            if (isForce) mbb.force()
            true
        } catch (e: IOException) {
            ALog.eTag(TAG, e, "writeFileFromBytesByMap")
            false
        } finally {
            CloseUtils.closeIOQuietly(fc)
        }
    }

    /**
     * 通过字符串写入文件
     * @param filePath 文件路径
     * @param content  写入内容
     * @return `true` success, `false` fail
     */
    fun writeFileFromString(filePath: String?, content: String?): Boolean {
        return writeFileFromString(FileUtils.getFileByPath(filePath), content, false)
    }

    /**
     * 通过字符串写入文件
     * @param filePath 文件路径
     * @param content  写入内容
     * @param append   是否追加到结尾
     * @return `true` success, `false` fail
     */
    fun writeFileFromString(filePath: String?, content: String?, append: Boolean): Boolean {
        return writeFileFromString(FileUtils.getFileByPath(filePath), content, append)
    }
    /**
     * 通过字符串写入文件
     * @param file    文件
     * @param content 写入内容
     * @param append  是否追加到结尾
     * @return `true` success, `false` fail
     */
    /**
     * 通过字符串写入文件
     * @param file    文件
     * @param content 写入内容
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun writeFileFromString(file: File?, content: String?, append: Boolean = false): Boolean {
        if (content == null || !FileUtils.createOrExistsFile(file)) return false
        var bw: BufferedWriter? = null
        return try {
            bw = BufferedWriter(FileWriter(file, append))
            bw.write(content)
            true
        } catch (e: IOException) {
            ALog.eTag(TAG, e, "writeFileFromString")
            false
        } finally {
            CloseUtils.closeIOQuietly(bw)
        }
    }
    // ==============
    // = 读写分界线 =
    // ==============
    /**
     * 读取文件内容, 返回换行 List
     * @param filePath 文件路径
     * @return 换行 [<]
     */
    fun readFileToList(filePath: String?): List<String>? {
        return readFileToList(FileUtils.getFileByPath(filePath), null)
    }

    /**
     * 读取文件内容, 返回换行 List
     * @param filePath    文件路径
     * @param charsetName 字符编码
     * @return 换行 [<]
     */
    fun readFileToList(filePath: String?, charsetName: String?): List<String>? {
        return readFileToList(FileUtils.getFileByPath(filePath), charsetName)
    }

    /**
     * 读取文件内容, 返回换行 List
     * @param file        文件
     * @param charsetName 字符编码
     * @return 换行 [<]
     */
    fun readFileToList(file: File?, charsetName: String?): List<String>? {
        return readFileToList(file, 0, Int.MAX_VALUE, charsetName)
    }

    /**
     * 读取文件内容, 返回换行 List
     * @param filePath 文件路径
     * @param start    开始位置
     * @param end      结束位置
     * @return 换行 [<]
     */
    fun readFileToList(filePath: String?, start: Int, end: Int): List<String>? {
        return readFileToList(FileUtils.getFileByPath(filePath), start, end, null)
    }

    /**
     * 读取文件内容, 返回换行 List
     * @param filePath    文件路径
     * @param start       开始位置
     * @param end         结束位置
     * @param charsetName 字符编码
     * @return 换行 [<]
     */
    fun readFileToList(
        filePath: String?,
        start: Int,
        end: Int,
        charsetName: String?
    ): List<String>? {
        return readFileToList(FileUtils.getFileByPath(filePath), start, end, charsetName)
    }
    /**
     * 读取文件内容, 返回换行 List
     * @param file        文件
     * @param start       开始位置
     * @param end         结束位置
     * @param charsetName 字符编码
     * @return 换行 [<]
     */
    /**
     * 读取文件内容, 返回换行 List
     * @param file 文件
     * @return 换行 [<]
     */
    /**
     * 读取文件内容, 返回换行 List
     * @param file  文件
     * @param start 开始位置
     * @param end   结束位置
     * @return 换行 [<]
     */
    @JvmOverloads
    fun readFileToList(
        file: File?,
        start: Int = 0,
        end: Int = Int.MAX_VALUE,
        charsetName: String? = null
    ): List<String>? {
        if (!FileUtils.isFileExists(file)) return null
        if (start > end) return null
        var br: BufferedReader? = null
        return try {
            var line: String
            var curLine = 1
            val list: MutableList<String> = ArrayList()
            br = if (CommUtils.isEmpty(charsetName)) {
                BufferedReader(InputStreamReader(FileInputStream(file)))
            } else {
                BufferedReader(InputStreamReader(FileInputStream(file), charsetName))
            }
            while (br.readLine().also { line = it } != null) {
                if (curLine > end) break
                if (start <= curLine && curLine <= end) list.add(line)
                ++curLine
            }
            list
        } catch (e: IOException) {
            ALog.eTag(TAG, e, "readFileToList")
            null
        } finally {
            CloseUtils.closeIOQuietly(br)
        }
    }
    // =
    /**
     * 读取文件内容, 返回字符串
     * @param filePath 文件路径
     * @return 文件内容字符串
     */
    fun readFileToString(filePath: String?): String? {
        return readFileToString(FileUtils.getFileByPath(filePath), null)
    }

    /**
     * 读取文件内容, 返回字符串
     * @param filePath    文件路径
     * @param charsetName 字符编码
     * @return 文件内容字符串
     */
    fun readFileToString(filePath: String?, charsetName: String?): String? {
        return readFileToString(FileUtils.getFileByPath(filePath), charsetName)
    }
    /**
     * 读取文件内容, 返回字符串
     * @param file        文件
     * @param charsetName 字符编码
     * @return 文件内容字符串
     */
    /**
     * 读取文件内容, 返回字符串
     * @param file 文件
     * @return 文件内容字符串
     */
    @JvmOverloads
    fun readFileToString(file: File?, charsetName: String? = null): String? {
        if (!FileUtils.isFileExists(file)) return null
        var br: BufferedReader? = null
        return try {
            val builder = StringBuilder()
            br = if (CommUtils.isEmpty(charsetName)) {
                BufferedReader(InputStreamReader(FileInputStream(file)))
            } else {
                BufferedReader(InputStreamReader(FileInputStream(file), charsetName))
            }
            var line: String?
            if (br.readLine().also { line = it } != null) {
                builder.append(line)
                while (br.readLine().also { line = it } != null) {
                    builder.append(NEW_LINE_STR).append(line)
                }
            }
            builder.toString()
        } catch (e: IOException) {
            ALog.eTag(TAG, e, "readFileToString")
            null
        } finally {
            CloseUtils.closeIOQuietly(br)
        }
    }

    /**
     * 读取文件内容, 返回 byte[]
     * @param filePath 文件路径
     * @return 文件内容 byte[]
     */
    fun readFileToBytesByStream(filePath: String?): ByteArray? {
        return readFileToBytesByStream(FileUtils.getFileByPath(filePath))
    }

    /**
     * 读取文件内容, 返回 byte[]
     * @param file 文件
     * @return 文件内容 byte[]
     */
    fun readFileToBytesByStream(file: File?): ByteArray? {
        if (!FileUtils.isFileExists(file)) return null
        var fis: FileInputStream? = null
        var baos: ByteArrayOutputStream? = null
        return try {
            fis = FileInputStream(file)
            baos = ByteArrayOutputStream()
            val b = ByteArray(sBufferSize)
            var len: Int
            while (fis.read(b, 0, sBufferSize).also { len = it } != EOF) {
                baos.write(b, 0, len)
            }
            baos.toByteArray()
        } catch (e: IOException) {
            ALog.eTag(TAG, e, "readFileToBytesByStream")
            null
        } finally {
            CloseUtils.closeIOQuietly(fis, baos)
        }
    }

    /**
     * 通过 FileChannel, 读取文件内容, 返回 byte[]
     * @param filePath 文件路径
     * @return 文件内容 byte[]
     */
    fun readFileToBytesByChannel(filePath: String?): ByteArray? {
        return readFileToBytesByChannel(FileUtils.getFileByPath(filePath))
    }

    /**
     * 通过 FileChannel, 读取文件内容, 返回 byte[]
     * @param file 文件
     * @return 文件内容 byte[]
     */
    fun readFileToBytesByChannel(file: File?): ByteArray? {
        if (!FileUtils.isFileExists(file)) return null
        var fc: FileChannel? = null
        return try {
            fc = RandomAccessFile(file, "r").channel
            val byteBuffer = ByteBuffer.allocate(fc.size().toInt())
            while (true) {
                if (fc.read(byteBuffer) <= 0) break
            }
            byteBuffer.array()
        } catch (e: IOException) {
            ALog.eTag(TAG, e, "readFileToBytesByChannel")
            null
        } finally {
            CloseUtils.closeIOQuietly(fc)
        }
    }

    /**
     * 通过 MappedByteBuffer, 读取文件内容, 返回 byte[]
     * @param filePath 文件路径
     * @return 文件内容 byte[]
     */
    fun readFileToBytesByMap(filePath: String?): ByteArray? {
        return readFileToBytesByMap(FileUtils.getFileByPath(filePath))
    }

    /**
     * 通过 MappedByteBuffer, 读取文件内容, 返回 byte[]
     * @param file 文件
     * @return 文件内容 byte[]
     */
    fun readFileToBytesByMap(file: File?): ByteArray? {
        if (!FileUtils.isFileExists(file)) return null
        var fc: FileChannel? = null
        return try {
            fc = RandomAccessFile(file, "r").channel
            val size = fc.size().toInt()
            val mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size.toLong()).load()
            val result = ByteArray(size)
            mbb[result, 0, size]
            result
        } catch (e: IOException) {
            ALog.eTag(TAG, e, "readFileToBytesByMap")
            null
        } finally {
            CloseUtils.closeIOQuietly(fc)
        }
    }
    // =
    /**
     * 复制 InputStream 到 OutputStream
     * @param inputStream  [InputStream] 读取流
     * @param outputStream [OutputStream] 写入流
     * @return bytes number
     */
    fun copyLarge(inputStream: InputStream, outputStream: OutputStream): Long {
        try {
            val data = ByteArray(sBufferSize)
            var count: Long = 0
            var n: Int
            while (EOF != inputStream.read(data).also { n = it }) {
                outputStream.write(data, 0, n)
                count += n.toLong()
            }
            return count
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "copyLarge")
        } finally {
            CloseUtils.closeIOQuietly(inputStream, outputStream)
        }
        return -1
    }
}