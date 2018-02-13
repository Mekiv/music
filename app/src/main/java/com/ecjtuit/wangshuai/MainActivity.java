package com.ecjtuit.wangshuai;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ecjtuit.wangshuai.adapter.LocalMusicListFragment;
import com.ecjtuit.wangshuai.data.DBManager;
import com.ecjtuit.wangshuai.data.GetMusicInfos;
import com.ecjtuit.wangshuai.data.Music;
import com.ecjtuit.wangshuai.module.OnlineMusic.OnlineMusicListFragment;
import com.ecjtuit.wangshuai.module.lyric.LyricActivity;
import com.ecjtuit.wangshuai.service.MusicPlayService;
import com.facebook.stetho.Stetho;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName(); //Log标签
    public static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;
    public static List<Music> musicList = new ArrayList<>(); //音乐列表
    public static int musicIndex = 0; //音乐索引
    @BindView(R.id.quit_button)
    ImageButton mQuitButton;
    @BindView(R.id.text_local_music)
    TextView mTextLocalMusic;
    @BindView(R.id.text_online_music)
    TextView mTextOnlineMusic;
    @BindView(R.id.search_button)
    ImageButton mSearchButton;
    public static ImageView mPlayBarImage;
    public static TextView mPlayBarMusicName;
    public static TextView mPlayBarMusicArtist;
    public static ImageButton mPlayBarButtonPlay;
    @BindView(R.id.play_bar_button_next)
    ImageButton mPlayBarButtonNext;
    @BindView(R.id.play_bar_button_previous)
    ImageButton mPlayBarButtonPrevious;
    @BindView(R.id.music_progress_bar)
    ProgressBar mMusicProgressBar;

    private Intent startIntent;
    private MusicPlayService.MyBind myBind;
    private LocalMusicListFragment localMusicListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       if ( ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else {
           initMusicList();
       }

        startIntent = new Intent(this, MusicPlayService.class);
        startService(startIntent);
//        musicList = DataSupport.findAll(Music.class);
//        if (musicList.size() == 0){
//            Connector.getDatabase();
//            new FragmentTask().execute();
//        }else {
//            new DBManager(this);
//            localMusicListFragment = new LocalMusicListFragment();
//            replaceFragment(localMusicListFragment);
//        }
        setContentView(R.layout.activity_main);
        initView();
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();//隐藏自带标题
        }

        setListener();

        Stetho.initialize(Stetho
                .newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(
                        Stetho.defaultInspectorModulesProvider(this)).build());
    }


    //设置监听
    private void setListener() {
        mPlayBarImage.setOnClickListener(this);
        mPlayBarButtonPlay.setOnClickListener(this);
        mPlayBarButtonNext.setOnClickListener(this);
        mPlayBarButtonPrevious.setOnClickListener(this);
        mQuitButton.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);
        mTextLocalMusic.setOnClickListener(this);
        mTextOnlineMusic.setOnClickListener(this);
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
            mMusicProgressBar.setMax(max);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //改变当前进度条的值
                    //设置当前进度
                    while (true) {
                        mMusicProgressBar.setProgress(myBind.getMusicCurrentPosition());
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

    public void play() {
        Intent intent = new Intent(this, MusicPlayService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        getPlayService().play();
    }

    public void next() {
        getPlayService().next();
    }

    public void previous() {
        getPlayService().previous();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.music_list_framelayout, fragment);
        fragmentTransaction.commit();
    }

    @OnClick({R.id.quit_button, R.id.text_local_music, R.id.text_online_music, R.id.search_button, R.id.play_bar_image, R.id.play_bar_music_name, R.id.play_bar_music_artist, R.id.play_bar_button_play, R.id.play_bar_button_next, R.id.play_bar_button_previous, R.id.music_progress_bar})
    public void onClick(View v) {
        switch (v.getId()) {
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
                Intent intentLyric = new Intent(this, LyricActivity.class);
                startActivity(intentLyric);
            case R.id.quit_button:
                this.finish();
            case R.id.text_local_music:
                replaceFragment(localMusicListFragment);
                mTextLocalMusic.setTextSize(20);
                mTextOnlineMusic.setTextSize(18);
                break;
            case R.id.text_online_music:
                replaceFragment(new OnlineMusicListFragment());
                mTextLocalMusic.setTextSize(18);
                mTextOnlineMusic.setTextSize(20);
            default:
                break;
        }
    }

    private void initView() {
        mPlayBarImage = (ImageView) findViewById(R.id.play_bar_image);
        mPlayBarMusicName = (TextView) findViewById(R.id.play_bar_music_name);
        mPlayBarMusicArtist = (TextView) findViewById(R.id.play_bar_music_artist);
        mPlayBarButtonPlay = (ImageButton) findViewById(R.id.play_bar_button_play);
    }

    private void  initMusicList(){
        musicList = DataSupport.findAll(Music.class);
        if (musicList.size() == 0){
            Connector.getDatabase();
            new FragmentTask().execute();
        }else {
            new DBManager(this);
            localMusicListFragment = new LocalMusicListFragment();
            replaceFragment(localMusicListFragment);
        }
    }

    class FragmentTask extends AsyncTask<Void,Integer,Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            musicList = new GetMusicInfos().getMusicInfos(getBaseContext());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            localMusicListFragment = new LocalMusicListFragment();
            replaceFragment(localMusicListFragment);
            DataSupport.deleteAll(Music.class);
            DataSupport.saveAll(musicList);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    initMusicList();
                }else {
                    Toast.makeText(this,"You",Toast.LENGTH_SHORT).show();
                    onDestroy();
                }
                break;
            default:
                break;
        }
    }
}
