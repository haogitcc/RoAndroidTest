package com.licheedev.serialtool.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.licheedev.serialtool.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class GpioActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "GpioActivity";
    Spinner gpiosSpinner;
    EditText valueEt;
    Button getBtn;
    Button setBtn;
    Button led1Btn;
    Button led2Btn;
    Button led3Btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gpio_layout);

        initViews();
        getBtn.setOnClickListener(this);
        setBtn.setOnClickListener(this);
        led1Btn.setOnClickListener(this);
        led2Btn.setOnClickListener(this);
        led3Btn.setOnClickListener(this);
    }



    private void initViews() {
        gpiosSpinner = findViewById(R.id.gpio_spinner);
        valueEt = findViewById(R.id.gpio_value_et);
        getBtn = findViewById(R.id.get_gpio_value_btn);
        setBtn = findViewById(R.id.set_gpio_value_btn);
        led1Btn = findViewById(R.id.btn_led1);
        led2Btn = findViewById(R.id.btn_led2);
        led3Btn = findViewById(R.id.btn_led3);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.set_gpio_value_btn:
                break;
            case R.id.get_gpio_value_btn:
                getGpioString("/sys/class/hwmon/hwmon0/led1");
                getGpioString("/sys/class/hwmon/hwmon0/led2");
                getGpioString("/sys/class/hwmon/hwmon0/led3");
                getGpioString("/sys/class/hwmon/hwmon0/rs485_de");
                getGpioString("/sys/class/hwmon/hwmon0/in1");
                getGpioString("/sys/class/hwmon/hwmon0/in2");
                getGpioString("/sys/class/hwmon/hwmon0/in3");
                getGpioString("/sys/class/hwmon/hwmon0/in4");
                getGpioString("/sys/class/hwmon/hwmon0/out1");
                getGpioString("/sys/class/hwmon/hwmon0/out2");
                getGpioString("/sys/class/hwmon/hwmon0/out3");
                getGpioString("/sys/class/hwmon/hwmon0/out4");

                break;
            case R.id.btn_led1:
                if(getGpioString("/sys/class/hwmon/hwmon0/led1").equals("1")) {
                    if(set_gpio0_low())
                        led1Btn.setBackgroundColor(R.color.black);
                }
                else
                {
                    if(set_gpio0_high())
                        led1Btn.setBackgroundColor(R.color.color_red);
                }
                break;
            case R.id.btn_led2:
                if(getGpioString("/sys/class/hwmon/hwmon0/led2").equals("1")) {
//                    if(set_gpio0_low())
                        led2Btn.setBackgroundColor(R.color.color_blue);
                }
                else
                {
//                    if(set_gpio0_high())
                        led2Btn.setBackgroundColor(R.color.white);
                }
                break;
            case R.id.btn_led3:
                if(getGpioString("/sys/class/hwmon/hwmon0/led3").equals("1")) {
//                    if(set_gpio0_low())
                        led3Btn.setBackgroundColor(R.color.black);
                }
                else
                {
//                    if(set_gpio0_high())
                        led3Btn.setBackgroundColor(R.color.color_blue);
                }
                break;
            default:
                    break;
        }
    }

    private String getGpioString(String path) {
        String defString = "0";// 默认值
        try {
            @SuppressWarnings("resource")
            BufferedReader reader = new BufferedReader(new FileReader(path));
            defString = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getGpioString: " + path + " -> " + defString);
        return defString;
    }

    public boolean gpio_input0() {
        return  RootCommand("echo  2 > /sys/class/hwmon/hwmon0/led1");
    }

    private boolean set_gpio0_high() {   //拉高
        boolean FLAG = RootCommand("echo  1 > /sys/class/hwmon/hwmon0/led1");
        Log.d(TAG, "set_gpio0_high: " + FLAG);
        return FLAG;
    }
    public boolean set_gpio0_low() {    //拉低
        boolean FLAG =  RootCommand("echo 0 > /sys/class/hwmon/hwmon0/led1");
        Log.d(TAG, "set_gpio0_low: "+FLAG);
        return FLAG;
    }

    private boolean RootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Log.e(TAG, "#1 RootCommand: " + e.getMessage() );

            try {
                process = new ProcessBuilder().command("/system/xbin/su").redirectErrorStream(true).start();
                os = new DataOutputStream(process.getOutputStream());
                os.writeBytes(command + "\n");
                os.writeBytes("exit\n");
                os.flush();
                process.waitFor();
            } catch (IOException | InterruptedException ex) {
                Log.e(TAG, "#2 RootCommand: " + e.getMessage() );
                return false;
            }
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }
}
