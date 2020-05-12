package com.bytedance.videoplayer;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Formatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static private int flag = 0;
    private VideoView playVideo;
    private TextView VTime;
    private Button ButtonP;
    private SeekBar VSeekbar;
    private Button FullS;
    boolean isFull=false;
    boolean isSee=true;
    StringBuilder strB = new StringBuilder();//线程安全,StringBuilder类的对象能够被多次的修改且不产生新的未使用对象
    Formatter strFormat = new Formatter(strB, Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics wh = getResources().getDisplayMetrics();
        int width = wh.widthPixels;
        int height = wh.heightPixels;
        playVideo = findViewById(R.id.PVideo);
        ButtonP = findViewById(R.id.buttonP);
        VTime = findViewById(R.id.VTime);
        VSeekbar = findViewById(R.id.VSeekbar);
        FullS = findViewById(R.id.Fullscreen);

        Uri uri = null;
        uri = getIntent().getData();
        if(uri == null)
            playVideo.setVideoPath("android.resource://" + this.getPackageName() + "/" + R.raw.dog);
        else {
            playVideo.setVideoURI(uri);
            playVideo.start();
            handler.sendEmptyMessage(flag);
        }
        //play&pause
        ButtonP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playVideo.isPlaying()){
                    playVideo.pause();
                    handler.removeMessages(flag);
                    ButtonP.setText("Play");
                }
                else{
                    playVideo.start();
                    handler.sendEmptyMessage(flag);
                    ButtonP.setText("Pause");
                }
            }
        });
        //播放进度
        VSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            //主要是用于监听进度值的改变
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                UpdateT(VTime,progress);
            }
            @Override
            //监听用户开始拖动进度条的时候
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(flag);
            }
            @Override
            //监听用户结束拖动进度条的时候
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                playVideo.seekTo(progress);
                handler.sendEmptyMessage(flag);
            }
        });
        //横竖屏
        FullS.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onClick(View view) {
                if (isFull){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    isFull = false;
                }else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    isFull = true;
                }
            }
        });

        playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSee) {
                    LinearLayout slide = findViewById(R.id.Bottom);
                    slide.setVisibility(View.GONE);
                    isSee = false;
                }
                else {
                    LinearLayout slide = findViewById(R.id.Bottom);
                    slide.setVisibility(View.VISIBLE);
                    isSee = true;
                }
            }
        });
    }
    //更新时间
    void UpdateT(TextView Vt, int nowT) {
        int all = nowT / 1000;
        int sec = all % 60;
        int min = (all / 60) % 60;
        int hour = all / 3600;

        strB.setLength(0);
        if (hour > 0) {
            Vt.setText(strFormat.format("%d:%02d:%02d", hour, min, sec).toString());
        } else {
            Vt.setText(strFormat.format("%02d:%02d", min, sec).toString());
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == flag){
                int nowT = playVideo.getCurrentPosition();
                int total = playVideo.getDuration();

                playVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        ButtonP.setText("Play");
                    }
                });

                VSeekbar.setMax(total);
                VSeekbar.setProgress(nowT);
                handler.sendEmptyMessageDelayed(flag,500);
            }
        }
    };
    //横竖屏转换
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("full", String.valueOf(isFull));
            isFull = true;
        } 
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            isFull = false;
            Log.i("half", String.valueOf(isFull));
        }
    }
}
