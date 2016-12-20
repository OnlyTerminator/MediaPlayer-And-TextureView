package videoview.zxf.com.videoplaymodule;

/**
 * Created by aotuman on 2016/11/15.
 */

public class MediaPlayListenerManager {
    private MediaPlayerListener listener;
    private static MediaPlayListenerManager instance;

    public static MediaPlayListenerManager getInstance(){
        if(null == instance){
            instance = new MediaPlayListenerManager();
        }
        return instance;
    }

    public void replaceListener(MediaPlayerListener listener){
        this.listener = listener;
    }

    public MediaPlayerListener getMediaPlayListener(){
        return listener;
    }
}
