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

package com.nesp.nvplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

/**
 * @Team: NESP Technology
 * @Author: 靳兆鲁
 * Email: 1756404649@qq.com
 * @Time: Created 2018/8/18 3:58
 * @Project NespMovie
 **/
public class LoadUtils {
    public static void loadImage(Context context, Uri uri, ImageView imageView) {
        Glide.with(context)
                .load(uri)
                .transition(new DrawableTransitionOptions().crossFade(500))
//                .apply(new RequestOptions().placeholder(R.mipmap.img_loading_bg))//加载时的图片
//                .apply(new RequestOptions().error(R.mipmap.img_loading_failed))//加载失败的图片
                .into(imageView);

    }  public static void loadImage(Context context, Bitmap bitmap, ImageView imageView) {
        Glide.with(context)
                .load(bitmap)
                .transition(new DrawableTransitionOptions().crossFade(500))
//                .apply(new RequestOptions().placeholder(R.mipmap.img_loading_bg))//加载时的图片
//                .apply(new RequestOptions().error(R.mipmap.img_loading_failed))//加载失败的图片
                .into(imageView);
    }

    public static void loadImage(Context context, File bitmap, ImageView imageView) {
        Glide.with(context)
                .load(bitmap)
                .transition(new DrawableTransitionOptions().crossFade(500))
//                .apply(new RequestOptions().placeholder(R.mipmap.img_loading_bg))//加载时的图片
//                .apply(new RequestOptions().error(R.mipmap.img_loading_failed))//加载失败的图片
                .into(imageView);
    }

    public static void loadLoginBgImage(Context context, @DrawableRes int resId, ImageView imageView) {
        Glide.with(context)
                .load(context.getDrawable(resId))
                .transition(new DrawableTransitionOptions().crossFade(500))
                .apply(new RequestOptions()
//                        .placeholder(R.mipmap.img_loading_bg)
//                        .error(R.mipmap.img_loading_failed)
                        .override(2000, 1500))//加载时的图片&加载失败的图片
                .into(imageView);
    }

}
