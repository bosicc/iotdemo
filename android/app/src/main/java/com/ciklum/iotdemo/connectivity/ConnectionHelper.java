package com.ciklum.iotdemo.connectivity;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ciklum.iotdemo.connectivity.btle.BTLEConnection;

public class ConnectionHelper implements Connection {
    private static ConnectionHelper instance;
    private Connection connection;

    private ConnectionHelper(Context context) {
        connection = BTLEConnection.getInstance(context);
    }

    public static ConnectionHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ConnectionHelper(context);
        }
        return instance;
    }

    @Override
    public void setScanningCallback(@NonNull ScanCallback scanCallback) {
        connection.setScanningCallback(scanCallback);
    }

    @Override
    public void startScan() {
        connection.startScan();
    }

    @Override
    public void stopScan() {
        connection.stopScan();
    }
}