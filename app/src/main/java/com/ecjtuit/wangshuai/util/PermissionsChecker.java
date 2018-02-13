package com.ecjtuit.wangshuai.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Mekiv on 2018/1/25.
 */

public class PermissionsChecker {
    private  Context mContext;
    private  Activity mActivity;
    private final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    public PermissionsChecker(Context context, Activity activity) {
        mContext = context.getApplicationContext();
        mActivity = activity;
        lacksPermissions(PERMISSIONS);
    }

    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }else {
                ActivityCompat.requestPermissions(mActivity, permissions, 1);
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

}
