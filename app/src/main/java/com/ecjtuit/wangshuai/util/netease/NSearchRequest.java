package com.ecjtuit.wangshuai.util.netease;

import java.io.Serializable;

/**
 * Created by Mekiv on 2018/1/26.
 */

public class NSearchRequest implements Serializable {
    public static final NSearchRequest DEFAULT_REQUEST = new NSearchRequest(-1,"",0,0);
    private static final long serialVersionUID = -4168031236748350436L;

    private String mKey;
    //网易查询的类型
    private int mNeteaseType;
    //本地数据库查询的类型
    private int mLocalType;
    private int mID;

    public NSearchRequest(int id,String key, int ntype,int ltype) {
        this.mID = id;
        this.mKey = key;
        this.mNeteaseType = ntype;
        this.mLocalType = ltype;
    }

    public int getLType(){
        return mLocalType;
    }

    public int getID() {
        return mID;
    }

    public String getKey() {
        return mKey;
    }

    public int getNType() {
        return mNeteaseType;
    }
}
