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

package com.nesp.nvplayer.utils.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build

/**
 *
 *
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-6 上午12:38
 * @project FishMovie
 **/
class Wifi {
    companion object {

        @JvmStatic
        fun getConnectedWifiSsid(context: Context): String {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                try {
                    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    wifiManager.connectionInfo.ssid
                } catch (e: Exception) {
                    ""
                }
            } else {
                try {
                    val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    connectivityManager.activeNetworkInfo.extraInfo.replace("\"", "")
                } catch (e: Exception) {
                    ""
                }

            }
        }
    }
}