package videoview.zxf.com.videoplaymodule;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;

import java.io.IOException;

/**
 * Created by aotuman on 2016/11/15.
 */

public class VideoPlayManager implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener{
    private static VideoPlayManager instance;
    private MediaPlayer mediaPlayer;
    public static VideoPlayManager getInstance(){
        if(null == instance){
            instance = new VideoPlayManager();
        }
        return instance;
    }

    public VideoPlayManager() {
        mediaPlayer = new MediaPlayer();
    }

    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

    public void initMediaPlay(SurfaceTexture surface){
//        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setSurface(new Surface(surface)); // 添加到容器中
        //播放完成的监听
        mediaPlayer.setOnCompletionListener(this);
        // 异步准备的一个监听函数，准备好了就调用里面的方法
        mediaPlayer.setOnPreparedListener(this);
        //播放错误的监听
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
    }

    public void destroyPlay(){
        mediaPlayer.reset();
        mediaPlayer.release();
    }

    public void startVideo(){
        mediaPlayer.start();
    }
    public void prepare() throws IOException {
        mediaPlayer.prepare();
    }

    public void prepareAsync(){
        mediaPlayer.prepareAsync();
    }
    public void pauseVideo(){
        mediaPlayer.pause();
    }

    public void setVideoUrl(String url){
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缓存更新
     * @param mp
     * @param percent
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        MediaPlayerListener listener = MediaPlayListenerManager.getInstance().getMediaPlayListener();
        if(null != listener){
            listener.onBufferingUpdate(percent);
        }
    }

    /**
     * 播放完成
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        MediaPlayerListener listener = MediaPlayListenerManager.getInstance().getMediaPlayListener();
        if(null != listener){
            listener.onCompletion();
        }
    }

    /**
     * 播放出现异常
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        MediaPlayerListener listener = MediaPlayListenerManager.getInstance().getMediaPlayListener();
        if(null != listener){
            listener.onError(what,extra);
        }
        return false;
    }

    /**
     * 视频准备完成的回掉
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        MediaPlayerListener listener = MediaPlayListenerManager.getInstance().getMediaPlayListener();
        if(null != listener){
            listener.onPrepared();
        }
    }
}
