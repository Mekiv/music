package com.ecjtuit.wangshuai.module.OnlineMusic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecjtuit.wangshuai.R;
import com.ecjtuit.wangshuai.data.Music;
import com.ecjtuit.wangshuai.service.MusicPlayService;
import com.ecjtuit.wangshuai.util.ItemClickListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Mekiv on 2018/1/30.
 */

public class OnlineMusicListFragment extends Fragment  {
    private RecyclerView recyclerView;
    private OnlineMusicListAdapter adapter;
    private String songID;
    private String url;
    private Music music = new Music();
    private List<OnlineMusic> onlineMusicList;
    private List<OnlineMusic> onlineMusicListTemp;
    private OnlineMusic onlineMusic;
    private Bitmap bitmap;
    int offset=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_music,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.online_music_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        new onlineListTask().execute();
        setListener();
        return view;
    }

    class onlineListTask extends AsyncTask<Void,Integer,Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                onlineMusicList = new GetOnlineMusicUtil().getOnlineMusicList(offset).getSong_list();
                offset =offset+20;
            }catch (Exception e){
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            adapter = new OnlineMusicListAdapter(onlineMusicList);
            recyclerView.setAdapter(adapter);
        }
    }

    class onlineUrlTask extends AsyncTask<Void,Integer,Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                url = new GetOnlineMusicUtil().getOnlineMusicUrl(songID);
                bitmap = getImage(onlineMusic.getPic_small());
            }catch (Exception e){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            MusicPlayService.mediaPlayer.reset();
            MusicPlayService.mediaPlayer = MusicPlayService.MusicPlay(url);
            music.setThumbBitmap(bitmap);
            new MusicPlayService().play();
        }
    }

    class addOnlineListTask extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                onlineMusicListTemp = new GetOnlineMusicUtil().getOnlineMusicList(offset).getSong_list();
                offset =offset+20;
            }catch (Exception e){
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            adapter.addMoreItem(onlineMusicListTemp);
        }
    }

    private void setListener(){
        recyclerView.addOnItemTouchListener(new ItemClickListener(recyclerView,
                new ItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        onlineMusic = onlineMusicList.get(position);
                        songID = onlineMusic.getSong_id();
                        music.setTitle(onlineMusic.getTitle());
                        music.setArtist(onlineMusic.getArtist_name());

                        MusicPlayService.playingMusic =music;
                        new onlineUrlTask().execute();
                    }
                }));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)){
                    new addOnlineListTask().execute();
                }
            }
        });
    }

    public  Bitmap getImage(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream is = conn.getInputStream();
        return BitmapFactory.decodeStream(is);
    }
}
