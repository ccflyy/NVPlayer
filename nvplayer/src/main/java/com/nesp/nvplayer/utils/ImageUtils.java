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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.zelory.compressor.Compressor;

/**
 * @Team: NESP Technology
 * @Author: 靳兆鲁
 * Email: 1756404649@qq.com
 * @Time: Created 18-6-4 下午5:00
 * @Project Assistant
 **/
public class ImageUtils {

    private static final String TAG = "ImageUtils";

    /**
     * uri 转 ImageFile
     *
     * @param uri
     * @return
     */
    public static File uriToImageFile(Uri uri, Activity activity) {

        if (uri == null) {
            return null;
        }

        File file;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = activity.managedQuery(uri, proj, null,
                null, null);
        int actual_image_column_index = actualimagecursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor
                .getString(actual_image_column_index);
        file = new File(img_path);
        return file;
    }

    public static Bitmap compressImageFile(Context context, Uri uri, Activity activity) throws IOException {
        return compressImageFile(context, uriToImageFile(uri, activity), 200, 200, 50);
    }

    public static Bitmap compressImageFile(Context context, File imageFile) throws IOException {
        return compressImageFile(context, imageFile, 90, 90, 40);
    }

    public static Bitmap compressImageFile(Context context, File imageFile, int maxWidth, int maxHeight, int quality) throws IOException {
        return new Compressor(context)
                .setMaxWidth(maxWidth)
                .setMaxHeight(maxHeight)
                .setQuality(quality)
                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .compressToBitmap(imageFile);
    }

    public static Bitmap compressImage(Bitmap image, int maxSize) {

        int options = maxSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        while (baos.toByteArray().length / 1024 > options) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= maxSize / 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    /**
     * 用省内存的方法加载图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 从View保存图像的方法：
     *
     * @param view
     * @param context
     */
    public static String saveBitmapFromView(View view, Context context) {
        return saveBitmapFromView(view, context, null);
    }

    /**
     * 从View保存图像的方法：
     *
     * @param view
     * @param context
     */
    public static String saveBitmapFromView(View view, Context context, OnSaveBitmapListener onSaveBitmapListener) {
        int w = view.getWidth();
        int h = view.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        view.layout(0, 0, w, h);
        view.draw(c);
        // 缩小图片
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f); //长和宽放大缩小的比例
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //文件路径
        return saveBitmap(bmp, format.format(new Date(System.currentTimeMillis())) + ".JPEG", context, onSaveBitmapListener);
//        return bmp;
    }

    public static String saveBitmap(Bitmap bitmap, Context context, OnSaveBitmapListener onSaveBitmapListener) {
        return saveBitmap(bitmap, new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date(System.currentTimeMillis())) + ".JPEG",
                context, onSaveBitmapListener);
    }

    /**
     * 保存文件，文件名为当前日期
     */
    public static String saveBitmap(Bitmap bitmap, String bitName, Context context, OnSaveBitmapListener onSaveBitmapListener) {
        String fileName;
        File file;
        if (Build.BRAND.equals("Xiaomi")) { // 小米手机
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + bitName;
        } else {  // Meizu 、Oppo
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/" + bitName;
        }
        file = new File(fileName);

        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
                // 插入图库
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), bitName, null);
                if (onSaveBitmapListener != null) {
                    onSaveBitmapListener.onResult(true,file.getAbsolutePath());
                }
            } else {
                if (onSaveBitmapListener != null) {
                    onSaveBitmapListener.onResult(false,file.getAbsolutePath());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (onSaveBitmapListener != null) {
                onSaveBitmapListener.onResult(false,file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (onSaveBitmapListener != null) {
                onSaveBitmapListener.onResult(false,file.getAbsolutePath());
            }
        }
        // 发送广播，通知刷新图库的显示
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
        //文件路径

        return file.getAbsolutePath();
    }

    public static String saveBitmap(Bitmap bitmap, File file, Context context, OnSaveBitmapListener onSaveBitmapListener) {

        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
                // 插入图库
//                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getTitle(), null);
                if (onSaveBitmapListener != null) {
                    onSaveBitmapListener.onResult(true,file.getAbsolutePath());
                }
            } else {
                if (onSaveBitmapListener != null) {
                    onSaveBitmapListener.onResult(false,file.getAbsolutePath());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (onSaveBitmapListener != null) {
                onSaveBitmapListener.onResult(false,file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (onSaveBitmapListener != null) {
                onSaveBitmapListener.onResult(false,file.getAbsolutePath());
            }
        }
        // 发送广播，通知刷新图库的显示

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        //文件路径
        return file.getAbsolutePath();
    }

    public interface OnSaveBitmapListener {
        void onResult(boolean isSuccess,String path);
    }

    public static void shareImage(Context context,File imageFile,String title){

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            // android 7.0系统解决拍照的问题
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();

        }

        /** * 分享图片 */
         Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("image/*");  //设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_STREAM, getUriFromFile(imageFile));
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, title);
        context.startActivity(share_intent);
    }
    /**
     * 获取本地文件的uri
     * @param file
     * @return
     */
    public static Uri getUriFromFile(File file)  {
        Uri imageUri = null;
        if (file != null && file.exists() && file.isFile()) {
            imageUri = Uri.fromFile(file);
        }
        return imageUri;
    }

}
