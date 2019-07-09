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

import com.nesp.android.cling.entity.ClingDevice
import com.nesp.android.cling.entity.IDevice

/**
 *
 *
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-5 上午9:55
 * @project FishMovie
 **/
interface IClingViewManager {

    fun initView()

    fun showClingSearchDevicePage()

    fun closeClingSearchDevicePage()

    fun showClingControlPage(deviceName: String)

    fun closeClingControlPage()

    fun showSelectEpisodePage()

    fun closeSelectEpisodePage()

    fun showHelpPage()

    fun closeHelpPage()

    fun onHostActivityResume()

    fun onHostActivityDestroy()

    fun onDeviceAdded(device: IDevice<Any>)

    fun onDeviceRemoved(device: IDevice<Any>)

    fun onSearchDevicesStateChange(state: String)

    fun onPlayStateChange(clingWorkState: ClingWorkState)

    fun onPlayPositionChange(currentPosition: String, duration: String, percent: Int)

    fun onPlayPositionChange(currentPosition: Long, duration: Long, percent: Int)

    fun onPlayVolumeChange(value: Int)

    fun onWifiStateChange(state: String)

    fun onStartSearchDevice()

    fun onTransitioning()

    fun onStartPlay()

    fun onContinuePlay()

    fun onPlaySuccess()

    fun onPlayFailed()

    fun onPaused()

    fun onPauseFailed()

    fun onStopped()

    fun onStopFailed()

    fun onSetMuteSuccess()

    fun onSetMuteFailed()

    fun onSetVolumeSuccess()

    fun onSetVolumeFailed()

    fun onSetSeekSuccess()

    fun onSetSeekFailed()

    fun onUnKnownError()

    fun onRefreshDevice(devices: Collection<ClingDevice>)

}