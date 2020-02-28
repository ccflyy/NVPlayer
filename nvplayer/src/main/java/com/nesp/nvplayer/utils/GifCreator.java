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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.shuyu.gsyvideoplayer.listener.GSYVideoGifSaveListener;
import com.shuyu.gsyvideoplayer.utils.AnimatedGifEncoder;
import com.shuyu.gsyvideoplayer.utils.GifCreateHelper;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static com.nesp.nvplayer.utils.ImageUtils.getRealFilePath;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-9-28 上午12:10
 * @project FishMovie
 **/
public class GifCreator extends GifCreateHelper {

    private Context mContext;
    private String mRelativePath;
    private OnGifSaveListener mOnGifSaveListener;

    public GifCreator(final StandardGSYVideoPlayer standardGSYVideoPlayer, final GSYVideoGifSaveListener gsyVideoGifSaveListener) {
        super(standardGSYVideoPlayer, gsyVideoGifSaveListener);
    }

    public GifCreator(final StandardGSYVideoPlayer standardGSYVideoPlayer, final GSYVideoGifSaveListener gsyVideoGifSaveListener, final int delay, final int inSampleSize, final int scaleSize, final int frequencyCount) {
        super(standardGSYVideoPlayer, gsyVideoGifSaveListener, delay, inSampleSize, scaleSize, frequencyCount);
    }

    public void setContext(final Context context) {
        mContext = context;
    }

    public void setRelativePath(final String relativePath) {
        mRelativePath = relativePath;
    }

    public void setOnGifSaveListener(final OnGifSaveListener onGifSaveListener) {
        mOnGifSaveListener = onGifSaveListener;
    }

    @Override
    public void createGif(File file, List<String> pics, int delay, int inSampleSize, int scaleSize,
                          final GSYVideoGifSaveListener gsyVideoGifSaveListener) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            createGifForAndroidQ(mContext, NVCommonUtils.getCurrentTimeString() + ".gif", mRelativePath, pics, delay, inSampleSize, scaleSize, mOnGifSaveListener);
//            return;
//        }

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

    private void createGifForAndroidQ(final Context context, final String fileName, final String relativePath, List<String> pics, int delay, int inSampleSize, int scaleSize,
                                      final OnGifSaveListener onGifSaveListener) {
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        ContentValues imgDetails = new ContentValues();
        imgDetails.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        imgDetails.put(MediaStore.MediaColumns.MIME_TYPE, "image/gif");
        imgDetails.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath);
        Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imgDetails);
        File file = new File(getRealFilePath(context, uri));

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
                if (onGifSaveListener != null) {
                    onGifSaveListener.result(false, uri, file);
                }
            }
            if (onGifSaveListener != null) {
                onGifSaveListener.process(i + 1, pics.size());
            }
        }
        localAnimatedGifEncoder.finish();//finish
        try {
            OutputStream fos = contentResolver.openOutputStream(uri);
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (onGifSaveListener != null) {
                onGifSaveListener.result(false, uri, file);
            }
            return;
        }
        if (onGifSaveListener != null) {
            onGifSaveListener.result(true, uri, file);
        }
    }

    public interface OnGifSaveListener extends GSYVideoGifSaveListener {
        void result(boolean success, Uri uri, File file);
    }

}
