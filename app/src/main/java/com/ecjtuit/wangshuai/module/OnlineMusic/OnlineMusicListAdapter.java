package com.ecjtuit.wangshuai.module.OnlineMusic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ecjtuit.wangshuai.R;

import java.util.List;

/**
 * Created by Mekiv on 2018/1/30.
 */

public class OnlineMusicListAdapter extends RecyclerView.Adapter<OnlineMusicListAdapter.ViewHolder>{
    private List<OnlineMusic> onlineMusicList;
    ViewGroup parent;
    static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView musicImage;
        private TextView musicName;
        private TextView artistName;

        public ViewHolder(View view){
            super(view);
            musicImage = (ImageView)view.findViewById(R.id.online_music_image);
            musicName = (TextView)view.findViewById(R.id.online_music_name);
            artistName = (TextView)view.findViewById(R.id.online_music_artist);
        }
    }
    public OnlineMusicListAdapter(List<OnlineMusic> musicList){
        onlineMusicList = musicList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_music_info,parent,false);
        this.parent=parent;
        ViewHolder hodler = new ViewHolder(view);
        return hodler;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        OnlineMusic music = onlineMusicList.get(position);

        holder.musicName.setText(music.getTitle());
        Glide.with(parent.getContext()).load(music.getPic_small()).into(holder.musicImage);
        holder.artistName.setText(music.getArtist_name()+" - "+music.getAlbum_title());
    }
    public void addMoreItem(List<OnlineMusic> newDatas) {
        onlineMusicList.addAll(newDatas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return onlineMusicList.size();
    }
}
