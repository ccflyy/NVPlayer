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

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-4-6 下午8:21
 * @project NVPlayerDemo
 **/
public class ThreadUtils {

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (onThreadRunningListener != null) {
                onThreadRunningListener.onResult(msg);
            }
        }
    };

    private Thread thread;

    public void startNewThread(final OnThreadRunningListener onThreadRunningListener) {
        this.onThreadRunningListener = onThreadRunningListener;
        new Thread(() -> {
            Looper.prepare();
            if (onThreadRunningListener != null) {
                onThreadRunningListener.onStart(handler);
            }
            Looper.loop();
        }).start();
    }

    public void startNewOneThread(final OnThreadRunningListener onThreadRunningListener) {
        this.onThreadRunningListener = onThreadRunningListener;
        if (thread != null) return;
        thread = new Thread(() -> {
            Looper.prepare();
            if (onThreadRunningListener != null) {
                onThreadRunningListener.onStart(handler);
            }
            Looper.loop();
        });
        thread.start();
    }

    private OnThreadRunningListener onThreadRunningListener;

    public interface OnThreadRunningListener {

        void onStart(Handler handler);

        void onResult(Message message);

    }

}
