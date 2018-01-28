package com.ecjtuit.wangshuai;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ecjtuit.wangshuai.adapter.MusicListAdapter;
import com.ecjtuit.wangshuai.data.GetMusicInfos;
import com.ecjtuit.wangshuai.data.Music;
import com.ecjtuit.wangshuai.module.lyric.LyricActivity;
import com.ecjtuit.wangshuai.module.lyric.SearchLyric;
import com.ecjtuit.wangshuai.service.MusicPlayService;
import com.ecjtuit.wangshuai.util.ItemClickListener;
import com.facebook.stetho.Stetho;

import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName(); //Log标签
    public static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=100;
    public static List<Music> musicList = new ArrayList<>(); //音乐列表
    GetMusicInfos musicInfos = new GetMusicInfos();
    public static int musicIndex=0; //音乐索引
    public static ImageButton barPlay; //播放按钮
    private ImageButton barNext; //下一首按钮
    private ImageButton barPrevious; //上一首按钮
    public static ImageView barImage; //播放条音乐封面
    public static TextView barMusicName;//播放条音乐名称
    public static TextView barMusicArtist;//播放条音乐作者
    private TextView titleLocalMusic;
    private TextView titleOnlineMusic;
    private RecyclerView recyclerView;//音乐列表
    private ProgressBar progressBar;
    private MusicPlayService.MyBind myBind;
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startIntent = new Intent(this, MusicPlayService.class);
        startService(startIntent);
        musicList = musicInfos.getMusicInfos(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }

        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();//隐藏自带标题
        }
        barPlay = (ImageButton) findViewById(R.id.play_bar_button_play);
        barNext= (ImageButton) findViewById(R.id.play_bar_button_next);
        barPrevious = (ImageButton) findViewById(R.id.play_bar_button_previous);
        barImage = (ImageView) findViewById(R.id.play_bar_image);
        barMusicName = (TextView) findViewById(R.id.play_bar_music_name);
        barMusicArtist = (TextView) findViewById(R.id.play_bar_music_artist);
        recyclerView = (RecyclerView) findViewById(R.id.music_list_view);
        progressBar = (ProgressBar) findViewById(R.id.music_progress_bar);
        titleLocalMusic = (TextView) findViewById(R.id.text_local_music);
        titleOnlineMusic = (TextView) findViewById(R.id.text_online_music);

        setListener();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MusicListAdapter adapter = new MusicListAdapter(musicList);
        recyclerView.setAdapter(adapter);
        Connector.getDatabase();

        Stetho.initialize(Stetho
                .newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(
                        Stetho.defaultInspectorModulesProvider(this)).build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }

    //设置监听
    private  void  setListener(){
        barImage.setOnClickListener(this);
        barPlay.setOnClickListener(this);
        barNext.setOnClickListener(this);
        barPrevious.setOnClickListener(this);
        titleLocalMusic.setOnClickListener(this);
        titleOnlineMusic.setOnClickListener(this);

        recyclerView.addOnItemTouchListener(new ItemClickListener(recyclerView,
                new ItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        musicIndex = position;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SearchLyric searchLyric = new SearchLyric(musicList.get(musicIndex));
                            }
                        }).start();
                        MusicPlayService.mediaPlayer.reset();
                        MusicPlayService.mediaPlayer = MusicPlayService.MusicPlay(musicList.get(musicIndex).getUrl());
                        play();
                    }
                }));
    }
    //播放按钮点击事件处理
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_bar_button_play:
                play();
                break;
            case R.id.play_bar_button_next:
                next();
                break;
            case R.id.play_bar_button_previous:
                previous();
                break;
            case R.id.play_bar_image:
                Intent intent = new Intent(this, LyricActivity.class);
                startActivity(intent);
            case R.id.text_local_music:
                titleLocalMusic.setTextSize(20);
                titleOnlineMusic.setTextSize(18);
                break;
            default:
                break;
        }
    }

    public MusicPlayService getPlayService() {
        MusicPlayService playService = new MusicPlayService();
        if (playService == null) {
            throw new NullPointerException("play service is null");
        }
        return playService;
    }
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBind = (MusicPlayService.MyBind) service;
            int max = myBind.getMusicDuration();
            progressBar.setMax(max);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //改变当前进度条的值
                    //设置当前进度
                    while (true) {
                        progressBar.setProgress(myBind.getMusicCurrentPosition());
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void play(){
        Intent intent = new Intent(this,MusicPlayService.class);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
        getPlayService().play();
    }

    public void next(){
        getPlayService().next();
    }

    public void previous(){
        getPlayService().previous();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

}