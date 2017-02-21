package com.ciklum.iotdemo.connectivity.btle;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ciklum.iotdemo.connectivity.Connection;
import com.ciklum.iotdemo.connectivity.btle.receiver.BTLEGattBroadcast;
import com.ciklum.iotdemo.connectivity.btle.receiver.BTLEStateChangesBroadcast;
import com.ciklum.iotdemo.connectivity.model.DeviceData;

import java.util.ArrayList;
import java.util.List;

public class BTLEConnection implements Connection {
    private static final String TAG = BTLEConnection.class.getSimpleName();
    private static BTLEConnection instance; // Service context used, it mustn't be a problem
    private BTLEGattBroadcast gattBroadcast;
    private BTLEStateChangesBroadcast stateBroadcast;
    private Context context;
    private List<DeviceCallback> deviceCallbacks;
    private int state;
    @Nullable
    private BTLEHelper btleHelper;
    private DeviceCallback connectionBroadcastCallback = new DeviceCallback() {
        @Override
        public void connected(@NonNull DeviceData deviceData) {
            Log.i(TAG, "connected: ");
            state = CONNECTED;
            callbackConnected(deviceData);
        }

        @Override
        public void disconnected() {
            Log.i(TAG, "disconnected: ");
            state = DISCONNECTED;
            if (btleHelper != null) {
                btleHelper.disconnect();
            }
            callbackDisconnected();
        }
    };

    private BTLEConnection(Context context) {
        this.context = context;
        this.btleHelper = new BTLEHelper(context);
        this.gattBroadcast = new BTLEGattBroadcast(connectionBroadcastCallback);
        this.stateBroadcast = new BTLEStateChangesBroadcast(this);
        this.deviceCallbacks = new ArrayList<>(0);
    }

    public static BTLEConnection getInstance(Context context) {
        if (instance == null) {
            instance = new BTLEConnection(context);
        }
        return instance;
    }

    @Override
    public void connect(@NonNull DeviceData deviceData) {
        if (btleHelper != null) {
            btleHelper.connect(deviceData);
        }
    }

    @Override
    public void disconnect() {
        if (btleHelper != null) {
            btleHelper.disconnect();
        }
    }

    @Override
    public boolean isConnected() {
        return state == CONNECTED;
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

    @Override
    public void addDeviceCallback(@NonNull DeviceCallback deviceCallback) {
        deviceCallbacks.add(deviceCallback);
    }

    @Override
    public void removeDeviceCallback(@NonNull DeviceCallback deviceCallback) {
        deviceCallbacks.remove(deviceCallback);
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart: ");
        context.registerReceiver(gattBroadcast, makeGattUpdateIntentFilter());
        context.registerReceiver(stateBroadcast, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop: ");
        context.unregisterReceiver(gattBroadcast);
        context.unregisterReceiver(stateBroadcast);
    }

    private void callbackConnected(@NonNull DeviceData deviceData) {
        for (int i = 0; i < deviceCallbacks.size(); i++) {
            deviceCallbacks.get(i).connected(deviceData);
        }
    }

    private void callbackDisconnected() {
        for (int i = 0; i < deviceCallbacks.size(); i++) {
            deviceCallbacks.get(i).disconnected();
        }
    }

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTLEGattBroadcast.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BTLEGattBroadcast.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }

    public void bluetoothDisabled() {
        Log.i("BTLEConnection", "bluetoothDisabled: ");
        btleHelper = null;
        connectionBroadcastCallback.disconnected();
    }

    public void bluetoothEnabled() {
        Log.i("BTLEConnection", "bluetoothEnabled: ");
        btleHelper = new BTLEHelper(context);
    }
}