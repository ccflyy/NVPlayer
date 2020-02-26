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

package com.nesp.nvplayer.utils.floatUtil;

import android.view.View;

/**
 * Created by yhao on 2017/12/22.
 * https://github.com/yhaolpz
 */

public abstract class IFloatWindow {
    public abstract void show();

    public abstract void hide();

    public abstract int getX();

    public abstract int getY();

    public abstract void updateX(int x);

    public abstract void updateX(@Screen.screenType int screenType, float ratio);

    public abstract void updateY(int y);

    public abstract void updateY(@Screen.screenType int screenType, float ratio);

    public abstract View getView();

    abstract void dismiss();
}
