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

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.shuyu.gsyvideoplayer.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shuyu on 2016/11/11.
 */

public class NVCommonUtils {
    private static final String TAG = "NVCommonUtils";

    public static void setViewHeight(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (null == layoutParams)
            return;
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    public static String saveBitmap(Bitmap bitmap) throws FileNotFoundException {
        String path = "";
        if (bitmap != null) {
            File file = new File(FileUtils.getPath(), "NESP-" + System.currentTimeMillis() + ".jpg");
            path = file.getAbsolutePath();
            OutputStream outputStream;
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            bitmap.recycle();
        }
        return path;
    }

    public static String getCurrentTimeString() {
        return new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    public static DecimalFormat getDecimalFormat() {
        return new DecimalFormat("0.0");
    }

    public static void cleanGifTmpFile(File fileGIf) {
        new Thread(() -> {
            if (fileGIf == null) return;
            final File parentDir = fileGIf.getParentFile();
            final File[] files;
            if (parentDir != null) {
                files = parentDir.listFiles();
                if (files == null) return;
                for (File fileItem : files) {
                    if (fileItem.getName().endsWith(".tmp")) {
                        fileItem.delete();
                    }
                }
            }
        }).start();
    }

    public static void refreshPhoneImageGallery(Context context, File imageFile) {
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), imageFile.getAbsolutePath(), imageFile.getTitle(), null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageFile.getAbsolutePath())));
    }

    public static void flashView(View view) {
        view.setVisibility(View.VISIBLE);
        final ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(800);
        valueAnimator.setFloatValues(0, 1, 0);
        valueAnimator.addUpdateListener(animation -> view.setAlpha((Float) valueAnimator.getAnimatedValue()));
        valueAnimator.start();
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


}
