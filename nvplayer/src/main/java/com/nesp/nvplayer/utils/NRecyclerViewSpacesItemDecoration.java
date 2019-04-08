/*
 *
 *  Copyright (c) 2018  NESP Technology Corporation. All rights reserved.
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms and conditions of the GNU General Public License,
 *  version 2, as published by the Free Software Foundation.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License.See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  If you have any questions or if you find a bug,
 *  please contact the author by email or ask for Issues.
 *
 *  Author:JinZhaolu <1756404649@qq.com>
 *
 */

package com.nesp.nvplayer.utils;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

/**
 * @Team: NESP Technology
 * @Author: 靳兆鲁
 * Email: 1756404649@qq.com
 * @Time: Created 2018/8/18 3:37
 * @Project NespMovie
 **/
public class NRecyclerViewSpacesItemDecoration extends RecyclerView.ItemDecoration {



    private HashMap<String, Integer> mSpaceValueMap;

    public static final String TOP_DECORATION = "top_decoration";
    public static final String BOTTOM_DECORATION = "bottom_decoration";
    public static final String LEFT_DECORATION = "left_decoration";
    public static final String RIGHT_DECORATION = "right_decoration";

    public NRecyclerViewSpacesItemDecoration(HashMap<String, Integer> mSpaceValueMap) {
        this.mSpaceValueMap = mSpaceValueMap;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        if (mSpaceValueMap.get(TOP_DECORATION) != null)
            outRect.top = mSpaceValueMap.get(TOP_DECORATION);
        if (mSpaceValueMap.get(LEFT_DECORATION) != null)

            outRect.left = mSpaceValueMap.get(LEFT_DECORATION);
        if (mSpaceValueMap.get(RIGHT_DECORATION) != null)
            outRect.right = mSpaceValueMap.get(RIGHT_DECORATION);
        if (mSpaceValueMap.get(BOTTOM_DECORATION) != null)

            outRect.bottom = mSpaceValueMap.get(BOTTOM_DECORATION);

    }

}
