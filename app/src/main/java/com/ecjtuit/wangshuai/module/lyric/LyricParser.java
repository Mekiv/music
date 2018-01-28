package com.ecjtuit.wangshuai.module.lyric;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.internal.cache.DiskLruCache;


/**
 * Created by Mekiv on 2018/1/26.
 */

public class LyricParser  {

    public List<Lyric> getLyrics(BufferedReader bufferedReader) {
        if(bufferedReader == null) {
            Log.i("bufferedReader为空", "");
            return null;
        }
        //解析歌词
        String s;
        DiskLruCache.Editor editor = null;
        OutputStream lrcCacheStream = null;

        List<Lyric> Lyrics = new ArrayList<>();
        try {
            while ((s = bufferedReader.readLine()) != null) {
                //解析每一行歌词
                List<Lyric> rows = Lyric.createRows(s);
                if(rows != null && rows.size() > 0)
                    Lyrics.addAll(rows);
            }
            if(Lyrics.size() == 0) {
                Log.i("lyrics", "");
                return null;
            }
            //为歌词排序
            Collections.sort(Lyrics);

            for (int i = 0; i < Lyrics.size() - 1; i++) {
                Lyrics.get(i).setTotalTime(Lyrics.get(i + 1).getTime() - Lyrics.get(i).getTime());
            }
            Lyrics.get(Lyrics.size() - 1).setTotalTime(5000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if(lrcCacheStream != null)
                    lrcCacheStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Lyrics;
    }
}
