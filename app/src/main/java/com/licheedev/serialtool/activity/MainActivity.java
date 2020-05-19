package com.licheedev.serialtool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.serialport.SerialPort;
import android.serialport.SerialPortFinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.licheedev.serialtool.R;
import com.licheedev.serialtool.activity.base.BaseActivity;
import com.licheedev.serialtool.comn.Device;
import com.licheedev.serialtool.comn.PortUtils;
import com.licheedev.serialtool.util.AllCapTransformationMethod;
import com.licheedev.serialtool.util.ToastUtil;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

import static com.licheedev.serialtool.R.array.baudrates;

public class MainActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    @BindView(R.id.spinner_devices)
    Spinner mSpinnerDevices;
    @BindView(R.id.spinner_baudrate)
    Spinner mSpinnerBaudrate;
    @BindView(R.id.btn_open_device)
    Button mBtnOpenDevice;
    @BindView(R.id.btn_send_data)
    Button mBtnSendData;
    @BindView(R.id.btn_load_list)
    Button mBtnLoadList;
    @BindView(R.id.et_data)
    EditText mEtData;

    @BindView(R.id.spinner_devices_1)
    Spinner mSpinnerDevices_RS485;
    @BindView(R.id.spinner_baudrate_1)
    Spinner mSpinnerBaudrate_RS485;
    @BindView(R.id.btn_open_device_1)
    Button mBtnOpenDevice_RS485;
    @BindView(R.id.btn_send_data_1)
    Button mBtnSendData_RS485;
    @BindView(R.id.btn_load_list_1)
    Button mBtnLoadList_RS485;
    @BindView(R.id.et_data_1)
    EditText mEtData_RS485;


    private String[] mDevices;
    private String[] mBaudrates;

    private boolean mIsOpen = false;
    private boolean mIsOpen_RS485 = false;
    Device mDevice;
    Device mDevice_RS485;
    SerialPort rs;
    SerialPort rs485;

    PortUtils portUtils;
    PortUtils portUtils_RS485;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEtData.setTransformationMethod(new AllCapTransformationMethod(true));

        initDevice();
        initSpinners();
        mDevice = new Device();
        mDevice_RS485 = new Device();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * 初始化设备列表
     */
    private void initDevice() {

        SerialPortFinder serialPortFinder = new SerialPortFinder();

        // 设备
        mDevices = serialPortFinder.getAllDevicesPath();
        if (mDevices.length == 0) {
            mDevices = new String[] {
                getString(R.string.no_serial_device)
            };
        }
        // 波特率
        mBaudrates = getResources().getStringArray(baudrates);

    }

    /**
     * 初始化下拉选项
     */
    private void initSpinners() {

        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<String>(this, R.layout.spinner_default_item, mDevices);
        deviceAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSpinnerDevices.setAdapter(deviceAdapter);
        mSpinnerDevices.setOnItemSelectedListener(this);

        mSpinnerDevices_RS485.setAdapter(deviceAdapter);
        mSpinnerDevices_RS485.setOnItemSelectedListener(this);

        ArrayAdapter<String> baudrateAdapter = new ArrayAdapter<String>(this, R.layout.spinner_default_item, mBaudrates);
        baudrateAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSpinnerBaudrate.setAdapter(baudrateAdapter);
        mSpinnerBaudrate.setOnItemSelectedListener(this);


        mSpinnerBaudrate_RS485.setAdapter(baudrateAdapter);
        mSpinnerBaudrate_RS485.setOnItemSelectedListener(this);

        mSpinnerDevices.setSelection(11);
        mSpinnerBaudrate.setSelection(12);
        mSpinnerDevices_RS485.setSelection(4);
        mSpinnerBaudrate_RS485.setSelection(12);
    }

    @OnClick({ R.id.btn_open_device, R.id.btn_send_data, R.id.btn_load_list,
            R.id.btn_open_device_1, R.id.btn_send_data_1, R.id.btn_load_list_1
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_open_device:
                if(mIsOpen == true)
                {
                    closeSerialPort(rs, portUtils);
                    mBtnOpenDevice.setText(R.string.open_serial_port);
                    mIsOpen = false;
                }
                else
                {
                    mDevice.setPath(mSpinnerDevices.getSelectedItem().toString());
                    mDevice.setBaudrate(mSpinnerBaudrate.getSelectedItem().toString());
                    rs = openSerialPort(mDevice);

                    portUtils = new PortUtils(mDevice.getPath(), rs);
                    portUtils.start();

                    mBtnOpenDevice.setText(R.string.close_serial_port);
                    mIsOpen = true;
                }
                break;
            case R.id.btn_open_device_1:
                if(mIsOpen_RS485 == true)
                {
                    closeSerialPort(rs485, portUtils_RS485);
                    mBtnOpenDevice_RS485.setText(R.string.open_serial_port);
                    mIsOpen_RS485 = false;
                }
                else {
                    mDevice_RS485.setPath(mSpinnerDevices_RS485.getSelectedItem().toString());
                    mDevice_RS485.setBaudrate(mSpinnerBaudrate_RS485.getSelectedItem().toString());
                    rs485 = openSerialPort(mDevice_RS485);
                    mBtnOpenDevice_RS485.setText(R.string.close_serial_port);
                    mIsOpen_RS485 = true;

                    portUtils_RS485 = new PortUtils(mDevice_RS485.getPath(), rs485);
                    portUtils_RS485.start();
                }
                break;
            case R.id.btn_send_data:
                sendData(mDevice.getPath(), portUtils, mEtData.getText().toString().trim());
                break;
            case R.id.btn_send_data_1:
                sendData(mDevice_RS485.getPath(), portUtils_RS485, mEtData_RS485.getText().toString().trim());
                break;
            case R.id.btn_load_list:
                startActivity(new Intent(this, LoadCmdListActivity.class));
                break;
        }
    }



    private void sendData(String mSender, PortUtils portUtils, String sendMsg)
    {
        if (TextUtils.isEmpty(sendMsg) || sendMsg.length() % 2 != 0) {
            ToastUtil.showOne(this, "无效数据: isEmpty 或者 length() % 2 != 0");
            return;
        }
        portUtils.sendCommand(sendMsg);
    }

    /**
     * 打开串口
     * @param mDevice
     * @return
     */
    private SerialPort openSerialPort(Device mDevice) {
        String uri = mDevice.getPath();
        int baudrate = Integer.valueOf(mDevice.getBaudrate());
        File device = new File(uri);
        SerialPort sp = null;
        if(device==null)
            return sp;

        try {
            sp = new SerialPort(device, baudrate, 0);
            Log.d(TAG, "OpenSerial: open "+uri+" in "+baudrate+" success!");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "打开串口失败: " + e.getMessage());
        }
        return sp;
    }

    /**
     * 关闭串口
     * @param serialPort
     */
    private void closeSerialPort(SerialPort serialPort, PortUtils portUtils) {
        portUtils.close();

        if(serialPort!=null) {
            Log.d(TAG, "closeSerialPort: close " +serialPort);
            serialPort.close();
            serialPort = null;
            Log.d(TAG, "closeSerialPort: close success!");
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // Spinner 选择监听
        switch (parent.getId()) {
            case R.id.spinner_devices:
                break;
            case R.id.spinner_baudrate:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // 空实现
    }
}
