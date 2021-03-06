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
 * @time: Created 2020/2/29 9:00 AM
 * @project FishMovie
 **/

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

import com.shuyu.gsyvideoplayer.render.view.GSYVideoGLView;

/**
 * 水印效果
 */
public class BitmapIconEffect implements GSYVideoGLView.ShaderInterface {

    private final static int NEVER_SET = -5555;

    private GLSurfaceView mGlSurfaceViewl;

    private Bitmap mBitmap;

    private int mWidth = -1;

    private int mHeight = -1;

    private float mAlpha = 1.0f;

    private float mPositionOffset = 1.0f;

    private float mPositionX = NEVER_SET;

    private float mPositionY = NEVER_SET;

    public BitmapIconEffect(Bitmap bitmap) {
        this(bitmap, bitmap.getWidth(), bitmap.getHeight());
    }

    public BitmapIconEffect(Bitmap bitmap, int width, int height) {
        this(bitmap, width, height, 1);
    }

    public BitmapIconEffect(Bitmap bitmap, int width, int height, float alpha) {
        this.mBitmap = bitmap;
        this.mWidth = width;
        this.mHeight = height;
        this.mAlpha = alpha;
    }

    @Override
    public String getShader(GLSurfaceView mGlSurfaceView) {
        this.mGlSurfaceViewl = mGlSurfaceView;
        String shader =
                "#extension GL_OES_EGL_image_external : require\n"
                        + "precision mediump float;\n"
                        + "varying vec2 vTextureCoord;\n"
                        + "uniform samplerExternalOES sTexture;\n"
                        + "uniform sampler2D sTexture2;\n"
                        + "void main() {\n"
                        + "  vec4 c1 = texture2D(sTexture2, vTextureCoord);\n"
                        + "  gl_FragColor = vec4(c1.rgb, c1.a *" + mAlpha + ");\n"
                        + "}\n";
        return shader;

    }

    public void setPositionX(float positionX) {
        this.mPositionX = positionX;
    }

    public void setPositionY(float positionY) {
        this.mPositionY = positionY;
    }

    public float getAlpha() {
        return mAlpha;
    }

    public float getPositionOffset() {
        return mPositionOffset;
    }

    public float getWidth() {
        return (float) mWidth;
    }

    public float getHeight() {
        return (float) mHeight;
    }

    /**
     * 水印图的默认比例
     */
    public float getScaleW() {
        return getWidth() / mGlSurfaceViewl.getWidth();
    }

    /**
     * 水印图的默认比例
     */
    public float getScaleH() {
        return getHeight() / mGlSurfaceViewl.getHeight();
    }

    /**
     * 水印图的起始位置，默认右边
     */
    public float getPositionX() {
        if (mPositionX != NEVER_SET) {
            return mPositionX;
        }
        return -(mGlSurfaceViewl.getWidth() / (getWidth()) - mPositionOffset);
    }

    /**
     * 水印图的起始位置，默认上
     */
    public float getPositionY() {
        if (mPositionY != NEVER_SET) {
            return mPositionY;
        }
        return -(mGlSurfaceViewl.getHeight() / (getHeight()) - mPositionOffset);
    }


    public float getMaxPositionX() {
        return mGlSurfaceViewl.getWidth() / (getWidth()) - mPositionOffset;
    }

    public float getMaxPositionY() {
        return mGlSurfaceViewl.getHeight() / (getHeight()) - mPositionOffset;
    }

    public float getMinPositionX() {
        return -(mGlSurfaceViewl.getWidth() / (getWidth()) - mPositionOffset);
    }

    public float getMinPositionY() {
        return -(mGlSurfaceViewl.getHeight() / (getHeight()) - mPositionOffset);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}