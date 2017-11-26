package com.fashare.activitytracker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkOverlayPermission();
        if (AccessibilityUtil.checkAccessibility(MainActivity.this)) {
            if (App.isRun) {
                startService(
                        new Intent(MainActivity.this, TrackerService.class)
                                .putExtra(TrackerService.COMMAND, TrackerService.COMMAND_CLOSE)
                );
                App.isRun = false;
            } else {
                startService(
                        new Intent(MainActivity.this, TrackerService.class)
                                .putExtra(TrackerService.COMMAND, TrackerService.COMMAND_OPEN)
                );
                App.isRun = true;
            }
            finish();
        }
    }


    /**
     * 判断服务是否后台运行
     *
     * @param mContext Context
     * @param className 判断的服务名字
     *
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRun(Context mContext, String className) {
        boolean isRun = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(40);
        int size = serviceList.size();
        for (int i = 0; i < size; i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRun = true;
                break;
            }
        }
        return isRun;
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                startActivityForResult(
                        new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        REQUEST_CODE
                );
                Toast.makeText(this, "请先授予 \"Activity 栈\" 悬浮窗权限", Toast.LENGTH_LONG).show();
            }
        }
    }
}
