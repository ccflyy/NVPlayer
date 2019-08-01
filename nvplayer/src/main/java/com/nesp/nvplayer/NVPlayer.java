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

package com.nesp.nvplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nesp.nvplayer.adapter.NEpisodeRecyclerViewAdapter;
import com.nesp.nvplayer.cling.ClingViewManagerImpl;
import com.nesp.nvplayer.dialog.RightSlideMenuDialog;
import com.nesp.nvplayer.model.NEpisode;
import com.nesp.nvplayer.utils.ImageUtils;
import com.nesp.nvplayer.utils.LoadUtils;
import com.nesp.nvplayer.utils.NRecyclerViewSpacesItemDecoration;
import com.nesp.nvplayer.utils.NVCommonUtils;
import com.nesp.nvplayer.utils.ThreadUtils;
import com.nesp.nvplayer.utils.floatUtil.FloatWindow;
import com.nesp.nvplayer.utils.floatUtil.MoveType;
import com.nesp.nvplayer.utils.floatUtil.Screen;
import com.nesp.nvplayer.utils.floatUtil.Util;
import com.nesp.nvplayer.widget.FloatPlayerView;
import com.nesp.nvplayer.widget.NVPlayerBatteryView;
import com.shuyu.gsyvideoplayer.listener.GSYVideoGifSaveListener;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.GifCreateHelper;
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.super_rabbit.wheel_picker.WheelPicker;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static com.nesp.nvplayer.utils.NVCommonUtils.cleanGifTmpFile;
import static com.nesp.nvplayer.utils.NVCommonUtils.flashView;
import static com.nesp.nvplayer.utils.NVCommonUtils.getDecimalFormat;
import static com.nesp.nvplayer.utils.NVCommonUtils.refreshPhoneImageGallery;
import static com.nesp.nvplayer.utils.NVCommonUtils.syncTimeToUi;
import static com.nesp.nvplayer.utils.UnitUtils.millisecondToString;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-4-6 上午11:24
 * @project NVPlayer
 **/
public class NVPlayer extends NormalGSYVideoPlayer {

    private static final String TAG = "NVPlayer";

    private ExPlayerContext exPlayerContext;

    private Context context;

    protected DecimalFormat decimalFormatGif = getDecimalFormat();

    protected View menuViewMenu;

    protected float speed = 1;

    protected int closeType = 0;

    protected LinearLayout linearLayoutCenterLoading;
    protected TextView textViewNetSpeed;

    protected NVPlayerBatteryView nvPlayerBatteryView;

    //记住切换数据源类型
    protected int mType = 0;

    protected int mTransformSize = 0;

    //数据源
    protected int mSourcePosition = 0;

    protected String appName = "FishMovie";
    protected String imageSavePath = Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_PICTURES + "/" + appName + "/";


    protected RelativeLayout relativeLayoutFullScreenBottomCustomContainer;
    protected RelativeLayout relativeLayoutFullScreenTopCustomContainer;
    protected RelativeLayout relativeLayoutNormalScreenTopCustomContainer;
    protected LinearLayout linearLayoutRightCustomContainerOne;

    protected ImageView imageViewStart, imageViewStartFull, imageViewStartNext, imageViewEnterSmallWin;

    private LinearLayout linearLayoutEnterSmallWinFull;

    protected ImageView imageViewScreenShot, imageViewScreenShotGif;

    protected Boolean isHighScreenShot = false;
    protected GifCreateHelper mGifCreateHelper;

    protected boolean pauseOnScreenshot = true;
    protected final int SHARE_DIALOG_DIMMISS_TIME = pauseOnScreenshot ? 3000 : 5000;
    protected long gifStartTime;
    protected long gifEndTime;
    protected final long MIN_GIF_TIME = 1000;
    protected boolean isShottingGif = false;
    protected Boolean isGifProcessing = false;

    protected RightSlideMenuDialog rightSlideMenuDialogEpisode;
    protected RightSlideMenuDialog rightSlideMenuDialogBottomSpeed;
    protected RightSlideMenuDialog rightSlideMenuDialogTopRightMenuDialog;

    protected boolean byStartedClick;
    protected RelativeLayout relativeLayoutErrorLayout;
    protected TextView textViewErrorText;
    protected String playErrorText = "";
    private ImageView imageViewShare;
    private boolean isClickContinuePlay = false;
    private boolean isDestroy = false;
    private TextView textViewTipCenter;
    private ClingViewManagerImpl clingViewManager;

    private String clingPlayUrl = "";
    private String videoName = "";

    public NVPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public NVPlayer(Context context) {
        super(context);
    }

    public NVPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.nesp_sdk_nvplayer;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        exPlayerContext = new ExPlayerContext();
        this.context = context;
        threadRunningListener = new Thread(() -> {
            while (true) {
                if (isDestroy) return;
                Log.e(TAG, "NVPlayer.onStart: " + getCurrentStringState());
                switch (getCurrentState()) {
                    case CURRENT_STATE_NORMAL:
                        break;
                    case CURRENT_STATE_PREPAREING: {
                        //更新网速
                        handler.sendEmptyMessage(1);
                    }
                    break;
                    case CURRENT_STATE_PLAYING:
                        break;
                    case CURRENT_STATE_PLAYING_BUFFERING_START: {
                        //更新网速
                        handler.sendEmptyMessage(0);
                    }
                    break;
                    case CURRENT_STATE_PAUSE:
                        break;
                    case CURRENT_STATE_AUTO_COMPLETE:
                        break;
                    case CURRENT_STATE_ERROR:
                        break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        initView();
        initSettings();
    }


    private void initSettings() {
        //滑动快进的比例，默认1。数值越大，滑动的产生的seek越小
        setSeekRatio(1000000000);
        //开始视频状态监听器
        startStateListener();
    }

    /*********************************Init View*************************************/
    //TODO:Init View
    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        linearLayoutRightCustomContainerOne = findViewById(R.id.nvplayer_rl_right_custom_container_one);
        relativeLayoutFullScreenBottomCustomContainer = findViewById(R.id.nvplayer_rl_full_screen_bottom_custom_container);
        relativeLayoutFullScreenTopCustomContainer = findViewById(R.id.nvplayer_rl_full_screen_top_custom_container);
        relativeLayoutNormalScreenTopCustomContainer = findViewById(R.id.nvplayer_rl_normal_screen_top_custom_container);

        //TODO:Cling投屏
        findViewById(R.id.nesp_nvplayer_iv_cling_tv)
                .setOnClickListener(v -> {
                    if (!mHadPlay) {
                        Toast.makeText(context, "无视频播放，该功能不可用", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (clingViewManager == null) {
                        clingViewManager = new ClingViewManagerImpl(
                                (AppCompatActivity) context
                                , this
                                , videoName
                                , nEpisodeList
                                , nEpisodeRecyclerViewAdapter.getSelectPosition()
                                , exPlayerContext.videoHeaderTime
                                , exPlayerContext.videoTailTime
                        );
                    }
                    clingViewManager.initView();
                    clingViewManager.showClingSearchDevicePage();
                });

        /*********************************底部的控件*************************************/
        initBottomView();
        /*********************************预览*************************************/
        initPreView();
        /*********************************中间的控件*************************************/

        textViewTipCenter = findViewById(R.id.nvplayer_tv_tip_center);

        textViewNetSpeed = findViewById(R.id.nesp_nvplayer_tv_net_speed);
        linearLayoutCenterLoading = findViewById(R.id.nesp_nvplayer_ll_center_loading);
        //封面图
        if (mThumbImageViewLayout != null &&
                (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.setVisibility(VISIBLE);
        }

        //错误视图
        relativeLayoutErrorLayout = findViewById(R.id.nvplayer_rl_error);
        textViewErrorText = findViewById(R.id.nvplayer_tv_error);
        if (playErrorText != null && !playErrorText.isEmpty())
            textViewErrorText.setText(playErrorText);

        //================================截图功能=======================================

        initScreenshotView();

        //================================GIF功能=======================================

        initScreenshotGifView();

        //================================顶部=======================================
        initTopMenuView();
        syncTimeToUi(findViewById(R.id.nvplayer_tv_time));

        imageViewShare = findViewById(R.id.nesp_nvplayer_iv_share);
        imageViewShare.setOnClickListener(v -> {
            if (onShareClickListener != null) {
                onShareClickListener.onClick(v);
            }
        });
    }

    /*********************************initTopMenuView*************************************/
    //TODO:initTopMenuView
    private void initTopMenuView() {
        nvPlayerBatteryView = findViewById(R.id.nesp_nvplayer_nvplayer_battery_view);
        menuViewMenu = findViewById(R.id.nesp_nvplayer_iv_menu);
        menuViewMenu.setOnClickListener(v -> {
            if (showNoVideoPlayNotFunctionToast()) return;
            //菜单选项
            View view = LayoutInflater.from(context).inflate(R.layout.nvplayer_right_top_menu_dialog, null);

            // TODO: 19-6-13 video header time
            TextView textViewVideoHeaderTime = view.findViewById(R.id.nvpalyer_right_top_menu_tv_video_header_time);
            TextView textViewVideoTailTime = view.findViewById(R.id.nvpalyer_right_top_menu_tv_video_tail_time);
            textViewVideoHeaderTime.setText(millisecondToString(exPlayerContext.getVideoHeaderTime()).split("h")[1]);
            textViewVideoTailTime.setText(millisecondToString(exPlayerContext.videoTailTime).split("h")[1]);
            view.findViewById(R.id.nvpalyer_right_top_menu_ll_video_header_time).setOnClickListener(v12 -> showSetVideoHeaderTailTime(true, textViewVideoHeaderTime));

            view.findViewById(R.id.nvpalyer_right_top_menu_ll_video_tail_time).setOnClickListener(v13 -> showSetVideoHeaderTailTime(false, textViewVideoTailTime));

            linearLayoutEnterSmallWinFull = view.findViewById(R.id.nvpalyer_right_top_menu_ll_small_full);
            linearLayoutEnterSmallWinFull.setOnClickListener(v1 -> startFloatWin());

            final SeekBar seekBarBrightness = view.findViewById(R.id.nvpalyer_right_top_menu_sb_light);

            seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                int preProgress = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) return;
                    float percent = (float) (progress - preProgress) / 100 + 0.00f;
                    onSlideDialogBrightnessSlide(percent);
                    preProgress = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    preProgress = seekBar.getProgress();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            final RadioGroup radioGroupScale = view.findViewById(R.id.nvpalyer_right_top_menu_rg_scale);

            radioGroupScale.setOnCheckedChangeListener((group, checkedId) -> {
                if (!mHadPlay) {
                    Toast.makeText(context, "没有影片,设置失败!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (checkedId == R.id.nvpalyer_right_top_menu_rb_scale_default) {
                    mType = 0;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_scale_100) {
                    mType = 3;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_scale_full) {
                    mType = 4;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_scale_75) {
                    mType = 1;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_scale_50) {
                    mType = 2;
                }
                resolveTypeUI();
            });

            RadioGroup radioGroupSpeed = view.findViewById(R.id.nvpalyer_right_top_menu_rg_speed);
            radioGroupSpeed.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_2_0) {
                    speed = 2f;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_1_5) {
                    speed = 1.5f;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_1_25) {
                    speed = 1.25f;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_1) {
                    speed = 1f;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_0_75) {
                    speed = 0.75f;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_0_5) {
                    speed = 0.5f;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_0_25) {
                    speed = 0.25f;
                }
                setSpeedPlaying(speed, true);
            });


            RadioGroup radioGroupClose = view.findViewById(R.id.nvpalyer_right_top_menu_rg_close);
            radioGroupClose.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.nvpalyer_right_top_menu_rb_close_disable) {
                    closeType = 0;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_close_1) {
                    closeType = 1;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_close_30) {
                    closeType = 2;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_close_60) {
                    closeType = 3;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_close_90) {
                    closeType = 4;
                }
                resolveClose();
            });


            rightSlideMenuDialogTopRightMenuDialog = new RightSlideMenuDialog(context, view);
            rightSlideMenuDialogTopRightMenuDialog.setOnShowListener(dialog -> {
                //初始化亮度控件
                seekBarBrightness.setProgress((int) onSlideDialogBrightnessSlide(0));
                //初始化尺寸控件
                int scaleRbId = R.id.nvpalyer_right_top_menu_rb_scale_default;
                if (mHadPlay) {
                    if (mType == 1) {
                        scaleRbId = R.id.nvpalyer_right_top_menu_rb_scale_75;
                    } else if (mType == 2) {
                        scaleRbId = R.id.nvpalyer_right_top_menu_rb_scale_50;
                    } else if (mType == 3) {
                        scaleRbId = R.id.nvpalyer_right_top_menu_rb_scale_100;
                    } else if (mType == 4) {
                        scaleRbId = R.id.nvpalyer_right_top_menu_rb_scale_full;
                    } else if (mType == 0) {
                        scaleRbId = R.id.nvpalyer_right_top_menu_rb_scale_default;
                    }
                }
                ((RadioButton) rightSlideMenuDialogTopRightMenuDialog.findViewById(scaleRbId)).setChecked(true);

                //初始化播放速度控件
                int speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_1;
                if (speed == 2) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_2_0;
                } else if (speed == 1.5f) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_1_5;
                } else if (speed == 1.25f) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_1_25;
                } else if (speed == 1f) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_1;
                } else if (speed == 0.75f) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_0_75;
                } else if (speed == 0.5f) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_0_5;
                } else if (speed == 0.25f) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_0_25;
                }

                ((RadioButton) rightSlideMenuDialogTopRightMenuDialog.findViewById(speedRbId)).setChecked(true);

                //TODO:初始化定时关闭控件
                int closeRbId = R.id.nvpalyer_right_top_menu_rb_close_disable;
                if (closeType == 0) {
                    closeRbId = R.id.nvpalyer_right_top_menu_rb_close_disable;
                } else if (closeType == 1) {
                    closeRbId = R.id.nvpalyer_right_top_menu_rb_close_1;
                } else if (closeType == 2) {
                    closeRbId = R.id.nvpalyer_right_top_menu_rb_close_30;
                } else if (closeType == 3) {
                    closeRbId = R.id.nvpalyer_right_top_menu_rb_close_60;
                } else if (closeType == 4) {
                    closeRbId = R.id.nvpalyer_right_top_menu_rb_close_90;
                }
//                resolveClose();
                ((RadioButton) rightSlideMenuDialogTopRightMenuDialog.findViewById(closeRbId)).setChecked(true);
            });
            rightSlideMenuDialogTopRightMenuDialog.show();
        });
    }

    protected void showSetVideoHeaderTailTime(Boolean isHeaderTime, TextView textView) {
        String time = textView.getText().toString();
        String tvMin = time.split("m")[0];
        String tvSec = time.split("s")[0].split("m")[1];

        String title = isHeaderTime ? "片头时长" : "片尾时长";

        View view = LayoutInflater.from(context).inflate(R.layout.nvplayer_dialog_set_video_header_tail_time, null);

        WheelPicker wheelPickerMin = view.findViewById(R.id.nvplayer_dialog_set_video_header_tail_time_min);
        WheelPicker wheelPickerSec = view.findViewById(R.id.nvplayer_dialog_set_video_header_tail_time_sec);
        wheelPickerMin.setSelectorRoundedWrapPreferred(true);
        wheelPickerSec.setSelectorRoundedWrapPreferred(true);
        wheelPickerMin.setSelectedTextColor(R.color.color_4_blue);
        wheelPickerSec.setSelectedTextColor(R.color.color_4_blue);
        wheelPickerMin.setUnselectedTextColor(R.color.color_3_dark_blue);
        wheelPickerSec.setUnselectedTextColor(R.color.color_3_dark_blue);

        wheelPickerMin.scrollToValue(tvMin);
        wheelPickerSec.scrollToValue(tvSec);

        AlertDialog setVideoHeaderTailTimeDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialog, which) -> {
                    textView.setText(wheelPickerMin.getCurrentItem() + "m" + wheelPickerSec.getCurrentItem() + "s");
                    if (isHeaderTime) {
                        exPlayerContext.setVideoHeaderTime(Integer.parseInt(wheelPickerMin.getCurrentItem()) * 60 * 1000 +
                                Integer.parseInt(wheelPickerSec.getCurrentItem()) * 1000L);
                    } else {
                        exPlayerContext.setVideoTailTime(Integer.parseInt(wheelPickerMin.getCurrentItem()) * 60 * 1000 +
                                Integer.parseInt(wheelPickerSec.getCurrentItem()) * 1000L);
                    }
                    if (onSetVideoHeaderTailTimeListener != null) {
                        onSetVideoHeaderTailTimeListener.onResult(isHeaderTime, Integer.parseInt(wheelPickerMin.getCurrentItem()) * 60 * 1000 +
                                Integer.parseInt(wheelPickerSec.getCurrentItem()) * 1000);
                    }
                })
                .setOnDismissListener(dialog -> {
                    if (!getGSYVideoManager().isPlaying()) {
                        imageViewStartFull.performClick();
                    }
                }).create();
        setVideoHeaderTailTimeDialog.getWindow().getDecorView().setAlpha(0.8f);
        setVideoHeaderTailTimeDialog.setOnShowListener(dialog -> {
            if (getGSYVideoManager().isPlaying()) {
                imageViewStartFull.performClick();
            }
        });

        setVideoHeaderTailTimeDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        setVideoHeaderTailTimeDialog.show();
        fullScreenImmersive(setVideoHeaderTailTimeDialog.getWindow().getDecorView());
        setVideoHeaderTailTimeDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        Button buttonOK = setVideoHeaderTailTimeDialog.findViewById(android.R.id.button1);
        buttonOK.setEnabled(false);

        wheelPickerMin.setOnValueChangeListener((wheelPicker, oldValue, newValue) -> {
            if (newValue.equals(tvMin) && wheelPickerSec.getCurrentItem().equals(tvSec)) {
                buttonOK.setEnabled(false);
            } else {
                buttonOK.setEnabled(true);
            }
        });

        wheelPickerSec.setOnValueChangeListener((wheelPicker, oldValue, newValue) -> {
            if (newValue.equals(tvSec) && wheelPickerMin.getCurrentItem().equals(tvMin)) {
                buttonOK.setEnabled(false);
            } else {
                buttonOK.setEnabled(true);
            }
        });

    }

    private OnSetVideoHeaderTailTimeListener onSetVideoHeaderTailTimeListener;

    public NVPlayer setOnSetVideoHeaderTailTimeListener(OnSetVideoHeaderTailTimeListener onSetVideoHeaderTailTimeListener) {
        this.onSetVideoHeaderTailTimeListener = onSetVideoHeaderTailTimeListener;
        return this;
    }

    public interface OnSetVideoHeaderTailTimeListener {
        void onResult(Boolean isHeaderTime, long time);
    }

    public NVPlayer setClingPlayUrl(String clingPlayUrl) {
        this.clingPlayUrl = clingPlayUrl;
        return this;
    }

    private void initBottomView() {
        imageViewStart = findViewById(R.id.nvplayer_iv_start);
        imageViewStart.setOnClickListener(v -> clickStartIcon());
        imageViewStartFull = findViewById(R.id.nvplayer_iv_start_full);
        imageViewStartFull.setOnClickListener(v -> clickStartIcon());
        imageViewStartNext = findViewById(R.id.nvplayer_iv_start_next);
        imageViewStartNext.setOnClickListener(v -> {
            if (onNextPlayClickListener != null) {
                onNextPlayClickListener.onClick(v);
            }
        });

        imageViewEnterSmallWin = findViewById(R.id.nvplayer_iv_enter_small_win);
        imageViewEnterSmallWin.setOnClickListener(v -> startFloatWin());

        findViewById(R.id.nvpalyer_bottom_slide_tv_speed)
                .setOnClickListener(v -> {
                    if (showNoVideoPlayNotFunctionToast()) return;
                    View viewBottomSpeedDialog = LayoutInflater.from(v.getContext())
                            .inflate(R.layout.nvplayer_bottom_speed_dialog, null);
                    final RadioGroup radioGroupBottomSpeed = viewBottomSpeedDialog.findViewById(R.id.nvpalyer_bottom_menu_rg_speed);
                    radioGroupBottomSpeed.setOnCheckedChangeListener((group, checkedId) -> {

                        if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_2_0) {
                            speed = 2f;
                        } else if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_1_5) {
                            speed = 1.5f;
                        } else if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_1_25) {
                            speed = 1.25f;
                        } else if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_1) {
                            speed = 1;
                        } else if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_0_75) {
                            speed = 0.75f;
                        } else if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_0_5) {
                            speed = 0.5f;
                        } else if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_0_25) {
                            speed = 0.25f;
                        }
                        setSpeedPlaying(speed, true);
                    });

                    rightSlideMenuDialogBottomSpeed = new RightSlideMenuDialog(context, viewBottomSpeedDialog);

                    rightSlideMenuDialogBottomSpeed.setOnShowListener(dialog -> {
                        //初始化播放速度控件
                        int speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_1;
                        if (speed == 2) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_2_0;
                        } else if (speed == 1.5f) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_1_5;
                        } else if (speed == 1.25f) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_1_25;
                        } else if (speed == 1) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_1;
                        } else if (speed == 0.75f) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_0_75;
                        } else if (speed == 0.5f) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_0_5;
                        } else if (speed == 0.25f) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_0_25;
                        }
                        ((RadioButton) rightSlideMenuDialogBottomSpeed.findViewById(speedRbId)).setChecked(true);
                    });
                    rightSlideMenuDialogBottomSpeed.show();
                    rightSlideMenuDialogBottomSpeed.setWidth(725);
                });


        // TODO: 19-6-14 init episode
        View viewEpisodeDialogView = LayoutInflater.from(context).inflate(R.layout.nvplayer_episode_dialog, null);
        RecyclerView recyclerViewEpisode = viewEpisodeDialogView.findViewById(R.id.nvplayer_episode_dialog_rv);

        rightSlideMenuDialogEpisode = new RightSlideMenuDialog(context, viewEpisodeDialogView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 5);

        nEpisodeList = new ArrayList<>();
        nEpisodeList.add(new NEpisode("", ""));
        nEpisodeList.clear();
        nEpisodeRecyclerViewAdapter = new NEpisodeRecyclerViewAdapter(nEpisodeList, context, recyclerViewEpisode);
        nEpisodeRecyclerViewAdapter.setOnEpisodeItemClickListener((episode, position) -> {
            rightSlideMenuDialogEpisode.dismiss();
            if (onEpisodeItemClickListener != null)
                onEpisodeItemClickListener.onEpisodeItemClick(episode, position);
        });

        recyclerViewEpisode.setAdapter(nEpisodeRecyclerViewAdapter);
        recyclerViewEpisode.setLayoutManager(gridLayoutManager);
        configVideoEpisodeRv(recyclerViewEpisode);

        textViewSelectEpisode = findViewById(R.id.nvpalyer_bottom_slide_tv_episode);
        textViewSelectEpisode.setOnClickListener(v -> {
            rightSlideMenuDialogEpisode.show();
            rightSlideMenuDialogEpisode.setWidth(845);
        });

    }

    private Boolean showNoVideoPlayNotFunctionToast() {
        if (getCurrentState() == CURRENT_STATE_ERROR || getCurrentState() == CURRENT_STATE_NORMAL) {
            Toast.makeText(context, "当前无视频播放,暂不可用", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void initGifHelper() {
        mGifCreateHelper = new GifCreateHelper(this, new GSYVideoGifSaveListener() {

            @Override
            public void result(boolean success, File file) {
                mGifCreateHelper.cancelTask();
                post(() -> {
                    isGifProcessing = false;
                    if (success) {
                        setViewShowState(imageViewShotGifDialogGif, VISIBLE);
                        setViewShowState(textViewShotGifDialogState, VISIBLE);
                        LoadUtils.loadImage(context, file, imageViewShotGifDialogGif);
                        imageViewShotGifDialogGif.setOnClickListener(v -> ImageUtils.shareImage(context, file, "分享GIF"));
                        textViewShotGifDialogTitle.setText("点击分享");
                        textViewShotGifDialogState.setText("已保存至:" + file.getAbsolutePath());
                        refreshPhoneImageGallery(context, file);
                    } else {
                        textViewShotGifDialogTitle.setText("GIF截取失败");
                    }
                    cleanGifTmpFile(file);
                });
            }

            @Override
            public void process(int curPosition, int total) {
                isGifProcessing = true;
                Message message = new Message();
                message.obj = new Integer[]{curPosition, total};
                handlerGifProcessing.sendMessage(message);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handlerGifProcessing = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Integer[] integers = (Integer[]) msg.obj;
            textViewShotGifDialogTitle.setText("正在保存:" + decimalFormatGif.format(integers[0] * 100.000f / integers[1]) + "%");
        }
    };

    // TODO: 19-4-23 截图View
    @SuppressLint("ClickableViewAccessibility")
    private void initScreenshotGifView() {
        View viewShotGifDialog = LayoutInflater.from(context).inflate(R.layout.nvplayer_video_share_screenshot_gif_dialog, null);
        rightSlideMenuDialogShotGif = new RightSlideMenuDialog(context, R.style.nvplayer_dialog_TRANSLUCENT, Gravity.CENTER, viewShotGifDialog);
        textViewShotGifDialogTitle = viewShotGifDialog.findViewById(R.id.nvplayer_share_screenshot_gif_dialog_tv_title);
        textViewShotGifDialogState = viewShotGifDialog.findViewById(R.id.nvplayer_share_screenshot_gif_dialog_tv_state);
        imageViewShotGifDialogGif = viewShotGifDialog.findViewById(R.id.nvplayer_share_screenshot_gif_dialog_iv);
        imageViewShotGifDialogClose = viewShotGifDialog.findViewById(R.id.nvplayer_share_screenshot_gif_dialog_iv_close);
        rightSlideMenuDialogShotGif.setOnDismissListener(dialog -> imageViewScreenShotGif.setVisibility(GONE));
        imageViewShotGifDialogClose.setOnClickListener(v -> rightSlideMenuDialogShotGif.dismiss());

        imageViewScreenShotGif = findViewById(R.id.nvplayer_iv_screenshot_gif);
        imageViewScreenShotGif.setOnTouchListener((v, event) -> {
            File fileGifDir = new File(imageSavePath + "/Gif/");
            if (!fileGifDir.exists()) {
                fileGifDir.mkdirs();
            }
            File fileGif = new File(fileGifDir.getAbsolutePath() + "/" + NVCommonUtils.getCurrentTimeString() + ".gif");

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!mHadPlay) {
                        Toast.makeText(context, "GIF功能暂不可用,请稍后重试", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    rightSlideMenuDialogShotGif.show();
                    rightSlideMenuDialogShotGif.setCancelable(false);
                    imageViewShotGifDialogGif.setVisibility(GONE);
                    textViewShotGifDialogState.setVisibility(GONE);
                    rightSlideMenuDialogShotGif.setSize(1296, 864);
                    isShottingGif = true;
                    setViewShowState(imageViewScreenShotGif, VISIBLE);
                    gifStartTime = System.currentTimeMillis();
                    initGifHelper();
                    mGifCreateHelper.startGif(fileGifDir);

                    new ThreadUtils().startNewThread(new ThreadUtils.OnThreadRunningListener() {
                        @Override
                        public void onStart(Handler handler) {

                            while (isShottingGif) {
                                try {
                                    Thread.sleep(50);
                                    if (isShottingGif)
                                        handler.sendEmptyMessage(0);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onResult(Message message) {
                            textViewShotGifDialogTitle.setText("正在截取:" + decimalFormatGif.format((System.currentTimeMillis() - gifStartTime) / 1000.0f) + "秒");
                        }
                    });
                    break;
                case MotionEvent.ACTION_UP:
                    isShottingGif = false;
                    gifEndTime = System.currentTimeMillis();
                    if (gifEndTime - gifStartTime < MIN_GIF_TIME) {
                        rightSlideMenuDialogShotGif.dismiss();
                        Toast.makeText(context, "截取时间少于" + MIN_GIF_TIME / 1000 + "秒", Toast.LENGTH_SHORT).show();
                    }
                    mGifCreateHelper.stopGif(fileGif);
                    break;
            }
            return true;
        });
    }

    private void initScreenshotView() {
        imageViewScreenShot = findViewById(R.id.nvplayer_iv_screenshot);
        imageViewScreenShot.setOnClickListener(v -> {
            //闪光动画
            if (pauseOnScreenshot) flashView(findViewById(R.id.navplayer_flash_view));
            taskShotPic(bitmap -> {
                if (bitmap == null)
                    Toast.makeText(context, "获取截图失败,请重试!", Toast.LENGTH_LONG).show();

                File fileImageDir = new File(imageSavePath + "/Screenshots/");

                if (!fileImageDir.exists()) fileImageDir.mkdirs();
                File fileImage = new File(fileImageDir.getAbsolutePath() + "/" + NVCommonUtils.getCurrentTimeString() + ".jpg");

                ImageUtils.saveBitmap(bitmap
                        , fileImage
                        , context, (isSuccess, path) -> {
                            if (!isSuccess) {
                                Toast.makeText(context, "获取截图失败,请重试!", Toast.LENGTH_LONG).show();
                            } else {
                                View viewShareImageDialog = LayoutInflater.from(context).inflate(R.layout.nvplayer_video_share_screenshot_dialog, null);
                                ImageView imageView = viewShareImageDialog.findViewById(R.id.nvplayer_share_screenshot_dialog_iv);
                                viewShareImageDialog.setOnClickListener(v1 -> ImageUtils.shareImage(context, new File(path), "分享截图"));
                                LoadUtils.loadImage(context, bitmap, imageView);
                                RightSlideMenuDialog rightSlideMenuDialogShareImage = new RightSlideMenuDialog(context
                                        , R.style.nvplayer_dialog_TRANSLUCENT
                                        , viewShareImageDialog);
                                rightSlideMenuDialogShareImage.setOnShowListener(dialog -> {
                                    if (pauseOnScreenshot) {
                                        imageViewStartFull.performClick();
                                    }
                                    new ThreadUtils().startNewThread(new ThreadUtils.OnThreadRunningListener() {
                                        @Override
                                        public void onStart(Handler handler) {
                                            try {
                                                Thread.sleep(SHARE_DIALOG_DIMMISS_TIME);
                                                handler.sendEmptyMessage(0);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onResult(Message message) {
                                            rightSlideMenuDialogShareImage.dismiss();
                                        }
                                    });
                                });
                                rightSlideMenuDialogShareImage.setOnDismissListener(dialog -> {
                                    if (pauseOnScreenshot) {
                                        imageViewStartFull.performClick();
                                    }
                                });
                                rightSlideMenuDialogShareImage.show();
                                rightSlideMenuDialogShareImage.setSize(345, 255);
                                rightSlideMenuDialogShareImage.offset(0, 0, 80, 0);
                                Toast.makeText(context, "已保存至 " + path, Toast.LENGTH_LONG).show();
                            }
                        });
            }, isHighScreenShot);
        });
    }

    private RightSlideMenuDialog rightSlideMenuDialogShotGif;
    private TextView textViewShotGifDialogTitle;
    private TextView textViewShotGifDialogState;
    private ImageView imageViewShotGifDialogGif;
    private ImageView imageViewShotGifDialogClose;

    private Thread threadRunningListener;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "NVPlayer.onResult: getNetSpeed___" + getNetSpeed());
            textViewNetSpeed.setText(getNetSpeedText());
        }
    };

    private String getCurrentStringState() {
        String state = "";
        switch (getCurrentState()) {
            case CURRENT_STATE_NORMAL:
                state = "CURRENT_STATE_NORMAL";
                break;
            case CURRENT_STATE_PREPAREING:
                state = "CURRENT_STATE_PREPAREING";
                break;
            case CURRENT_STATE_PLAYING:
                state = "CURRENT_STATE_PLAYING";
                break;
            case CURRENT_STATE_PLAYING_BUFFERING_START:
                state = "CURRENT_STATE_PLAYING_BUFFERING_START";
                break;
            case CURRENT_STATE_PAUSE:
                state = "CURRENT_STATE_PAUSE";
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                state = "CURRENT_STATE_AUTO_COMPLETE";
                break;
            case CURRENT_STATE_ERROR:
                state = "CURRENT_STATE_ERROR";
                break;
        }
        return state;
    }

    private void startStateListener() {
        threadRunningListener.start();
    }

    @Override
    public String getNetSpeedText() {
        long speed = getNetSpeed();
        String[] units = new String[]{"B/s", "KB/s", "MB/s", "GB/s"};
        String speedText;
        int unitIndex;
        if (isMiddleIn(speed, 0, (long) 102.4)) {
            unitIndex = 0;
            speedText = speedTextFormat(speed, 1);
        } else if (speed < (1024 * 1024)) {
            unitIndex = 1;
            speedText = speedTextFormat(speed, 1024);
        } else if (speed < (1024 * 1024 * 1024)) {
            unitIndex = 2;
            speedText = speedTextFormat(speed, 1024 * 1024);
        } else {
            unitIndex = 3;
            speedText = speedTextFormat(speed, 1024 * 1024 * 1024);
        }
        return speedText + units[unitIndex];
    }


    private String speedTextFormat(long up, long down) {
        return decimalFormatGif.format((double) up / (double) down);
    }

    private Boolean isMiddleIn(long sum, double small, long large) {
        return (sum >= small) && (sum < large);
    }

    //================================声音和光度Dialog=======================================

    @Override
    protected int getVolumeLayoutId() {
        return R.layout.nvplayer_video_volume_dialog;
    }

    @Override
    protected int getVolumeProgressId() {
        return R.id.nvPlayer_volume_progressbar;
    }

    @Override
    protected int getBrightnessLayoutId() {
        return R.layout.nvplayer_video_brightness_dialog;
    }

    protected int getBrightnessProgressId() {
        return R.id.nvPlayer_brightness_progressbar;
    }


    //亮度文本
    protected ProgressBar mBrightnessProgressBar;

    private Boolean isExitApp = false;

    private Boolean isHaveNext = true;


    /**
     * 触摸亮度dialog，如需要自定义继承重写即可，记得重写dismissBrightnessDialog
     */
    @Override
    protected void showBrightnessDialog(float percent) {
        if (mBrightnessDialog == null) {
            View localView = LayoutInflater.from(getActivityContext()).inflate(getBrightnessLayoutId(), null);
            if (localView.findViewById(getBrightnessProgressId()) instanceof ProgressBar) {
                mBrightnessProgressBar = localView.findViewById(getBrightnessProgressId());
            }
//            if (localView.findViewById(getBrightnessTextId()) instanceof TextView) {
//                mBrightnessDialogTv = localView.findViewById(getBrightnessTextId());
//            }
            mBrightnessDialog = new Dialog(getActivityContext(), R.style.video_style_dialog_progress);
            mBrightnessDialog.setContentView(localView);
            mBrightnessDialog.getWindow().addFlags(8);
            mBrightnessDialog.getWindow().addFlags(32);
            mBrightnessDialog.getWindow().addFlags(16);
            mBrightnessDialog.getWindow().setLayout(-2, -2);
            WindowManager.LayoutParams localLayoutParams = mBrightnessDialog.getWindow().getAttributes();
            localLayoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
            localLayoutParams.width = getWidth();
            localLayoutParams.height = getHeight();
            int[] location = new int[2];
            getLocationOnScreen(location);
            localLayoutParams.x = location[0];
            localLayoutParams.y = location[1];
            mBrightnessDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }

        int percentInt = (int) (percent * 100);
        ImageView imageViewBrightness = mBrightnessDialog.findViewById(R.id.nvPlayer_brightness_iv_icon);

        if (percentInt < 50) {
            imageViewBrightness.setImageDrawable(getResources().getDrawable(R.drawable.ic_nvplayer_night));
        } else {
            imageViewBrightness.setImageDrawable(getResources().getDrawable(R.drawable.ic_nvplayer_brightness));
        }
        if (mBrightnessProgressBar != null)
            mBrightnessProgressBar.setProgress(percentInt);
    }

    @Override
    protected void dismissBrightnessDialog() {
        super.dismissBrightnessDialog();
    }

    @Override
    protected void showVolumeDialog(float deltaY, int volumePercent) {
        if (mVolumeDialog == null) {
            View localView = LayoutInflater.from(getActivityContext()).inflate(getVolumeLayoutId(), null);
            if (localView.findViewById(getVolumeProgressId()) instanceof ProgressBar) {
                mDialogVolumeProgressBar = localView.findViewById(getVolumeProgressId());
                if (mVolumeProgressDrawable != null && mDialogVolumeProgressBar != null) {
                    mDialogVolumeProgressBar.setProgressDrawable(mVolumeProgressDrawable);
                }
            }
            mVolumeDialog = new Dialog(getActivityContext(), R.style.video_style_dialog_progress);
            mVolumeDialog.setContentView(localView);
            mVolumeDialog.getWindow().addFlags(8);
            mVolumeDialog.getWindow().addFlags(32);
            mVolumeDialog.getWindow().addFlags(16);
            mVolumeDialog.getWindow().setLayout(-2, -2);
            WindowManager.LayoutParams localLayoutParams = mVolumeDialog.getWindow().getAttributes();
            localLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            localLayoutParams.width = getWidth();
            localLayoutParams.height = getHeight();
            int[] location = new int[2];
            getLocationOnScreen(location);
            localLayoutParams.x = location[0];
            localLayoutParams.y = location[1];
            mVolumeDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }

        ImageView imageViewVolume = mVolumeDialog.findViewById(R.id.nvPlayer_volume_iv_icon);
        if (volumePercent <= 0) {
            imageViewVolume.setImageDrawable(getResources().getDrawable(R.drawable.ic_nvplayer_volume_none));
        } else if (volumePercent < 50) {
            imageViewVolume.setImageDrawable(getResources().getDrawable(R.drawable.ic_nvplayer_volume_small));
        } else {
            imageViewVolume.setImageDrawable(getResources().getDrawable(R.drawable.ic_nvplayer_volume_large));
        }

        if (mDialogVolumeProgressBar != null) {
            mDialogVolumeProgressBar.setProgress(volumePercent);
        }
    }

    @Override
    protected void dismissVolumeDialog() {
        super.dismissVolumeDialog();
    }

    @Override
    protected void touchSurfaceMove(float deltaX, float deltaY, float y) {
        int curWidth = CommonUtil.getCurrentScreenLand((Activity) getActivityContext()) ? mScreenHeight : mScreenWidth;
        int curHeight = CommonUtil.getCurrentScreenLand((Activity) getActivityContext()) ? mScreenWidth : mScreenHeight;

        if (mChangePosition) {
            int totalTimeDuration = getDuration();
            mSeekTimePosition = (int) (mDownPosition + (deltaX * totalTimeDuration / curWidth) / mSeekRatio);
            if (mSeekTimePosition > totalTimeDuration)
                mSeekTimePosition = totalTimeDuration;
            String seekTime = CommonUtil.stringForTime(mSeekTimePosition);
            String totalTime = CommonUtil.stringForTime(totalTimeDuration);
            showProgressDialog(deltaX, seekTime, mSeekTimePosition, totalTime, totalTimeDuration);
        } else if (mChangeVolume) {
            deltaY = -deltaY / 2f;
            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int deltaV = (int) (max * deltaY * 3 / curHeight);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
            int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / curHeight);

            showVolumeDialog(-deltaY, volumePercent);
        } else if (!mChangePosition && mBrightness) {
            if (Math.abs(deltaY) > 10) {
                float percent = (-deltaY / curHeight);
                onBrightnessSlide(percent);
                mDownY = y;
            }
        }
    }

    @Override
    protected void onBrightnessSlide(float percent) {
        mBrightnessData = ((Activity) (mContext)).getWindow().getAttributes().screenBrightness;
        if (mBrightnessData <= 0.00f) {
            mBrightnessData = 0.50f;
        } else if (mBrightnessData < 0.01f) {
            mBrightnessData = 0.01f;
        }
        WindowManager.LayoutParams lpa = ((Activity) (mContext)).getWindow().getAttributes();
        lpa.screenBrightness = mBrightnessData + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        showBrightnessDialog(lpa.screenBrightness);
        ((Activity) (mContext)).getWindow().setAttributes(lpa);
    }


    protected float onSlideDialogBrightnessSlide(float percent) {
        mBrightnessData = ((Activity) (mContext)).getWindow().getAttributes().screenBrightness;
        if (mBrightnessData <= 0.00f) {
            mBrightnessData = 0.50f;
        } else if (mBrightnessData < 0.01f) {
            mBrightnessData = 0.01f;
        }

        WindowManager.LayoutParams lpa = ((Activity) (mContext)).getWindow().getAttributes();
        lpa.screenBrightness = mBrightnessData + percent;

        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }

        ((Activity) (mContext)).getWindow().setAttributes(lpa);
        return lpa.screenBrightness * 100;
    }

    @Override
    protected void showWifiDialog() {
        super.showWifiDialog();
    }


    //================================UI点击控制=======================================
    // TODO: 19-4-9 UI点击控制

    /**
     * 点击触摸显示和隐藏逻辑
     */
    @Override
    protected void onClickUiToggle() {
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            setViewShowState(mLockScreen, VISIBLE);
            setViewShowState(mBottomProgressBar, VISIBLE);
            return;
        }
        byStartedClick = true;
        super.onClickUiToggle();
    }

    @Override
    protected void changeUiToNormal() {
        Log.e(TAG, "NVPlayer.changeUiToNormal: ");
        super.changeUiToNormal();
        textViewTipCenter.setText("即将播放");
        byStartedClick = false;
        setViewShowState(mTopContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(linearLayoutCenterLoading, INVISIBLE);
        setViewShowState(linearLayoutRightCustomContainerOne, INVISIBLE);
        if (!isShottingGif) setViewShowState(imageViewScreenShotGif, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mBottomProgressBar, (mIfCurrentIsFullscreen && mLockCurScreen) ? VISIBLE : GONE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);
        mThumbImageViewLayout.setVisibility(VISIBLE);
        checkAllCustomWidget();
    }

    @Override
    protected void changeUiToClear() {
        Log.e(TAG, "NVPlayer.changeUiToClear: ");
        super.changeUiToClear();
        setViewShowState(linearLayoutCenterLoading, INVISIBLE);
        setViewShowState(linearLayoutRightCustomContainerOne, INVISIBLE);
        if (!isShottingGif) setViewShowState(imageViewScreenShotGif, INVISIBLE);
        setViewShowState(imageViewScreenShotGif, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);
    }

    @Override
    protected void changeUiToPreparingShow() {
        Log.e(TAG, "NVPlayer.changeUiToPreparingShow: ");
        super.changeUiToPreparingShow();
        textViewTipCenter.setText("正在载入视频,请稍后...");
        setViewShowState(linearLayoutCenterLoading, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);

        setViewShowState(mTopContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(linearLayoutCenterLoading, INVISIBLE);
        if (mIfCurrentIsFullscreen) {
            setViewShowState(linearLayoutRightCustomContainerOne, VISIBLE);
            setViewShowState(imageViewScreenShotGif, VISIBLE);

        }
        checkAllCustomWidget();
        closeType = 0;

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);

    }

    @Override
    protected void changeUiToPrepareingClear() {
        Log.e(TAG, "NVPlayer.changeUiToPrepareingClear: ");
        super.changeUiToPrepareingClear();
        setViewShowState(linearLayoutCenterLoading, INVISIBLE);
        setViewShowState(linearLayoutRightCustomContainerOne, INVISIBLE);
        if (!isShottingGif) setViewShowState(imageViewScreenShotGif, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);
    }

    @Override
    protected void changeUiToPlayingShow() {
        Log.e(TAG, "NVPlayer.changeUiToPlayingShow: ");
        super.changeUiToPlayingShow();
        if (!byStartedClick) {
            setViewShowState(mBottomContainer, INVISIBLE);
            setViewShowState(mStartButton, INVISIBLE);
        }
        setViewShowState(mTopContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomProgressBar, (mIfCurrentIsFullscreen && mLockCurScreen) ? VISIBLE : GONE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(linearLayoutCenterLoading, INVISIBLE);
        if (mIfCurrentIsFullscreen) {
            setViewShowState(linearLayoutRightCustomContainerOne, VISIBLE);
            setViewShowState(imageViewScreenShotGif, VISIBLE);
        }
        checkAllCustomWidget();

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);

        //开始ScrollTextView
//        ((ScrollTextView) findViewById(R.id.nvplayer_stv))
//                .startScroll();
    }

    @Override
    protected void changeUiToPlayingClear() {
        Log.e(TAG, "NVPlayer.changeUiToPlayingClear: ");
        super.changeUiToPlayingClear();
        textViewTipCenter.setText("即将播放");

        setViewShowState(mBottomProgressBar, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);
    }

    @Override
    protected void changeUiToPauseShow() {
        Log.e(TAG, "NVPlayer.changeUiToPauseShow: ");
        super.changeUiToPauseShow();
        setViewShowState(mTopContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomProgressBar, (mIfCurrentIsFullscreen && mLockCurScreen) ? VISIBLE : GONE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(linearLayoutCenterLoading, INVISIBLE);
        if (mIfCurrentIsFullscreen) {
            setViewShowState(linearLayoutRightCustomContainerOne, VISIBLE);
            setViewShowState(imageViewScreenShotGif, VISIBLE);
        }
        checkAllCustomWidget();

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);
    }

    @Override
    protected void changeUiToPauseClear() {
        Log.e(TAG, "NVPlayer.changeUiToPauseClear: ");
        super.changeUiToPauseClear();
        setViewShowState(mBottomProgressBar, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        Log.e(TAG, "NVPlayer.changeUiToPlayingBufferingShow: ");
        super.changeUiToPlayingBufferingShow();
        textViewTipCenter.setText("正在缓冲");
        if (!byStartedClick) {
            setViewShowState(mBottomContainer, INVISIBLE);
            setViewShowState(mStartButton, INVISIBLE);
        }

        setViewShowState(linearLayoutCenterLoading, VISIBLE);
        setViewShowState(mLoadingProgressBar, VISIBLE);

        if (mIfCurrentIsFullscreen) {
            setViewShowState(linearLayoutRightCustomContainerOne, VISIBLE);
            setViewShowState(imageViewScreenShotGif, VISIBLE);
        }

        checkAllCustomWidget();

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);
    }

    @Override
    protected void changeUiToPlayingBufferingClear() {
        Log.e(TAG, "NVPlayer.changeUiToPlayingBufferingClear: ");
        super.changeUiToPlayingBufferingClear();
        textViewTipCenter.setText("即将播放");
        setViewShowState(linearLayoutCenterLoading, VISIBLE);
        setViewShowState(linearLayoutRightCustomContainerOne, INVISIBLE);
        if (!isShottingGif) setViewShowState(imageViewScreenShotGif, INVISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);
    }

    @Override
    protected void changeUiToCompleteShow() {
        Log.e(TAG, "NVPlayer.changeUiToCompleteShow: ");
        super.changeUiToCompleteShow();
        setViewShowState(mBottomProgressBar, (mIfCurrentIsFullscreen && mLockCurScreen) ? VISIBLE : GONE);
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(linearLayoutCenterLoading, INVISIBLE);
        if (mIfCurrentIsFullscreen) {
            setViewShowState(linearLayoutRightCustomContainerOne, VISIBLE);
            setViewShowState(imageViewScreenShotGif, VISIBLE);
        }
        checkAllCustomWidget();

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);
    }

    public void onCompleteByUser() {
        if (closeType == 1) {
            showCloseTypeDialog();
        }
    }

    @Override
    protected void changeUiToCompleteClear() {
        Log.e(TAG, "NVPlayer.changeUiToCompleteClear: ");
        super.changeUiToCompleteClear();
        setViewShowState(mBottomProgressBar, (mIfCurrentIsFullscreen && mLockCurScreen) ? VISIBLE : GONE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(linearLayoutCenterLoading, INVISIBLE);
        setViewShowState(linearLayoutRightCustomContainerOne, INVISIBLE);
        if (!isShottingGif) setViewShowState(imageViewScreenShotGif, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);
    }

    @Override
    protected void changeUiToError() {
        Log.e(TAG, "NVPlayer.changeUiToError: ");
        super.changeUiToError();
        setViewShowState(mBottomProgressBar, (mIfCurrentIsFullscreen && mLockCurScreen) ? VISIBLE : GONE);
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(linearLayoutRightCustomContainerOne, INVISIBLE);
        if (!isShottingGif) setViewShowState(imageViewScreenShotGif, INVISIBLE);
        setViewShowState(imageViewStart, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(imageViewEnterSmallWin, mIfCurrentIsFullscreen ? GONE : GONE);
        setViewShowState(linearLayoutCenterLoading, INVISIBLE);
        setViewShowState(imageViewStartNext, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutErrorLayout, VISIBLE);
    }

    /**
     * 锁屏时隐藏的控件
     */
    @Override
    protected void hideAllWidget() {
        super.hideAllWidget();
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(linearLayoutRightCustomContainerOne, INVISIBLE);
        if (!isShottingGif) setViewShowState(imageViewScreenShotGif, INVISIBLE);
        setViewShowState(relativeLayoutErrorLayout, GONE);
    }

    @Override
    public View getStartButton() {
        return mIfCurrentIsFullscreen ? imageViewStartFull : imageViewStart;
    }

    /**
     * 定义开始按键显示
     */
    @Override
    protected void updateStartImage() {
        super.updateStartImage();
        if (mCurrentState == CURRENT_STATE_PLAYING) {
            setPlayIvState(false);
        } else if (mCurrentState == CURRENT_STATE_ERROR) {
            setPlayIvState(true);
        } else {
            setPlayIvState(true);
        }
    }

    @Override
    public int getEnlargeImageRes() {
        if (mEnlargeImageRes == -1) {
            return R.drawable.ic_nvplayer_full_window;
        }
        return mEnlargeImageRes;
    }

    private void setPlayIvState(Boolean isPlay) {
        imageViewStartFull.setImageDrawable(getResources()
                .getDrawable(isPlay ?
                        R.drawable.ic_nvplayer_bottom_play
                        : R.drawable.ic_nvplayer_bottom_pause_2));
        imageViewStart.setImageDrawable(getResources()
                .getDrawable(isPlay ?
                        R.drawable.ic_nvplayer_bottom_play
                        : R.drawable.ic_nvplayer_bottom_pause_2));

    }

    private void checkAllCustomWidget() {
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(imageViewStartNext, isHaveNext ? VISIBLE : INVISIBLE);
        setViewShowState(relativeLayoutFullScreenTopCustomContainer, mIfCurrentIsFullscreen ? VISIBLE : GONE);
        setViewShowState(relativeLayoutNormalScreenTopCustomContainer, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(relativeLayoutFullScreenBottomCustomContainer, mIfCurrentIsFullscreen ? VISIBLE : GONE);
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(linearLayoutRightCustomContainerOne, INVISIBLE);
            setViewShowState(imageViewScreenShotGif, INVISIBLE);
        } else {
            setViewShowState(imageViewStart, GONE);
            setViewShowState(imageViewEnterSmallWin, GONE);
        }
    }

    /**
     * 处理锁屏屏幕触摸逻辑
     */
    @Override
    protected void lockTouchLogic() {
        if (mLockCurScreen) {
            mLockScreen.setImageResource(R.drawable.ic_nvplayer_unlock);
            mLockCurScreen = false;
        } else {
            mLockScreen.setImageResource(R.drawable.ic_nvplayer_lock);
            mLockCurScreen = true;
            hideAllWidget();
        }

        Log.e(TAG, "NVPlayer.lockTouchLogic:mLockCurScreen " + mLockCurScreen);

        if (!mLockCurScreen) {
            if (mOrientationUtils != null)
                mOrientationUtils.setEnable(isRotateViewAuto());
        } else {
            if (mOrientationUtils != null)
                mOrientationUtils.setEnable(false);
        }
    }


    /*********************************定时关闭,比例等*************************************/
    //TODO:定时关闭,比例等
    @SuppressLint("HandlerLeak")
    private Handler handlerClosePlayer = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showCloseTypeDialog();
        }
    };
    private Runnable runnableClosePlayer = new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    switch (closeType) {
                        case 2:
                            Thread.sleep(30 * 60 * 1000);
                            handlerClosePlayer.sendEmptyMessage(0);
                            return;
                        case 3:
                            Thread.sleep(60 * 60 * 1000);
                            handlerClosePlayer.sendEmptyMessage(0);
                            return;
                        case 4:
                            Thread.sleep(90 * 60 * 1000);
                            handlerClosePlayer.sendEmptyMessage(0);
                            return;
                        default:
                            return;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    private Thread threadClosePlayer = new Thread(runnableClosePlayer);

    private void resolveClose() {
        if (threadClosePlayer.isAlive()) {
            threadClosePlayer.interrupt();
        }
        threadClosePlayer = new Thread(runnableClosePlayer);
        threadClosePlayer.start();
    }

    private int closeTypeDialogCount_DEFAULT_VALUE = 15;
    private int closeTypeDialogCount = closeTypeDialogCount_DEFAULT_VALUE;

    private void showCloseTypeDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage("设定的时间到了,是否退出当前播放")
                .setPositiveButton("继续播放", (dialog, which) -> {
                    isClickContinuePlay = true;
                    closeType = 0;
                })
                .setNegativeButton("退出播放(" + closeTypeDialogCount + ")", (dialog, which) -> {
                    closeType = 0;
                    if (isExitApp) {
                        System.exit(0);
                        return;
                    }
                    ((Activity) context).finish();
                }).create();

        alertDialog.setOnShowListener(dialog -> {
            closeTypeDialogCount = closeTypeDialogCount_DEFAULT_VALUE;
            new ThreadUtils().startNewThread(new ThreadUtils.OnThreadRunningListener() {
                @Override
                public void onStart(Handler handler) {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        closeTypeDialogCount = closeTypeDialogCount - 1;
                        if (closeTypeDialogCount < 0) {
                            break;
                        }
                        handler.sendEmptyMessage(0);
                    }
                    if (!isClickContinuePlay) {
                        handler.sendEmptyMessage(1);
                        isClickContinuePlay = false;
                    }
                }

                @Override
                public void onResult(Message message) {
                    if (message.what == 0) {
                        alertDialog.getButton(BUTTON_NEGATIVE).setText("退出播放(" + closeTypeDialogCount + ")");
                    }
                    if (message.what == 1) {
                        alertDialog.getButton(BUTTON_NEGATIVE).performClick();
                    }
                }
            });
        });

        try {
            if (!isDestroy)
                alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        // TODO: 19-6-13 startWindowFullscreen
        NVPlayer nvPlayer = (NVPlayer) super.startWindowFullscreen(context, actionBar, statusBar);

        //        nvPlayer.mSourcePosition=mSourcePosition;
        nvPlayer.mType = mType;
//        nvPlayer.mTransformSize=mTransformSize;
        nvPlayer.resolveTypeUI();
        //预览图
        startWindowFullscreenPreView(nvPlayer);
        nvPlayer.exPlayerContext = exPlayerContext;
        nvPlayer.onShareClickListener = onShareClickListener;
        nvPlayer.onSetVideoHeaderTailTimeListener = onSetVideoHeaderTailTimeListener;
        nvPlayer.onEpisodeItemClickListener = onEpisodeItemClickListener;
        nvPlayer.onNextPlayClickListener = onNextPlayClickListener;
        nvPlayer.imageViewShare = imageViewShare;
        nvPlayer.nEpisodeList = nEpisodeList;
        nvPlayer.rightSlideMenuDialogEpisode = rightSlideMenuDialogEpisode;
        nvPlayer.isHaveNext = isHaveNext;
        return nvPlayer;
    }


    /**
     * 推出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF
     * @param vp
     * @param gsyVideoPlayer
     */
    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null) {
            NVPlayer nvPlayer = (NVPlayer) gsyVideoPlayer;
//            mSourcePosition = nvPlayer.mSourcePosition;
            mType = nvPlayer.mType;
//            mTransformSize = nvPlayer.mTransformSize;
            resolveTypeUI();
        }
    }

    /**
     * 显示比例
     * 注意，GSYVideoType.setShowType是全局静态生效，除非重启APP。
     */
    private void resolveTypeUI() {
        if (!mHadPlay) {
            return;
        }
        if (mType == 1) {
//            mMoreScale.setText("16:9");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
        } else if (mType == 2) {
//            mMoreScale.setText("4:3");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_4_3);
        } else if (mType == 3) {
//            mMoreScale.setText("全屏");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        } else if (mType == 4) {
//            mMoreScale.setText("拉伸全屏");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        } else if (mType == 0) {
//            mMoreScale.setText("默认比例");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        }
        changeTextureViewShowType();
        if (mTextureView != null)
            mTextureView.requestLayout();
    }

    //================================预览=======================================
    // TODO: 19-4-9 预览

    private RelativeLayout mPreviewLayout;

    private ImageView mPreView;

    //是否因为用户点击
    private boolean mIsFromUser;

    //是否打开滑动预览
    private boolean mOpenPreView = false;

    private int mPreProgress = -2;

    private void initPreView() {
        mPreviewLayout = findViewById(R.id.preview_layout);
        mPreView = findViewById(R.id.preview_image);
    }

    private int preBottomMainSeekBarProgress = 0;
    private int bottomMainSeekBarDownPosition = 0;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        if (fromUser) {
            if (mOpenPreView) {
                int width = seekBar.getWidth();
                int time = progress * getDuration() / 100;
                int offset = (int) (width - (getResources().getDimension(R.dimen.seek_bar_image) / 2)) / 100 * progress;
                Debuger.printfError("***************** " + progress);
                Debuger.printfError("***************** " + time);
                showPreView(mOriginUrl, time);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPreviewLayout.getLayoutParams();
                layoutParams.leftMargin = offset;
                //设置帧预览图的显示位置
                mPreviewLayout.setLayoutParams(layoutParams);
                if (mHadPlay && mOpenPreView) {
                    mPreProgress = progress;
                }
            }
//            else {
//                if (mIfCurrentIsFullscreen) {
//                    int totalTimeDuration = getDuration();
//                    float deltaX = progress - preBottomMainSeekBarProgress;
//                    mSeekTimePosition = (int) (bottomMainSeekBarDownPosition + (deltaX * totalTimeDuration / 100));
//                    if (mSeekTimePosition > totalTimeDuration)
//                        mSeekTimePosition = totalTimeDuration;
//                    String seekTime = CommonUtil.stringForTime(mSeekTimePosition);
//                    String totalTime = CommonUtil.stringForTime(totalTimeDuration);
//                    showProgressDialog(deltaX, seekTime, mSeekTimePosition, totalTime, totalTimeDuration);
//                }
//            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        byStartedClick = true;
        super.onStartTrackingTouch(seekBar);
        if (mOpenPreView) {
            mIsFromUser = true;
            mPreviewLayout.setVisibility(VISIBLE);
            mPreProgress = -2;
        }
//        else {
//            if (mIfCurrentIsFullscreen) {
//                bottomMainSeekBarDownPosition = getCurrentPositionWhenPlaying();
//                preBottomMainSeekBarProgress = seekBar.getProgress();
//            }
//        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
//        seekBar.setThumbOffset(0);
//        seekBar.setThumb(getResources().getDrawable(R.drawable.nvplayer_video_seek_thumb_normal));
        if (mOpenPreView) {
            if (mPreProgress >= 0) {
                seekBar.setProgress(mPreProgress);
            }
            super.onStopTrackingTouch(seekBar);
            mIsFromUser = false;
            mPreviewLayout.setVisibility(GONE);
        } else {
            super.onStopTrackingTouch(seekBar);
            dismissProgressDialog();
        }
    }

    @Override
    public void onSurfaceUpdated(Surface surface) {
        super.onSurfaceUpdated(surface);
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.getVisibility() == VISIBLE) {
            mThumbImageViewLayout.setVisibility(INVISIBLE);
        }
    }

    @Override
    protected void setViewShowState(View view, int visibility) {
        if (view == mThumbImageViewLayout && visibility != VISIBLE) {
            return;
        }
        super.setViewShowState(view, visibility);
    }

    @Override
    protected void setTextAndProgress(int secProgress) {
        if (mIsFromUser) {
            return;
        }
        super.setTextAndProgress(secProgress);
    }


    private void startWindowFullscreenPreView(NVPlayer nvPlayer) {
        nvPlayer.mOpenPreView = mOpenPreView;
    }


    @Override
    public void onPrepared() {
        super.onPrepared();
        startDownFrame(mOriginUrl);
    }

    public boolean isOpenPreView() {
        return mOpenPreView;
    }

    /**
     * 如果是需要进度条预览的设置打开，默认关闭
     */
    public void setOpenPreView(boolean localFile) {
        this.mOpenPreView = localFile;
    }

    private void showPreView(String url, long time) {
        int width = CommonUtil.dip2px(getContext(), 150);
        int height = CommonUtil.dip2px(getContext(), 100);
        Glide.with(getContext().getApplicationContext())
                .setDefaultRequestOptions(
                        new RequestOptions()
                                //这里限制了只从缓存读取
                                .onlyRetrieveFromCache(true)
                                .frame(1000 * time)
                                .override(width, height)
                                .dontAnimate()
                                .centerCrop())
                .load(url)
                .into(mPreView);
    }

    private void startDownFrame(String url) {
        for (int i = 1; i <= 100; i++) {
            int time = i * getDuration() / 100;
            int width = CommonUtil.dip2px(getContext(), 150);
            int height = CommonUtil.dip2px(getContext(), 100);
            Glide.with(getContext().getApplicationContext())
                    .setDefaultRequestOptions(
                            new RequestOptions()
                                    .frame(1000 * time)
                                    .override(width, height)
                                    .centerCrop())
                    .load(url).preload(width, height);

        }
    }
    //================================剧集=======================================
    // TODO: 19-4-9 剧集

    private List<NEpisode> nEpisodeList;

    private NEpisodeRecyclerViewAdapter nEpisodeRecyclerViewAdapter;

    public List<NEpisode> getnEpisodeList() {
        return nEpisodeList;
    }

    private TextView textViewSelectEpisode;

    public NVPlayer setnEpisodeList(List<NEpisode> nEpisodeList) {
//        this.nEpisodeList = nEpisodeList;
        this.nEpisodeList.clear();
        this.nEpisodeList.addAll(nEpisodeList);
        nEpisodeRecyclerViewAdapter.notifyDataSetChanged();
        return this;
    }

    public void setEpisodeItemClickPosition(int position) {
        if (nEpisodeRecyclerViewAdapter != null) {
            nEpisodeRecyclerViewAdapter.setClickPosition(position);
        }
    }

    private void configVideoEpisodeRv(RecyclerView recyclerView) {
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(NRecyclerViewSpacesItemDecoration.TOP_DECORATION, 10);//top间距
        stringIntegerHashMap.put(NRecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 10);//底部间距
        stringIntegerHashMap.put(NRecyclerViewSpacesItemDecoration.LEFT_DECORATION, 5);//左间距
        stringIntegerHashMap.put(NRecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 5);//右间距
        recyclerView.addItemDecoration(new NRecyclerViewSpacesItemDecoration(stringIntegerHashMap));
    }

    private OnEpisodeItemClickListener onEpisodeItemClickListener;

    public void setOnEpisodeItemClickListener(OnEpisodeItemClickListener onEpisodeItemClickListener) {
        this.onEpisodeItemClickListener = onEpisodeItemClickListener;
    }

    public interface OnEpisodeItemClickListener {
        void onEpisodeItemClick(NEpisode episode, int position);
    }

    /*********************************悬浮窗*************************************/
    // TODO: 19-4-9 悬浮窗
    public void startFloatWin() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Util.hasPermission(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                ((Activity) context).startActivityForResult(intent, 1);
            }
        }

        if (FloatWindow.get() != null) {
            return;
        }
        FloatPlayerView floatPlayerView = new FloatPlayerView(context.getApplicationContext());
        FloatWindow
                .with(context.getApplicationContext())
                .setView(floatPlayerView)
                .setWidth(Screen.width, 0.4f)
                .setHeight(Screen.width, 0.4f)
                .setX(Screen.width, 0.8f)
                .setY(Screen.height, 0.3f)
                .setMoveType(MoveType.slide)
                .setFilter(false)
                .setMoveStyle(500, new BounceInterpolator())
                .build();
        FloatWindow.get().show();
    }


    /*********************************Other*************************************/
    // TODO: 19-4-9 其他
    public NVPlayer setVideoName(String videoName) {
        this.videoName = videoName;
        return this;
    }

    public void onNVPlayerPause() {
        try {
            rightSlideMenuDialogEpisode.dismiss();
            rightSlideMenuDialogShotGif.dismiss();
            rightSlideMenuDialogBottomSpeed.dismiss();
            rightSlideMenuDialogTopRightMenuDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHaveNext(Boolean haveNext) {
        this.isHaveNext = haveNext;
    }

    public Boolean getHaveNext() {
        return isHaveNext;
    }

    public Boolean getExiteApp() {
        return isExitApp;
    }

    public NVPlayer setExiteApp(Boolean exiteApp) {
        isExitApp = exiteApp;
        return this;
    }

    public Boolean getHighScreenShot() {
        return isHighScreenShot;
    }

    public NVPlayer setHighScreenShot(Boolean highScreenShot) {
        isHighScreenShot = highScreenShot;
        return this;
    }

    private OnNextPlayClickListener onNextPlayClickListener;

    public NVPlayer setOnNextPlayClickListener(OnNextPlayClickListener onNextPlayClickListener) {
        this.onNextPlayClickListener = onNextPlayClickListener;
        return this;
    }

    public interface OnNextPlayClickListener {
        void onClick(View view);
    }

    private OnShareClickListener onShareClickListener;

    public NVPlayer setOnShareClickListener(OnShareClickListener onShareClickListener) {
        this.onShareClickListener = onShareClickListener;
        return this;
    }

    public interface OnShareClickListener {
        void onClick(View view);
    }


    public void play() {
        getCurrentPlayer().startPlayLogic();
    }


    public boolean setNextUrlUp(String url, String title) {
        return setNextUrlUp(url, false, null, title);
    }

    public boolean setNextUrlUp(String url, boolean cacheWithPlay, String title) {
        return setNextUrlUp(url, cacheWithPlay, null, title);
    }

    public boolean setNextUrlUp(String url, boolean cacheWithPlay, File cachePath, String title) {
        return setUp(url, cacheWithPlay, cachePath, title, false);
    }

    public boolean isPauseOnScreenshot() {
        return pauseOnScreenshot;
    }

    public NVPlayer setPauseOnScreenshot(boolean pauseOnScreenshot) {
        this.pauseOnScreenshot = pauseOnScreenshot;
        return this;
    }

    public String getPlayErrorText() {
        return playErrorText;
    }

    public NVPlayer setPlayErrorText(String playErrorText) {
        this.playErrorText = playErrorText;
        textViewErrorText.setText(playErrorText);
        return this;
    }

    public ImageView getImageViewShare() {
        return imageViewShare;
    }

    public void onRestart() {
    }

    public void onResume() {
        if (clingViewManager != null)
            clingViewManager.onHostActivityResume();
    }

    public void onDestroy() {
        isDestroy = true;
        if (threadRunningListener.isAlive()) {
            threadRunningListener.interrupt();
        }
        try {
            nvPlayerBatteryView.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fullScreenImmersive(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }

    public Boolean getIfCurrentIsFullScreen() {
        return mIfCurrentIsFullscreen;
    }

    public Boolean getNeedLockFull() {
        return mNeedLockFull;
    }

    public Boolean getLockCurrentScreen() {
        return mLockCurScreen;
    }

    public Boolean getIsLockingFullScreen() {
        return mIfCurrentIsFullscreen && ((NVPlayer) getCurrentPlayer()).getLockCurrentScreen();
    }

    public NVPlayer setVideoHeaderTime(long time) {
        exPlayerContext.setVideoHeaderTime(time);
        return this;
    }

    public NVPlayer setVideoTailTime(long time) {
        exPlayerContext.setVideoTailTime(time);
        return this;
    }

    private class ExPlayerContext {
        private Long videoHeaderTime,
                videoTailTime;

        public Long getVideoHeaderTime() {
            return videoHeaderTime;
        }

        public ExPlayerContext setVideoHeaderTime(Long videoHeaderTime) {
            this.videoHeaderTime = videoHeaderTime;
            return this;
        }

        public Long getVideoTailTime() {
            return videoTailTime;
        }

        public ExPlayerContext setVideoTailTime(Long videoTailTime) {
            this.videoTailTime = videoTailTime;
            return this;
        }
    }


}
