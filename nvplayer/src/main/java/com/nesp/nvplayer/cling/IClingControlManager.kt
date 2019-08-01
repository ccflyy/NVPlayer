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

/**
 *
 *
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-5 上午8:13
 * @project FishMovie
 **/
interface IClingControlManager {

    fun init()

    fun onControlDialogShown()

    fun changeDevice()

    fun startPlay(currentPlayUrl: String)

    fun pausePlay()

    fun stopPlay()

    fun setMute(isMute: Boolean)

    fun setVolume(value: Int)

    fun addVolume()

    fun addVolume(add: Int)

    fun reduceVolume()

    fun reduceVolume(reduce: Int)

    fun setSeek(seekMillisecond: Long)

    fun addSeek()

    fun addSeek(add: Long)

    fun reduceSeek()

    fun reduceSeek(reduce: Long)

    fun refreshDeviceList()

    fun onHostActivityDestroy()

    fun onControlDialogDismiss()
}
