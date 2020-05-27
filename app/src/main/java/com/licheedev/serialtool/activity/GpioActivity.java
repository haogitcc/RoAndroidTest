package com.licheedev.serialtool.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.licheedev.serialtool.R;

import jnigpio.GPIOControl;

public class GpioActivity extends Activity {
    private static final String TAG = "GpioActivity";
    private static final String GPIO_OUT1 = "out1";
    private static final String GPIO_OUT2 = "out2";
    private static final String GPIO_OUT3 = "out3";
    private static final String GPIO_OUT4 = "out4";
    final int LED1 = 1;
    final int LED2 = 2;
    final int LED3 = 3;
    final int LED_ON = 1;
    final int LED_OFF = 0;

    Spinner gpiosSpinner;
    Spinner valueEt;
    CheckBox gpoCb_1;
    CheckBox gpoCb_2;
    CheckBox gpoCb_3;
    CheckBox gpoCb_4;
    CheckBox gpiCb_1;
    CheckBox gpiCb_2;
    CheckBox gpiCb_3;
    CheckBox gpiCb_4;
    CheckBox gpoCb_all;

    GPIOControl gpioControl;
    int deviceValue;
    private int gpoSet_ret = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gpio_layout);
        initViews();
        // 初始化控件
        // 建立数据源
        String[] mItems = getResources().getStringArray(R.array.device_values);
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        valueEt .setAdapter(adapter);

        gpioControl = new GPIOControl();
        deviceValue = getDeviceStatus(gpiosSpinner.getSelectedItem().toString());
    }

    private void initViews() {
        gpiosSpinner = findViewById(R.id.gpio_spinner);
        valueEt = findViewById(R.id.device_value_et);

        gpoCb_1 = findViewById(R.id.gpio_out1_cb);
        gpoCb_2 = findViewById(R.id.gpio_out2_cb);
        gpoCb_3 = findViewById(R.id.gpio_out3_cb);
        gpoCb_4 = findViewById(R.id.gpio_out4_cb);

        gpiCb_1 = findViewById(R.id.gpio_in1_cb);
        gpiCb_2 = findViewById(R.id.gpio_in2_cb);
        gpiCb_3 = findViewById(R.id.gpio_in3_cb);
        gpiCb_4 = findViewById(R.id.gpio_in4_cb);
        gpoCb_all = findViewById(R.id.gpio_out_all_cb);
    }

    private int getLedStatus(int led_num) {
        String device_path = "/sys/class/hwmon/hwmon0/led" + led_num;
        return gpioControl.getDeviceStatus(device_path);
    }

    private boolean setLedStatus(int led_num, int status) {
        String device_path = "/sys/class/hwmon/hwmon0/led" + led_num;
        return gpioControl.setDeviceStatus(device_path, status)==1?true:false;
    }

    private int getDeviceStatus(String device_name) {
        String device_path = "/sys/class/hwmon/hwmon0/" + device_name;
        return gpioControl.getDeviceStatus(device_path);
    }

    private int setDeviceStatus(String device_name, int status) {
        String device_path = "/sys/class/hwmon/hwmon0/" + device_name;
        return gpioControl.setDeviceStatus(device_path, status);
    }

    public void gpioCheckUpdate(View view) {
        boolean isChecked = ((CheckBox)view).isChecked();
        switch (view.getId())
        {
            case R.id.gpio_out1_cb:
                gpoSet_ret = setDeviceStatus(GPIO_OUT1, isChecked?1:0);
                gpiCb_1.setChecked(getDeviceStatus(GPIO_OUT1)==1?true:false);
                break;
            case R.id.gpio_out2_cb:
                gpoSet_ret = setDeviceStatus(GPIO_OUT2, isChecked?1:0);
                gpiCb_2.setChecked(getDeviceStatus(GPIO_OUT2)==1?true:false);
                break;
            case R.id.gpio_out3_cb:
                gpoSet_ret = setDeviceStatus(GPIO_OUT3, isChecked?1:0);
                gpiCb_3.setChecked(getDeviceStatus(GPIO_OUT3)==1?true:false);
                break;
            case R.id.gpio_out4_cb:
                gpoSet_ret = setDeviceStatus(GPIO_OUT4, isChecked?1:0);
                gpiCb_4.setChecked(getDeviceStatus(GPIO_OUT4)==1?true:false);
                break;
            default:
                break;
        }
    }

    public void updateAllGpioCb(View view) {
        boolean isChecked = ((CheckBox)view).isChecked();
        gpoCb_1.setChecked(isChecked);
        gpioCheckUpdate(gpoCb_1);

        gpoCb_2.setChecked(isChecked);
        gpioCheckUpdate(gpoCb_2);

        gpoCb_3.setChecked(isChecked);
        gpioCheckUpdate(gpoCb_3);

        gpoCb_4.setChecked(isChecked);
        gpioCheckUpdate(gpoCb_4);

        Log.e(TAG, "updateAllGpioCb: gpoSet_ret=" + gpoSet_ret );
        if(gpoSet_ret == 1)
            Toast.makeText(this, R.string.test_all_gpio_success, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, R.string.test_all_gpio_failed, Toast.LENGTH_LONG).show();
    }

    public void getDeviceValus(View view) {
        deviceValue = getDeviceStatus(gpiosSpinner.getSelectedItem().toString());
        if(deviceValue < 0)
            Toast.makeText(this, "获取异常！", Toast.LENGTH_LONG).show();
        valueEt.setSelection(deviceValue);
    }

    public void setDeviceValus(View view) {
        int ret = setDeviceStatus(gpiosSpinner.getSelectedItem().toString(), Integer.parseInt(valueEt.getSelectedItem().toString()));
        if(ret > 0)
            Toast.makeText(this, "设置成功！", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "设置失败！", Toast.LENGTH_LONG).show();
    }

    public void setLedStatus(View view) {
        int id = view.getId();
        TextView tv = findViewById(id);
        switch (id)
        {
            case R.id.btn_led1:
                updateLed(tv, LED1, Color.RED);
                break;
            case R.id.btn_led2:
                updateLed(tv, LED2, Color.YELLOW);
                break;
            case R.id.btn_led3:
                updateLed(tv, LED3, Color.BLUE);
                break;
            default:
                break;
        }
    }

    private void updateLed(TextView tv, int led, int color) {
        if(getLedStatus(led) == LED_ON) {
            if(setLedStatus(led, LED_OFF))
                tv.setTextColor(Color.BLACK);
        }
        else {
            if(setLedStatus(led, LED_ON))
                tv.setTextColor(color);
        }
    }
}
