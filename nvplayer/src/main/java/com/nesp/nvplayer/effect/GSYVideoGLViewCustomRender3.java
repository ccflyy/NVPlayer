/*
 *
 *   Copyright (c) 2020  NESP Technology Corporation. All rights reserved.
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

package com.nesp.nvplayer.effect;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 2020/2/29 9:03 AM
 * @project FishMovie
 **/

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.nesp.nvplayer.R;
import com.shuyu.gsyvideoplayer.render.glrender.GSYVideoGLViewSimpleRender;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * 图片穿孔透视播放
 */
@SuppressLint("ViewConstructor")
public class GSYVideoGLViewCustomRender3 extends GSYVideoGLViewSimpleRender {

    private int mTexturesBitmap[] = new int[1];

    private BitmapEffect mBitmapEffect = new BitmapEffect();

    public GSYVideoGLViewCustomRender3() {
        super();
    }

    @Override
    protected void bindDrawFrameTexture() {
        super.bindDrawFrameTexture();

        //绑定注入bitmap
        int mFilterInputTextureUniform2 = GLES20.glGetUniformLocation(getProgram(), "sTexture2");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexturesBitmap[0]);
        GLES20.glUniform1i(mFilterInputTextureUniform2, mTexturesBitmap[0]);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        super.onSurfaceCreated(glUnused, config);

        Bitmap bitmap = BitmapFactory.decodeResource(mSurfaceView.getResources(), R.drawable.video_brightness_6_white_36dp);
        //创建bitmap
        GLES20.glGenTextures(1, mTexturesBitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexturesBitmap[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }

    @Override
    protected String getFragmentShader() {
        return mBitmapEffect.getShader(mSurfaceView);
    }


}

