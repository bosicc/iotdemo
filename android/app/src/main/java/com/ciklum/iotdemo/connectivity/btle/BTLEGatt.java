package com.ciklum.iotdemo.connectivity.btle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ciklum.iotdemo.connectivity.btle.receiver.BTLEGattBroadcast;
import com.ciklum.iotdemo.connectivity.model.DeviceData;

/**
 * Service for managing connection and generalData communication with a GATT server hosted on a
 * given Bluetooth LE device_fragment.
 */
class BTLEGatt {
    private final static String TAG = BTLEGatt.class.getSimpleName();
    private final Context context;
    private String deviceAddress;
    private BluetoothGatt gatt;
    private BluetoothAdapter bluetoothAdapter;
    private DeviceData deviceData;
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // Attempts to discover services after successful connection.
                try {
                    Log.i(TAG, "Attempting to start service discovery:" + BTLEGatt.this.gatt.discoverServices());
                } catch (NullPointerException e) {
                    Log.e(TAG, "onConnectionStateChange: gatt is null!");
                    disconnect();
                    broadcastUpdate(BTLEGattBroadcast.ACTION_GATT_DISCONNECTED);
                }
                broadcastUpdate(BTLEGattBroadcast.ACTION_GATT_CONNECTED);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                broadcastUpdate(BTLEGattBroadcast.ACTION_GATT_DISCONNECTED);
            }
        }
    };

    BTLEGatt(Context context, BluetoothAdapter bluetoothAdapter) {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        intent.putExtra(BTLEGattBroadcast.ACTION_DEVICE_DATA, deviceData);
        context.sendBroadcast(intent);
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device_fragment.
     *
     * @param deviceData The device_fragment address of the destination device_fragment.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    boolean connect(@NonNull DeviceData deviceData) {
        this.deviceData = deviceData;
        String address = deviceData.getAddress();
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device_fragment.  Try to reconnect.
        if (deviceAddress != null && address.equals(deviceAddress)
                && gatt != null) {
            Log.d(TAG, "Trying to use an existing gatt for connection.");
            return gatt.connect();
        }
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device_fragment, so we are setting the autoConnect
        // parameter to false.
        gatt = device.connectGatt(context, false, mGattCallback);
        deviceAddress = address;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    void disconnect() {
        if (bluetoothAdapter == null || gatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        gatt.disconnect();
        close();
    }

    /**
     * After using a given BLE device_fragment, the app must call this method to ensure resources are
     * released properly.
     */
    private void close() {
        if (gatt != null) {
            gatt.close();
            gatt = null;
            broadcastUpdate(BTLEGattBroadcast.ACTION_GATT_DISCONNECTED);
        }
    }
}