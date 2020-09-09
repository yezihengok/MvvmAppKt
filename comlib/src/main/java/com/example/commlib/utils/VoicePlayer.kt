package com.example.commlib.utils

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.util.Log
import androidx.annotation.RawRes
import com.blankj.ALog
import com.example.commlib.api.App
import com.example.commlib.api.App.Companion.instance
import java.io.IOException

/**
 * 播放本地音频
 * Created by yzh on 2020/1/6 11:00.
 */
class VoicePlayer private constructor() : OnCompletionListener, OnPreparedListener {
    private var mediaPlayer: MediaPlayer? = null

    /**
     * 是否播放完毕
     */
    var isCompletion = false //是否播放完毕
        private set
    private var isPrepared = false
    private var mListener: Listener? = null
    fun setListener(listener: Listener?) {
        mListener = listener
    }

    private fun initPlayer() {
        if (mediaPlayer == null) {
            try {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.isLooping = false //是否循环
                mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer?.setOnCompletionListener(this)
                mediaPlayer?.setOnPreparedListener(this)
            } catch (e: Exception) {
                Log.e("mediaPlayer", "error", e)
            }
        }
    }

    /**
     * 播放raw目录下的音乐mp3文件
     * @param rawId R.raw.abc
     */
    fun playRawMusic(@RawRes rawId: Int) {
        //  mediaPlayer = MediaPlayer.create(PrimaryApplication.get(), rawId);//也可以直接这么创建
        initPlayer()
        val file = App.instance.resources.openRawResourceFd(rawId)
        try {
            mediaPlayer?.reset() //每次播放前最好reset一下
            mediaPlayer?.setDataSource(file.fileDescriptor, file.startOffset, file.length)
            mediaPlayer?.prepare()
            file.close()
            play()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private var mRawIds: MutableList<Int>? = null

    /**
     * 连续播放多个raw目录下的音乐mp3文件
     */
    fun playRawList(rawIds: MutableList<Int>?): VoicePlayer {
        if (CommUtils.isListNull(rawIds)) {
            return this
        }
        mRawIds = rawIds
        if(!mRawIds.isNullOrEmpty()){
            playRawMusic(mRawIds!![0])
        }

        return this
    }

    /**
     * 播放assets下的音乐mp3文件
     * @param fileName abc.mp3
     */
    fun playAssetMusic(fileName: String?) {
        initPlayer()
        try {
            //播放 assets/abc.mp3 音乐文件
            val file = App.instance.assets.openFd(fileName!!)
            //mediaPlayer = new MediaPlayer();
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(file.fileDescriptor, file.startOffset, file.length)
            mediaPlayer?.prepare()
            file.close()
            play()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 根据路径播放
     * @param videoUrl 资源文件名称   本地地址  和网络地址
     */
    fun playUrl(videoUrl: String?) {
        initPlayer()
        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(videoUrl)
            mediaPlayer?.prepare()
            play()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying ?:false

    /**
     * 播放
     */
    fun play() {
        if (mediaPlayer != null && !isPlaying) {
            isCompletion = false
            // mediaPlayer.prepare();
            mediaPlayer?.start()
        }
    }

    /**
     * 暂停
     */
    fun pause() {
       // if (mediaPlayer?.isPlaying) {
            mediaPlayer?.pause()
        //}
    }

    /**
     * 释放资源
     */
    fun destory() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            if (CommUtils.isListNotNull(mRawIds)) {
                mRawIds?.clear()
            }
        }
    }

    /**
     * 重播
     */
    fun replay() {
        if (mediaPlayer != null) {
            mediaPlayer?.seekTo(0)
        }
    }

    override fun onPrepared(arg0: MediaPlayer) {
        isPrepared = true
    }

    override fun onCompletion(arg0: MediaPlayer) {
        isCompletion = true

        //如果存在需要连续播放的资源
        if (CommUtils.isListNotNull(mRawIds)) {
            mRawIds?.removeAt(0)
            if (CommUtils.isListNotNull(mRawIds)) {
                ALog.v("播放完一个移除一个，移除了还有继续播放")
                playRawMusic(mRawIds!![0])
            } else {
                destory()
                if (mListener != null) {
                    mListener?.onCompletion()
                }
                ALog.v("播放完毕")
            }
        } else {
            destory()
            if (mListener != null) {
                mListener?.onCompletion()
            }
            ALog.v("播放完毕")
        }
    }

    interface Listener {
        fun onCompletion()
    }

    companion object {
        @JvmStatic
        val instance: VoicePlayer by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { VoicePlayer() }

    }

    init {
        initPlayer()
    }
}