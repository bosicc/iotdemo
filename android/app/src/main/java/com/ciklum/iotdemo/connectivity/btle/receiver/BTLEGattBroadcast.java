package com.ciklum.iotdemo.connectivity.btle.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ciklum.iotdemo.connectivity.Connection;
import com.ciklum.iotdemo.connectivity.model.DeviceData;

public class BTLEGattBroadcast extends BroadcastReceiver {
    private static final String TAG = BTLEGattBroadcast.class.getSimpleName();
    public static final String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public static final String ACTION_DEVICE_DATA = "ACTION_DEVICE_DATA";
    private final Connection.DeviceCallback connectionBroadcastCallback;

    public BTLEGattBroadcast(Connection.DeviceCallback connectionBroadcastCallback) {
        this.connectionBroadcastCallback = connectionBroadcastCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        DeviceData deviceData = intent.getParcelableExtra(ACTION_DEVICE_DATA);
        switch (action) {
            case ACTION_GATT_CONNECTED:
                Log.d(TAG, "onReceive: action = " + action);
                connectionBroadcastCallback.connected(deviceData);
                break;
            case ACTION_GATT_DISCONNECTED:
                Log.d(TAG, "onReceive: action = " + action);
                connectionBroadcastCallback.disconnected();
                break;
            default:
                break;

        }
    }
}