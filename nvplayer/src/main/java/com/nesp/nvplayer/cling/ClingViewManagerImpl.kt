/*
 *
 *   Copyright (c) 2019  NESP Technology Corporation. All rights reserved.
 *
 *   This program is free software; you can redistribute it and/or modify it
 *   under the terms and conditions of the GNU General Public License,
 *   version 2, as published by the Free Software Foundation.
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License.See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
 */

package com.nesp.nvplayer.cling

import android.app.Service
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nesp.android.cling.entity.ClingDevice
import com.nesp.android.cling.entity.IDevice
import com.nesp.nvplayer.NVPlayer
import com.nesp.nvplayer.model.NEpisode
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.nesp.nvplayer.cling.dialog.*
import com.nesp.nvplayer.cling.model.ClingSettings
import kotlin.math.abs


/**
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-5 上午10:14
 * @project FishMovie
 **/
class ClingViewManagerImpl(private val hostActivity: AppCompatActivity
                           , private val nvplayer: NVPlayer
                           , private val videoName: String
                           , private val nEpisodes: MutableList<NEpisode>
                           , private var currentEpisodePosition: Int
                           , private var videoHeaderTime: Long
                           , private var videoTailTime: Long
) : IClingViewManager
        , ISearchDeviceDialog, IControlDialog, ISelectEpisodeDialog {

    /*********************************Data*************************************/
    //TODO:Data
    private var isNeedNVPlayerPause = false
    private var currentEpisode = nEpisodes[currentEpisodePosition]

    /*********************************Managers*************************************/
    //TODO:Managers
    private val iClingControlManager = ClingControlManagerImpl(hostActivity, this)

    /*********************************Dialog*************************************/
    //TODO:Dialog
    /**
     * 控制设备的Dialog
     */
    private var controlDialogFragment = ControlDialogFragment(this)
    /**
     * 搜索设备的Dialog
     */
    private var searchDeviceDialogFragment = SearchDeviceDialogFragment(this, this)
    /**
     * 选集的Dialog
     */
    private var selectEpisodeDialogFragment = SelectEpisodeDialogFragment(nEpisodes, currentEpisodePosition, this)
    /**
     * 帮助的Dialog
     */
    private val helpDialogFragment = BrowserDialogFragment("https://nesp.gitee.io/api/android/app/com.nesp.movie/html/cling/help/")

    /*********************************IClingViewManager*************************************/
    //TODO:IClingViewManager
    /**
     * This function is called when the cast button on the player is clicked.
     */

    /*********************************Settings*************************************/
    //TODO:Settings
    private var clingSettings = ClingSettings(isAutoSkipHeaderTail = false, isAutoPlayNextEpisode = false)

    override fun initView() {
        iClingControlManager.startSearchDevices()
    }

    override fun showClingSearchDevicePage() {
        checkDialogFragment(searchDeviceDialogFragment)
        isNeedNVPlayerPause = true
        searchDeviceDialogFragment.show(hostActivity.supportFragmentManager, "searchDeviceDialogFragment")
    }

    override fun closeClingSearchDevicePage() {
        isNeedNVPlayerPause = false
        searchDeviceDialogFragment.dismiss()
    }

    override fun showClingControlPage(deviceName: String) {
        checkDialogFragment(controlDialogFragment)
        controlDialogFragment.show(hostActivity.supportFragmentManager, "controlDialogFragment"
                , deviceName
                , "$videoName ${currentEpisode.name}")

    }

    override fun closeClingControlPage() {
        controlDialogFragment.dismiss()
    }

    override fun showSelectEpisodePage() {
        checkDialogFragment(selectEpisodeDialogFragment)
        selectEpisodeDialogFragment.show(hostActivity.supportFragmentManager, "selectEpisodeDialogFragment", currentEpisodePosition)
    }

    override fun closeSelectEpisodePage() {
        selectEpisodeDialogFragment.dismiss()
    }

    override fun showHelpPage() {
        checkDialogFragment(helpDialogFragment)
        helpDialogFragment.show(hostActivity.supportFragmentManager, "helpDialogFragment")
    }

    override fun closeHelpPage() {
        helpDialogFragment.dismiss()
    }

    override fun onHostActivityDestroy() {
//        iClingControlManager.onHostActivityDestroy()
    }

    override fun onHostActivityResume() {
        if (isNeedNVPlayerPause) {
            nvplayer.startButton.performClick()
        }
    }

    override fun onDeviceAdded(device: IDevice<Any>) {
        searchDeviceDialogFragment.onDeviceAdded(device)
    }

    override fun onDeviceRemoved(device: IDevice<Any>) {
        searchDeviceDialogFragment.onDeviceRemoved(device)
    }

    override fun onSearchDevicesStateChange(state: String) {
    }

    override fun onPlayStateChange(clingWorkState: ClingWorkState) {
    }

    private val TAG = "ClingViewManagerImpl"
    override fun onPlayPositionChange(currentPosition: String, duration: String, percent: Int) {
        controlDialogFragment.setPlayTime(currentPosition, duration)
        controlDialogFragment.setPlayProgress(percent)
    }

    private var isSkippedHeader = false
    private var isSkippedTail = false

    override fun onPlayPositionChange(currentPosition: Long, duration: Long, percent: Int) {

        if (clingSettings.isAutoPlayNextEpisode) {
            if ((currentPosition in 3000 until videoHeaderTime) && percent == 1) {
                playNextEpisode()
                return
            }
        }

        if (clingSettings.isAutoSkipHeaderTail) {
            if (!isSkippedHeader && (currentPosition in 3000 until videoHeaderTime)) {
                showShortToast("即将为你跳过片头")
                iClingControlManager.setSeek(videoHeaderTime)
                isSkippedHeader = true
                isSkippedTail = false
            } else if (!isSkippedTail && (duration - currentEpisodePosition) < videoTailTime) {
                isSkippedHeader = false
                isSkippedTail = true
                showShortToast("即将为你跳过片尾")
                playNextEpisode()
            }
        }
    }

    private fun playNextEpisode() {
        if (!clingSettings.isAutoPlayNextEpisode) return

        if (currentEpisodePosition >= nEpisodes.size - 1) {
            showShortToast("没有下一集")
            return
        }
        showShortToast("即将播放下一集")
        currentEpisodePosition += 1
        currentEpisode = nEpisodes[currentEpisodePosition]
        selectEpisodeDialogFragment.setEpisodeSelectPosition(currentEpisodePosition)
        iClingControlManager.startPlay(currentEpisode.playInfoUrl)
    }


    override fun onPlayVolumeChange(value: Int) {

    }

    override fun onWifiStateChange(state: String) {
        searchDeviceDialogFragment.setWifiState(state)
    }

    override fun onStartSearchDevice() {
        searchDeviceDialogFragment.startSearchDevice()
    }

    override fun onTransitioning() {
        showShortToast("正在连接")
    }

    override fun onStartPlay() {
        showShortToast("正在投屏")
    }

    override fun onContinuePlay() {

    }

    override fun onPlaySuccess() {
        if (isPlayPauseClick) {
            vibrator()
            isPlayPauseClick = false
        }
        currentEpisode = nEpisodes[currentEpisodePosition]
        controlDialogFragment.setPlayName("$videoName ${currentEpisode.name}")
        controlDialogFragment.onStartPlay()
        selectEpisodeDialogFragment.setEpisodeSelectPosition(currentEpisodePosition)
    }

    override fun onPlayFailed() {
        isPlayPauseClick = false
        showShortToast("投屏失败")
    }

    override fun onPaused() {
        if (isPlayPauseClick) {
            vibrator()
            isPlayPauseClick = false
        }
        controlDialogFragment.onPausePlay()
    }

    override fun onPauseFailed() {
        isPlayPauseClick = false
        showShortToast("暂停失败")
    }

    override fun onStopped() {
        controlDialogFragment.onStopPlay()
    }

    override fun onStopFailed() {
        showShortToast("停止失败")
    }

    private var isMute = false

    override fun onSetMuteSuccess() {
        isMute = if (isMute) {
            controlDialogFragment.onUnMute()
            false
        } else {
            controlDialogFragment.onMute()
            true
        }
    }

    override fun onSetMuteFailed() {
        showShortToast("静音设置失败")
    }

    override fun onSetVolumeSuccess() {
        vibrator()
    }

    override fun onSetVolumeFailed() {
        showShortToast("音量设置失败")
    }

    override fun onSetSeekSuccess() {
        vibrator()
    }

    override fun onSetSeekFailed() {
        showShortToast("播放进度设置失败")
    }

    override fun onUnKnownError() {
        showShortToast("未知错误")
    }

    override fun onRefreshDevice(devices: Collection<ClingDevice>) {
        searchDeviceDialogFragment.startRefreshDevice(devices)
    }

    /*********************************IBaseDialog*************************************/
    //TODO:IBaseDialog

    override fun onDialogShown(dialog: DialogInterface) {

    }

    override fun onDialogDismiss(dialog: DialogInterface) {

    }

    /*********************************SearchDeviceDialogFragment*************************************/
    //TODO:SearchDeviceDialogFragment


    override fun onSearchRefreshClick(isSuccess: Boolean, msg: String) {
        if (isSuccess) {
            iClingControlManager.refreshDeviceList()
            return
        }
        showShortToast(msg)
    }

    override fun OnSearchCloseClick() {

    }

    override fun onSearchDialogShown(dialog: DialogInterface) {
        vibrator = hostActivity.applicationContext.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator?
        if (nvplayer.gsyVideoManager.isPlaying)
            nvplayer.startButton.performClick()
    }


    override fun onSearchDialogDismiss(dialog: DialogInterface) {
        vibrator?.cancel()
        if (!nvplayer.gsyVideoManager.isPlaying)
            nvplayer.startButton.performClick()
        iClingControlManager.stopSearchDevices()
    }

    /*********************************ClingControlDialogFragment*************************************/
    //TODO:ClingControlDialogFragment

    var isContronDialogCloseClick = false

    override fun OnControlCloseClick() {
        isContronDialogCloseClick = true
        closeClingControlPage()
    }

    override fun OnControlDialogShown(dialog: DialogInterface) {
        iClingControlManager.onControlDialogShown()
        searchDeviceDialogFragment.stopSearchDevices()
    }

    override fun OnControlDialogDismiss(dialog: DialogInterface) {
        iClingControlManager.onControlDialogDismiss()
        if (isContronDialogCloseClick) {
            closeClingSearchDevicePage()
            isContronDialogCloseClick = false
        }
        iClingControlManager.stopPlay()
    }

    override fun OnControlChangeDeviceClick() {
        closeClingControlPage()
    }

    override fun OnControlSelectEpisodeClick() {
        showSelectEpisodePage()
    }

    override fun OnControlMuteClick(isMute: Boolean) {
        iClingControlManager.setMute(isMute)
    }

    private var isPlayPauseClick = false

    override fun OnControlPlayPauseClick(isPlay: Boolean) {
        isPlayPauseClick = true
        if (isPlay)
            iClingControlManager.startPlay(nEpisodes[currentEpisodePosition].playInfoUrl)
        else
            iClingControlManager.pausePlay()
    }

    override fun OnControlSeekAddClick() {
        iClingControlManager.addSeek()
    }

    override fun OnControlSeekReduceClick() {
        iClingControlManager.reduceSeek()
    }

    override fun OnControlVolumeAddClick() {
        iClingControlManager.addVolume()
    }

    override fun OnControlVolumeReduceClick() {
        iClingControlManager.reduceVolume()
    }

    override fun OnControlTouchDoubleClick(isPlay: Boolean) {
        OnControlPlayPauseClick(isPlay)
    }

    override fun OnControlTouchSetVolume(volumeChange: Int) {
        if (volumeChange > 0) {
            iClingControlManager.addVolume(abs(volumeChange))
        } else if (volumeChange < 0) {
            iClingControlManager.reduceVolume(abs(volumeChange))
        }
    }

    override fun OnControlTouchSetSeek(seekChange: Long) {
        if (seekChange > 0) {
            iClingControlManager.addSeek(abs(seekChange))
        } else if (seekChange < 0) {
            iClingControlManager.reduceSeek(abs(seekChange))
        }
    }

    override fun OnControlHelpClick() {
        showHelpPage()
    }

    /*********************************SelectEpisodesDialog*************************************/
    //TODO:SelectEpisodesDialog
    override fun OnSelectEpisodeDialogShown(dialog: DialogInterface) {

    }

    override fun OnSelectEpisodeDialogDismiss(dialog: DialogInterface) {

    }

    override fun OnSelectEpisodeDialogEpisodeItemClick(episode: NEpisode, position: Int) {
        closeSelectEpisodePage()
        currentEpisode = nEpisodes[position]
        currentEpisodePosition = position
        iClingControlManager.startPlay(currentEpisode.playInfoUrl)
    }

    /*********************************Utils*************************************/
    //TODO:Utils

    private fun showShortToast(msg: String) {
        val toast = Toast.makeText(hostActivity, null, Toast.LENGTH_SHORT)
        toast.setText(msg)
        toast.show()
    }

    private fun checkDialogFragment(dialogFragment: DialogFragment) {
        val ft = hostActivity.supportFragmentManager.beginTransaction()
        if (hostActivity.supportFragmentManager.fragments.contains(dialogFragment)) {
            hostActivity.supportFragmentManager.fragments.remove(dialogFragment)
            ft.remove(dialogFragment)
        }
    }


    private var vibrator: Vibrator? = null

    /**
     * 震动
     */
    private fun vibrator() {
        if (vibrator?.hasVibrator()!!) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                vibrator?.vibrate(20)
            } else {
                vibrator?.vibrate(VibrationEffect.createOneShot(20, 1))
            }
        }
    }
}