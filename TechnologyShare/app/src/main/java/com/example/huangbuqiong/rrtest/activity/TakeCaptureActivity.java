package com.example.huangbuqiong.rrtest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.huangbuqiong.rrtest.R;
import com.example.huangbuqiong.rrtest.view.CameraDerectView;
import com.example.huangbuqiong.rrtest.view.CameraSurfaceView;

/**
 * Created by huangbuqiong on 2017/11/13.
 */

public class TakeCaptureActivity extends AppCompatActivity implements View.OnClickListener,CameraDerectView.IAutoFocus {

    private CameraSurfaceView mCameraSurfaceView;
    private CameraDerectView mCameraDerectView;
    private Button takePicBtn;

    private boolean isClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.cerame);
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        mCameraDerectView = (CameraDerectView) findViewById(R.id.cameraDerectView);
        takePicBtn= (Button) findViewById(R.id.takePic);
        mCameraDerectView.setIAutoFocus(this);
        takePicBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.takePic:
                mCameraSurfaceView.takePicture();
                break;
            default:
                break;
        }
    }


    @Override
    public void autoFocus() {
        mCameraSurfaceView.setAutoFocus();
    }

}
