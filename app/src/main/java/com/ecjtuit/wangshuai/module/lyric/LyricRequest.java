package com.ecjtuit.wangshuai.module.lyric;

/**
 * Created by Mekiv on 2018/1/23.
 */

public class LyricRequest {
    public int ID;
    public String AccessKey;
    public LyricRequest(){}
    public LyricRequest(int ID, String accessKey) {
        this.ID = ID;
        AccessKey = accessKey;
    }
}
