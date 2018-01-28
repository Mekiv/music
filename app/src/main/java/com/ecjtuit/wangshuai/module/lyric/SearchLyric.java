package com.ecjtuit.wangshuai.module.lyric;

import android.text.TextUtils;

import com.ecjtuit.wangshuai.data.Music;
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

    String[] userAgentList = {"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
            "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Mobile/14F89;GameHelper",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A300 Safari/602.1",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:46.0) Gecko/20100101 Firefox/46.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:46.0) Gecko/20100101 Firefox/46.0",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)",
            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)",
            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Win64; x64; Trident/6.0)",
            "Mozilla/5.0 (Windows NT 6.3; Win64, x64; Trident/7.0; rv:11.0) like Gecko",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/13.10586",
            "Mozilla/5.0 (iPad; CPU OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A300 Safari/602.1"};


    private  OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .addNetworkInterceptor(new StethoInterceptor())
            .build();

    public  SearchLyric(Music music){
        Gson gson = new Gson();
        String mKey = music.getTitle() + "/" + music.getArtist();
        try {
            int id =gson.fromJson(getMusicID(baseUrl,mKey), NSongSearchResponse.class).result.songs.get(0).id;
            combine = getLyric(baseUrl,id);
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
                .addHeader("User-Agent",userAgentList[new Random(100).nextInt(userAgentList.length)])
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
                .addHeader("User-Agent",userAgentList[new Random(100).nextInt(userAgentList.length)])
                .get()
                .build();
        Response response = client.newCall(request).execute();

        NLrcResponse lrcResponse = new Gson().fromJson(response.body().string(), NLrcResponse.class);
        final Charset utf8 = Charset.forName("utf8");
        List<Lyric> combine = mLrcParser.getLyrics(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(lrcResponse.lrc.lyric.getBytes(utf8)),utf8)));
        //有翻译 合并
        if(lrcResponse.tlyric != null && !TextUtils.isEmpty(lrcResponse.tlyric.lyric)){
            List<Lyric> translate = mLrcParser.getLyrics(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(lrcResponse.tlyric.lyric.getBytes(utf8)),utf8)));
            if(translate != null && translate.size() > 0){
                for(int i = 0 ; i < translate.size();i++){
                    for(int j = 0 ; j < combine.size();j++){
                        if(translate.get(i).getTime() == combine.get(j).getTime()){
                            combine.get(j).setTranslate(translate.get(i).getContent());
                            break;
                        }
                    }
                }

            }
        }
        return combine;
    }

    public List<Lyric> getCombine() {
        return combine;
    }
}
