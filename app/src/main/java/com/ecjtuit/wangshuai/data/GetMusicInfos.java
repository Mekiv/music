package com.ecjtuit.wangshuai.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.ecjtuit.wangshuai.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mekiv on 2018/1/15.
 */

public class GetMusicInfos {
    private static final String SELECTION = MediaStore.Audio.AudioColumns.SIZE + " >= ? AND " + MediaStore.Audio.AudioColumns.DURATION + " >= ?";


    public List<Music> getMusicInfos(Context context){

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        List<Music> mp3Infos = new ArrayList<Music>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Music mp3Info = new Music();
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));   //音乐id
                String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));  //文件大小
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));  //文件路径
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐
                int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)); //专辑ID
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));//专辑
//                Log.i("测试",""+duration);
                if (isMusic != 0 && duration > 30*1000) {     //只把音乐添加到集合当中
                    mp3Info.setId(id);
                    mp3Info.setTitle(title);
                    mp3Info.setArtist(artist);
                    mp3Info.setDuration(duration);
                    mp3Info.setSize(size);
                    mp3Info.setUrl(url);
                    mp3Info.setThumbBitmap(getAlbumArt(albumId,context));
                    mp3Info.setAlbum(album);
                    mp3Info.save();
                    mp3Infos.add(mp3Info);
                }
            }
        }
        cursor.close();
        save(context,mp3Infos);
        return mp3Infos;
    }

    //通过专辑ID获取专辑图片
    private Bitmap getAlbumArt(int albumId,Context context) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cursor = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(albumId)), projection, null, null, null);
        String albumArt = null;
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            cursor.moveToNext();
            albumArt = cursor.getString(0);
        }
        cursor.close();
        Bitmap bm = null;
        if (albumArt != null) {
            bm = BitmapFactory.decodeFile(albumArt);
        } else {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_cover);
        }
        return bm;
    }

    public void save(Context context,List<Music> list){
        SharedPreferences sp = context.getSharedPreferences("SP_PEOPLE_LIST", Activity.MODE_PRIVATE);//创建sp对象
        Gson gson = new Gson();
        String jsonStr=gson.toJson(list); //将List转换成Json
        SharedPreferences.Editor editor = sp.edit() ;
        editor.putString("KEY_PEOPLE_LIST_DATA", jsonStr) ; //存入json串
        editor.commit() ;  //提交
    }

    public List<Music> read(Context context){
        SharedPreferences sp = context.getSharedPreferences("SP_PEOPLE_LIST",Activity.MODE_PRIVATE);//创建sp对象,如果有key为"SP_PEOPLE"的sp就取出
        String ListJson = sp.getString("KEY_PEOPLE_LIST_DATA","");  //取出key为"KEY_PEOPLE_DATA"的值，如果值为空，则将第二个参数作为默认值赋值
        if(ListJson!="")  //防空判断
        {
            Gson gson = new Gson();
            List<Music> list = gson.fromJson(ListJson, new TypeToken<List<Music>>() {}.getType()); //将json字符串转换成List集合
            return list;
        }
        return null;
    }
}
