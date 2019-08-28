/*
 *
 *   Copyright (c) 2019  NESP Technology Corporation. All rights reserved.
 *
 *   This program is free software; you can redistribute it and/or modify it
 *   under the terms and conditions of the GNU General Public License,
 *   version 2, as published by the Free Software Foundation.
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License.See the License for the specific language governing permission and
 *   limitations under the License.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
 */

package com.nesp.nvplayer.cling.dialog

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import com.nesp.nvplayer.R
import com.nesp.sdk.android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


/**
 *
 *
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-6 下午6:04
 * @project FishMovie
 **/
class ControlDialogFragment(private var iControlDialog: IControlDialog) : BaseDialog() {

    private val TAG = "ControlDialogFragment"

    var isMuting: Boolean = false
    var isPlaying = false

    private var deviceName = ""
    private var videoName = ""

    private var firstClickTouchPanelTime = 0L
    private var secondClickTouchPanelTime = 0L

    /*********************************View*************************************/
    private lateinit var ivClose: AppCompatImageView

    private lateinit var tvDeviceName: TextView
    private lateinit var ivPlayPauseShow: AppCompatImageView
    private lateinit var tvPlayTime: TextView
    private lateinit var tvPlayName: TextView
    private lateinit var pbPlayProgress: ProgressBar

    private lateinit var cbChangeDevice: AppCompatCheckBox
    private lateinit var cbSelectEpisode: AppCompatCheckBox
    private lateinit var cbMute: AppCompatCheckBox

    private lateinit var ivVolumeAdd: AppCompatImageView
    private lateinit var ivVolumeReduce: AppCompatImageView
    private lateinit var ivPlayAdd: AppCompatImageView
    private lateinit var ivPlayReduce: AppCompatImageView
    private lateinit var ivPlayPause: AppCompatImageView

    private lateinit var flControlTypeRemote: FrameLayout
    private lateinit var flControlTypeTouch: FrameLayout
    private lateinit var ivChangeControlType: ImageView
    private lateinit var cvControlPanel: CardView
    private lateinit var ivHelp: ImageView

    private var isCanPerformControlClick = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        contentView = inflater.inflate(R.layout.nvplayer_cling_dialog_device_control, container, false)

        ivClose = findView(R.id.nvplayer_cling_dialog_device_control_iv_close)
        ivClose.setOnClickListener { iControlDialog.OnControlCloseClick() }

        tvDeviceName = findView(R.id.nvplayer_cling_dialog_device_control_tv_device_name)
        setDeviceName(deviceName)

        ivPlayPauseShow = findView(R.id.nvplayer_cling_dialog_device_control_iv_play_pause_show)
        tvPlayTime = findView(R.id.nvplayer_cling_dialog_device_control_tv_play_time)
        tvPlayName = findView(R.id.nvplayer_cling_dialog_device_control_tv_play_name)
        setPlayName(videoName)

        pbPlayProgress = findView(R.id.nvplayer_cling_dialog_device_control_pb_play_progress)

        cbChangeDevice = findView(R.id.nvplayer_cling_dialog_device_control_cb_change_device)
        cbChangeDevice.setOnClickListener { iControlDialog.OnControlChangeDeviceClick() }
        cbSelectEpisode = findView(R.id.nvplayer_cling_dialog_device_control_cb_select_episode)
        cbSelectEpisode.setOnClickListener { iControlDialog.OnControlSelectEpisodeClick() }

        cbMute = findView(R.id.nvplayer_cling_dialog_device_control_cb_mute)
        cbMute.setOnClickListener {
            if (isCanPerformControlClick) {
                iControlDialog.OnControlMuteClick(!isMuting)
                startPerformControlClick()
            } else {
                showShortToast("您点击的太频繁了，慢点来")
            }
        }

        ivVolumeAdd = findView(R.id.nvplayer_cling_dialog_device_control_iv_volume_add)
        ivVolumeAdd.setOnClickListener {
            if (isCanPerformControlClick) {
                iControlDialog.OnControlVolumeAddClick()
                startPerformControlClick()
            } else {
                showShortToast("您点击的太频繁了，慢点来")
            }
        }

        ivVolumeReduce = findView(R.id.nvplayer_cling_dialog_device_control_iv_volume_reduce)
        ivVolumeReduce.setOnClickListener {
            if (isCanPerformControlClick) {
                iControlDialog.OnControlVolumeReduceClick()
                startPerformControlClick()
            } else {
                showShortToast("您点击的太频繁了，慢点来")
            }
        }

        ivPlayAdd = findView(R.id.nvplayer_cling_dialog_device_control_iv_play_add)
        ivPlayAdd.setOnClickListener {
            if (isCanPerformControlClick) {
                iControlDialog.OnControlSeekAddClick()
                startPerformControlClick()
            } else {
                showShortToast("您点击的太频繁了，慢点来")
            }
        }
        ivPlayReduce = findView(R.id.nvplayer_cling_dialog_device_control_iv_play_reduce)
        ivPlayReduce.setOnClickListener {
            if (isCanPerformControlClick) {
                iControlDialog.OnControlSeekReduceClick()
                startPerformControlClick()
            } else {
                showShortToast("您点击的太频繁了，慢点来")
            }
        }
        ivPlayPause = findView(R.id.nvplayer_cling_dialog_device_control_iv_play_pause)
        ivPlayPause.setOnClickListener {
            if (isCanPerformControlClick) {
                iControlDialog.OnControlPlayPauseClick(!isPlaying)
                startPerformControlClick()
            } else {
                showShortToast("您点击的太频繁了，慢点来")
            }
        }

        flControlTypeRemote = findView(R.id.nvplayer_cling_dialog_device_control_fl_control_type_remote)

        flControlTypeTouch = findView(R.id.nvplayer_cling_dialog_device_control_fl_control_type_touch)

        ivChangeControlType = findView(R.id.nvplayer_cling_dialog_device_control_iv_change_control_type)
        ivChangeControlType.setOnClickListener {
            if (controlTypeIsRemote) {
                onChangeControlTypeToTouch()
            } else {
                onChangeControlTypeToRemote()
            }
        }

        ivHelp = findView(R.id.nvplayer_cling_dialog_device_control_iv_help)
        ivHelp.setOnClickListener {
            iControlDialog.OnControlHelpClick()
        }

        cvControlPanel = findView(R.id.nvplayer_cling_dialog_device_control_cv_control_panel)

        var touchDownPointY = 0f
        var touchDownPointX = 0f

        var touchCurrentPointY: Float
        var touchCurrentPointX: Float

        var touchDistanceX: Float//<0,左滑,reduceSeek
        var touchDistanceY: Float//<0,上滑,addVolume

        val TOUCH_MIN_SMOOTH_DISTANCE_VOLUME = 260f//最小滑动距离
        val TOUCH_MIN_SMOOTH_DISTANCE_SEEK = 50f//最小滑动距离
        val TOUCH_MAX_CLICK_DISTANCE = 0f//最点击最大距离范围
        val TOUCH_MOVE_DISTANCE_VOLUME_VALUE_RATE = 100f//移动的距离与音量值的变化量的比值
        val TOUCH_MOVE_DISTANCE_SEEK_VALUE_RATE = 0.002f//移动的距离与进度值的变化量的比值

        cvControlPanel.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        touchDownPointX = event.x
                        touchDownPointY = event.y
//                        Log.e(TAG, "ControlDialogFragment.onTouch_DOWN: " +
//                                "touchDownPointX=$touchDownPointX\n" +
//                                "touchDownPointY=$touchDownPointY\n"
//                        )
                    }

                    MotionEvent.ACTION_MOVE -> {
                        touchCurrentPointX = event.x
                        touchCurrentPointY = event.y
//                        Log.e(TAG, "ControlDialogFragment.onTouch_MOVE:\n" +
//                                "touchCurrentPointX=$touchCurrentPointX\n" +
//                                "touchCurrentPointY=$touchCurrentPointY\n"
//                        )
                    }

                    MotionEvent.ACTION_UP -> {
                        touchCurrentPointX = event.x
                        touchCurrentPointY = event.y

                        touchDistanceX = touchCurrentPointX - touchDownPointX
                        touchDistanceY = touchCurrentPointY - touchDownPointY

                        Log.e(TAG, "ControlDialogFragment.onTouch_UP:\n" +
                                "touchCurrentPointX=$touchCurrentPointX\n" +
                                "touchCurrentPointY=$touchCurrentPointY\n" +
                                "touchDistanceX=$touchDistanceX\n" +
                                "touchDistanceY=$touchDistanceY\n" +
                                "")
                        if (abs(touchDistanceY) > TOUCH_MIN_SMOOTH_DISTANCE_VOLUME) {
                            if (isCanPerformControlClick) {
                                Log.e(TAG, "ControlDialogFragment.onTouch_UP:Volume \n" +
                                        "int=${(-touchDistanceY / TOUCH_MOVE_DISTANCE_VOLUME_VALUE_RATE).toInt()}\n " +
                                        "origin= ${(-touchDistanceY / TOUCH_MOVE_DISTANCE_VOLUME_VALUE_RATE)}" +
                                        "")
                                iControlDialog.OnControlTouchSetVolume((-touchDistanceY / TOUCH_MOVE_DISTANCE_VOLUME_VALUE_RATE).toInt())
                                startPerformControlClick()
                            } else {
                                showShortToast("您滑动的太频繁了，慢点来")
                            }
                        } else if (abs(touchDistanceX) > TOUCH_MIN_SMOOTH_DISTANCE_SEEK) {
                            if (isCanPerformControlClick) {
                                Log.e(TAG, "ControlDialogFragment.onTouch_UP:Seek\n" +
                                        "long=${(touchDistanceX / TOUCH_MOVE_DISTANCE_SEEK_VALUE_RATE).toLong()}\n" +
                                        "origin=${touchDistanceX / TOUCH_MOVE_DISTANCE_SEEK_VALUE_RATE}\n" +
                                        "sec=${(touchDistanceX / TOUCH_MOVE_DISTANCE_SEEK_VALUE_RATE).toLong() / 1000}\n" +
                                        "min=${(touchDistanceX / TOUCH_MOVE_DISTANCE_SEEK_VALUE_RATE).toLong() / 60000}\n" +
                                        ""
                                )
                                iControlDialog.OnControlTouchSetSeek((touchDistanceX / TOUCH_MOVE_DISTANCE_SEEK_VALUE_RATE).toLong())
                                startPerformControlClick()
                            } else {
                                showShortToast("您滑动的太频繁了，慢点来")
                            }
                        }


                        return if (abs(touchDistanceX) <= TOUCH_MAX_CLICK_DISTANCE
                                && abs(touchDistanceY) <= TOUCH_MAX_CLICK_DISTANCE) {
                            v?.performClick()
                            false
                        } else {
                            true
                        }
                    }
                }
                return true
            }
        })
        //双击暂停或播放
        cvControlPanel.setOnClickListener {
            secondClickTouchPanelTime = System.currentTimeMillis()
            if (secondClickTouchPanelTime - firstClickTouchPanelTime > 2000) {
                firstClickTouchPanelTime = secondClickTouchPanelTime
            } else {
                if (isCanPerformControlClick) {
                    iControlDialog.OnControlTouchDoubleClick(!isPlaying)
                    startPerformControlClick()
                } else {
                    showShortToast("您点击的太频繁了，慢点来")
                }
            }
        }

        return contentView
    }


    /**
     * true:Remote,false:Touch
     */
    private var controlTypeIsRemote = true

    private fun onChangeControlTypeToRemote() {
        controlTypeIsRemote = true
        ivChangeControlType.setImageDrawable(getDrawable(context!!, R.drawable.ic_nvplayer_cling_control_type_touch))
        flControlTypeRemote.visibility = View.VISIBLE
        flControlTypeTouch.visibility = View.GONE
    }

    private fun onChangeControlTypeToTouch() {
        ivChangeControlType.setImageDrawable(getDrawable(context!!, R.drawable.ic_nvplayer_cling_control_type_remote))
        controlTypeIsRemote = false
        flControlTypeRemote.visibility = View.GONE
        flControlTypeTouch.visibility = View.VISIBLE
    }

    fun setDeviceName(name: String) {
        tvDeviceName.text = name
    }

    fun setPlayName(name: String) {
        tvPlayName.text = name
    }

    fun setPlayTime(realTime: String, duration: String) {
        tvPlayTime.text = "$realTime/$duration"
    }

    fun setPlayProgress(process: Int) {
        pbPlayProgress.progress = process
    }

    override fun onShown(dialog: DialogInterface) {
        super.onShown(dialog)
        isShown = true
        iControlDialog.onDialogShown(dialog)
        iControlDialog.OnControlDialogShown(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        isShown = false
        iControlDialog.onDialogDismiss(dialog)
        iControlDialog.OnControlDialogDismiss(dialog)
    }

    fun onTransitioning() {

    }

    fun onStartPlay() {
        isPlaying = true
        try {
            ivPlayPauseShow.setImageDrawable(getDrawable(context!!, R.drawable.ic_nvplayer_cling_state_play))
            ivPlayPause.setImageDrawable(getDrawable(context!!, R.drawable.ic_nvplayer_cling_control_pause))
        } catch (e: Exception) {
        }

    }

    fun onPausePlay() {
        isPlaying = false
        try {
            ivPlayPauseShow.setImageDrawable(getDrawable(context!!, R.drawable.ic_nvplayer_cling_state_pause))
            ivPlayPause.setImageDrawable(getDrawable(context!!, R.drawable.ic_nvplayer_cling_control_play))
        } catch (e: Exception) {
        }
    }

    fun onStopPlay() {
        isPlaying = false
        try {
            ivPlayPauseShow.setImageDrawable(getDrawable(context!!, R.drawable.ic_nvplayer_cling_state_stop))
            ivPlayPause.setImageDrawable(getDrawable(context!!, R.drawable.ic_nvplayer_cling_control_play))
        } catch (e: Exception) {
        }

    }

    fun onMute() {
        isMuting = true
        cbMute.text = "取消静音"
    }

    fun onUnMute() {
        isMuting = false
        cbMute.text = "静音"
    }

    fun show(manager: FragmentManager, tag: String?, deviceName: String, videoName: String) {
        this.deviceName = deviceName
        this.videoName = videoName
        show(manager, tag)
    }

    private fun startPerformControlClick() {
        GlobalScope.launch { startPerformControlClickExec() }

        //        threadCanPerformControlClick = Thread(Runnable {
//            isCanPerformControlClick = false
//            try {
//                Thread.sleep(1000)
//            } catch (e: Exception) {
//                return@Runnable
//            }
//            isCanPerformControlClick = true
//        })
//
//        threadCanPerformControlClick?.start()
    }

    private suspend fun startPerformControlClickExec() {
        isCanPerformControlClick = false
        delay(500L)
        isCanPerformControlClick = true
    }
}