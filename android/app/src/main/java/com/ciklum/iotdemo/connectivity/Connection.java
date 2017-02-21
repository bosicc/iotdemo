package com.ciklum.iotdemo.connectivity;

import android.support.annotation.NonNull;

public interface Connection {

    void startScan();

    void stopScan();

    void setScanningCallback(@NonNull ScanCallback scanCallback);

    interface ScanCallback {
        void deviceDiscovered(@NonNull byte[] data);
    }
}