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

package com.nesp.nvplayer.utils;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-6-14 上午12:09
 * @project FishMovie
 **/
public class UnitUtils {
    public static String millisecondToString(long ms) {
        int hour = 0;
        int min;
        int sec;
        if (ms < 60 * 1000) {
            min = 0;
            sec = (int) (ms / 1000);
        } else if (ms < 60 * 60 * 1000) {
            min = (int) (ms / (60 * 1000));
            sec = (int) ((ms % (60 * 1000)) / 1000);
        } else {
            hour = (int) (ms / (60 * 60 * 1000));
            min = (int) ((ms % (60 * 60 * 1000)) / (60 * 1000));
            sec = (int) ((ms % (60 * 1000)) / (1000));
        }
        return hour + "h" + min + "m" + sec + "s";
    }
}
