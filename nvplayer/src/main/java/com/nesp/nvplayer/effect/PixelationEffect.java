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

import android.opengl.GLSurfaceView;

import com.shuyu.gsyvideoplayer.render.view.GSYVideoGLView;

/**
 * 马赛克效果
 * Created by guoshuyu on 2017/09/20.
 */
public class PixelationEffect implements GSYVideoGLView.ShaderInterface {

    private float pixel = 40f;


    public PixelationEffect() {
    }

    /**
     * 1 - 100
     */
    public PixelationEffect(float pixel) {
        this.pixel = pixel;
    }

    @Override
    public String getShader(GLSurfaceView mGlSurfaceView) {

        String shader = "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +

                "float imageWidthFactor = " + (1 / (float) mGlSurfaceView.getWidth()) + ";\n" +
                "float imageHeightFactor = " + (1 / (float) mGlSurfaceView.getHeight()) + ";\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "float pixel = " + pixel + ";\n" +

                "void main()\n" +
                "{\n" +
                "  vec2 uv  = vTextureCoord.xy;\n" +
                "  float dx = pixel * imageWidthFactor;\n" +
                "  float dy = pixel * imageHeightFactor;\n" +
                "  vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));\n" +
                "  vec3 tc = texture2D(sTexture, coord).xyz;\n" +
                "  gl_FragColor = vec4(tc, 1.0);\n" +
                "}";

        return shader;

    }
}