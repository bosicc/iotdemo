package com.ciklum.iotdemo.connectivity.btle.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ciklum.iotdemo.connectivity.btle.BTLEConnection;

public class BTLEStateChangesBroadcast extends BroadcastReceiver {
    private final BTLEConnection btleConnection;

    public BTLEStateChangesBroadcast(BTLEConnection connection) {
        this.btleConnection = connection;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    btleConnection.bluetoothDisabled();
                    break;
                case BluetoothAdapter.STATE_ON:
                    btleConnection.bluetoothEnabled();
                    break;
                default:
                    break;
            }
        }
    }
}