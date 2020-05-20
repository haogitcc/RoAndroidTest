package com.licheedev.serialtool.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.licheedev.serialtool.R;

import jnigpio.GPIOControl;

public class GpioActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
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
    CheckBox isSetCb;
//    EditText valueEt;
    Spinner valueEt;
    TextView valueTv;
    Button getBtn;
    Button setBtn;
    Button led1Btn;
    Button led2Btn;
    Button led3Btn;
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

        getBtn.setOnClickListener(this);
        setBtn.setOnClickListener(this);
        led1Btn.setOnClickListener(this);
        led2Btn.setOnClickListener(this);
        led3Btn.setOnClickListener(this);
        isSetCb.setOnCheckedChangeListener(this);
        gpoCb_1.setOnCheckedChangeListener(this);
        gpoCb_2.setOnCheckedChangeListener(this);
        gpoCb_3.setOnCheckedChangeListener(this);
        gpoCb_4.setOnCheckedChangeListener(this);
        gpoCb_all.setOnCheckedChangeListener(this);
    }



    private void initViews() {
        gpiosSpinner = findViewById(R.id.gpio_spinner);
        isSetCb = findViewById(R.id.device_is_set_cb);
        valueEt = findViewById(R.id.device_value_et);
        valueTv = findViewById(R.id.device_value_tv);
        getBtn = findViewById(R.id.set_device_value_btn);
        setBtn = findViewById(R.id.get_device_value_btn);
        led1Btn = findViewById(R.id.btn_led1);
        led2Btn = findViewById(R.id.btn_led2);
        led3Btn = findViewById(R.id.btn_led3);

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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.get_device_value_btn:
                deviceValue = getDeviceStatus(gpiosSpinner.getSelectedItem().toString());
                updateDeviceValue(isSetCb.isChecked());
                if(deviceValue < 0)
                    Toast.makeText(this, "获取异常！", Toast.LENGTH_LONG).show();
                break;
            case R.id.set_device_value_btn:
                int ret = setDeviceStatus(gpiosSpinner.getSelectedItem().toString(), Integer.parseInt(valueEt.getSelectedItem().toString()));
                if(ret > 0)
                    Toast.makeText(this, "设置成功！", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "设置失败！", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_led1:
                if(getLedStatus(LED1) == LED_ON) {
                    if(setLedStatus(LED1, LED_OFF))
                        led1Btn.setTextColor(R.color.black);
                }
                else
                {
                    if(setLedStatus(LED1, LED_ON))
                        led1Btn.setTextColor(R.color.color_red);
                }
                break;
            case R.id.btn_led2:
                if(getLedStatus(LED2) == LED_ON) {
                    if(setLedStatus(LED2, LED_OFF))
                        led2Btn.setTextColor(R.color.color_blue);
                }
                else
                {
                    if(setLedStatus(LED2, LED_ON))
                        led2Btn.setTextColor(R.color.white);
                }
                break;
            case R.id.btn_led3:
                if(getLedStatus(LED3) == LED_ON) {
                    if(setLedStatus(LED3, LED_OFF))
                        led3Btn.setTextColor(R.color.black);
                }
                else
                {
                    if(setLedStatus(LED3, LED_ON))
                        led3Btn.setTextColor(R.color.color_blue);
                }
                break;
            default:
                    break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId())
        {
            case R.id.device_is_set_cb:
                updateDeviceValue(isChecked);
                break;
            case R.id.gpio_out_all_cb:
                gpoCb_1.setChecked(isChecked);
                gpoCb_2.setChecked(isChecked);
                gpoCb_3.setChecked(isChecked);
                gpoCb_4.setChecked(isChecked);
                break;
            case R.id.gpio_out1_cb:
                setDeviceStatus(GPIO_OUT1, isChecked?1:0);
                gpiCb_1.setChecked(getDeviceStatus(GPIO_OUT1)==1?true:false);
                break;
            case R.id.gpio_out2_cb:
                setDeviceStatus(GPIO_OUT2, isChecked?1:0);
                gpiCb_2.setChecked(getDeviceStatus(GPIO_OUT2)==1?true:false);
                break;
            case R.id.gpio_out3_cb:
                setDeviceStatus(GPIO_OUT3, isChecked?1:0);
                gpiCb_3.setChecked(getDeviceStatus(GPIO_OUT3)==1?true:false);
                break;
            case R.id.gpio_out4_cb:
                setDeviceStatus(GPIO_OUT4, isChecked?1:0);
                gpiCb_4.setChecked(getDeviceStatus(GPIO_OUT4)==1?true:false);
                break;
            default:
                break;
        }
    }

    private void updateDeviceValue(boolean isChecked) {
        if(isChecked)
        {
            valueEt.setVisibility(View.VISIBLE);
            valueTv.setVisibility(View.GONE);
//            valueEt.setText("" + deviceValue);
            valueEt.setSelection(deviceValue);
            setBtn.setEnabled(true);
        }
        else
        {
            valueEt.setVisibility(View.GONE);
            valueTv.setVisibility(View.VISIBLE);
            valueTv.setText("" + deviceValue);
            setBtn.setEnabled(true);
        }
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
}
