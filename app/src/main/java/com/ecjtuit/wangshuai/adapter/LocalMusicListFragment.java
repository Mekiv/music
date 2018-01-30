package com.ecjtuit.wangshuai.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecjtuit.wangshuai.MainActivity;
import com.ecjtuit.wangshuai.R;
import com.ecjtuit.wangshuai.service.MusicPlayService;
import com.ecjtuit.wangshuai.util.ItemClickListener;

/**
 * Created by Mekiv on 2018/1/30.
 */

public class LocalMusicListFragment extends Fragment {
    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_list,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.music_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        MusicListAdapter adapter = new MusicListAdapter(MainActivity.musicList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new ItemClickListener(recyclerView,
                new ItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        MainActivity.musicIndex = position;
                        MusicPlayService.playingMusic = MainActivity.musicList.get(MainActivity.musicIndex);
                        MusicPlayService.mediaPlayer.reset();
                        MusicPlayService.mediaPlayer = MusicPlayService.MusicPlay(MusicPlayService.playingMusic.getUrl());
                        new MusicPlayService().play();
                    }
                }));
        return view;
    }
}
