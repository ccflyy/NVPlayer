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
 * @time: Created 19-7-5 上午9:57
 * @project FishMovie
 **/
class ClingWorkState {

    companion object {
        /**
         * 正在连接...
         */
        const val TRANSITIONING = 0xb1
        /**
         * 开始投屏
         */
        const val START_PLAY = 0xb2

        /**
         * 投屏成功
         */
        const val PLAY_SUCCESS = 0xb3
        /**
         * 投屏失败
         */
        const val PLAY_FAILED = 0xb4
        /**
         * 暂停成功
         */
        const val PAUSED = 0xb6
        /**
         * 暂停失败
         */
        const val PAUSE_FAILED = 0xb7
        /**
         * 停止成功
         */
        const val STOPPED = 0xb9
        /**
         * 停止失败
         */
        const val STOP_FAILED = 0xba

        /**
         * 静音成功
         */
        const val SET_MUTE_SUCCESS = 0xbb

        const val SET_MUTE_FAILED = 0xbc

        /**
         * 音量设置成功
         */
        const val SET_VOLUME_SUCCESS = 0xbd

        const val SET_VOLUME_FAILED = 0xbe
        /**
         * 进度设置成功
         */
        const val SET_SEEK_SUCCESS = 0xbf

        const val SET_SEEK_FAILED = 0xc1
        /**
         * 未知错误
         */
        const val UNKNOWN_ERROR = 0xc2

        const val PLAY_POSITION_CALLBACK = 0xc3
        const val PLAY_VOLUME_CALLBACK = 0xc4

        /**
         * 暂停后继续播放
         */
        const val CONTINUE_PLAY = 0xc5


    }
}