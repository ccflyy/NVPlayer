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
 * @time: Created 2020/2/29 9:02 AM
 * @project FishMovie
 **/

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.shuyu.gsyvideoplayer.render.glrender.GSYVideoGLViewSimpleRender;

import javax.microedition.khronos.opengles.GL10;


/**
 * 双重播放效果
 */
@SuppressLint("ViewConstructor")
public class GSYVideoGLViewCustomRender2 extends GSYVideoGLViewSimpleRender {

    public GSYVideoGLViewCustomRender2() {
        super();
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);
        float[] transform = new float[16];
        Matrix.setIdentityM(transform, 0);
        Matrix.scaleM(transform, 0, 0.8f, 0.8f, 1);
        GLES20.glUniformMatrix4fv(getMuMVPMatrixHandle(), 1, false, transform, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glFinish();
    }
}
