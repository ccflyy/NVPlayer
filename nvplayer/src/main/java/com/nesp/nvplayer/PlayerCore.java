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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import androidx.annotation.IntDef;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nesp.nvplayer.adapter.NEpisodeRecyclerViewAdapter;
import com.nesp.nvplayer.cling.ClingViewManagerImpl;
import com.nesp.nvplayer.dialog.RightSlideMenuDialog;
import com.nesp.nvplayer.entity.NEpisode;
import com.nesp.nvplayer.utils.GifCreator;
import com.nesp.nvplayer.utils.ImageUtils;
import com.nesp.nvplayer.utils.LoadUtils;
import com.nesp.nvplayer.utils.NRecyclerViewSpacesItemDecoration;
import com.nesp.nvplayer.utils.NVCommonUtils;
import com.nesp.nvplayer.utils.ThreadUtils;
import com.nesp.nvplayer.utils.floatUtil.FloatWindow;
import com.nesp.nvplayer.utils.floatUtil.MoveType;
import com.nesp.nvplayer.utils.floatUtil.Screen;
import com.nesp.nvplayer.utils.floatUtil.Util;
import com.nesp.nvplayer.utils.net.Internet;
import com.nesp.nvplayer.widget.FloatPlayerView;
import com.nesp.nvplayer.widget.NVPlayerBatteryView;
import com.nesp.sdk.android.net.LocationUtils;
import com.nesp.sdk.android.util.ClipboardUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYVideoGifSaveListener;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.super_rabbit.wheel_picker.WheelPicker;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoSourceManager;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static com.nesp.nvplayer.utils.NVCommonUtils.cleanGifTmpFile;
import static com.nesp.nvplayer.utils.NVCommonUtils.flashView;
import static com.nesp.nvplayer.utils.NVCommonUtils.getDecimalFormat;
import static com.nesp.nvplayer.utils.NVCommonUtils.refreshPhoneImageGallery;
import static com.nesp.nvplayer.utils.UnitUtils.millisecondToString;
import static com.nesp.sdk.android.widget.Toast.showShortToast;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-4-6 上午11:24
 * @project NVPlayer
 **/
public class PlayerCore extends NormalGSYVideoPlayer {

    private static final String TAG = "PlayerCore";

    private static final String SHARE_PREFERENCES_FILENAME = "PlayCore";

    private ExPlayerContext mExPlayerContext;

    private Context context;

    protected DecimalFormat mDecimalFormat = getDecimalFormat();

    protected View mMenuViewMenu;

    protected float mPlaySpeed = 1;

    protected int mCloseType = 0;

    protected LinearLayout mLlCenterLoading;
    protected TextView mTvNetSpeed;

    protected NVPlayerBatteryView mNVPlayerBatteryView;

    //记住切换数据源类型
    protected int mType = 0;

    protected int mTransformSize = 0;

    //数据源
    protected int mSourcePosition = 0;

    protected String mFishMovie = "FishMovie";
    protected String mImageSavePath;

    protected RelativeLayout mRlFullScreenBottomCustomContainer;
    protected RelativeLayout mRlFullScreenTopCustomContainer;
    protected RelativeLayout mRlNormalScreenTopCustomContainer;
    protected LinearLayout mLlRightCustomContainerOne;

    protected ImageView mIvStart, mIvStartFull, mIvStartNext, mIvEnterSmallWin, mIvClingTv;

    private LinearLayout mLinearLayoutEnterSmallWinFull;

    protected ImageView mImageViewScreenShot, mImageViewScreenShotGif;

    protected Boolean mIsHighScreenShot = false;
    protected GifCreator mGifCreator;

    protected boolean mPauseOnScreenshot = true;

    protected final int SHARE_DIALOG_DISMISS_TIME = mPauseOnScreenshot ? 3000 : 5000;

    protected long mGifStartTime;
    protected long mGifEndTime;
    protected final long MIN_GIF_TIME = 1000;
    protected boolean mIsShottingGif = false;
    protected Boolean mIsGifProcessing = false;

    protected RightSlideMenuDialog mRightSlideMenuDialogEpisode;
    protected RightSlideMenuDialog mRightSlideMenuDialogBottomSpeed;
    protected RightSlideMenuDialog mRightSlideMenuDialogTopRightMenuDialog;

    protected boolean mByStartedClick;

    private ImageView mIvShare;
    private boolean mIsClickContinuePlay = false;
    private boolean mIsDestroy = false;

    protected RelativeLayout mRlTipContent;

    protected LinearLayout mLlPlayPrepareTip, mLlMobileInternetTip, mLlNoInternetTip, mLlPlayErrorTip, mLlPlayCompleteTip;

    private TextView mTvPlayPrepareTip;

    protected TextView mTvPlayError;
    protected String mPlayErrorText = "";

    private ClingViewManagerImpl mClingViewManager;

    private String mVideoName = "";
    private TextView mTvTopTime;
    private Thread mThreadSyncTopTime;
    private Thread mThreadShottingGif;

    private LinearLayout mLlTitle;
    private TextView mTvInfoUrl;
    private String infoUrl;

    public PlayerCore(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public PlayerCore(Context context) {
        super(context);
    }

    public PlayerCore(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.nesp_sdk_nvplayer;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mImageSavePath = Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_PICTURES + "/" + mFishMovie + "/";

        mExPlayerContext = new ExPlayerContext();
        this.context = context;
        mThreadRunningListener = new Thread(() -> {
            while (true) {
                if (mIsDestroy) return;
                Log.e(TAG, "PlayerCore.onStart: " + getCurrentStringState());
                switch (getCurrentState()) {
                    case CURRENT_STATE_NORMAL:
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(CURRENT_STATE_NORMAL);
                        }
                        break;
                    case CURRENT_STATE_PREPAREING: {
                        //更新网速
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(CURRENT_STATE_PREPAREING);
                        }
                    }
                    break;
                    case CURRENT_STATE_PLAYING:
                        break;
                    case CURRENT_STATE_PLAYING_BUFFERING_START: {
                        //更新网速
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(CURRENT_STATE_PLAYING_BUFFERING_START);
                        }
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
        initPlayerConfig();
    }

    private void initPlayerConfig() {
        IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);
        //EXOPlayer内核，支持格式更多
//        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        //系统内核模式
//        PlayerFactory.setPlayManager(SystemPlayerManager.class);
        //ijk内核，默认模式
        PlayerFactory.setPlayManager(IjkPlayerManager.class);

        //exo缓存模式，支持m3u8，只支持exo
//        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
        //代理缓存模式，支持所有模式，不支持m3u8等，默认
//        CacheFactory.setCacheManager(ProxyCacheManager.class);
        //播放器优化
        ExoSourceManager.setSkipSSLChain(true);
        final List<VideoOptionModel> list = new ArrayList<>();
//        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1));
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1));
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp"));
        GSYVideoManager.instance().setOptionModelList(list);
    }

    private void initSettings() {
        //滑动快进的比例，默认1。数值越大，滑动的产生的seek越小
        setSeekRatio(1);
        //开始视频状态监听器
        startStateListener();
    }

    /*********************************Init View*************************************/
    //TODO:Init View
    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mLlRightCustomContainerOne = findViewById(R.id.nvplayer_rl_right_custom_container_one);
        mRlFullScreenBottomCustomContainer = findViewById(R.id.nvplayer_rl_full_screen_bottom_custom_container);
        mRlFullScreenTopCustomContainer = findViewById(R.id.nvplayer_rl_full_screen_top_custom_container);
        mRlNormalScreenTopCustomContainer = findViewById(R.id.nvplayer_rl_normal_screen_top_custom_container);

        //TODO:Cling投屏
        mIvClingTv = findViewById(R.id.nesp_nvplayer_iv_cling_tv);
        mIvClingTv.setOnClickListener(v -> {
            if (!mHadPlay) {
                Toast.makeText(context, "无视频播放，该功能不可用", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (
                            ActivityCompat.shouldShowRequestPermissionRationale(mExPlayerContext.getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(mExPlayerContext.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(mExPlayerContext.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    ) {
                        new AlertDialog.Builder(context)
                                .setTitle("权限申请")
                                .setMessage("Android10以上需要定位权限才能获得WIFI名字，是否申请权限！")
                                .setPositiveButton("申请", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        ActivityCompat.requestPermissions(mExPlayerContext.getActivity(),
                                                new String[]{
                                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                                },
                                                PERMISSIONS_REQUEST_CODE_FOR_WIFI_ID);
                                    }
                                })
                                .setNegativeButton("不申请", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        showClingSearchDeviceDialog();
                                    }
                                })
                                .create().show();
                    } else {
                        ActivityCompat.requestPermissions(mExPlayerContext.getActivity(),
                                new String[]{
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                },
                                PERMISSIONS_REQUEST_CODE_FOR_WIFI_ID);
                    }
                } else {
                    showClingSearchDeviceDialog();
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mExPlayerContext.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        new AlertDialog.Builder(context)
                                .setTitle("权限申请")
                                .setMessage("Android9以上需要定位权限才能获得WIFI名字，是否申请权限！")
                                .setPositiveButton("申请", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        ActivityCompat.requestPermissions(mExPlayerContext.getActivity(),
                                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                                PERMISSIONS_REQUEST_CODE_FOR_WIFI_ID);
                                    }
                                })
                                .setNegativeButton("不申请", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        showClingSearchDeviceDialog();
                                    }
                                })
                                .create().show();
                    } else {
                        ActivityCompat.requestPermissions(mExPlayerContext.getActivity(),
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSIONS_REQUEST_CODE_FOR_WIFI_ID);
                    }
                } else {
                    showClingSearchDeviceDialog();
                }
            } else {
                showClingSearchDeviceDialog();
            }
        });


        /*********************************顶部控件*************************************/
        mLlTitle = findViewById(R.id.ll_title);
        mTvInfoUrl = findViewById(R.id.info_url);
        ((View) mTvInfoUrl.getParent()).setOnLongClickListener(v -> {
            ClipboardUtils.copyTextToClipboard(context, mTvInfoUrl.getText().toString());
            showShortToast(context, "已复制链接地址！");
            return true;
        });

        /*********************************底部的控件*************************************/
        //@TODO 底部的控件
        initBottomView();
        /*********************************预览*************************************/
        initPreView();
        /*********************************中间的控件*************************************/


        mTvNetSpeed = findViewById(R.id.nesp_nvplayer_tv_net_speed);
        mLlCenterLoading = findViewById(R.id.nesp_nvplayer_ll_center_loading);
        //封面图
        if (mThumbImageViewLayout != null &&
                (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.setVisibility(VISIBLE);
        }

        //init Tip View
        initTipView();
        showPlayPrepareTipView();
        //================================截图功能=======================================
        initScreenshotView();

        //================================GIF功能=======================================
        initScreenshotGifView();

        //================================顶部=======================================
        initTopMenuView();
        mTvTopTime = findViewById(R.id.nvplayer_tv_time);
        mThreadSyncTopTime = new Thread(() -> {
            try {
                while (true) {
                    if (mIsDestroy) return;
                    Thread.sleep(1000);
                    if (mHandler != null) {
                        mHandler.post(() -> mTvTopTime.setText(new SimpleDateFormat("HH:mm").format(new Date())));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        mThreadSyncTopTime.start();

        mIvShare = findViewById(R.id.nesp_nvplayer_iv_share);
        mIvShare.setOnClickListener(v -> {
            if (mExPlayerContext.getPlayerMode() == PlayerMode.TV_DIRECT) {
                showShortToast(context, "直播模式下，该功能不可用！");
                return;
            } else if (mExPlayerContext.getPlayerMode() == PlayerMode.LOCATION) {
                showShortToast(context, "本地模式下，该功能不可用！");
                return;
            }
            if (onShareClickListener != null) {
                onShareClickListener.onClick(v);
            }
        });
    }

    public static final int PERMISSIONS_REQUEST_CODE_FOR_WIFI_ID = 100;

    public void showClingSearchDeviceDialog() {
        if (mClingViewManager == null) {
            mClingViewManager = new ClingViewManagerImpl(
                    (AppCompatActivity) context
                    , this
                    , mVideoName
                    , nEpisodeList
                    , nEpisodeRecyclerViewAdapter.getSelectPosition()
                    , mExPlayerContext.videoHeaderTime
                    , mExPlayerContext.videoTailTime
            );
        }
        mClingViewManager.initView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 && !LocationUtils.isLocationEnable(context) && !getIsDisableLocationSettings()) {
            new AlertDialog.Builder(context)
                    .setMessage("Android9以上需要打开定位才能获得WIFI名字,是否打开")
                    .setPositiveButton("打开", (dialog, which) -> {
                        LocationUtils.openLocationSettings(context);
                    })
                    .setNegativeButton("不打开", (dialog, which) -> {
                        mClingViewManager.showClingSearchDevicePage();
                        saveIsDisableLocationSettings(true);
                    })
                    .create().show();
            return;
        }
        mClingViewManager.showClingSearchDevicePage();
    }

    /*********************************Init Tip View*************************************/
    //TODO:Init Tip View
    private void initTipView() {
        mRlTipContent = findViewById(R.id.nvplayer_rl_play_tip_content);

        mLlPlayPrepareTip = findViewById(R.id.nvplayer_ll_play_prepare_tip);
        mTvPlayPrepareTip = findViewById(R.id.nvplayer_tv_play_prepare_tip);

        mLlMobileInternetTip = findViewById(R.id.nvplayer_ll_mobile_internet_tip);
        final Button btnContinuePlayForMobileInternet = findViewById(R.id.nvplayer_btn_continue_play_for_mobile_internet);
        btnContinuePlayForMobileInternet.setOnClickListener(v -> {
            if (mExPlayerContext != null && mExPlayerContext.getOnContinuePlayForMobileInternetClickListener() != null) {
                mExPlayerContext.getOnContinuePlayForMobileInternetClickListener().onClick(v);
                hideTipContent();
            }
        });

        mLlNoInternetTip = findViewById(R.id.nvplayer_ll_no_internet_tip);
        final Button btnReplayForNoInternet = findViewById(R.id.nvplayer_btn_replay_for_no_internet);
        btnReplayForNoInternet.setOnClickListener(v -> {
            if (mExPlayerContext != null && mExPlayerContext.getOnReplayClickListener() != null) {
                mExPlayerContext.getOnReplayClickListener().onClick(v);
                if (Internet.NetWork.isNetworkConnected(context)) {
                    hideTipContent();
                } else {
                    showShortToast(context, "网络未连接！请检查网络设置");
                }
            }
        });

        mLlPlayErrorTip = findViewById(R.id.nvplayer_ll_play_error_tip);
        mTvPlayError = findViewById(R.id.nvplayer_tv_play_error_tip);
        if (mPlayErrorText != null && !mPlayErrorText.isEmpty()) {
            mTvPlayError.setText(mPlayErrorText);
        }
        final Button btnReplayForPlayError = findViewById(R.id.nvplayer_btn_replay_for_play_error);
        btnReplayForPlayError.setOnClickListener(v -> {
            if (mExPlayerContext != null && mExPlayerContext.getOnReplayClickListener() != null) {
                mExPlayerContext.getOnReplayClickListener().onClick(v);
                hideTipContent();
            }
        });

        mLlPlayCompleteTip = findViewById(R.id.nvplayer_ll_play_complete_tip);
    }

    public PlayerCore setInfoUrl(String infoUrl) {
        if (this.mTvInfoUrl != null && infoUrl != null) {
            if (mExPlayerContext != null) {
                mExPlayerContext.infoUrl = infoUrl;
            }
        }

        return this;
    }

    public PlayerCore setOnReplayClickListener(final OnClickListener onReplayClickListener) {
        mExPlayerContext.setOnReplayClickListener(onReplayClickListener);
        return this;
    }

    public PlayerCore setOnContinuePlayForMobileInternetClickListener(final OnClickListener onContinuePlayForMobileInternetClickListener) {
        mExPlayerContext.setOnContinuePlayForMobileInternetClickListener(onContinuePlayForMobileInternetClickListener);
        return this;
    }

    private void showPlayPrepareTipView(String text) {
        mTvPlayPrepareTip.setText(text);
        showPlayPrepareTipView();
    }

    private void showPlayPrepareTipView() {
        showTipContent();
        setViewShowState(mLlPlayPrepareTip, VISIBLE);
        setViewShowState(mLlMobileInternetTip, GONE);
        setViewShowState(mLlNoInternetTip, GONE);
        setViewShowState(mLlPlayErrorTip, GONE);
        setViewShowState(mLlPlayCompleteTip, GONE);
    }

    public void showMobileInternetTipView() {
        showTipContent();
        setViewShowState(mLlPlayPrepareTip, GONE);
        setViewShowState(mLlMobileInternetTip, VISIBLE);
        setViewShowState(mLlNoInternetTip, GONE);
        setViewShowState(mLlPlayErrorTip, GONE);
        setViewShowState(mLlPlayCompleteTip, GONE);
    }

    public void showNoInternetTipView() {
        showTipContent();
        setViewShowState(mLlPlayPrepareTip, GONE);
        setViewShowState(mLlMobileInternetTip, GONE);
        setViewShowState(mLlNoInternetTip, VISIBLE);
        setViewShowState(mLlPlayErrorTip, GONE);
        setViewShowState(mLlPlayCompleteTip, GONE);
    }

    private void showPlayErrorTipView() {
        showTipContent();
        setViewShowState(mLlPlayPrepareTip, GONE);
        setViewShowState(mLlMobileInternetTip, GONE);
        setViewShowState(mLlNoInternetTip, GONE);
        setViewShowState(mLlPlayErrorTip, VISIBLE);
        setViewShowState(mLlPlayCompleteTip, GONE);
    }

    private void showPlayCompleteTipView() {
        showTipContent();
        setViewShowState(mLlPlayPrepareTip, GONE);
        setViewShowState(mLlMobileInternetTip, GONE);
        setViewShowState(mLlNoInternetTip, GONE);
        setViewShowState(mLlPlayErrorTip, GONE);
        setViewShowState(mLlPlayCompleteTip, VISIBLE);
    }

    private Boolean isShowTipContentView() {
        return mRlTipContent.getVisibility() == VISIBLE;
    }

    public void hideTipContent() {
        setViewShowState(mRlTipContent, GONE);
    }

    private void showTipContent() {
        setViewShowState(mRlTipContent, VISIBLE);
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mRlNormalScreenTopCustomContainer, INVISIBLE);
    }

    /*********************************initTopMenuView*************************************/
    //TODO:initTopMenuView
    private void initTopMenuView() {
        mNVPlayerBatteryView = findViewById(R.id.nesp_nvplayer_nvplayer_battery_view);
        mMenuViewMenu = findViewById(R.id.nesp_nvplayer_iv_menu);
        mMenuViewMenu.setOnClickListener(v -> {
            if (showNoVideoPlayNotFunctionToast()) return;
            //菜单选项
            final View view = LayoutInflater.from(context).inflate(R.layout.nvplayer_right_top_menu_dialog, null);

            // TODO: 19-6-13 video header time
            final TextView textViewVideoHeaderTime = view.findViewById(R.id.nvpalyer_right_top_menu_tv_video_header_time);
            final TextView textViewVideoTailTime = view.findViewById(R.id.nvpalyer_right_top_menu_tv_video_tail_time);
            textViewVideoHeaderTime.setText(millisecondToString(mExPlayerContext.getVideoHeaderTime()).split("h")[1]);
            textViewVideoTailTime.setText(millisecondToString(mExPlayerContext.videoTailTime).split("h")[1]);
            view.findViewById(R.id.nvpalyer_right_top_menu_ll_video_header_time).setOnClickListener(v12 -> showSetVideoHeaderTailTime(true, textViewVideoHeaderTime));

            view.findViewById(R.id.nvpalyer_right_top_menu_ll_video_tail_time).setOnClickListener(v13 -> showSetVideoHeaderTailTime(false, textViewVideoTailTime));

            mLinearLayoutEnterSmallWinFull = view.findViewById(R.id.nvpalyer_right_top_menu_ll_small_full);
            mLinearLayoutEnterSmallWinFull.setOnClickListener(v1 -> startFloatWin());

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

            final RadioGroup radioGroupSpeed = view.findViewById(R.id.nvpalyer_right_top_menu_rg_speed);
            radioGroupSpeed.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_2_0) {
                    mPlaySpeed = 2f;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_1_5) {
                    mPlaySpeed = 1.5f;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_1_25) {
                    mPlaySpeed = 1.25f;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_1) {
                    mPlaySpeed = 1f;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_0_75) {
                    mPlaySpeed = 0.75f;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_0_5) {
                    mPlaySpeed = 0.5f;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_speed_0_25) {
                    mPlaySpeed = 0.25f;
                }
                setSpeedPlaying(mPlaySpeed, true);
            });


            final RadioGroup radioGroupClose = view.findViewById(R.id.nvpalyer_right_top_menu_rg_close);
            radioGroupClose.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.nvpalyer_right_top_menu_rb_close_disable) {
                    mCloseType = 0;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_close_1) {
                    mCloseType = 1;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_close_30) {
                    mCloseType = 2;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_close_60) {
                    mCloseType = 3;
                } else if (checkedId == R.id.nvpalyer_right_top_menu_rb_close_90) {
                    mCloseType = 4;
                }
                resolveClose();
            });


            mRightSlideMenuDialogTopRightMenuDialog = new RightSlideMenuDialog(context, view);
            mRightSlideMenuDialogTopRightMenuDialog.setOnShowListener(dialog -> {
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
                final RadioButton rbScaleSelect = mRightSlideMenuDialogTopRightMenuDialog.findViewById(scaleRbId);
                rbScaleSelect.setChecked(true);

                //初始化播放速度控件
                int speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_1;
                if (mPlaySpeed == 2) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_2_0;
                } else if (mPlaySpeed == 1.5f) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_1_5;
                } else if (mPlaySpeed == 1.25f) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_1_25;
                } else if (mPlaySpeed == 1f) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_1;
                } else if (mPlaySpeed == 0.75f) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_0_75;
                } else if (mPlaySpeed == 0.5f) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_0_5;
                } else if (mPlaySpeed == 0.25f) {
                    speedRbId = R.id.nvpalyer_right_top_menu_rb_speed_0_25;
                }

                final RadioButton rbPlaySpeedSelect = mRightSlideMenuDialogTopRightMenuDialog.findViewById(speedRbId);
                rbPlaySpeedSelect.setChecked(true);

                //TODO:初始化定时关闭控件
                int closeRbId = R.id.nvpalyer_right_top_menu_rb_close_disable;
                if (mCloseType == 0) {
                    closeRbId = R.id.nvpalyer_right_top_menu_rb_close_disable;
                } else if (mCloseType == 1) {
                    closeRbId = R.id.nvpalyer_right_top_menu_rb_close_1;
                } else if (mCloseType == 2) {
                    closeRbId = R.id.nvpalyer_right_top_menu_rb_close_30;
                } else if (mCloseType == 3) {
                    closeRbId = R.id.nvpalyer_right_top_menu_rb_close_60;
                } else if (mCloseType == 4) {
                    closeRbId = R.id.nvpalyer_right_top_menu_rb_close_90;
                }
//                resolveClose();
                final RadioButton rbCloseSelect = mRightSlideMenuDialogTopRightMenuDialog.findViewById(closeRbId);
                rbCloseSelect.setChecked(true);
            });
            mRightSlideMenuDialogTopRightMenuDialog.show();
        });
    }

    protected void showSetVideoHeaderTailTime(Boolean isHeaderTime, TextView textView) {
        if (mExPlayerContext.getPlayerMode() == PlayerMode.TV_DIRECT) {
            showShortToast(context, "在直播模式下，该功能不可用！");
            return;
        } else if (mExPlayerContext.getPlayerMode() == PlayerMode.LOCATION) {
            showShortToast(context, "在本地模式下，该功能不可用！");
            return;
        }
        final String time = textView.getText().toString();
        final String tvMin = time.split("m")[0];
        final String tvSec = time.split("s")[0].split("m")[1];

        final String title = isHeaderTime ? "片头时长" : "片尾时长";

        final View view = LayoutInflater.from(context).inflate(R.layout.nvplayer_dialog_set_video_header_tail_time, null);

        final WheelPicker wheelPickerMin = view.findViewById(R.id.nvplayer_dialog_set_video_header_tail_time_min);
        final WheelPicker wheelPickerSec = view.findViewById(R.id.nvplayer_dialog_set_video_header_tail_time_sec);
        wheelPickerMin.setSelectorRoundedWrapPreferred(true);
        wheelPickerSec.setSelectorRoundedWrapPreferred(true);
        wheelPickerMin.setSelectedTextColor(R.color.color_4_blue);
        wheelPickerSec.setSelectedTextColor(R.color.color_4_blue);
        wheelPickerMin.setUnselectedTextColor(R.color.color_3_dark_blue);
        wheelPickerSec.setUnselectedTextColor(R.color.color_3_dark_blue);

        wheelPickerMin.scrollToValue(tvMin);
        wheelPickerSec.scrollToValue(tvSec);

        final AlertDialog setVideoHeaderTailTimeDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialog, which) -> {
                    textView.setText(wheelPickerMin.getCurrentItem() + "m" + wheelPickerSec.getCurrentItem() + "s");
                    if (isHeaderTime) {
                        mExPlayerContext.setVideoHeaderTime(Integer.parseInt(wheelPickerMin.getCurrentItem()) * 60 * 1000 +
                                Integer.parseInt(wheelPickerSec.getCurrentItem()) * 1000L);
                    } else {
                        mExPlayerContext.setVideoTailTime(Integer.parseInt(wheelPickerMin.getCurrentItem()) * 60 * 1000 +
                                Integer.parseInt(wheelPickerSec.getCurrentItem()) * 1000L);
                    }
                    if (onSetVideoHeaderTailTimeListener != null) {
                        onSetVideoHeaderTailTimeListener.onResult(isHeaderTime, Integer.parseInt(wheelPickerMin.getCurrentItem()) * 60 * 1000 +
                                Integer.parseInt(wheelPickerSec.getCurrentItem()) * 1000);
                    }
                })
                .setOnDismissListener(dialog -> {
                    if (!getGSYVideoManager().isPlaying()) {
                        mIvStartFull.performClick();
                    }
                }).create();
        if (setVideoHeaderTailTimeDialog.getWindow() != null) {
            setVideoHeaderTailTimeDialog.getWindow().getDecorView().setAlpha(0.8f);
        }
        setVideoHeaderTailTimeDialog.setOnShowListener(dialog -> {
            if (getGSYVideoManager().isPlaying()) {
                mIvStartFull.performClick();
            }
        });

        setVideoHeaderTailTimeDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        setVideoHeaderTailTimeDialog.show();
        fullScreenImmersive(setVideoHeaderTailTimeDialog.getWindow().getDecorView());
        setVideoHeaderTailTimeDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        final Button buttonOK = setVideoHeaderTailTimeDialog.findViewById(android.R.id.button1);
        if (buttonOK != null) {
            buttonOK.setEnabled(false);
        }

        wheelPickerMin.setOnValueChangeListener((wheelPicker, oldValue, newValue) -> {
            if (buttonOK == null) {
                return;
            }
            if (newValue.equals(tvMin) && wheelPickerSec.getCurrentItem().equals(tvSec)) {
                buttonOK.setEnabled(false);
            } else {
                buttonOK.setEnabled(true);
            }
        });

        wheelPickerSec.setOnValueChangeListener((wheelPicker, oldValue, newValue) -> {
            if (buttonOK == null) {
                return;
            }
            if (newValue.equals(tvSec) && wheelPickerMin.getCurrentItem().equals(tvMin)) {
                buttonOK.setEnabled(false);
            } else {
                buttonOK.setEnabled(true);
            }
        });

    }

    private OnSetVideoHeaderTailTimeListener onSetVideoHeaderTailTimeListener;

    public PlayerCore setOnSetVideoHeaderTailTimeListener(OnSetVideoHeaderTailTimeListener onSetVideoHeaderTailTimeListener) {
        this.onSetVideoHeaderTailTimeListener = onSetVideoHeaderTailTimeListener;
        return this;
    }

    public interface OnSetVideoHeaderTailTimeListener {
        void onResult(Boolean isHeaderTime, long time);
    }

    public PlayerCore setClingPlayUrl(String clingPlayUrl) {
        return this;
    }

    private void initBottomView() {
        mIvStart = findViewById(R.id.nvplayer_iv_start);
        mIvStart.setOnClickListener(v -> clickStartIcon());
        mIvStartFull = findViewById(R.id.nvplayer_iv_start_full);
        mIvStartFull.setOnClickListener(v -> clickStartIcon());
        mIvStartNext = findViewById(R.id.nvplayer_iv_start_next);
        mIvStartNext.setOnClickListener(v -> {
            if (onNextPlayClickListener != null) {
                onNextPlayClickListener.onClick(v);
            }
        });

        mIvEnterSmallWin = findViewById(R.id.nvplayer_iv_enter_small_win);
        mIvEnterSmallWin.setOnClickListener(v -> startFloatWin());

        findViewById(R.id.nvpalyer_bottom_slide_tv_speed)
                .setOnClickListener(v -> {
                    if (showNoVideoPlayNotFunctionToast()) return;
                    View viewBottomSpeedDialog = LayoutInflater.from(v.getContext())
                            .inflate(R.layout.nvplayer_bottom_speed_dialog, null);
                    final RadioGroup radioGroupBottomSpeed = viewBottomSpeedDialog.findViewById(R.id.nvpalyer_bottom_menu_rg_speed);
                    radioGroupBottomSpeed.setOnCheckedChangeListener((group, checkedId) -> {

                        if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_2_0) {
                            mPlaySpeed = 2f;
                        } else if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_1_5) {
                            mPlaySpeed = 1.5f;
                        } else if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_1_25) {
                            mPlaySpeed = 1.25f;
                        } else if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_1) {
                            mPlaySpeed = 1;
                        } else if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_0_75) {
                            mPlaySpeed = 0.75f;
                        } else if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_0_5) {
                            mPlaySpeed = 0.5f;
                        } else if (checkedId == R.id.nvpalyer_bottom_menu_rb_speed_0_25) {
                            mPlaySpeed = 0.25f;
                        }
                        setSpeedPlaying(mPlaySpeed, true);
                    });

                    mRightSlideMenuDialogBottomSpeed = new RightSlideMenuDialog(context, viewBottomSpeedDialog);

                    mRightSlideMenuDialogBottomSpeed.setOnShowListener(dialog -> {
                        //初始化播放速度控件
                        int speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_1;
                        if (mPlaySpeed == 2) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_2_0;
                        } else if (mPlaySpeed == 1.5f) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_1_5;
                        } else if (mPlaySpeed == 1.25f) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_1_25;
                        } else if (mPlaySpeed == 1) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_1;
                        } else if (mPlaySpeed == 0.75f) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_0_75;
                        } else if (mPlaySpeed == 0.5f) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_0_5;
                        } else if (mPlaySpeed == 0.25f) {
                            speedRbId = R.id.nvpalyer_bottom_menu_rb_speed_0_25;
                        }
                        ((RadioButton) mRightSlideMenuDialogBottomSpeed.findViewById(speedRbId)).setChecked(true);
                    });
                    mRightSlideMenuDialogBottomSpeed.show();
                    mRightSlideMenuDialogBottomSpeed.setWidth(725);
                });


        // TODO: 19-6-14 init episode
        final View viewEpisodeDialogView = LayoutInflater.from(context).inflate(R.layout.nvplayer_episode_dialog, null);
        final RecyclerView recyclerViewEpisode = viewEpisodeDialogView.findViewById(R.id.nvplayer_episode_dialog_rv);

        mRightSlideMenuDialogEpisode = new RightSlideMenuDialog(context, viewEpisodeDialogView);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 5);

        nEpisodeList = new ArrayList<>();
        nEpisodeList.add(new NEpisode("", ""));
        nEpisodeList.clear();
        nEpisodeRecyclerViewAdapter = new NEpisodeRecyclerViewAdapter(nEpisodeList, context, recyclerViewEpisode);
        nEpisodeRecyclerViewAdapter.setOnEpisodeItemClickListener((episode, position) -> {
            mRightSlideMenuDialogEpisode.dismiss();
            if (onEpisodeItemClickListener != null)
                onEpisodeItemClickListener.onEpisodeItemClick(episode, position);
        });

        recyclerViewEpisode.setAdapter(nEpisodeRecyclerViewAdapter);
        recyclerViewEpisode.setLayoutManager(gridLayoutManager);
        configVideoEpisodeRv(recyclerViewEpisode);

        mTvSelectEpisode = findViewById(R.id.nvpalyer_bottom_slide_tv_episode);
        mTvSelectEpisode.setOnClickListener(v -> {
            mRightSlideMenuDialogEpisode.show();
            mRightSlideMenuDialogEpisode.setWidth(845);
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
        GSYVideoGifSaveListener gsyVideoGifSaveListener = new GSYVideoGifSaveListener() {
            @Override
            public void result(boolean success, File file) {
                mGifCreator.cancelTask();
                post(() -> {
                    mIsGifProcessing = false;
                    if (success) {
                        setViewShowState(mIvShotGifDialogGif, VISIBLE);
                        setViewShowState(mTvShotGifDialogState, VISIBLE);
                        LoadUtils.loadImage(context, file, mIvShotGifDialogGif);
                        mIvShotGifDialogGif.setOnClickListener(v -> ImageUtils.shareImage(context, file, "分享GIF"));
                        mTvShotGifDialogTitle.setText("点击分享");
                        mTvShotGifDialogState.setText("已保存至:" + file.getAbsolutePath());
                        refreshPhoneImageGallery(context, file);
                    } else {
                        mTvShotGifDialogTitle.setText("GIF截取失败");
                    }
                    cleanGifTmpFile(file);
                });
            }

            @Override
            public void process(int curPosition, int total) {
                mIsGifProcessing = true;
                Message message = new Message();
                message.obj = new Integer[]{curPosition, total};
                mHandlerGifProcessing.sendMessage(message);
            }
        };
        mGifCreator = new GifCreator(this, gsyVideoGifSaveListener);
        mGifCreator.setContext(context);
        mGifCreator.setRelativePath("DCIM/" + mFishMovie + "/GIF/");
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandlerGifProcessing = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Integer[] integers = (Integer[]) msg.obj;
            mTvShotGifDialogTitle.setText("正在保存:" + mDecimalFormat.format(integers[0] * 100.000f / integers[1]) + "%");
        }
    };

    // TODO: 19-4-23 截图View
    @SuppressLint("ClickableViewAccessibility")
    private void initScreenshotGifView() {
        final View viewShotGifDialog = LayoutInflater.from(context).inflate(R.layout.nvplayer_video_share_screenshot_gif_dialog, null);
        mRightSlideMenuDialogShotGif = new RightSlideMenuDialog(context, R.style.nvplayer_dialog_TRANSLUCENT, Gravity.CENTER, viewShotGifDialog);
        mTvShotGifDialogTitle = viewShotGifDialog.findViewById(R.id.nvplayer_share_screenshot_gif_dialog_tv_title);
        mTvShotGifDialogState = viewShotGifDialog.findViewById(R.id.nvplayer_share_screenshot_gif_dialog_tv_state);
        mIvShotGifDialogGif = viewShotGifDialog.findViewById(R.id.nvplayer_share_screenshot_gif_dialog_iv);
        final ImageView ivShotGifDialogClose = viewShotGifDialog.findViewById(R.id.nvplayer_share_screenshot_gif_dialog_iv_close);
        mRightSlideMenuDialogShotGif.setOnDismissListener(dialog -> mImageViewScreenShotGif.setVisibility(GONE));
        ivShotGifDialogClose.setOnClickListener(v -> mRightSlideMenuDialogShotGif.dismiss());

        mImageViewScreenShotGif = findViewById(R.id.nvplayer_iv_screenshot_gif);
        mImageViewScreenShotGif.setOnTouchListener((v, event) -> {
            File fileGifDir = new File(mImageSavePath + "/Gif/");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                fileGifDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/Gif/");
            }
            if (!fileGifDir.exists()) {
                fileGifDir.mkdirs();
            }
            final File fileGif = new File(fileGifDir.getAbsolutePath() + "/" + NVCommonUtils.getCurrentTimeString() + ".gif");

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!mHadPlay) {
                        Toast.makeText(context, "GIF功能暂不可用,请稍后重试", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    mRightSlideMenuDialogShotGif.show();
                    mRightSlideMenuDialogShotGif.setCancelable(false);
                    mIvShotGifDialogGif.setVisibility(GONE);
                    mTvShotGifDialogState.setVisibility(GONE);
                    mRightSlideMenuDialogShotGif.setSize(1296, 864);
                    mIsShottingGif = true;
                    setViewShowState(mImageViewScreenShotGif, VISIBLE);
                    mGifStartTime = System.currentTimeMillis();
                    initGifHelper();
                    try {
                        mGifCreator.startGif(fileGifDir);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showShortToast(context, "截取失败");
                    }

                    mThreadShottingGif = new Thread(() -> {
                        while (mIsShottingGif) {
                            try {
                                Thread.sleep(50);
                                if (mIsShottingGif)
                                    mHandler.post(() -> mTvShotGifDialogTitle.setText("正在截取:" + mDecimalFormat.format((System.currentTimeMillis() - mGifStartTime) / 1000.0f) + "秒"));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mThreadShottingGif.start();
//                    new ThreadUtils().startNewThread(new ThreadUtils.OnThreadRunningListener() {
//                        @Override
//                        public void onStart(Handler handler) {
//
//                            while (mIsShottingGif) {
//                                try {
//                                    Thread.sleep(50);
//                                    if (mIsShottingGif)
//                                        handler.sendEmptyMessage(0);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onResult(Message message) {
//                            mTvShotGifDialogTitle.setText("正在截取:" + mDecimalFormat.format((System.currentTimeMillis() - mGifStartTime) / 1000.0f) + "秒");
//                        }
//                    });
                    break;
                case MotionEvent.ACTION_UP:
                    mIsShottingGif = false;
                    mGifEndTime = System.currentTimeMillis();
                    if (mGifEndTime - mGifStartTime < MIN_GIF_TIME) {
                        mRightSlideMenuDialogShotGif.dismiss();
                        Toast.makeText(context, "截取时间少于" + MIN_GIF_TIME / 1000 + "秒", Toast.LENGTH_SHORT).show();
                    }
                    try {
                        mGifCreator.stopGif(fileGif);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "创建失败," + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return true;
        });
    }

    private void initScreenshotView() {
        mImageViewScreenShot = findViewById(R.id.nvplayer_iv_screenshot);
        mImageViewScreenShot.setOnClickListener(v -> {
            //闪光动画
            if (mPauseOnScreenshot) flashView(findViewById(R.id.navplayer_flash_view));
            taskShotPic(bitmap -> {
                if (bitmap == null)
                    Toast.makeText(context, "获取截图失败,请重试!", Toast.LENGTH_LONG).show();

                final File fileImageDir = new File(mImageSavePath + "/Screenshots/");

                if (!fileImageDir.exists()) fileImageDir.mkdirs();
                final File fileImage = new File(fileImageDir.getAbsolutePath() + "/" + NVCommonUtils.getCurrentTimeString() + ".jpg");

                ImageUtils.OnSaveBitmapListener onSaveBitmapListener = new ImageUtils.OnSaveBitmapListener() {
                    @Override
                    public void onResult(final boolean isSuccess, final String path, final Uri uri) {
                        if (!isSuccess) {
                            Toast.makeText(context, "获取截图失败,请重试!", Toast.LENGTH_LONG).show();
                        } else {
                            View viewShareImageDialog = LayoutInflater.from(context).inflate(R.layout.nvplayer_video_share_screenshot_dialog, null);
                            ImageView imageView = viewShareImageDialog.findViewById(R.id.nvplayer_share_screenshot_dialog_iv);
                            viewShareImageDialog.setOnClickListener(v1 -> {
                                if (uri == null) {
                                    ImageUtils.shareImage(context, new File(path), "分享截图");
                                } else {
                                    ImageUtils.shareImage(context, uri, "分享截图");
                                }
                            });
                            LoadUtils.loadImage(context, bitmap, imageView);
                            RightSlideMenuDialog rightSlideMenuDialogShareImage = new RightSlideMenuDialog(context
                                    , R.style.nvplayer_dialog_TRANSLUCENT
                                    , viewShareImageDialog);
                            rightSlideMenuDialogShareImage.setOnShowListener(dialog -> {
                                if (mPauseOnScreenshot) {
                                    mIvStartFull.performClick();
                                }
                                new ThreadUtils().startNewThread(new ThreadUtils.OnThreadRunningListener() {
                                    @Override
                                    public void onStart(Handler handler) {
                                        try {
                                            Thread.sleep(SHARE_DIALOG_DISMISS_TIME);
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
                                if (mPauseOnScreenshot) {
                                    mIvStartFull.performClick();
                                }
                            });
                            rightSlideMenuDialogShareImage.show();
                            rightSlideMenuDialogShareImage.setSize(345, 255);
                            rightSlideMenuDialogShareImage.offset(0, 0, 80, 0);
                            Toast.makeText(context, "已保存至 " + path, Toast.LENGTH_LONG).show();
                        }
                    }
                };

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ImageUtils.saveBitmapForAndroidQ(context, bitmap, NVCommonUtils.getCurrentTimeString() + ".jpg", "DCIM/" + mFishMovie + "/Screenshots", onSaveBitmapListener);
                } else {
                    ImageUtils.saveBitmap(bitmap
                            , fileImage
                            , context, onSaveBitmapListener);
                }
            }, mIsHighScreenShot);
        });
    }

    private RightSlideMenuDialog mRightSlideMenuDialogShotGif;
    private TextView mTvShotGifDialogTitle;
    private TextView mTvShotGifDialogState;
    private ImageView mIvShotGifDialogGif;

    private Thread mThreadRunningListener;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CURRENT_STATE_NORMAL) {
                if (mThumbImageViewLayout.getVisibility() == VISIBLE && isShowTipContentView()) {
                    mThumbImageViewLayout.setVisibility(INVISIBLE);
                }
            } else if (msg.what == CURRENT_STATE_PREPAREING || msg.what == CURRENT_STATE_PLAYING_BUFFERING_START) {
                Log.e(TAG, "PlayerCore.onResult: getNetSpeed___" + getNetSpeed());
                mTvNetSpeed.setText(getNetSpeedText());
            }
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
        mThreadRunningListener.start();
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
        return mDecimalFormat.format((double) up / (double) down);
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
            final View localView = LayoutInflater.from(getActivityContext()).inflate(getBrightnessLayoutId(), null);
            if (localView.findViewById(getBrightnessProgressId()) instanceof ProgressBar) {
                mBrightnessProgressBar = localView.findViewById(getBrightnessProgressId());
            }
//            if (localView.findViewById(getBrightnessTextId()) instanceof TextView) {
//                mBrightnessDialogTv = localView.findViewById(getBrightnessTextId());
//            }
            mBrightnessDialog = new Dialog(getActivityContext(), R.style.video_style_dialog_progress);
            mBrightnessDialog.setContentView(localView);
            if (mBrightnessDialog.getWindow() != null) {
                mBrightnessDialog.getWindow().addFlags(8);
                mBrightnessDialog.getWindow().addFlags(32);
                mBrightnessDialog.getWindow().addFlags(16);
                mBrightnessDialog.getWindow().setLayout(-2, -2);
                final WindowManager.LayoutParams localLayoutParams = mBrightnessDialog.getWindow().getAttributes();
                localLayoutParams.gravity = Gravity.TOP | Gravity.START;
                localLayoutParams.width = getWidth();
                localLayoutParams.height = getHeight();
                int[] location = new int[2];
                getLocationOnScreen(location);
                localLayoutParams.x = location[0];
                localLayoutParams.y = location[1];
                mBrightnessDialog.getWindow().setAttributes(localLayoutParams);
            }
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }

        int percentInt = (int) (percent * 100);
        final ImageView imageViewBrightness = mBrightnessDialog.findViewById(R.id.nvPlayer_brightness_iv_icon);

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
            final View localView = LayoutInflater.from(getActivityContext()).inflate(getVolumeLayoutId(), null);
            if (localView.findViewById(getVolumeProgressId()) instanceof ProgressBar) {
                mDialogVolumeProgressBar = localView.findViewById(getVolumeProgressId());
                if (mVolumeProgressDrawable != null && mDialogVolumeProgressBar != null) {
                    mDialogVolumeProgressBar.setProgressDrawable(mVolumeProgressDrawable);
                }
            }
            mVolumeDialog = new Dialog(getActivityContext(), R.style.video_style_dialog_progress);
            mVolumeDialog.setContentView(localView);
            if (mVolumeDialog.getWindow() != null) {
                mVolumeDialog.getWindow().addFlags(8);
                mVolumeDialog.getWindow().addFlags(32);
                mVolumeDialog.getWindow().addFlags(16);
                mVolumeDialog.getWindow().setLayout(-2, -2);

                final WindowManager.LayoutParams localLayoutParams = mVolumeDialog.getWindow().getAttributes();
                localLayoutParams.gravity = Gravity.TOP | Gravity.START;
                localLayoutParams.width = getWidth();
                localLayoutParams.height = getHeight();
                int[] location = new int[2];
                getLocationOnScreen(location);
                localLayoutParams.x = location[0];
                localLayoutParams.y = location[1];
                mVolumeDialog.getWindow().setAttributes(localLayoutParams);
            }
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }

        final ImageView imageViewVolume = mVolumeDialog.findViewById(R.id.nvPlayer_volume_iv_icon);
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
        final int curWidth = CommonUtil.getCurrentScreenLand((Activity) getActivityContext()) ? mScreenHeight : mScreenWidth;
        final int curHeight = CommonUtil.getCurrentScreenLand((Activity) getActivityContext()) ? mScreenWidth : mScreenHeight;

        if (mChangePosition) {
            final int totalTimeDuration = getDuration();
            mSeekTimePosition = (int) (mDownPosition + (deltaX * 500));
            if (mSeekTimePosition > totalTimeDuration) {
                mSeekTimePosition = totalTimeDuration;
            }
            final String seekTime = CommonUtil.stringForTime(mSeekTimePosition);
            final String totalTime = CommonUtil.stringForTime(totalTimeDuration);
            showProgressDialog(deltaX, seekTime, mSeekTimePosition, totalTime, totalTimeDuration);
        } else if (mChangeVolume) {
            deltaY = -deltaY / 2f;
            final int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            final int deltaV = (int) (max * deltaY * 3 / curHeight);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
            final int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / curHeight);

            showVolumeDialog(-deltaY, volumePercent);
        } else if (!mChangePosition && mBrightness) {
            if (Math.abs(deltaY) > 10) {
                final float percent = (-deltaY / curHeight);
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
        final WindowManager.LayoutParams lpa = ((Activity) (mContext)).getWindow().getAttributes();
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

        final WindowManager.LayoutParams lpa = ((Activity) (mContext)).getWindow().getAttributes();
        lpa.screenBrightness = mBrightnessData + percent;

        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }

        if (mContext instanceof Activity) {
            ((Activity) (mContext)).getWindow().setAttributes(lpa);
        }
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
        mByStartedClick = true;
        super.onClickUiToggle();
    }

    @Override
    protected void changeUiToNormal() {
        Log.e(TAG, "PlayerCore.changeUiToNormal: ");
        super.changeUiToNormal();

        mByStartedClick = false;

        setViewShowState(mTopContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mLlCenterLoading, INVISIBLE);
        setViewShowState(mLlRightCustomContainerOne, INVISIBLE);
        if (!mIsShottingGif) setViewShowState(mImageViewScreenShotGif, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mBottomProgressBar, (mIfCurrentIsFullscreen && mLockCurScreen) ? VISIBLE : GONE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);

        if (isShowTipContentView()) {
            setViewShowState(mThumbImageViewLayout, INVISIBLE);
        } else {
            setViewShowState(mThumbImageViewLayout, VISIBLE);
        }
        hideTipContent();

        checkAllCustomWidget();
    }

    @Override
    protected void changeUiToClear() {
        Log.e(TAG, "PlayerCore.changeUiToClear: ");
        super.changeUiToClear();
        setViewShowState(mLlCenterLoading, INVISIBLE);
        setViewShowState(mLlRightCustomContainerOne, INVISIBLE);
        if (!mIsShottingGif) setViewShowState(mImageViewScreenShotGif, INVISIBLE);
        setViewShowState(mImageViewScreenShotGif, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        hideTipContent();
    }

    @Override
    protected void changeUiToPreparingShow() {
        Log.e(TAG, "PlayerCore.changeUiToPreparingShow: ");
        super.changeUiToPreparingShow();

        showPlayPrepareTipView("正在载入视频,请稍后...");

        setViewShowState(mLlCenterLoading, VISIBLE);
//        setViewShowState(mLoadingProgressBar, INVISIBLE);

        setViewShowState(mTopContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mLlCenterLoading, INVISIBLE);

        if (mIfCurrentIsFullscreen) {
            setViewShowState(mLlRightCustomContainerOne, VISIBLE);
            setViewShowState(mImageViewScreenShotGif, VISIBLE);

        }

        checkAllCustomWidget();
        mCloseType = 0;

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);

    }

    @Override
    protected void changeUiToPrepareingClear() {
        Log.e(TAG, "PlayerCore.changeUiToPrepareingClear: ");
        super.changeUiToPrepareingClear();

        setViewShowState(mLlCenterLoading, INVISIBLE);
        setViewShowState(mLlRightCustomContainerOne, INVISIBLE);

        if (!mIsShottingGif) setViewShowState(mImageViewScreenShotGif, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
    }

    @Override
    protected void changeUiToPlayingShow() {
        Log.e(TAG, "PlayerCore.changeUiToPlayingShow: ");
        super.changeUiToPlayingShow();

        if (!mByStartedClick) {
            setViewShowState(mBottomContainer, INVISIBLE);
            setViewShowState(mStartButton, INVISIBLE);
        }

        setViewShowState(mTopContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomProgressBar, (mIfCurrentIsFullscreen && mLockCurScreen) ? VISIBLE : GONE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLlCenterLoading, INVISIBLE);

        if (mIfCurrentIsFullscreen) {
            setViewShowState(mLlRightCustomContainerOne, VISIBLE);
            setViewShowState(mImageViewScreenShotGif, VISIBLE);
        }

        checkAllCustomWidget();

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);

        hideTipContent();

        //开始ScrollTextView
//        ((ScrollTextView) findViewById(R.id.nvplayer_stv))
//                .startScroll();
    }

    @Override
    protected void changeUiToPlayingClear() {
        Log.e(TAG, "PlayerCore.changeUiToPlayingClear: ");
        super.changeUiToPlayingClear();

        hideTipContent();

        setViewShowState(mBottomProgressBar, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
    }

    @Override
    protected void changeUiToPauseShow() {
        Log.e(TAG, "PlayerCore.changeUiToPauseShow: ");
        super.changeUiToPauseShow();
        setViewShowState(mTopContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomContainer, mLockCurScreen ? INVISIBLE : VISIBLE);
        setViewShowState(mBottomProgressBar, (mIfCurrentIsFullscreen && mLockCurScreen) ? VISIBLE : GONE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLlCenterLoading, INVISIBLE);
        if (mIfCurrentIsFullscreen) {
            setViewShowState(mLlRightCustomContainerOne, VISIBLE);
            setViewShowState(mImageViewScreenShotGif, VISIBLE);
        }
        checkAllCustomWidget();

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        hideTipContent();
    }

    @Override
    protected void changeUiToPauseClear() {
        Log.e(TAG, "PlayerCore.changeUiToPauseClear: ");
        super.changeUiToPauseClear();
        setViewShowState(mBottomProgressBar, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        hideTipContent();
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        Log.e(TAG, "PlayerCore.changeUiToPlayingBufferingShow: ");
        super.changeUiToPlayingBufferingShow();

        if (!mByStartedClick) {
            setViewShowState(mBottomContainer, INVISIBLE);
            setViewShowState(mStartButton, INVISIBLE);
        }
        if (isShowTipContentView()) {
            showPlayPrepareTipView("正在缓冲");
        } else {
            if (mThumbImageViewLayout.getVisibility() == VISIBLE) {
                setViewShowState(mLlCenterLoading, INVISIBLE);
                setViewShowState(mLoadingProgressBar, INVISIBLE);
            } else {
                setViewShowState(mLlCenterLoading, VISIBLE);
                setViewShowState(mLoadingProgressBar, VISIBLE);
            }
        }

        if (mIfCurrentIsFullscreen) {
            setViewShowState(mLlRightCustomContainerOne, VISIBLE);
            setViewShowState(mImageViewScreenShotGif, VISIBLE);
        }

        checkAllCustomWidget();

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
    }

    @Override
    protected void changeUiToPlayingBufferingClear() {
        Log.e(TAG, "PlayerCore.changeUiToPlayingBufferingClear: ");
        super.changeUiToPlayingBufferingClear();

        setViewShowState(mLlCenterLoading, VISIBLE);
        setViewShowState(mLlRightCustomContainerOne, INVISIBLE);

        if (!mIsShottingGif) setViewShowState(mImageViewScreenShotGif, INVISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        hideTipContent();
    }

    @Override
    protected void changeUiToCompleteShow() {
        Log.e(TAG, "PlayerCore.changeUiToCompleteShow: ");
        super.changeUiToCompleteShow();
        setViewShowState(mBottomProgressBar, (mIfCurrentIsFullscreen && mLockCurScreen) ? VISIBLE : GONE);
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLlCenterLoading, INVISIBLE);
        if (mIfCurrentIsFullscreen) {
            setViewShowState(mLlRightCustomContainerOne, VISIBLE);
            setViewShowState(mImageViewScreenShotGif, VISIBLE);
        }
        checkAllCustomWidget();

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        showPlayCompleteTipView();
    }

    public void onCompleteByUser() {
        if (mCloseType == 1) {
            showCloseTypeDialog();
        }
    }

    @Override
    protected void changeUiToCompleteClear() {
        Log.e(TAG, "PlayerCore.changeUiToCompleteClear: ");
        super.changeUiToCompleteClear();
        setViewShowState(mBottomProgressBar, (mIfCurrentIsFullscreen && mLockCurScreen) ? VISIBLE : GONE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLlCenterLoading, INVISIBLE);
        setViewShowState(mLlRightCustomContainerOne, INVISIBLE);
        if (!mIsShottingGif) setViewShowState(mImageViewScreenShotGif, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        hideTipContent();
    }

    @Override
    protected void changeUiToError() {
        Log.e(TAG, "PlayerCore.changeUiToError: ");
        super.changeUiToError();
        setViewShowState(mBottomProgressBar, (mIfCurrentIsFullscreen && mLockCurScreen) ? VISIBLE : GONE);
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLlRightCustomContainerOne, INVISIBLE);
        if (!mIsShottingGif) setViewShowState(mImageViewScreenShotGif, INVISIBLE);
        setViewShowState(mIvStart, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(mIvEnterSmallWin, mIfCurrentIsFullscreen ? GONE : GONE);
        setViewShowState(mLlCenterLoading, INVISIBLE);
        setViewShowState(mIvStartNext, INVISIBLE);

        setViewShowState(mFullscreenButton, mIfCurrentIsFullscreen ? GONE : VISIBLE);

        showPlayErrorTipView();
    }

    /**
     * 锁屏时隐藏的控件
     */
    @Override
    protected void hideAllWidget() {
        super.hideAllWidget();
        setViewShowState(mBottomProgressBar, GONE);
        setViewShowState(mLlRightCustomContainerOne, INVISIBLE);
        if (!mIsShottingGif) setViewShowState(mImageViewScreenShotGif, INVISIBLE);
        hideTipContent();
    }

    @Override
    public View getStartButton() {
        return mIfCurrentIsFullscreen ? mIvStartFull : mIvStart;
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

    public PlayerCore setActivity(final Activity activity) {
        mExPlayerContext.setActivity(activity);
        return this;
    }

    private void setPlayIvState(Boolean isPlay) {
        mIvStartFull.setImageDrawable(getResources()
                .getDrawable(isPlay ?
                        R.drawable.ic_nvplayer_bottom_play
                        : R.drawable.ic_nvplayer_bottom_pause_2));
        mIvStart.setImageDrawable(getResources()
                .getDrawable(isPlay ?
                        R.drawable.ic_nvplayer_bottom_play
                        : R.drawable.ic_nvplayer_bottom_pause_2));

    }

    private void checkAllCustomWidget() {
        setViewShowState(mStartButton, INVISIBLE);
        if (mExPlayerContext.getPlayerMode() != PlayerMode.TV_DIRECT && mExPlayerContext.getPlayerMode() != PlayerMode.LOCATION) {
            setViewShowState(mIvStartNext, isHaveNext ? VISIBLE : INVISIBLE);
        } else {
            setViewShowState(mIvStartNext, INVISIBLE);
            setViewShowState(mIvShare, INVISIBLE);
        }
        setViewShowState(mRlFullScreenTopCustomContainer, mIfCurrentIsFullscreen ? VISIBLE : GONE);
        setViewShowState(mRlNormalScreenTopCustomContainer, mIfCurrentIsFullscreen ? GONE : VISIBLE);
        setViewShowState(mRlFullScreenBottomCustomContainer, mIfCurrentIsFullscreen ? VISIBLE : GONE);
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mLlRightCustomContainerOne, INVISIBLE);
            setViewShowState(mImageViewScreenShotGif, INVISIBLE);
        } else {
            setViewShowState(mIvStart, GONE);
            setViewShowState(mIvEnterSmallWin, GONE);
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

        Log.e(TAG, "PlayerCore.lockTouchLogic:mLockCurScreen " + mLockCurScreen);

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
    private Handler mHandlerClosePlayer = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showCloseTypeDialog();
        }
    };
    private Runnable mRunnableClosePlayer = () -> {
        try {
            while (true) {
                if (mIsDestroy) return;
                switch (mCloseType) {
                    case 2:
                        Thread.sleep(30 * 60 * 1000);
                        mHandlerClosePlayer.sendEmptyMessage(0);
                        return;
                    case 3:
                        Thread.sleep(60 * 60 * 1000);
                        mHandlerClosePlayer.sendEmptyMessage(0);
                        return;
                    case 4:
                        Thread.sleep(90 * 60 * 1000);
                        mHandlerClosePlayer.sendEmptyMessage(0);
                        return;
                    default:
                        return;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };
    private Thread mThreadClosePlayer = new Thread(mRunnableClosePlayer);

    private void resolveClose() {
        if (mThreadClosePlayer.isAlive()) {
            mThreadClosePlayer.interrupt();
        }
        mThreadClosePlayer = new Thread(mRunnableClosePlayer);
        mThreadClosePlayer.start();
    }

    private int closeTypeDialogCount_DEFAULT_VALUE = 15;
    private int closeTypeDialogCount = closeTypeDialogCount_DEFAULT_VALUE;

    private void showCloseTypeDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage("设定的时间到了,是否退出当前播放")
                .setPositiveButton("继续播放", (dialog, which) -> {
                    mIsClickContinuePlay = true;
                    mCloseType = 0;
                })
                .setNegativeButton("退出播放(" + closeTypeDialogCount + ")", (dialog, which) -> {
                    mCloseType = 0;
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
                        if (mIsDestroy) return;
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
                    if (!mIsClickContinuePlay) {
                        handler.sendEmptyMessage(1);
                        mIsClickContinuePlay = false;
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
            if (!mIsDestroy) {
                alertDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        // TODO: 19-6-13 startWindowFullscreen
        PlayerCore playerCore = (PlayerCore) super.startWindowFullscreen(context, actionBar, statusBar);

        //        nvPlayer.mSourcePosition=mSourcePosition;
        playerCore.mType = mType;
//        nvPlayer.mTransformSize=mTransformSize;
        playerCore.resolveTypeUI();
        //预览图
        startWindowFullscreenPreView(playerCore);
        playerCore.mExPlayerContext = mExPlayerContext;
        playerCore.mTvInfoUrl.setText(mExPlayerContext.infoUrl);
        playerCore.mTvInfoUrl.setVisibility(VISIBLE);

        playerCore.onShareClickListener = onShareClickListener;
        playerCore.onSetVideoHeaderTailTimeListener = onSetVideoHeaderTailTimeListener;
        playerCore.onEpisodeItemClickListener = onEpisodeItemClickListener;
        playerCore.onNextPlayClickListener = onNextPlayClickListener;
        playerCore.mIvShare = mIvShare;
        playerCore.nEpisodeList = nEpisodeList;
        playerCore.mRightSlideMenuDialogEpisode = mRightSlideMenuDialogEpisode;
        playerCore.isHaveNext = isHaveNext;

        if (mExPlayerContext.playerMode == PlayerMode.LOCATION || mExPlayerContext.playerMode == PlayerMode.TV_DIRECT) {
            setViewShowState(playerCore.mTvSelectEpisode, GONE);
            setViewShowState(playerCore.mIvClingTv, GONE);
            setViewShowState(playerCore.mIvShare, GONE);
            setViewShowState(playerCore.mIvStartNext, GONE);
        } else {
            setViewShowState(playerCore.mTvSelectEpisode, VISIBLE);
            setViewShowState(playerCore.mIvClingTv, VISIBLE);
            setViewShowState(playerCore.mIvShare, VISIBLE);
            setViewShowState(playerCore.mIvStartNext, VISIBLE);
        }

        return playerCore;
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
            PlayerCore playerCore = (PlayerCore) gsyVideoPlayer;
//            mSourcePosition = nvPlayer.mSourcePosition;
            mType = playerCore.mType;
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
        mByStartedClick = true;
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


    private void startWindowFullscreenPreView(PlayerCore playerCore) {
        playerCore.mOpenPreView = mOpenPreView;
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

    private TextView mTvSelectEpisode;

    public PlayerCore setnEpisodeList(List<NEpisode> nEpisodeList) {
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
    public PlayerCore setVideoName(String videoName) {
        this.mVideoName = videoName;
        return this;
    }

    public void onPlayerPause() {
        try {
            mRightSlideMenuDialogEpisode.dismiss();
            mRightSlideMenuDialogShotGif.dismiss();
            mRightSlideMenuDialogBottomSpeed.dismiss();
            mRightSlideMenuDialogTopRightMenuDialog.dismiss();
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

    public PlayerCore setExiteApp(Boolean exiteApp) {
        isExitApp = exiteApp;
        return this;
    }

    public Boolean getHighScreenShot() {
        return mIsHighScreenShot;
    }

    public PlayerCore setHighScreenShot(Boolean highScreenShot) {
        mIsHighScreenShot = highScreenShot;
        return this;
    }

    private OnNextPlayClickListener onNextPlayClickListener;

    public PlayerCore setOnNextPlayClickListener(OnNextPlayClickListener onNextPlayClickListener) {
        this.onNextPlayClickListener = onNextPlayClickListener;
        return this;
    }

    public interface OnNextPlayClickListener {
        void onClick(View view);
    }

    private OnShareClickListener onShareClickListener;

    public PlayerCore setOnShareClickListener(OnShareClickListener onShareClickListener) {
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
        return mPauseOnScreenshot;
    }

    public PlayerCore setPauseOnScreenshot(boolean pauseOnScreenshot) {
        this.mPauseOnScreenshot = pauseOnScreenshot;
        return this;
    }

    public String getPlayErrorText() {
        return mPlayErrorText;
    }

    public PlayerCore setPlayErrorText(String playErrorText) {
        this.mPlayErrorText = playErrorText;
        mTvPlayError.setText(playErrorText);
        return this;
    }

    public ImageView getImageViewShare() {
        return mIvShare;
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
        return mIfCurrentIsFullscreen && ((PlayerCore) getCurrentPlayer()).getLockCurrentScreen();
    }

    public PlayerCore setVideoHeaderTime(long time) {
        mExPlayerContext.setVideoHeaderTime(time);
        return this;
    }

    public PlayerCore setVideoTailTime(long time) {
        mExPlayerContext.setVideoTailTime(time);
        return this;
    }

    public PlayerCore setPlayerMode(@PlayerMode int playerMode) {
        mExPlayerContext.setPlayerMode(playerMode);
        if (playerMode == PlayerMode.LOCATION || playerMode == PlayerMode.TV_DIRECT) {
            setViewShowState(mTvSelectEpisode, GONE);
            setViewShowState(mIvClingTv, GONE);
            setViewShowState(mIvShare, GONE);
            setViewShowState(mIvStartNext, GONE);
        } else {
            setViewShowState(mTvSelectEpisode, VISIBLE);
            setViewShowState(mIvClingTv, VISIBLE);
            setViewShowState(mIvShare, VISIBLE);
            setViewShowState(mIvStartNext, VISIBLE);
        }
        return this;
    }

    @IntDef({PlayerMode.INTERNET, PlayerMode.TV_DIRECT, PlayerMode.LOCATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayerMode {
        int INTERNET = 0;
        int TV_DIRECT = 1;
        int LOCATION = 2;
    }

    private static class ExPlayerContext {
        private Long videoHeaderTime = 0L,
                videoTailTime = 0L;

        private View.OnClickListener mOnContinuePlayForMobileInternetClickListener, mOnReplayClickListener;

        private String infoUrl;

        private @PlayerMode
        int playerMode = PlayerMode.INTERNET;

        private Activity mActivity;

        public Activity getActivity() {
            return mActivity;
        }

        public ExPlayerContext setActivity(final Activity activity) {
            mActivity = activity;
            return this;
        }

        public @PlayerMode
        int getPlayerMode() {
            return playerMode;
        }

        public ExPlayerContext setPlayerMode(@PlayerMode final int playerMode) {
            this.playerMode = playerMode;
            return this;
        }

        public String getInfoUrl() {
            return infoUrl;
        }

        public ExPlayerContext setInfoUrl(final String infoUrl) {
            this.infoUrl = infoUrl;
            return this;
        }

        OnClickListener getOnContinuePlayForMobileInternetClickListener() {
            return mOnContinuePlayForMobileInternetClickListener;
        }

        ExPlayerContext setOnContinuePlayForMobileInternetClickListener(final OnClickListener onContinuePlayForMobileInternetClickListener) {
            mOnContinuePlayForMobileInternetClickListener = onContinuePlayForMobileInternetClickListener;
            return this;
        }

        OnClickListener getOnReplayClickListener() {
            return mOnReplayClickListener;
        }

        ExPlayerContext setOnReplayClickListener(final OnClickListener onReplayClickListener) {
            mOnReplayClickListener = onReplayClickListener;
            return this;
        }

        Long getVideoHeaderTime() {
            return videoHeaderTime;
        }

        ExPlayerContext setVideoHeaderTime(Long videoHeaderTime) {
            this.videoHeaderTime = videoHeaderTime;
            return this;
        }

        Long getVideoTailTime() {
            return videoTailTime;
        }

        ExPlayerContext setVideoTailTime(Long videoTailTime) {
            this.videoTailTime = videoTailTime;
            return this;
        }
    }

    /**
     * 生命周期
     */
    public void onRestart() {
    }

    public void onResume() {
        if (mClingViewManager != null) {
            mClingViewManager.onHostActivityResume();
        }

        getCurrentPlayer().onVideoResume(false);
    }

    public void onPause() {
        getCurrentPlayer().onVideoPause();
        onPlayerPause();
    }

    public void onDestroy(Boolean isPlay, GSYVideoOptionBuilder gsyVideoOptionBuilder) {
        try {
            gsyVideoOptionBuilder
                    .setUrl("")
                    .build(this);
            getCurrentPlayer()
                    .setUp("", false, "");

            play();
            getGSYVideoManager().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (isPlay) {
                getGSYVideoManager().stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gsyVideoOptionBuilder != null) {
            gsyVideoOptionBuilder = null;
        }

        onDestroy();
    }

    public void onDestroy() {
        mIsDestroy = true;

        if (mThreadRunningListener != null && mThreadRunningListener.isAlive()) {
            mThreadRunningListener.interrupt();
        }

        if (mThreadShottingGif != null && mThreadShottingGif.isAlive()) {
            mThreadShottingGif.interrupt();
        }

        if (mThreadClosePlayer != null && mThreadClosePlayer.isAlive()) {
            mThreadClosePlayer.interrupt();
        }

        if (mThreadSyncTopTime != null && mThreadSyncTopTime.isAlive()) {
            mThreadSyncTopTime.interrupt();
        }

        try {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
            if (mHandlerGifProcessing != null) {
                mHandlerGifProcessing.removeCallbacksAndMessages(null);
            }
            if (mHandlerClosePlayer != null) {
                mHandlerClosePlayer.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (mNVPlayerBatteryView != null) {
                mNVPlayerBatteryView.onDestroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            clearCurrentCache();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            getGSYVideoManager().clearCache(context, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getGSYVideoManager() != null && getGSYVideoManager().getPlayer() != null) {
            getGSYVideoManager().getPlayer().release();
            getGSYVideoManager().getPlayer().releaseSurface();
            getGSYVideoManager().releaseMediaPlayer();
        }

        if (getCurrentPlayer() != null) {
            getCurrentPlayer().release();
        }

        if (getFullWindowPlayer() != null) {
            getFullWindowPlayer().release();
        }
        release();
    }

    private static final String SP_KEY_IS_DISABLE_LOCATION_SETTINGS = "SP_KEY_IS_DISABLE_LOCATION_SETTINGS";

    private Boolean getIsDisableLocationSettings() {
        SharedPreferences sharePreferences = getSharePreferences();
        return sharePreferences.getBoolean(SP_KEY_IS_DISABLE_LOCATION_SETTINGS, false);
    }

    private void saveIsDisableLocationSettings(Boolean value) {
        SharedPreferences sharePreferences = getSharePreferences();
        SharedPreferences.Editor edit = sharePreferences.edit();
        edit.putBoolean(SP_KEY_IS_DISABLE_LOCATION_SETTINGS, value).apply();
    }

    private SharedPreferences getSharePreferences() {
        return context.getApplicationContext().getSharedPreferences(SHARE_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
    }

}