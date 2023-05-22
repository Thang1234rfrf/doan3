package com.example.doan

import android.content.Context
import android.media.MediaPlayer
import java.io.IOException

class MediaPlayerManager private constructor(context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    init {
        // Khởi tạo MediaPlayer
        mediaPlayer = MediaPlayer()
    }

    fun playMusic(url: String) {
        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
    }

    companion object {
        private var instance: MediaPlayerManager? = null

        fun getInstance(context: Context): MediaPlayerManager {
            if (instance == null) {
                instance = MediaPlayerManager(context.applicationContext)
            }
            return instance as MediaPlayerManager
        }
    }
}

