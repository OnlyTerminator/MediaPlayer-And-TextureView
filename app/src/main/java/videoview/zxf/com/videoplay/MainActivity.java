package videoview.zxf.com.videoplay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import videoview.zxf.com.videoplaymodule.VideoPlayTexture;

public class MainActivity extends AppCompatActivity {
    private VideoPlayTexture videoPlayTexture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
        videoPlayTexture = (VideoPlayTexture) findViewById(R.id.video);
        videoPlayTexture.setVideoPlaySource("http://svideo.spriteapp.com/video/2016/0703/7b5bc740-4134-11e6-ac2b-d4ae5296039d_wpd.mp4","");
        videoPlayTexture.isAutoPlay(true);
    }
}
