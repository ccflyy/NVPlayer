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

package com.nesp.nvplayer.utils.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import static android.content.Intent.EXTRA_DOCK_STATE;

/**
 * @Team: NESP Technology
 * @Author: 靳兆鲁
 * Email: 1756404649@qq.com
 * @Time: Created 2018/11/28 9:14
 * @Project software_android_assistant
 **/
public class Battery {
    private static Battery battery;
    private Context context;

    public static Battery getInstance(Context context) {
        return battery == null ? battery = new Battery(context) : battery;
    }

    public Battery(Context context) {
        this.context = context;
    }

    public int getBatteryLevel() {

        return getBatteryIntent().getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }

    /**
     * 电池刻度
     *
     * @return
     */
    public int getBatteryScale() {
        return getBatteryIntent().getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    }

    /**
     * 状态	描述
     * int BATTERY_STATUS_CHARGING = 2	充电中
     * int BATTERY_STATUS_DISCHARGING = 3	放电中
     * int BATTERY_STATUS_NOT_CHARGING = 4	未充电
     * int BATTERY_STATUS_FULL = 5	已充满
     * int BATTERY_STATUS_UNKNOWN = 1	状态未知
     * @return
     */
    public int getBatteryStatus() {
        return getBatteryIntent().getIntExtra(BatteryManager.EXTRA_SCALE, BatteryManager.BATTERY_STATUS_UNKNOWN);
    }

    /**
     * 方式	描述
     * int BATTERY_PLUGGED_AC = 1	使用充电器充电
     * int BATTERY_PLUGGED_USB = 2	使用USB充电
     * int BATTERY_PLUGGED_WIRELESS = 4	使用无线方式充电
     * @return
     */
    public int getBatteryPlug() {
        return getBatteryIntent().getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
    }

    /**
     * 底座类型	描述
     * int EXTRA_DOCK_STATE_CAR = 2	车载底座
     * int EXTRA_DOCK_STATE_DESK = 1	桌面底座
     * int EXTRA_DOCK_STATE_LE_DESK = 3	低端（模拟）桌面基座 API >= 11
     * int EXTRA_DOCK_STATE_HE_DESK = 4	高端（数字）桌面基座 API >= 11
     * @return
     */
    public int getBatteryDockState() {
        return getBatteryIntent().getIntExtra(EXTRA_DOCK_STATE, -1);
    }

    /**
     * 是否插入了底座
     * @return
     */
    public Boolean getBatteryIsDocked () {
        return getBatteryDockState()!= Intent.EXTRA_DOCK_STATE_UNDOCKED;
    }

    private Intent getBatteryIntent() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        return context.registerReceiver(null, filter);
    }
}
