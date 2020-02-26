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
import android.content.*
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.NestedScrollView
import com.nesp.android.cling.entity.ClingDevice
import com.nesp.android.cling.entity.IDevice
import com.nesp.nvplayer.R
import com.nesp.nvplayer.cling.IClingViewManager
import com.nesp.nvplayer.cling.adapter.ClingDevicesLvAdapter
import com.nesp.nvplayer.utils.net.Internet
import com.nesp.nvplayer.utils.net.Wifi


/**
 *
 *
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-5 下午12:28
 * @project FishMovie
 **/
class SearchDeviceDialogFragment(private val iClingViewManager: IClingViewManager, private var iSearchDeviceDialog: ISearchDeviceDialog?) : BaseDialog() {

    private val SEARCH_DEVICES_TIME = 30 * 1000L

    private val handlerBreakSearch = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            stopSearchDevices()
        }
    }

    private var wifiConnectChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (WifiManager.WIFI_STATE_CHANGED_ACTION == action) {//这个监听wifi的打开与关闭，与wifi的连接无关
                when (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)) {
                    WifiManager.WIFI_STATE_DISABLED,
                    WifiManager.WIFI_STATE_DISABLING,
                    WifiManager.WIFI_STATE_ENABLING,
                    WifiManager.WIFI_STATE_UNKNOWN -> {
                        setErrorWifiState()
                    }
                    WifiManager.WIFI_STATE_ENABLED -> {
                        setNormalWifiState()
                    }
                }//
            }
        }
    }

    private val WIFI_NAME_PREFIX = "当前WIFI："

    private lateinit var searchDeviceRotaAnim: RotateAnimation
    private lateinit var textViewCurrentWifi: TextView
    private lateinit var textViewCurrentWifiError: TextView
    private lateinit var imageViewClose: AppCompatImageView
    private lateinit var imageViewRefresh: AppCompatImageView
    private lateinit var textViewState: TextView
    private lateinit var lvDevices: ListView
    private lateinit var ClingDevicesLvAdapter: ClingDevicesLvAdapter
    private lateinit var nsvTips: NestedScrollView
    private var isSearchingDevice = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentView = inflater.inflate(R.layout.nvplayer_cling_dialog_search_device, container, false)
        imageViewClose = findView(R.id.nvplayer_cling_dialog_search_device_iv_close)
        imageViewRefresh = findView(R.id.nvplayer_cling_dialog_search_device_iv_refresh)
        textViewState = findView(R.id.nvplayer_cling_dialog_search_device_tv_state)
        textViewCurrentWifi = findView(R.id.nvplayer_cling_dialog_search_device_tv_current_wifi)
        textViewCurrentWifiError = findView(R.id.nvplayer_cling_dialog_search_device_tv_current_wifi_error)
        textViewCurrentWifiError.setOnClickListener {
            context!!.startActivity(Intent(android.provider.Settings.ACTION_WIFI_SETTINGS))
        }
        lvDevices = findView(R.id.nvplayer_cling_dialog_search_device_lv_devices)
        nsvTips = findView(R.id.nvplayer_cling_dialog_search_device_nsv_tips)
        ClingDevicesLvAdapter = ClingDevicesLvAdapter(context, iClingViewManager)
        lvDevices.adapter = ClingDevicesLvAdapter

        imageViewClose.setOnClickListener {
            dismiss()
        }
        imageViewRefresh.setOnClickListener {
            if (isSearchingDevice) {
                iSearchDeviceDialog?.onSearchRefreshClick(false, "正在搜索设备，请稍后...")
                return@setOnClickListener
            }
            startSearchDevice()
        }

        if (!isWifiEnable()) {
            setErrorWifiState()
        } else if (Internet.NetWork.isWifi(context)) {
            setNormalWifiState()
        }
        return contentView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerWifiConnectChangeReceiver()
    }

    fun setWifiState(state: String) {
        textViewCurrentWifi.text = state
    }

    fun startSearchDevice() {
        setSearchDeviceState("正在搜寻可投屏设备")
        searchDeviceRotaAnim = RotateAnimation(0f, 720f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        searchDeviceRotaAnim.apply {
            duration = 2000
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
            fillAfter = true
        }
        isSearchingDevice = true
        imageViewRefresh.startAnimation(searchDeviceRotaAnim)
        iSearchDeviceDialog?.onSearchRefreshClick(true)
        Thread(Runnable {
            Looper.prepare()

            Thread.sleep(SEARCH_DEVICES_TIME)
            handlerBreakSearch.sendEmptyMessage(0)

            Looper.loop()
        }).start()
    }


    private fun isWifiEnable(): Boolean {
        return (!Internet.NetWork.isNetworkConnected(context) || Internet.NetWork.isMobile(context))
                && Wifi.getConnectedWifiSsid(context!!).isNotEmpty()
    }

    private fun setErrorWifiState() {
        ClingDevicesLvAdapter.clear()
        setWifiState("未连接Wi-Fi,")
        textViewCurrentWifiError.visibility = View.VISIBLE
//        textViewCurrentWifi.setTextColor(resources.getColor(R.color.nvplay_wifi_state_normal))

        showTips(true)
    }

    private fun setNormalWifiState() {
        textViewCurrentWifiError.visibility = View.GONE
        setWifiState(WIFI_NAME_PREFIX + Wifi.getConnectedWifiSsid(context!!))
        textViewCurrentWifi.setOnClickListener {}
//        textViewCurrentWifi.setTextColor(resources.getColor(R.color.nvplay_wifi_state_normal))
        showTips(false)
    }

    /**
     * 设置刷新状态
     */
    fun setSearchDeviceState(state: String) {
        textViewState.text = state
    }

    fun startRefreshDevice(devices: Collection<ClingDevice>) {
        if (isSearchingDevice) return

        isSearchingDevice = true
        setSearchDeviceState("正在搜寻可投屏设备")
        if (searchDeviceRotaAnim.hasEnded() && !searchDeviceRotaAnim.hasStarted()) {
            imageViewRefresh.startAnimation(searchDeviceRotaAnim)
        }
        ClingDevicesLvAdapter.clear()
        ClingDevicesLvAdapter.addAll(devices)
        Thread(Runnable {
            Looper.prepare()

            Thread.sleep(SEARCH_DEVICES_TIME)
            handlerBreakSearch.sendEmptyMessage(0)

            Looper.loop()
        }).start()
    }

    /**
     * 停止刷新设备
     */
    fun stopSearchDevices() {
        if (!isSearchingDevice) return

        isSearchingDevice = false
        setSearchDeviceState(if (ClingDevicesLvAdapter.count > 0) {
            "请选择投屏设备"
        } else {
            "未搜索到投屏设备"
        })
        if (searchDeviceRotaAnim.hasStarted() && !searchDeviceRotaAnim.hasEnded()) {
            searchDeviceRotaAnim.apply {
                cancel()
                reset()
            }
        }
        showTips()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        isShown = false
        context?.unregisterReceiver(wifiConnectChangedReceiver)
        iSearchDeviceDialog?.onDialogDismiss(dialog)
        iSearchDeviceDialog?.onSearchDialogDismiss(dialog)
    }

    override fun onShown(dialog: DialogInterface) {
        super.onShown(dialog)
        iSearchDeviceDialog?.onDialogShown(dialog)
        iSearchDeviceDialog?.onSearchDialogShown(dialog)
        startSearchDevice()
    }

    fun onDeviceAdded(device: IDevice<Any>) {
        ClingDevicesLvAdapter.add(device as ClingDevice)
        showTips()
    }

    fun onDeviceRemoved(device: IDevice<Any>) {
        ClingDevicesLvAdapter.remove(device as ClingDevice)
        showTips()
    }

    private fun registerWifiConnectChangeReceiver() {
        val filter = IntentFilter()
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
        context?.registerReceiver(wifiConnectChangedReceiver, filter)
    }

    private fun showTips() {
        showTips(ClingDevicesLvAdapter.count < 1)
    }

    private fun showTips(isShowTips: Boolean) {
        if (isShowTips) {
            nsvTips.visibility = View.VISIBLE
            lvDevices.visibility = View.INVISIBLE
        } else {
            nsvTips.visibility = View.INVISIBLE
            lvDevices.visibility = View.VISIBLE
        }
    }

}