package com.licheedev.serialtool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.licheedev.serialtool.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
    }

    private void startActivity(Class<?> toActivity) {
        Intent intent = new Intent(StartActivity.this, toActivity);
        startActivity(intent);
    }

    public void startPsamTest(View view) {
        startActivity(PsamActivity.class);
    }

    public void startSerialPortTest(View view) {
        startActivity(MainActivity.class);
    }

    public void startGpioTest(View view) {
        startActivity(GpioActivity.class);
    }
}
