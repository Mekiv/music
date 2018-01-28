//package com.ecjtuit.wangshuai.util;
//
//import android.content.Context;
//import android.media.AudioManager;
//import android.support.annotation.NonNull;
//
//import com.ecjtuit.wangshuai.service.MusicPlayService;
//
///**
// * Created by Mekiv on 2018/1/18.
// */
//
//public class AudioFocusManagerUtil implements AudioManager.OnAudioFocusChangeListener {
//    private MusicPlayService mMusicPlayService;
//    private AudioManager mAudioManager;
//    private boolean isPausedByFocusLossTransient;
//    private int mVolumeWhenFocusLossTransientCanDuck;
//
//    public AudioFocusManagerUtil(@NonNull MusicPlayService MusicPlayService) {
//        mMusicPlayService = MusicPlayService;
//        mAudioManager = (AudioManager) MusicPlayService.getSystemService(Context.AUDIO_SERVICE);
//    }
//
//    /**
//     * 播放音乐前先请求音频焦点
//     */
//    public boolean requestAudioFocus() {
//        return mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
//                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
//    }
//
//    /**
//     * 退出播放器后不再占用音频焦点
//     */
//    public void abandonAudioFocus() {
//        mAudioManager.abandonAudioFocus(this);
//    }
//
//    /**
//     * 音频焦点监听回调
//     */
//    @Override
//    public void onAudioFocusChange(int focusChange) {
//        int volume;
//        switch (focusChange) {
//            // 重新获得焦点
//            case AudioManager.AUDIOFOCUS_GAIN:
//                if (!willPlay() && isPausedByFocusLossTransient) {
//                    // 通话结束，恢复播放
//                    mMusicPlayService.play();
//                }
//
//                volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                if (mVolumeWhenFocusLossTransientCanDuck > 0 && volume == mVolumeWhenFocusLossTransientCanDuck / 2) {
//                    // 恢复音量
//                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck,
//                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//                }
//
//                isPausedByFocusLossTransient = false;
//                mVolumeWhenFocusLossTransientCanDuck = 0;
//                break;
//            // 永久丢失焦点，如被其他播放器抢占
//            case AudioManager.AUDIOFOCUS_LOSS:
//                if (willPlay()) {
//                    forceStop();
//                }
//                break;
//            // 短暂丢失焦点，如来电
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                if (willPlay()) {
//                    forceStop();
//                    isPausedByFocusLossTransient = true;
//                }
//                break;
//            // 瞬间丢失焦点，如通知
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                // 音量减小为一半
//                volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                if (willPlay() && volume > 0) {
//                    mVolumeWhenFocusLossTransientCanDuck = volume;
//                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck / 2,
//                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//                }
//                break;
//        }
//    }
//
//    private boolean willPlay() {
//        return mMusicPlayService.isPreparing() || mMusicPlayService.isPlaying();
//    }
//
//    private void forceStop() {
//        if (mMusicPlayService.isPreparing()) {
//            mMusicPlayService.stop();
//        } else if (mMusicPlayService.isPlaying()) {
//            mMusicPlayService.pause();
//        }
//    }
//
//}
//
