package com.licheedev.serialtool.comn;

import android.os.HandlerThread;
import android.serialport.api.SerialPort;
import android.util.Log;

import com.licheedev.myutils.LogPlus;
import com.licheedev.serialtool.comn.message.LogManager;
import com.licheedev.serialtool.comn.message.SendMessage;
import com.licheedev.serialtool.util.ByteUtil;

import java.io.IOException;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class PortUtils {

    private static final String TAG = "SerialPortManager";

    private SerialReadThread mReadThread;
    private OutputStream mOutputStream;
    private HandlerThread mWriteThread;
    private Scheduler mSendScheduler;
    String endian;
    SerialPort mSerialPort;

    public PortUtils(String path, SerialPort rs) {
        this.endian = path;
        this.mSerialPort = rs;
    }

    public void start() {

        try {
            mReadThread = new SerialReadThread(endian, mSerialPort.getInputStream());
            mReadThread.start();

            mOutputStream = mSerialPort.getOutputStream();

            mWriteThread = new HandlerThread(endian + " write-thread");
            mWriteThread.start();
            mSendScheduler = AndroidSchedulers.from(mWriteThread.getLooper());

        } catch (Throwable tr) {
            LogPlus.e(TAG, "start", tr);
            close();
        }
    }

    /**
     * 关闭串口
     */
    public void close() {
        if (mReadThread != null) {
            mReadThread.close();
        }

        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mWriteThread != null) {
            mWriteThread.quit();
        }
    }

    /**
     * 发送数据
     *
     * @param datas
     * @return
     */
    private void sendData(byte[] datas) throws Exception {
        mOutputStream.write(datas);
    }

    /**
     * (rx包裹)发送数据
     *
     * @param datas
     * @return
     */
    private Observable<Object> rxSendData(final byte[] datas) {

        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                try {
                    sendData(datas);
                    emitter.onNext(new Object());
                    Log.e(TAG, "----> " + new String(datas, "ASCII") );
                } catch (Exception e) {

                    LogPlus.e(endian + " 发送：" + ByteUtil.bytes2HexStr(datas) + " 失败", e);

                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                        return;
                    }
                }
                emitter.onComplete();
            }
        });
    }

    /**
     * 发送命令包
     */
    public void sendCommand(final String command) {

        // TODO: 2018/3/22
        LogPlus.i(endian + " 发送命令：" + command);

//        byte[] bytes = ByteUtil.hexStr2bytes(command);
        String sendData = command + "\r";
        byte[] bytes = sendData.getBytes();
        rxSendData(bytes).subscribeOn(mSendScheduler).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                LogManager.instance().post(new SendMessage(endian, command));
            }

            @Override
            public void onError(Throwable e) {
                LogPlus.e(endian + " 发送失败", e);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
