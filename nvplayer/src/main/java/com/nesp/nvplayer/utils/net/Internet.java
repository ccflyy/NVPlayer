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

package com.nesp.nvplayer.utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * @Team : NESP Technology
 * @Author : Jinzhaolu
 * Email : 1756404649@qq.com
 * @Time : 18-3-8 下午3:56
 */
public class Internet {

    public static class NetWork {

        /**
         * 用法
         * if(!NetWork.isNetworkConnected(this)){
         * Toast.makeText(this,"当前网络不可用",Toast.LENGTH_LONG).function();
         * finish();
         * }
         * <p>
         * 判断是否有网络连接
         *
         * @param context
         * @return
         */
        public static boolean isNetworkConnected(Context context) {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable();
                }
            }
            return false;
        }

        /**
         * 判断WIFI网络是否可用
         *
         * @param context
         * @return
         */
        public static boolean isWifiConnected(Context context) {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mWiFiNetworkInfo != null) {
                    return mWiFiNetworkInfo.isAvailable();
                }
            }
            return false;
        }

        /**
         * 判断MOBILE网络是否可用
         *
         * @param context
         * @return
         */
        public static boolean isMobileConnected(Context context) {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mMobileNetworkInfo = mConnectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (mMobileNetworkInfo != null) {
                    return mMobileNetworkInfo.isAvailable();
                }
            }
            return false;
        }

        /**
         * 网络连接方式
         *
         * @param context
         * @return
         */
        public static String getNetTypeName(Context context) {
            //网络连接方式
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo.getTypeName();//MOBILE(数据连接)/WIFI
        }

        public static Boolean isWifi(Context context) {
            return getNetType(context) == NetType.WIFI;
        }
        public static Boolean isMobile(Context context) {
            return getNetType(context) == NetType.MOBILE;
        }

        public static NetType getNetType(Context context) {
            switch (getNetTypeName(context)) {
                case "MOBILE":
                    return NetType.MOBILE;
                case "WIFI":
                    return NetType.WIFI;
                default:
                    return NetType.UNKNOWN;
            }
        }

        public enum NetType {
            MOBILE,
            WIFI,
            UNKNOWN
        }

        /**
         * 获取当前网络连接的类型信息
         *
         * @param context
         * @return
         */
        public static int getConnectedType(Context context) {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                    return mNetworkInfo.getType();
                }
            }
            return -1;
        }

        /**
         * 获取当前的网络状态 ：没有网络0：WIFI网络1：3G网络2：2G网络3
         *
         * @param context
         * @return
         */
        public static int getAPNType(Context context) {
            int netType = 0;
            ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null) {
                return netType;
            }
            int nType = networkInfo.getType();
            if (nType == ConnectivityManager.TYPE_WIFI) {
                netType = 1;// wifi_backup
            } else if (nType == ConnectivityManager.TYPE_MOBILE) {
                int nSubType = networkInfo.getSubtype();
                TelephonyManager mTelephony = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                        && !mTelephony.isNetworkRoaming()) {
                    netType = 2;// 3G
                } else {
                    netType = 3;// 2G
                }
            }
            return netType;
        }
    }
}