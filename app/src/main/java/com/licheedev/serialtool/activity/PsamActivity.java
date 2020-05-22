package com.licheedev.serialtool.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.licheedev.serialtool.R;

import jnisctool.SCTool;

public class PsamActivity extends AppCompatActivity {
    private TextView tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.psam_test_layout);

        tvShow = findViewById(R.id.sample_text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SCTool.close();
    }

    public static String bytesToHexString(byte[] bytes, int fromIndex, int len) {
        if (fromIndex < len && fromIndex < bytes.length) {
            if (fromIndex + len > bytes.length) {
                len = bytes.length - fromIndex;
            }

            StringBuilder strResult = new StringBuilder(len - fromIndex);

            for (int nloop = fromIndex; nloop < fromIndex + len; ++nloop) {
                String strTemp = String.format(" %02X", bytes[nloop]);
                strResult.append(strTemp);
            }

            return strResult.toString();
        } else {
            return "";
        }
    }

    public void startDetect(View view) {
        byte[] src = {0x06, (byte) 0xDC, (byte) 0x8F, 0x30, 0x08, (byte) 0xF3, 0x50, (byte) 0xA5};
        byte[] dec = new byte[8];
        int result = SCTool.decrypt(SCTool.DECRYPT_TYPE2, src, dec);
        if (result == SCTool.SUCCESS) {
            tvShow.setTextColor(Color.GREEN);
            tvShow.setText("SUCCESS");
        } else {
            tvShow.setTextColor(Color.RED);
            if (result == SCTool.ERROR_PARAM) {
                tvShow.setText("参数错误");
            } else if (result == SCTool.ERROR_NO_CARD) {
                tvShow.setText("无卡或无Reader");
            } else if (result == SCTool.ERROR_TRANSMISSION) {
                tvShow.setText("通信异常");
            } else if (result == SCTool.ERROR_TYPE) {
                tvShow.setText("解密类型不支持");
            } else if (result == SCTool.ERROR_DECRYPT) {
                tvShow.setText("解密异常");
            }
        }
    }
}
