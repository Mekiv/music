package com.ecjtuit.wangshuai.module.lyric;

import android.text.TextUtils;

import com.ecjtuit.wangshuai.data.Music;
import com.ecjtuit.wangshuai.util.http.UserAgentList;
import com.ecjtuit.wangshuai.util.netease.NLrcResponse;
import com.ecjtuit.wangshuai.util.netease.NSongSearchResponse;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Mekiv on 2018/1/23.
 */

public class SearchLyric {
    private String baseUrl = "http://music.163.com/api/";
    private static final long TIMEOUT = 1000;
    public static final MediaType REQUEST_HEADER = MediaType.parse("application/x-www-form-urlencoded");
    private LyricParser mLrcParser = new LyricParser();
    private List<Lyric> combine;

    private  OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .addNetworkInterceptor(new StethoInterceptor())
            .build();

    public  SearchLyric(Music music){
        Gson gson = new Gson();
        String mKey = music.getTitle();
        try {
            int id =gson.fromJson(getMusicID(baseUrl,mKey), NSongSearchResponse.class).result.songs.get(0).id;
            this.combine = getLyric(baseUrl,id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    String getMusicID(String url,String mKey) throws IOException{
        RequestBody  body = new FormBody.Builder()
                .add("s",mKey)
                .add("offset","0")
                .add("limit","1")
                .add("type","1")
                .build();

        Request request = new Request.Builder()
                .url(url+"search/get/")
                .header( "Referer", "http://music.163.com/")
                .addHeader("Cookie","appver=1.5.0.75771;")
                .addHeader("User-Agent", UserAgentList.userAgentList[new Random(100).nextInt(UserAgentList.userAgentList.length)])
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    List<Lyric> getLyric(String url,int id) throws IOException{

        Request request = new Request.Builder()
                .url(url+"song/lyric"+"?os=osx&id="+id+"&lv=-1&kv=-1&tv=-1")
                .header( "Referer", "http://music.163.com/")
                .addHeader("Cookie","appver=1.5.0.75771;")
                .addHeader("User-Agent",UserAgentList.userAgentList[new Random(100).nextInt(UserAgentList.userAgentList.length)])
                .get()
                .build();
        Response response = client.newCall(request).execute();

        NLrcResponse lrcResponse = new Gson().fromJson(response.body().string(), NLrcResponse.class);
        final Charset utf8 = Charset.forName("utf8");
        List<Lyric> lyrics = mLrcParser.getLyrics(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(lrcResponse.lrc.lyric.getBytes(utf8)),utf8)));
        //有翻译 合并
        if(lrcResponse.tlyric != null && !TextUtils.isEmpty(lrcResponse.tlyric.lyric)){
            List<Lyric> translate = mLrcParser.getLyrics(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(lrcResponse.tlyric.lyric.getBytes(utf8)),utf8)));
            if(translate != null && translate.size() > 0){
                for(int i = 0 ; i < translate.size();i++){
                    for(int j = 0 ; j < lyrics.size();j++){
                        if(translate.get(i).getTime() == lyrics.get(j).getTime()){
                            lyrics.get(j).setTranslate(translate.get(i).getContent());
                            break;
                        }
                    }
                }

            }
        }
        return lyrics;
    }

    public List<Lyric> getCombine() {
        return combine;
    }
}
