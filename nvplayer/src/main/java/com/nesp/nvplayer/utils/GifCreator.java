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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import com.shuyu.gsyvideoplayer.listener.GSYVideoGifSaveListener;
import com.shuyu.gsyvideoplayer.utils.AnimatedGifEncoder;
import com.shuyu.gsyvideoplayer.utils.GifCreateHelper;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-9-28 上午12:10
 * @project FishMovie
 **/
public class GifCreator extends GifCreateHelper {

    public GifCreator(final StandardGSYVideoPlayer standardGSYVideoPlayer, final GSYVideoGifSaveListener gsyVideoGifSaveListener) {
        super(standardGSYVideoPlayer, gsyVideoGifSaveListener);
    }

    public GifCreator(final StandardGSYVideoPlayer standardGSYVideoPlayer, final GSYVideoGifSaveListener gsyVideoGifSaveListener, final int delay, final int inSampleSize, final int scaleSize, final int frequencyCount) {
        super(standardGSYVideoPlayer, gsyVideoGifSaveListener, delay, inSampleSize, scaleSize, frequencyCount);
    }

    @Override
    public void createGif(File file, List<String> pics, int delay, int inSampleSize, int scaleSize,
                          final GSYVideoGifSaveListener gsyVideoGifSaveListener) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(delay);
        for (int i = 0; i < pics.size(); i++) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = true; // 先获取原大小
            BitmapFactory.decodeFile(pics.get(i), options);
            double w = (double) options.outWidth / scaleSize;
            double h = (double) options.outHeight / scaleSize;
            options.inJustDecodeBounds = false; // 获取新的大小
            final Bitmap bitmap = BitmapFactory.decodeFile(pics.get(i), options);
            final Bitmap pic = ThumbnailUtils.extractThumbnail(bitmap, (int) w, (int) h);
            localAnimatedGifEncoder.addFrame(pic);
            if (bitmap != null) {
                bitmap.recycle();
            }
            if (pic != null) {
                pic.recycle();
            }
            if (bitmap == null || pic == null) {
                if (gsyVideoGifSaveListener != null) {
                    gsyVideoGifSaveListener.result(false, file);
                }
            }
            if (gsyVideoGifSaveListener != null) {
                gsyVideoGifSaveListener.process(i + 1, pics.size());
            }
        }
        localAnimatedGifEncoder.finish();//finish
        try {
            FileOutputStream fos = new FileOutputStream(file.getPath());
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (gsyVideoGifSaveListener != null) {
                gsyVideoGifSaveListener.result(false, file);
            }
            return;
        }
        if (gsyVideoGifSaveListener != null) {
            gsyVideoGifSaveListener.result(true, file);
        }
    }
}
