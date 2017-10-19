package com.example.huangbuqiong.rrtest.service.presenter;

import android.content.Intent;

import com.example.huangbuqiong.rrtest.service.view.View;

/**
 * Created by huangbuqiong on 2017/10/16.
 */

public interface Presenter {
    void onCreate();

    void onStart();

    void onStop();

    void onPause();

    void onAttachView(View view);

    void attachIncomingIntent(Intent intent);
}
