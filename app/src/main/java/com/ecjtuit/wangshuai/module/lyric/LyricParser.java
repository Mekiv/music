package com.ecjtuit.wangshuai.module.lyric;

import android.util.Log;

import com.ecjtuit.wangshuai.util.cache.DiskLruCache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @ClassName
 * @Description 解析歌词实现类
 * @Author Xiaoborui
 * @Date 2016/10/28 09:50
 */

public class LyricParser  {
//    @Override
//    public void saveLyrics(List<Lyric> Lyrics, String key){
//        if(Lyrics == null || Lyrics.size() == 0)
//            return;
//        DiskLruCache.Editor editor;
//        OutputStream lrcCacheStream = null;
//        try {
//            editor = DiskCache.getLrcDiskCache().edit(Util.hashKeyForDisk(key));
//            lrcCacheStream = editor.newOutputStream(0);
////            for(Lyric Lyric : Lyrics){
////                lrcCacheStream.write((Lyric + "\n").getBytes());
////            }
//            lrcCacheStream.write(new Gson().toJson(Lyrics,new TypeToken<List<Lyric>>(){}.getType()).getBytes());
//            lrcCacheStream.flush();
//            editor.commit();
//
//            DiskCache.getLrcDiskCache().flush();
//        }catch (Exception e){
//
//        } finally {
//            try {
//                if(lrcCacheStream != null)
//                    lrcCacheStream.close();
//            }catch (Exception e){
//
//            }
//        }
//
//    }

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
//            if (needCache ) {
//                editor = DiskCache.getLrcDiskCache().edit(this.hashKeyForDisk(songName + "/" + artistName));
//                if(editor != null)
//                    lrcCacheStream = editor.newOutputStream(0);
//                if(lrcCacheStream != null){
//                    lrcCacheStream.write(new Gson().toJson(Lyrics,new TypeToken<List<Lyric>>(){}.getType()).getBytes());
//                    lrcCacheStream.flush();
//                }
//                if (editor != null) {
//                    editor.commit();
//                }
//                DiskCache.getLrcDiskCache().flush();
//            }

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
//        public static String hashKeyForDisk(String key) {
//            String cacheKey;
//            try {
//                final MessageDigest mDigest = MessageDigest.getInstance("MD5");
//                mDigest.update(key.getBytes());
//                cacheKey = bytesToHexString(mDigest.digest());
//            } catch (NoSuchAlgorithmException e) {
//                cacheKey = String.valueOf(key.hashCode());
//            }
//            return cacheKey;
//        }
//
//        public static String bytesToHexString(byte[] bytes) {
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < bytes.length; i++) {
//                String hex = Integer.toHexString(0xFF & bytes[i]);
//                if (hex.length() == 1) {
//                    sb.append('0');
//                }
//                sb.append(hex);
//            }
//            return sb.toString();
//        }

        return Lyrics;
    }
}
