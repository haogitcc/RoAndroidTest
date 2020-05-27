package com.licheedev.serialtool.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.serialport.api.SerialPort;
import android.serialport.api.SerialPortFinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.licheedev.serialtool.R;
import com.licheedev.serialtool.activity.base.BaseActivity;
import com.licheedev.serialtool.comn.Device;
import com.licheedev.serialtool.comn.SerialReadThread;
import com.licheedev.serialtool.comn.message.LogManager;
import com.licheedev.serialtool.comn.message.SendMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.licheedev.serialtool.R.array.baudrates;

public class LoRaActivity extends BaseActivity {

    private static final String TAG = "LoRaActivity";
    SerialPort serialPort;
    private SerialReadThread loraReadThread;
    private Spinner mSpinnerDevices;
    private Spinner mSpinnerBaudrate;
    private String[] mDevices;
    private String[] mBaudrates;
    private Device device;
    private EditText mFromEt;
    private EditText mDestEt;
    private boolean loraSendStarted = false;
    private LoRaOpThread lora;
    private EditText mLoraDataEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.lora_test_layout); // cause extends BaseActivity

        initDevices();
        initViews();
    }

    private void initDevices() {
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

    private void initViews() {
        mSpinnerDevices = findViewById(R.id.lora_device_spinner);
        mSpinnerBaudrate = findViewById(R.id.lora_baudrate_spinner);
        mFromEt = findViewById(R.id.lora_from_et);
        mDestEt = findViewById(R.id.lora_dest_et);
        mLoraDataEt = findViewById(R.id.lora_data_et);

        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<String>(this, R.layout.spinner_default_item, mDevices);
        deviceAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSpinnerDevices.setAdapter(deviceAdapter);

        ArrayAdapter<String> baudrateAdapter = new ArrayAdapter<String>(this, R.layout.spinner_default_item, mBaudrates);
        baudrateAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSpinnerBaudrate.setAdapter(baudrateAdapter);

        mSpinnerDevices.setSelection(11);
        mSpinnerBaudrate.setSelection(12);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lora_test_layout;
    }

    public void loraCommTest(View view) throws IOException {
        List<String> datalist = new ArrayList<String>();
        datalist.add("+++");

        sendDatalist(datalist);
    }

    public void loraRadioTest(View view) throws IOException {
        List<String> datalist = new ArrayList<String>();
        datalist.add("+++");
        datalist.add("at+def");
        datalist.add("+++");
        datalist.add("at+freq=470000000");
        datalist.add("at+sf=12");
        datalist.add("at+bw=125");
        datalist.add("at+mode=radio");
        datalist.add("at+reboot");
        datalist.add("+++");
        datalist.add("at+exit");
        datalist.add("Radio 470MHz test");

        sendDatalist(datalist);
    }

    public void loraUnicastTest(View view) throws IOException {
        String from = mFromEt.toString();
        String dest = mDestEt.toString();
        List<String> datalist = new ArrayList<String>();
        datalist.add("+++");
        datalist.add("at+def");
        datalist.add("+++");
        datalist.add("at+mode=pmac");
        datalist.add("at+reboot");
        datalist.add("+++");
        datalist.add("at+plocal=0x"+from);
        datalist.add("at+ptransdest=0x"+dest);
        datalist.add("at+ptransmode=ucast");
        datalist.add("at+exit");
        datalist.add("PMAC Unicast test");

        sendDatalist(datalist);
    }

    public void loraMulticastTest(View view) throws IOException {
        List<String> datalist = new ArrayList<String>();

        sendDatalist(datalist);
    }

    public void loraBroadcastTest(View view) throws IOException {
        List<String> datalist = new ArrayList<String>();
        datalist.add("+++");
        datalist.add("at+def");
        datalist.add("+++");
        datalist.add("at+mode=pmac");
        datalist.add("at+reboot");
        datalist.add("+++");
        datalist.add("at+ptransdest=0xffff");
        datalist.add("at+ptransmode=ucast");
        datalist.add("at+exit");
        datalist.add("PMAC BroadCast Test");

        sendDatalist(datalist);
    }

    private void sendDatalist(List<String> datalist) throws IOException {
        String[] datas = datalist.toArray(new String[datalist.size()]);
        for (String data : datas) {
            sendMsg(TAG,  data);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMsg(String tag, String sendData) throws IOException {
        Log.d(TAG, "sendMsg: " + sendData);
        LogManager.instance().post(new SendMessage(TAG, sendData));
        byte[] data = sendData.getBytes();
        serialPort.getOutputStream().write(data);
    }

    public void loraOpenSerial(View view) {
        Button btn = (Button) view;
        if(btn.getText().toString().equals(getString(R.string.open_serial_port)))
        {
            btn.setText(getString(R.string.close_serial_port));
            String dev = mSpinnerDevices.getSelectedItem().toString();
            String baud = mSpinnerBaudrate.getSelectedItem().toString();
            device = new Device(dev, baud);
            try {
                serialPort = new SerialPort(device.getDevice(), device.getBaudrateInt(), 0);
                loraReadThread = new SerialReadThread(TAG, serialPort.getInputStream());
                loraReadThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            btn.setText(getString(R.string.open_serial_port));
            if (loraReadThread != null) {
                loraReadThread.close();
            }
            serialPort.close();
        }
    }

    public void loraSendData(View view) {
        if(!loraSendStarted)
        {
            loraSendStarted = true;
            lora = new LoRaOpThread(view);
            lora.execute();
        }
        else
        {
            lora.stop();
            loraSendStarted = false;
        }

    }

    private class LoRaOpThread extends AsyncTask {

        private View view;
        private boolean start = false;

        public LoRaOpThread(View view) {
            this.view = view;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Button btn = (Button) view;
            if(btn.getText().toString().equals(getString(R.string.send_data)))
            {
                btn.setText(getString(R.string.sending_data));
                start = true;
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            while (start)
            {
                String data = mLoraDataEt.getText().toString();
                try {
                    sendMsg(TAG, data);
                    Thread.sleep(1000);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public void stop() {
            start = false;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Button btn = (Button) view;
            btn.setText(getString(R.string.send_data));
        }
    }
}
