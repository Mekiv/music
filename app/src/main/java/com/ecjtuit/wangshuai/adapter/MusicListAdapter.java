package com.ecjtuit.wangshuai.adapter;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecjtuit.wangshuai.R;
import com.ecjtuit.wangshuai.data.Music;

import java.util.List;

;

/**
 * Created by Mekiv on 2018/1/15.
 */

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {
    private List<Music> mMusicList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView musicImage;
        TextView musicName;
        TextView artistName;

        public ViewHolder(View view){
            super(view);
            musicImage = (ImageView)view.findViewById(R.id.music_image);
            musicName = (TextView)view.findViewById(R.id.music_name);
            artistName = (TextView)view.findViewById(R.id.music_artist);
        }
    }
    public MusicListAdapter(List<Music> musicList){
        mMusicList = musicList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_info,parent,false);
        ViewHolder hodler = new ViewHolder(view);
        return hodler;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Music music = mMusicList.get(position);

        holder.musicName.setText(music.getTitle());
        holder.musicImage.setImageBitmap(BitmapFactory.decodeByteArray(music.getThumbBitmap(),0,music.getThumbBitmap().length));
        holder.artistName.setText(music.getArtist()+" - "+music.getAlbum());
    }
    @Override
    public int getItemCount(){
        return mMusicList.size();
    }


}
