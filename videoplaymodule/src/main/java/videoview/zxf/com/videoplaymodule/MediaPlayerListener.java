package videoview.zxf.com.videoplaymodule;

/**
 * Created by Nathen on 16/7/26.
 */
public interface MediaPlayerListener {
    void onPrepared();

    void onCompletion();

    void onBufferingUpdate(int percent);

    void onSeekComplete();

    void onError(int what, int extra);

    void onInfo(int what, int extra);

    void onVideoSizeChanged();

    int getState();

    void changeSystemVoice(int voice);
}
