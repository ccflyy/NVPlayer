package com.nesp.nvplayerdemo

import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nesp.nvplayer.NVPlayer
import com.nesp.nvplayer.model.NEpisode
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    var videoPlayer: NVPlayer? = null
    var orientationUtils: OrientationUtils? = null

    val videoUrl: String = "https://youku.g9mi6.com/xunlong3.m3u8?sign=223b73dce679fecd6b17de621a10f6ef&t=5ca8c324"
    val videoLocalUrl: String = "file:///" + Environment.getExternalStorageDirectory().absolutePath + "/sample.mp4";
    var isLocal: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
                , android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 0
        )

        initPlayer()


        videoPlayer?.play()
    }

    override fun onPostResume() {
        super.onPostResume()
    }

    private var isPlay: Boolean = false

    fun initPlayer() {
        videoPlayer = findViewById(R.id.activity_main_player)

        videoPlayer?.setOnEpisodeItemClickListener { episode, _ ->
            Log.e("CClick", "click ${episode.name}")
            Toast.makeText(this@MainActivity, "click ${episode.name}", Toast.LENGTH_LONG).show()
        }
        orientationUtils = OrientationUtils(this, videoPlayer)
        orientationUtils?.isEnable = true
        videoPlayer?.setActivityFinish(object : NVPlayer.ActivityFinish {
            override fun finish() {
                finish()
            }
        })

        videoPlayer

        val gsyVideoOption = GSYVideoOptionBuilder()
        gsyVideoOption.setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setAutoFullWithSize(true)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setUrl(if (isLocal) videoLocalUrl else videoUrl)
            .setCacheWithPlay(false)
            .setVideoTitle("测试视频")
            .setVideoAllCallBack(object : GSYSampleCallBack() {

                override fun onAutoComplete(url: String?, vararg objects: Any?) {
                    super.onAutoComplete(url, *objects)
                    videoPlayer?.play()
                }

                override fun onPrepared(url: String?, vararg objects: Any?) {
                    Debuger.printfError("***** onPrepared **** " + objects[0])
                    Debuger.printfError("***** onPrepared **** " + objects[1])
                    super.onPrepared(url, *objects)
                    //开始播放了才能旋转和全屏
                    orientationUtils?.isEnable = true
                    isPlay = true
                    isPause = false
                }

                override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                    super.onQuitFullscreen(url, *objects)
                    Debuger.printfError("***** onQuitFullscreen **** " + objects[0])//title
                    Debuger.printfError("***** onQuitFullscreen **** " + objects[1])//当前非全屏player
                    if (orientationUtils != null) {
                        orientationUtils?.backToProtVideo()
                    }
                }

            }).setLockClickListener { view, lock ->
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils?.isEnable = !lock
                }
            }.build(videoPlayer)

        videoPlayer?.fullscreenButton?.setOnClickListener {
            //直接横屏
            orientationUtils?.resolveByClick()

            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            videoPlayer?.startWindowFullscreen(this@MainActivity, true, true)


            val nEpisodes = ArrayList<NEpisode>()
            for (i in 0..79) {
                nEpisodes.add(NEpisode("name$i", "url"))
            }

            videoPlayer?.setnEpisodeList(nEpisodes)
        }
    }


    override fun onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils?.backToProtVideo()
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }

    private var isPause: Boolean = false

    override fun onPause() {
        videoPlayer?.getCurrentPlayer()?.onVideoPause()
        super.onPause()
        isPause = true
    }

    override fun onResume() {
        videoPlayer?.getCurrentPlayer()?.onVideoResume(false)
        super.onResume()
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            videoPlayer?.currentPlayer?.release()
        }
        orientationUtils?.releaseListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            videoPlayer?.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

}

