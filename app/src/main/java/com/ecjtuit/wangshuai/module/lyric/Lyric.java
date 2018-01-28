package com.ecjtuit.wangshuai.module.lyric;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mekiv on 2018/1/19.
 */

public class Lyric implements Comparable<Lyric> {
    /**开始时间 为00:10:00***/
    private String mTimeStr;
    /**开始时间 毫米数  00:10:00  为10000**/
    private int mTime;
    /**歌词内容**/
    private String mContent;
    /**歌词内容翻译*/
    private String mTranslate;
    /**该行歌词显示的总时间**/
    private long mTotalTime;
    /** 该行歌词内容所占的高度*/
    private int mContentHeight;
    /** 该行歌词翻译所占的高度*/
    private int mTranslateHeight;
    /** 该句歌词所占的总共高度*/
    private int mTotalHeight;

    public void setTotalHeight(int height){
        this.mTotalHeight = height;
    }
    public int getTotalHeight(){
        return mTotalHeight;
    }

    public void setContentHeight(int height){
        this.mContentHeight = height;
    }
    public int getContentHeight(){
        return mContentHeight;
    }

    public void setTranslateHeight(int height){
        this.mTranslateHeight = height;
    }
    public int getTranslateHeight(){
        return mTranslateHeight;
    }

    public long getTotalTime() {
        return mTotalTime;
    }
    public void setTotalTime(int totalTime) {
        this.mTotalTime = totalTime;
    }

    public String getTimeStr() {
        return mTimeStr;
    }
    public void setTimeStr(String timeStr) {
        this.mTimeStr = timeStr;
    }

    public int getTime() {
        return mTime;
    }
    public void setTime(int time) {
        this.mTime = time;
    }

    public String getContent() {
        return mContent;
    }
    public void setContent(String content) {
        this.mContent = content;
    }

    public String getTranslate(){
        return mTranslate;
    }
    public void setTranslate(String translate){
        mTranslate = translate;
    }

    public boolean hasTranslate(){
        return !TextUtils.isEmpty(mTranslate);
    }

    public Lyric() {
    }

    public Lyric(Lyric lrcRow){
        mTimeStr = lrcRow.getTimeStr();
        mTime = lrcRow.getTime();
        mTotalTime = lrcRow.getTotalTime();
        mContent = lrcRow.getContent();
        mTranslate = lrcRow.getTranslate();
    }

    public Lyric(String timeStr, int time, String content) {
        super();
        mTimeStr = timeStr;
        mTime = time;
        String[] mulitiContent = content.split("\t");
        mContent = mulitiContent[0];
        if(mulitiContent.length > 1){
            mTranslate = mulitiContent[1];
        }
    }

    /**
     * 将歌词文件中的某一行 解析成一个List<Lyric>
     * 因为一行中可能包含了多个Lyric对象
     * 比如  [03:33.02][00:36.37]当鸽子不再象征和平  ，就包含了2个对象
     * @param lrcLine
     * @return
     */
    public static List<Lyric> createRows(String lrcLine){
        if(!lrcLine.startsWith("[") || (lrcLine.indexOf("]") != 9 && lrcLine.indexOf(']') != 10)){
            return null;
        }
        //最后一个"]"
        int lastIndexOfRightBracket = lrcLine.lastIndexOf("]");
        //歌词内容
        String content = lrcLine.substring(lastIndexOfRightBracket + 1, lrcLine.length());
        //截取出歌词时间，并将"[" 和"]" 替换为"-"   [offset:0]

        // -03:33.02--00:36.37-
        String times = lrcLine.substring(0, lastIndexOfRightBracket + 1).replace("[", "-").replace("]", "-");
        String[] timesArray = times.split("-");
        List<Lyric> lrcRows = new ArrayList<>();
        for (String tem : timesArray) {
            if(TextUtils.isEmpty(tem.trim())){
                continue;
            }
            try{
                Lyric lrcRow = new Lyric(tem, formatTime(tem), content);
                lrcRows.add(lrcRow);
            }catch(Exception e){
                Log.d("LrcRow", e.getMessage());
            }
        }
        return lrcRows;
    }
    /****
     * 把歌词时间转换为毫秒值  如 将00:10.00  转为10000
     * @param timeStr
     * @return
     */
    private static int formatTime(String timeStr) {
        timeStr = timeStr.replace('.', ':');
        String[] times = timeStr.split(":");

        return Integer.parseInt(times[0] )* 60 * 1000
                + Integer.parseInt(times[1]) * 1000
                + Integer.parseInt(times[2]);
    }
    @Override
    public int compareTo(Lyric anotherLrcRow) {
        return this.mTime - anotherLrcRow.mTime;
    }


    @Override
    public String toString() {
        return "[" + mTimeStr + "] " + mContent;
    }
}
