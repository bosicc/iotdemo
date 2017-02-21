package com.ciklum.iotdemo.connectivity.btle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ciklum.iotdemo.connectivity.Connection;

public class BTLEConnection implements Connection {
    private static BTLEConnection instance;
    @Nullable
    private BTLEHelper btleHelper;

    private BTLEConnection(Context context) {
        this.btleHelper = new BTLEHelper(context);
    }

    public static BTLEConnection getInstance(Context context) {
        if (instance == null) {
            instance = new BTLEConnection(context);
        }
        return instance;
    }

    @Override
    public void setScanningCallback(@NonNull ScanCallback scanCallback) {
        if (btleHelper != null) {
            btleHelper.setScanCallback(scanCallback);
        }
    }

    @Override
    public void startScan() {
        if (btleHelper != null) {
            btleHelper.startScan();
        }
    }

    @Override
    public void stopScan() {
        if (btleHelper != null) {
            btleHelper.stopScan();
        }
    }
}