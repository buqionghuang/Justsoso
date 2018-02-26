package com.example.huangbuqiong.rrtest.activity;

import android.hardware.Camera;

/**
 * Created by huangbuqiong on 2018/2/6.
 */

public class CameraOperationHelper {
    private static  CameraOperationHelper instance;
    public static synchronized  CameraOperationHelper getInstance (int cameraId) {
        if (instance == null) {
            return new CameraOperationHelper();
        }
        return instance;
    }
}
