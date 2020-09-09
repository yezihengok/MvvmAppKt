package com.example.commlib.download

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


class DownLoadStateBean : Serializable, Parcelable {
    var total //  文件总大小
            : Long
    var bytesLoaded //已加载文件的大小
            : Long
    var tag // 多任务下载时的一个标记
            : String? = null

    constructor(total: Long, bytesLoaded: Long) {
        this.total = total
        this.bytesLoaded = bytesLoaded
    }

    constructor(total: Long, bytesLoaded: Long, tag: String?) {
        this.total = total
        this.bytesLoaded = bytesLoaded
        this.tag = tag
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(total)
        dest.writeLong(bytesLoaded)
        dest.writeString(tag)
    }

    protected constructor(`in`: Parcel) {
        total = `in`.readLong()
        bytesLoaded = `in`.readLong()
        tag = `in`.readString()
    }

    companion object {
        val CREATOR: Parcelable.Creator<DownLoadStateBean?> =
            object : Parcelable.Creator<DownLoadStateBean?> {
                override fun createFromParcel(source: Parcel): DownLoadStateBean? {
                    return DownLoadStateBean(source)
                }

                override fun newArray(size: Int): Array<DownLoadStateBean?> {
                    return arrayOfNulls(size)
                }
            }
    }
}