package com.ecjtuit.wangshuai.module.lyric;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecjtuit.wangshuai.MainActivity;
import com.ecjtuit.wangshuai.R;
import com.ecjtuit.wangshuai.data.Music;

import java.util.List;

import io.reactivex.disposables.Disposable;
/**
 * Created by Mekiv on 2018/1/25.
 */
public class LyricFragment extends Fragment {
    private Music mInfo=MainActivity.musicList.get(MainActivity.musicIndex);
    LyricView mLyricView ;

    private List<Lyric> mLrcList;

    private Disposable mDisposable;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lyric_view,container,false);
        mLyricView.setLyrics(new SearchLyric(mInfo).getCombine());
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }
}
