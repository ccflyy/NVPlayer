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

package com.nesp.nvplayer;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-9-10 上午1:02
 * @project FishMovie
 **/
public class NVPlayer extends PlayerCore {

    public NVPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public NVPlayer(Context context) {
        super(context);
    }

    public NVPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
