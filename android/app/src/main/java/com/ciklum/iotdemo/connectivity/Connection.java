package com.ciklum.iotdemo.connectivity;

import android.support.annotation.NonNull;

import com.ciklum.iotdemo.connectivity.model.DeviceData;

public interface Connection {
    int CONNECTED = 100;
    int DISCONNECTED = 102;

    void connect(@NonNull DeviceData deviceData);

    void disconnect();

    boolean isConnected();

    void startScan();

    void stopScan();

    void onStart();

    void onStop();

    void setScanningCallback(@NonNull ScanCallback scanCallback);

    void addDeviceCallback(@NonNull DeviceCallback deviceCallback);

    void removeDeviceCallback(@NonNull DeviceCallback deviceCallback);

    interface DeviceCallback {

        void connected(@NonNull DeviceData deviceData);

        void disconnected();
    }

    interface ScanCallback {
        void deviceDiscovered(@NonNull DeviceData deviceData, @NonNull byte [] data);
    }
}