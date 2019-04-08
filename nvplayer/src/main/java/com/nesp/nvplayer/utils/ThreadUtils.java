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
