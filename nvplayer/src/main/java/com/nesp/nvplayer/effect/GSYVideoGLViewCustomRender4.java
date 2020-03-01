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
 * @time: Created 2020/2/29 9:04 AM
 * @project FishMovie
 **/

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.shuyu.gsyvideoplayer.render.effect.NoEffect;
import com.shuyu.gsyvideoplayer.render.glrender.GSYVideoGLViewSimpleRender;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * 铺满的双重播放
 * 配合高斯模糊，可以实现，高斯拉伸视频铺满背景，替换黑色，前台正常比例播放
 */
@SuppressLint("ViewConstructor")
public class GSYVideoGLViewCustomRender4 extends GSYVideoGLViewSimpleRender {

    private int mProgram;

    public GSYVideoGLViewCustomRender4() {
        super();
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);

        GLES20.glUseProgram(mProgram);

        float[] transform = new float[16];
        Matrix.setIdentityM(transform, 0);
        Matrix.scaleM(transform, 0, (float) mCurrentViewWidth / mSurfaceView.getWidth(),
                (float) mCurrentViewHeight / mSurfaceView.getHeight(), 1);

        GLES20.glUniformMatrix4fv(getMuSTMatrixHandle(), 1, false, mSTMatrix, 0);

        GLES20.glUniformMatrix4fv(getMuMVPMatrixHandle(), 1, false, transform, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glFinish();
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        super.onSurfaceCreated(glUnused, config);
        mProgram = createProgram(getVertexShader(), new NoEffect().getShader(mSurfaceView));
    }

    @Override
    public void initRenderSize() {
        Matrix.scaleM(mMVPMatrix, 0, 1f, 1f, 1);
    }
}
