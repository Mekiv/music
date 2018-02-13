package com.ecjtuit.wangshuai.data;

import android.content.Context;
import android.os.AsyncTask;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Mekiv on 2018/2/4.
 */

public class DBManager {
    Context context;
    List<Music> musicList;
    public DBManager(Context context) {
        this.context = context;
        new DBTask().execute();
    }

    class DBTask extends  AsyncTask<Void,Integer,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            musicList = new GetMusicInfos().getMusicInfos(context);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            DataSupport.deleteAll(Music.class);
            DataSupport.saveAll(musicList);
        }
    }
}
