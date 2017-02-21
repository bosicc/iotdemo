package com.ciklum.iotdemo.connectivity;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ciklum.iotdemo.connectivity.btle.BTLEConnection;
import com.ciklum.iotdemo.connectivity.model.DeviceData;

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
    public void connect(@NonNull DeviceData deviceData) {
        connection.connect(deviceData);
    }

    @Override
    public void disconnect() {
        connection.disconnect();
    }

    @Override
    public boolean isConnected() {
        return connection.isConnected();
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

    @Override
    public void onStart() {
        connection.onStart();
    }

    @Override
    public void onStop() {
        connection.onStop();
    }

    @Override
    public void addDeviceCallback(@NonNull DeviceCallback deviceCallback) {
        connection.addDeviceCallback(deviceCallback);
    }

    @Override
    public void removeDeviceCallback(@NonNull DeviceCallback deviceCallback) {
        connection.removeDeviceCallback(deviceCallback);
    }
}