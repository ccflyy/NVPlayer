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

package com.nesp.nvplayer.cling.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.nesp.android.cling.entity.ClingDevice;
import com.nesp.android.cling.service.manager.ClingManager;
import com.nesp.android.cling.util.Utils;
import com.nesp.nvplayer.R;
import com.nesp.nvplayer.cling.IClingViewManager;
import com.nesp.nvplayer.cling.dialog.ControlDialogFragment;

import org.fourthline.cling.model.meta.Device;

/**
 * 说明：
 * 作者：zhouzhan
 * 日期：17/6/28 15:50
 */

public class ClingDevicesLvAdapter extends ArrayAdapter<ClingDevice> {

    private LayoutInflater layoutInflater;

    private IClingViewManager iClingViewManager;

    private int singleSelectPosition = -1;

    private final int normalColor = Color.parseColor("#616161");

    public ClingDevicesLvAdapter(Context context, IClingViewManager iClingViewManager) {
        super(context, 0);
        this.iClingViewManager = iClingViewManager;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.nvplayer_item_cling_search_device, null);

        ClingDevice item = getItem(position);
        if (item == null || item.getDevice() == null) {
            return convertView;
        }

        Device device = item.getDevice();

        CardView cardView = convertView.findViewById(R.id.nvplayer_item_cling_search_device_cv_device_item);
        cardView.setCardBackgroundColor(position == singleSelectPosition
                ? getContext().getResources().getColor(R.color.nvpalyer_iosBlue)
                : normalColor
        );
        cardView.setOnClickListener(v -> {
            ClingDevice selectDevice = getItem(position);
            if (Utils.isNull(selectDevice)) {
                return;
            }
            ClingManager.getInstance().setSelectedDevice(selectDevice);
            setSingleSelectPosition(position);
            iClingViewManager.showClingControlPage(device.getDetails().getFriendlyName());
        });

        TextView tvDeviceName = convertView.findViewById(R.id.nvplayer_item_cling_search_device_tv_device_name);
        tvDeviceName.setText(device.getDetails().getFriendlyName());
        return convertView;
    }

    public int getSingleSelectPosition() {
        return singleSelectPosition;
    }

    public void setSingleSelectPosition(int position) {
        this.singleSelectPosition = position;
        notifyDataSetChanged();
    }

    public void clearSelectPosition() {
        this.singleSelectPosition = -1;
    }


}