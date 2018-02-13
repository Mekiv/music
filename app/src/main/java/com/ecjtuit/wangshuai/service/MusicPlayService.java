package com.ecjtuit.wangshuai.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ecjtuit.wangshuai.MainActivity;
import com.ecjtuit.wangshuai.data.Music;

public class MusicPlayService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener,MediaPlayer.OnPreparedListener {
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static Music playingMusic;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;
    private int mPlayState = STATE_IDLE;
    private int musicDuration;


    public MusicPlayService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //音乐播放完成事件监听
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        Notification notification= new NotificationCompat.Builder(this)
                .setContentTitle("Title")
                .build();
        startForeground(1,notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyBind extends Binder{
        public int getMusicDuration(){
            musicDuration = mediaPlayer.getDuration();
            return musicDuration;
        }

        public int getMusicCurrentPosition(){
            int musicCurrentPosition = mediaPlayer.getCurrentPosition();
            return musicCurrentPosition;
        }

        public void seekTo(int position){
            mediaPlayer.seekTo(position);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return new MyBind();
    }

    public static MediaPlayer MusicPlay(String url) {
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            return mediaPlayer;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void onCompletion(MediaPlayer mp) {
        Log.i("音乐播放：","Completion");
        next();
    }

    public boolean onError(MediaPlayer mp,int what,int extra){
        Log.i("Error","onError:"+what);
        playPrepare();
        return true;
    }

    public void onPrepared(MediaPlayer mp){
    }

    public void play(){
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            MainActivity.mPlayBarButtonPlay.setImageResource(android.R.drawable.ic_media_pause);
        }else {
            mediaPlayer.pause();
            MainActivity.mPlayBarButtonPlay.setImageResource(android.R.drawable.ic_media_play);
        }
        MainActivity.mPlayBarImage.setImageBitmap(BitmapFactory.decodeByteArray(MusicPlayService.playingMusic.getThumbBitmap(),0,MusicPlayService.playingMusic.getThumbBitmap().length));
        MainActivity.mPlayBarMusicName.setText(MusicPlayService.playingMusic.getTitle());
        MainActivity.mPlayBarMusicArtist.setText(MusicPlayService.playingMusic.getArtist());
    }

    public void next(){
        if (MainActivity.musicIndex < MainActivity.musicList.size()-1) {
            MainActivity.musicIndex++;
            playPrepare();
        }else {
            MainActivity.musicIndex = 0;
            playPrepare();
        }
    }

    public void previous(){
        MainActivity.musicIndex--;
        playPrepare();
    }
    public void playPrepare(){
        mediaPlayer.reset();
        MusicPlayService.playingMusic = MainActivity.musicList.get(MainActivity.musicIndex);
        mediaPlayer = MusicPlay(MusicPlayService.playingMusic .getUrl());
        play();
    }
    public void  stop(){
        mediaPlayer.stop();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mPlayState == STATE_PLAYING;
    }

    public boolean isPausing() {
        return mPlayState == STATE_PAUSE;
    }

    public boolean isPreparing() {
        return mPlayState == STATE_PREPARING;
    }

    public boolean isIdle() {
        return mPlayState == STATE_IDLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
