package com.licheedev.serialtool.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.licheedev.serialtool.R;

public class StartActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "StartActivity";

    Button sBtnSerial;
    Button sBtnGPIO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        initViews();

        sBtnSerial.setOnClickListener(this);
        sBtnGPIO.setOnClickListener(this);
    }

    private void initViews() {
        sBtnSerial = findViewById(R.id.serial_test_btn);
        sBtnGPIO = findViewById(R.id.gpio_test_btn);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.serial_test_btn:
//                Log.d(TAG, "onClick: 跳转到串口测试");
                startActivity(MainActivity.class);
                break;
            case R.id.gpio_test_btn:
                startActivity(GpioActivity.class);
                break;
            case R.id.can_test_btn:
                break;
            case R.id.led_test_btn:
                break;
            case R.id.psam_test_btn:
                break;
            default:
                break;
        }
    }

    private void startActivity(Class<?> toActivity) {
        Intent intent = new Intent(StartActivity.this, toActivity);
        startActivity(intent);
    }
}
