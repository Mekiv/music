package com.ecjtuit.wangshuai.module.lyric;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ecjtuit.wangshuai.R;
import com.ecjtuit.wangshuai.data.Music;
import com.ecjtuit.wangshuai.service.MusicPlayService;

public class LyricActivity extends AppCompatActivity {
    private SearchLyric searchLyric;
    private LyricView lyricView;
    private Music mInfo= MusicPlayService.playingMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LyricTask().execute();
        setContentView(R.layout.lyric_view);
    }

    class LyricTask extends AsyncTask<Void,Integer,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                searchLyric = new SearchLyric(mInfo);
            }catch (Exception e){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            lyricView = (LyricView) findViewById(R.id.lyricView);
            lyricView.setLyrics(searchLyric.getCombine());
        }
    }
}
