package com.example.huangbuqiong.rrtest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.huangbuqiong.rrtest.R;

/**
 * Created by huangbuqiong on 2017/11/7.
 */

public class TechniqueListActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mBt_rr;
    private Button mBt_edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.techique_list);
        initView();
    }

    private void initView() {
        mBt_rr = (Button)findViewById(R.id.rx_r);
        mBt_edit = (Button)findViewById(R.id.pic_edit);
        mBt_rr.setOnClickListener(this);
        mBt_edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rx_r:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.pic_edit:
                startActivity(new Intent(this, TakeCapture2Activity.class));
                break;
        }
    }
}
