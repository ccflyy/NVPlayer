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

package com.nesp.nvplayer.cling

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import com.nesp.android.cling.Intents.*
import com.nesp.android.cling.control.ClingPlayControl
import com.nesp.android.cling.control.callback.ControlCallback
import com.nesp.android.cling.control.callback.ControlReceiveCallback
import com.nesp.android.cling.entity.*
import com.nesp.android.cling.entity.DLANPlayState.*
import com.nesp.android.cling.listener.BrowseRegistryListener
import com.nesp.android.cling.listener.DeviceListChangedListener
import com.nesp.android.cling.service.ClingUpnpService
import com.nesp.android.cling.service.manager.ClingManager
import com.nesp.android.cling.service.manager.DeviceManager
import com.nesp.movie.engine.entity.VideoType
import com.nesp.movie.engine.video.OnParseVideoPlayUrlListener
import com.nesp.movie.engine.video.manager.utils.ParserPlayUrl
import com.nesp.sdk.android.widget.Toast.showShortToast
import org.fourthline.cling.support.model.PositionInfo

/**
 *
 *
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-5 上午8:37
 * @project FishMovie
 **/
class ClingControlManagerImpl(private val hostActivity: Activity, var iClingViewManager: IClingViewManager) : IClingControlManager {

    private var threadListener: Thread? = null
    private var parserPlayUrl: ParserPlayUrl? = null
    private val TAG = "ClingControlManagerImpl"
    private var clingPlayControl: ClingPlayControl = ClingPlayControl()
    private var transportStateBroadcastReceiver: BroadcastReceiver? = null
    /** 用于监听发现设备  */
    private val browseRegistryListener = BrowseRegistryListener()

    private val playControlCallback = object : ControlCallback<Any> {

        override fun success(response: IResponse<Any>) {
            Log.e(TAG, "play success")
//                                        ClingUpnpServiceeManager.newInstance().subscribeMediaRender();
//                                        getPositionInfofo();
            // TODO: 17/7/21 play success
            ClingManager.getInstance().registerAVTransport(hostActivity)
            ClingManager.getInstance().registerRenderingControl(hostActivity)
            handler.sendEmptyMessage(ClingWorkState.PLAY_SUCCESS)
        }

        override fun fail(response: IResponse<Any>) {
            Log.e(TAG, "play fail")
            handler.sendEmptyMessage(ClingWorkState.PLAY_FAILED)
        }
    }

    private val continuePlayControlCallBack = object : ControlCallback<Any> {
        override fun success(response: IResponse<Any>) {
            Log.e(TAG, "play success")
            handler.sendEmptyMessage(ClingWorkState.PLAY_SUCCESS)
        }

        override fun fail(response: IResponse<Any>) {
            Log.e(TAG, "play fail")
            handler.sendEmptyMessage(ClingWorkState.PLAY_FAILED)
        }
    }

    private val upnpServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e(TAG, "ClingControlManagerImpl.upnpServiceConnection.onServiceConnected: ")

            val binder = service as ClingUpnpService.LocalBinder
            val beyondUpnpService = binder.service

            val clingUpnpServiceManager = ClingManager.getInstance()
            clingUpnpServiceManager.setUpnpService(beyondUpnpService)
            clingUpnpServiceManager.setDeviceManager(DeviceManager())

            clingUpnpServiceManager.registry.addListener(browseRegistryListener)
            //Search on service created.
            clingUpnpServiceManager.searchDevices()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG, "ClingControlManagerImpl.upnpServiceConnection.onServiceDisconnected: ")
            ClingManager.getInstance().setUpnpService(null)
        }

    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                ClingWorkState.TRANSITIONING -> {
                    iClingViewManager.onTransitioning()
                }

                ClingWorkState.START_PLAY -> {
                    if (clingPlayControl.currentState == PAUSE) {
                        iClingViewManager.onContinuePlay()
                    } else {
                        if (isNeedShownStartPlayTip) {
                            isNeedShownStartPlayTip = false
                            iClingViewManager.onStartPlay()
                        }
                    }
                    clingPlayControl.currentState = PLAY
                }

                ClingWorkState.PLAY_SUCCESS -> iClingViewManager.onPlaySuccess()

                ClingWorkState.PLAY_FAILED -> iClingViewManager.onPlayFailed()

                ClingWorkState.PAUSED -> {
                    clingPlayControl.currentState = PAUSE
                    iClingViewManager.onPaused()
                }

                ClingWorkState.PAUSE_FAILED -> iClingViewManager.onPauseFailed()

                ClingWorkState.STOPPED -> {
                    clingPlayControl.currentState = STOP
                    iClingViewManager.onStopped()
                }

                ClingWorkState.STOP_FAILED -> iClingViewManager.onStopFailed()

                ClingWorkState.SET_MUTE_SUCCESS -> iClingViewManager.onSetMuteSuccess()

                ClingWorkState.SET_MUTE_FAILED -> iClingViewManager.onSetMuteFailed()

                ClingWorkState.SET_VOLUME_SUCCESS -> iClingViewManager.onSetVolumeSuccess()

                ClingWorkState.SET_VOLUME_FAILED -> iClingViewManager.onSetVolumeFailed()

                ClingWorkState.SET_SEEK_SUCCESS -> iClingViewManager.onSetSeekSuccess()

                ClingWorkState.SET_SEEK_FAILED -> iClingViewManager.onSetSeekFailed()

                ClingWorkState.UNKNOWN_ERROR -> iClingViewManager.onUnKnownError()

                ClingWorkState.PLAY_POSITION_CALLBACK -> {
                    val positionInfo = msg.obj as PositionInfo
                    currentPlayDuration = positionInfo.trackDurationSeconds * 1000
                    currentPlayPosition = positionInfo.trackElapsedSeconds * 1000

                    iClingViewManager.onPlayPositionChange(positionInfo.relTime, positionInfo.trackDuration, positionInfo.elapsedPercent)

                    iClingViewManager.onPlayPositionChange(currentPlayPosition, currentPlayDuration, positionInfo.elapsedPercent)
                }

                ClingWorkState.PLAY_VOLUME_CALLBACK -> iClingViewManager.onPlayVolumeChange(msg.arg1)

                ClingWorkState.CONTINUE_PLAY -> iClingViewManager.onContinuePlay()
            }
        }
    }

    private var isInitted = false
    private var currentVolume: Int = -1
    private var isVolumeChangeSingleNum = true
    private val VOLUME_SINGLE_CHANGE_NUM = 3
    private val VOLUME_DOUBLE_CHANGE_NUM = 2

    private var currentPlayPosition = -1L
    private var currentPlayDuration = -1L
    private val SEEK_CHANGE_NUM = 15 * 1000L

    override fun init() {
        isInitted = true
        initListeners()
        bindServices()
        registerReceivers()
    }

    private var isNeedShownStartPlayTip = true
    override fun onControlDialogShown() {
        threadListener = Thread(Runnable {
            Looper.prepare()
            while (true) {
                clingPlayControl.getPositionInfo(object : ControlReceiveCallback {
                    override fun receive(response: IResponse<*>?) {
                        val message = Message()
                        message.what = ClingWorkState.PLAY_POSITION_CALLBACK
                        message.obj = (response as ClingPositionResponse).response
                        handler.sendMessage(message)
                    }

                    override fun success(response: IResponse<*>?) {

                    }

                    override fun fail(response: IResponse<*>?) {

                    }
                })
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    return@Runnable
                }
            }
        })
        threadListener?.start()
    }

    private fun registerReceivers() {
        //Register play status broadcast
        transportStateBroadcastReceiver = TransportStateBroadcastReceiver()
        val filter = IntentFilter()
        filter.addAction(ACTION_PLAYING)
        filter.addAction(ACTION_PAUSED_PLAYBACK)
        filter.addAction(ACTION_STOPPED)
        filter.addAction(ACTION_TRANSITIONING)

        filter.addAction(ACTION_POSITION_CALLBACK)
        filter.addAction(ACTION_VOLUME_CALLBACK)

        hostActivity.registerReceiver(transportStateBroadcastReceiver, filter)
    }

    private fun bindServices() {
        // Bind UPnP service
        val upnpServiceIntent = Intent(hostActivity, ClingUpnpService::class.java)
        hostActivity.bindService(upnpServiceIntent, upnpServiceConnection, Context.BIND_AUTO_CREATE)
        // Bind System service
        //        Intent systemServiceIntent = new Intent(MainActivity.this, SystemService.class);
        //        bindService(systemServiceIntent, mSystemServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private fun initListeners() {

        // 设置发现设备监听
        browseRegistryListener.setOnDeviceListChangedListener(object : DeviceListChangedListener {
            override fun onDeviceAdded(device: IDevice<Any>) {
                hostActivity.runOnUiThread { iClingViewManager.onDeviceAdded(device) }

            }

            override fun onDeviceRemoved(device: IDevice<Any>) {
                hostActivity.runOnUiThread { iClingViewManager.onDeviceRemoved(device) }
            }
        })
    }

    override fun changeDevice() {

    }

    private var currentPlayUrl = ""

    override fun startPlay(currentPlayUrl: String) {
        @DLANPlayStates val currentState = clingPlayControl.currentState

        /**
         * 通过判断状态 来决定 是继续播放 还是重新播放
         */
        if (currentState == STOP || this.currentPlayUrl != currentPlayUrl) {
            startPlayWithParse(currentPlayUrl)
        } else {
            clingPlayControl.play(continuePlayControlCallBack)
        }

    }

    private fun startPlayWithParse(currentPlayUrl: String) {
        this.currentPlayUrl = currentPlayUrl
        parserPlayUrl = ParserPlayUrl(hostActivity)
                .setShowFullWindowLoadingDialog(false)
                .setShowLoadingDialog(true)
                .setPlayUrl(currentPlayUrl)
                .setOnParserVideoPlayUrlListener(object : OnParseVideoPlayUrlListener {
                    override fun onSuccess(videoPlayUrl: String?, videoType: Int) {
                        if (videoPlayUrl?.isNotEmpty() == true) {
                            clingPlayControl.playNew(videoPlayUrl, playControlCallback)
                        } else {
                            showShortToast(hostActivity, "视频资源获取失败")
                        }
                    }

                    override fun onFailed(error: String) {
                        showShortToast(hostActivity, "视频资源获取失败")
                    }
                })
        parserPlayUrl?.exec("")
    }

    override fun pausePlay() {
        clingPlayControl.pause(object : ControlCallback<Any> {

            override fun success(response: IResponse<Any>) {
                Log.e(TAG, "pause success")
                handler.sendEmptyMessage(ClingWorkState.PAUSED)
            }

            override fun fail(response: IResponse<Any>) {
                Log.e(TAG, "pause fail")
                handler.sendEmptyMessage(ClingWorkState.PAUSE_FAILED)
            }
        })
    }

    override fun stopPlay() {
        clingPlayControl.stop(object : ControlCallback<Any> {
            override fun success(response: IResponse<Any>) {
                Log.e(TAG, "stop success")
                handler.sendEmptyMessage(ClingWorkState.STOPPED)

            }

            override fun fail(response: IResponse<Any>) {
                Log.e(TAG, "stop fail")
                handler.sendEmptyMessage(ClingWorkState.STOP_FAILED)
            }
        })
    }

    override fun setMute(isMute: Boolean) {
        clingPlayControl.getVolume(object : ControlReceiveCallback {

            override fun success(response: IResponse<Any>?) {

            }

            override fun receive(response: IResponse<Any>) {
                Log.e(TAG, "ClingControlManagerImpl.receive: " + (response as ClingVolumeResponse).response)
                Log.e(TAG, "setMute success")
                setVolume(if (isMute) {
                    currentVolume = (response as ClingVolumeResponse).response
                    0
                } else {
                    currentVolume
                })
                handler.sendEmptyMessage(ClingWorkState.SET_MUTE_SUCCESS)
            }

            override fun fail(response: IResponse<Any>) {
                Log.e(TAG, "setMute fail")
                handler.sendEmptyMessage(ClingWorkState.SET_MUTE_FAILED)
            }
        })
//        clingPlayControl.setMute(isMute, object : ControlCallback<Any> {
//            override fun success(response: IResponse<Any>) {
//                Log.e(TAG, "setMute success")
//                handler.sendEmptyMessage(ClingWorkState.SET_MUTE_SUCCESS)
//            }
//
//            override fun fail(response: IResponse<Any>) {
//                Log.e(TAG, "setMute fail")
//                handler.sendEmptyMessage(ClingWorkState.SET_MUTE_FAILED)
//            }
//        })
    }

    override fun setVolume(value: Int) {
        clingPlayControl.setVolume(value, object : ControlCallback<Any> {
            override fun success(response: IResponse<Any>) {
                Log.e(TAG, "volume success")
                handler.sendEmptyMessage(ClingWorkState.SET_VOLUME_SUCCESS)
            }

            override fun fail(response: IResponse<Any>) {
                Log.e(TAG, "volume fail")
                handler.sendEmptyMessage(ClingWorkState.SET_VOLUME_FAILED)
            }
        })
    }

    override fun addVolume() {
        addVolume(if (isVolumeChangeSingleNum) {
            isVolumeChangeSingleNum = false
            VOLUME_SINGLE_CHANGE_NUM
        } else {
            isVolumeChangeSingleNum = true
            VOLUME_DOUBLE_CHANGE_NUM
        })
    }

    override fun addVolume(add: Int) {
        if (currentVolume != -1) {
            currentVolume += add
            setVolume(currentVolume)
            return
        }
        clingPlayControl.getVolume(object : ControlReceiveCallback {

            override fun success(response: IResponse<Any>?) {

            }

            override fun receive(response: IResponse<Any>) {
                currentVolume = (response as ClingVolumeResponse).response
                currentVolume += add
                if (currentVolume > 100) currentVolume = 100
                setVolume(currentVolume)
            }

            override fun fail(response: IResponse<Any>) {
                handler.sendEmptyMessage(ClingWorkState.SET_VOLUME_FAILED)
            }
        })
    }

    override fun reduceVolume() {
        reduceVolume(if (isVolumeChangeSingleNum) {
            isVolumeChangeSingleNum = false
            VOLUME_SINGLE_CHANGE_NUM
        } else {
            isVolumeChangeSingleNum = true
            VOLUME_DOUBLE_CHANGE_NUM
        })
    }

    override fun reduceVolume(reduce: Int) {
        if (currentVolume != -1) {
            currentVolume -= reduce
            if (currentVolume < 0) currentVolume = 0
            setVolume(currentVolume)
            return
        }

        clingPlayControl.getVolume(object : ControlReceiveCallback {

            override fun success(response: IResponse<Any>?) {

            }

            override fun receive(response: IResponse<Any>) {
                currentVolume = (response as ClingVolumeResponse).response

            }

            override fun fail(response: IResponse<Any>) {
                handler.sendEmptyMessage(ClingWorkState.SET_VOLUME_FAILED)
            }
        })
    }

    override fun setSeek(seekMillisecond: Long) {
        clingPlayControl.seek(seekMillisecond.toInt(), object : ControlCallback<Any> {
            override fun success(response: IResponse<Any>) {
                Log.e(TAG, "seek success")
                handler.sendEmptyMessage(ClingWorkState.SET_SEEK_SUCCESS)
            }

            override fun fail(response: IResponse<Any>) {
                Log.e(TAG, "seek fail")
                handler.sendEmptyMessage(ClingWorkState.SET_SEEK_FAILED)
            }
        })
    }

    override fun addSeek() {
        addSeek(SEEK_CHANGE_NUM)
    }

    override fun addSeek(add: Long) {
        if (currentPlayPosition != -1L && currentPlayDuration != -1L) {
            currentPlayPosition += add
            if (currentPlayPosition > currentPlayDuration) currentPlayPosition = currentPlayDuration
            setSeek(currentPlayPosition)
            return
        }

    }

    override fun reduceSeek() {
        reduceSeek(SEEK_CHANGE_NUM)
    }

    override fun reduceSeek(reduce: Long) {
        if (currentPlayPosition != -1L && currentPlayDuration != -1L) {
            currentPlayPosition -= reduce
            if (currentPlayPosition < 0) currentPlayPosition = 0
            setSeek(currentPlayPosition)
            return
        }
    }

    fun startSearchDevices() {
        init()
    }

    fun stopSearchDevices() {
        onHostActivityDestroy()
    }

    /**
     * 刷新设备
     */
    override fun refreshDeviceList() {
        val devices = ClingManager.getInstance().dmrDevices
        ClingDeviceList.getInstance().clingDeviceList = devices
        if (devices != null) {
            iClingViewManager.onRefreshDevice(devices)
        }
    }

    /**
     * 接收状态改变信息
     */
    private inner class TransportStateBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.e(TAG, "Receive playback intent:" + action!!)

            when (action) {
                ACTION_PLAYING -> handler.sendEmptyMessage(ClingWorkState.START_PLAY)
                ACTION_PAUSED_PLAYBACK -> handler.sendEmptyMessage(ClingWorkState.PAUSED)
                ACTION_STOPPED -> handler.sendEmptyMessage(ClingWorkState.STOPPED)
                ACTION_TRANSITIONING -> handler.sendEmptyMessage(ClingWorkState.TRANSITIONING)
                ACTION_POSITION_CALLBACK -> {
//                    handler.sendMessage(Message().apply {
//                        what = ClingWorkState.PLAY_POSITION_CALLBACK
//                        arg1 = intent.getIntExtra(EXTRA_POSITION, -1)
//                    })

                }
                ACTION_VOLUME_CALLBACK -> {
                    handler.sendMessage(Message().apply {
                        what = ClingWorkState.PLAY_VOLUME_CALLBACK
                        arg1 = intent.getIntExtra(EXTRA_VOLUME, -1)
                    })
                }
            }
        }
    }

    override fun onHostActivityDestroy() {
        handler.removeCallbacksAndMessages(null)
        // Unbind UPnP service
        hostActivity.unbindService(upnpServiceConnection)
        // UnRegister Receiver
        hostActivity.unregisterReceiver(transportStateBroadcastReceiver)
        ClingManager.getInstance().destroy()
        ClingDeviceList.getInstance().destroy()
    }

    override fun onControlDialogDismiss() {
        threadListener?.interrupt()
        threadListener = null
        parserPlayUrl?.onDestroy()
    }

}

