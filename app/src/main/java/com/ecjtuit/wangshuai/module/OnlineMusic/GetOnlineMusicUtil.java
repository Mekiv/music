package com.ecjtuit.wangshuai.module.OnlineMusic;

import com.ecjtuit.wangshuai.util.http.UserAgentList;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Mekiv on 2018/1/30.
 */

public class GetOnlineMusicUtil {
    private static final long TIMEOUT = 1000;

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .addNetworkInterceptor(new StethoInterceptor())
            .build();

    public  OnlineMusicList getOnlineMusicList(int offset) throws IOException{

        Request request = new Request.Builder()
                .url("http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.billboard.billList&type=2&size=20&offset="+offset)
                .addHeader("User-Agent", UserAgentList.userAgentList[new Random(100).nextInt(UserAgentList.userAgentList.length)])
                .get()
                .build();
        Response response = client.newCall(request).execute();
        OnlineMusicList onlineMusicList =new Gson().fromJson(response.body().string(), OnlineMusicList.class);

        return onlineMusicList;
    }

    public String getOnlineMusicUrl(String id)throws IOException{
        Request request = new Request.Builder()
                .url("http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.play&songid="+id)
                .addHeader("User-Agent", UserAgentList.userAgentList[new Random(100).nextInt(UserAgentList.userAgentList.length)])
                .get()
                .build();
        Response response = client.newCall(request).execute();
        DownloadInfo downloadInfo = new Gson().fromJson(response.body().string(), DownloadInfo.class);
        String url = downloadInfo.getBitrate().getFile_link();
        return url;
    }
}
