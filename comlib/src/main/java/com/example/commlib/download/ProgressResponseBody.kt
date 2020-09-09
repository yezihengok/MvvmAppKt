package com.example.commlib.download

 
import com.example.commlib.rx.RxBus
import com.example.commlib.rx.RxBusCode
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class ProgressResponseBody @JvmOverloads constructor(var responseBody: ResponseBody?, tag: String?="") : ResponseBody() {
 //   private var responseBody: ResponseBody?
    private var bufferedSource: BufferedSource? = null
    private var tag: String? = null

//    constructor(responseBody: ResponseBody?) {
//        this.responseBody = responseBody
//    }
//
//    constructor(responseBody: ResponseBody?, tag: String?) {
//        this.responseBody = responseBody
//        this.tag = tag
//    }

    override fun contentType(): MediaType? {
        return responseBody?.contentType()
    }

    override fun contentLength(): Long {
        return responseBody?.contentLength()?:0
    }

    override fun source(): BufferedSource? {
        if (bufferedSource == null&&responseBody!=null) {
            bufferedSource = Okio.buffer(source(responseBody!!.source()))
        }
        return bufferedSource
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var bytesReaded: Long = 0
            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                bytesReaded += if (bytesRead == -1L) 0 else bytesRead
                //使用RxBus的方式，实时发送当前已读取(上传/下载)的字节数据
                RxBus.instance.post(RxBusCode.TYPE_2,
                    DownLoadStateBean(contentLength(), bytesReaded, tag)
                )
                return bytesRead
            }
        }
    }
}