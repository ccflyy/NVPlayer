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

package com.nesp.nvplayer.cling.dialog

import android.content.DialogInterface

/**
 *
 *
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-6 下午6:03
 * @project FishMovie
 **/
interface IControlDialog : IBaseDialog {

    fun OnControlCloseClick()

    fun OnControlChangeDeviceClick()

    fun OnControlSelectEpisodeClick()

    fun OnControlMuteClick(isMute: Boolean)

    fun OnControlPlayPauseClick(isPlay: Boolean)

    fun OnControlSeekAddClick()

    fun OnControlSeekReduceClick()

    fun OnControlVolumeAddClick()

    fun OnControlVolumeReduceClick()

    fun OnControlHelpClick()

    fun OnControlDialogShown(dialog: DialogInterface)

    fun OnControlDialogDismiss(dialog: DialogInterface)

    /*********************************触控板*************************************/

    fun OnControlTouchDoubleClick(isPlay: Boolean)

    fun OnControlTouchSetVolume(volumeChange: Int)

    fun OnControlTouchSetSeek(seekChange: Long)
}