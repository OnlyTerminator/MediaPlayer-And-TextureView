package videoview.zxf.com.videoplaymodule;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;

import videoview.zxf.com.videoplaymodule.view.ProgressWheel;

/**
 * Created by aotuman on 2016/11/15.
 */

public class VideoPlayTexture extends FrameLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, TextureView.SurfaceTextureListener, MediaPlayerListener {
    private static String TAG = "VideoPlayTexture";

    public static final int CURRENT_STATE_NORMAL = 0; //初始状态
    public static final int CURRENT_STATE_PREPARING = 1; //正在准备
    public static final int CURRENT_STATE_PLAYING = 2; //正在播放
    public static final int CURRENT_STATE_PLAYING_BUFFERING_START = 3; //正在缓冲
    public static final int CURRENT_STATE_PAUSE = 5; //暂停
    public static final int CURRENT_STATE_AUTO_COMPLETE = 6; //播放完成
    public static final int CURRENT_STATE_ERROR = 7; //播放出现异常

    private static final int SHOW_PROGRESS = 2;
    private static final int HIDE_PROGRESS = 1;
    private static final int SHOW_TIME = 3000;
    private Context context;
    private TextureView video_play_view;
    private VideoPlayManager videoPlayManager;
    private boolean autoPlay = false;
    private String videoPath;
    private TextView tv_time_start, tv_time_end;
    private SeekBar seekBar;
    private ImageView iv_play_video;
    private ImageView iv_full_careen;
    private int currentState;
    private RelativeLayout rl_bottom_menu;
    private ProgressWheel progressWheel;
    private View inflate;

    public VideoPlayTexture(Context context) {
        this(context, null);
        Log.i("-----------------------", "1");
    }

    public VideoPlayTexture(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        Log.i("-----------------------", "2");
    }

    public VideoPlayTexture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("-----------------------", "3");
        this.context = context;
        initView();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "texture is creat");
        videoPlayManager.initMediaPlay(surface);
        videoPlayManager.setVideoUrl(videoPath);
        if (autoPlay) {
            setAllControlsVisible(View.VISIBLE, View.GONE);
            prepareAsyncVideo();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "texture is change");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(TAG, "texture is destroy");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void initView() {
        if (null == inflate) {
            inflate = View.inflate(context, R.layout.video_play_view, this);
            rl_bottom_menu = (RelativeLayout) inflate.findViewById(R.id.rl_bottom_menu);
            video_play_view = (TextureView) inflate.findViewById(R.id.ttv_video_play);
            tv_time_start = (TextView) inflate.findViewById(R.id.tv_time_start);
            tv_time_end = (TextView) inflate.findViewById(R.id.tv_time_end);
            seekBar = (SeekBar) inflate.findViewById(R.id.seekBar);
            iv_play_video = (ImageView) inflate.findViewById(R.id.iv_play_video);
            iv_full_careen = (ImageView) inflate.findViewById(R.id.iv_fullScreen);
            progressWheel = (ProgressWheel) inflate.findViewById(R.id.player_progressBar);
            videoPlayManager = VideoPlayManager.getInstance();
            seekBar.setMax(1000);

            video_play_view.setSurfaceTextureListener(this);
            iv_full_careen.setOnClickListener(this);
            iv_play_video.setOnClickListener(this);
            seekBar.setOnSeekBarChangeListener(this);
        }
    }

    public void setVideoPlaySource(String videoPath, String picPath) {
        this.videoPath = videoPath;
        changePlayCurrentState(CURRENT_STATE_NORMAL);
    }

    public void startVideo() {
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        videoPlayManager.startVideo();
        changePlayCurrentState(CURRENT_STATE_PLAYING);
        setAllControlsVisible(View.INVISIBLE, View.GONE);
    }

    public void prepareAsyncVideo() {
        videoPlayManager.prepareAsync();
        MediaPlayListenerManager.getInstance().replaceListener(this);
        changePlayCurrentState(CURRENT_STATE_PREPARING);
    }

    public void pauseVideo() {
        videoPlayManager.pauseVideo();
        changePlayCurrentState(CURRENT_STATE_PAUSE);
    }

    public void isAutoPlay(boolean flag) {
        this.autoPlay = flag;
    }

    private void changePlayCurrentState(int state) {
        currentState = state;
        setPlayView();
    }

    /**
     * 播放时间换算
     *
     * @param timeMs
     * @return
     */
    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 控制所有布局的显示
     */
    private void setAllControlsVisible(int bottomVisibility, int progressVisiblity) {
        progressWheel.setVisibility(progressVisiblity);
        rl_bottom_menu.setVisibility(bottomVisibility);
    }

    private void showBottomView(int timeout) {
        rl_bottom_menu.setVisibility(View.VISIBLE);
        if (timeout != 0) {
            mHandler.removeMessages(HIDE_PROGRESS);
            Message msg = mHandler.obtainMessage(HIDE_PROGRESS);
            mHandler.sendMessageDelayed(msg, SHOW_TIME);
        }
    }

    private void hideBottomView() {
        rl_bottom_menu.setVisibility(View.GONE);
    }

    private void setPlayView() {
        if (currentState == CURRENT_STATE_PLAYING) {
            iv_play_video.setImageResource(R.drawable.mn_player_pause);
        } else if (currentState == CURRENT_STATE_ERROR) {
            iv_play_video.setImageResource(R.drawable.mn_player_play);
        } else {
            iv_play_video.setImageResource(R.drawable.mn_player_play);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showBottomView(0); // show until hide is called
                break;
            case MotionEvent.ACTION_UP:
                showBottomView(SHOW_TIME); // start timeout
                break;
            case MotionEvent.ACTION_CANCEL:
                hideBottomView();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 播放状态改变
     */
    @Override
    public void onPrepared() {
        Log.i(TAG, "video prepared");
        if (autoPlay) {
            setAllControlsVisible(View.GONE, View.GONE);
            startVideo();
        } else {
            showBottomView(0);
        }
    }

    @Override
    public void onCompletion() {
        Log.i(TAG, "video completion");
        currentState = CURRENT_STATE_AUTO_COMPLETE;
    }

    @Override
    public void onBufferingUpdate(int percent) {
        seekBar.setSecondaryProgress(percent * 10);
    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onError(int what, int extra) {
    }

    @Override
    public void onInfo(int what, int extra) {

    }

    @Override
    public void onVideoSizeChanged() {

    }

    @Override
    public int getState() {
        return currentState;
    }

    @Override
    public void changeSystemVoice(int voice) {

    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_play_video == id) {
            showBottomView(SHOW_TIME);
            if (currentState == CURRENT_STATE_PAUSE) {
                startVideo();
            } else if (currentState == CURRENT_STATE_PLAYING) {
                pauseVideo();
            } else if (currentState == CURRENT_STATE_NORMAL) {
                prepareAsyncVideo();
            } else if (currentState == CURRENT_STATE_PREPARING) {
                startVideo();
            }
        } else if (R.id.iv_fullScreen == id) {
            //设置横屏
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (rl_bottom_menu.getVisibility() == View.VISIBLE) {
//                mn_rl_top_menu.setVisibility(View.VISIBLE);
            }
            Toast.makeText(this.getContext(), "点击全屏", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * SeekBar 变换
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeMessages(SHOW_PROGRESS);
        ViewParent vpdown = getParent();
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true);
            vpdown = vpdown.getParent();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeMessages(SHOW_PROGRESS);
        ViewParent vpup = getParent();
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }
        if ((currentState != CURRENT_STATE_PLAYING && currentState != CURRENT_STATE_PAUSE)) {
            return;
        }
        int time = seekBar.getProgress() * getDuration() / 1000;
        videoPlayManager.getMediaPlayer().seekTo(time);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {
                case HIDE_PROGRESS:
                    hideBottomView();
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (VideoPlayManager.getInstance().getMediaPlayer().isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    };

    private int setProgress() {
        MediaPlayer mediaPlayer = VideoPlayManager.getInstance().getMediaPlayer();
        if (mediaPlayer == null) {
            return 0;
        }
        int position = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
            }
        }

        if (tv_time_end != null)
            tv_time_end.setText(stringForTime(duration));
        if (tv_time_start != null)
            tv_time_start.setText(stringForTime(position));

        return position;
    }

    private int getDuration() {
        int duration = 0;
        try {
            duration = videoPlayManager.getMediaPlayer().getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }
}
