package com.ciklum.iotdemo.connectivity.btle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.ciklum.iotdemo.R;
import com.ciklum.iotdemo.connectivity.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class BTLEHelper {
    private static final String TAG = BTLEHelper.class.getSimpleName();
    private static final UUID DEVICE_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter.LeScanCallback scanCallback;
    private BluetoothLeScanner leScanner; // for api >= 21
    private ScanCallback scanCallbackV21; // for api >= 21
    private Connection.ScanCallback sCallback;
    private Context context;

    BTLEHelper(Context context) {
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initScanCallbackV21();
        } else {
            initScanCallback();
        }
    }

    @SuppressWarnings("deprecation")
    void startScan() {
        Log.d(TAG, "startScan: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startScanV21();
        } else {
            bluetoothAdapter.startLeScan(new UUID[]{DEVICE_UUID}, scanCallback);
        }
    }

    @SuppressWarnings("deprecation")
    void stopScan() {
        Log.d(TAG, "stopScan: ");
        if (checkIfBleEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                stopScanV21();
            } else {
                bluetoothAdapter.stopLeScan(scanCallback);
            }
        }
    }

    void setScanCallback(Connection.ScanCallback scanCallback) {
        this.sCallback = scanCallback;
    }

    private void initScanCallback() {
        scanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                deviceDiscovered(device, scanRecord);
            }
        };
    }

    @RequiresApi(21)
    private void initScanCallbackV21() {
        scanCallbackV21 = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                byte[] data = new byte[]{};
                if (result.getScanRecord() != null) {
                    data = result.getScanRecord().getBytes();
                }
                deviceDiscovered(result.getDevice(), data);
            }
        };
    }

    @RequiresApi(21)
    private void startScanV21() {
        leScanner = bluetoothAdapter.getBluetoothLeScanner();
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(DEVICE_UUID))
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(scanFilter);

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .build();

        leScanner.startScan(filters, scanSettings, scanCallbackV21);
    }

    @RequiresApi(21)
    private void stopScanV21() {
        if (leScanner != null) {
            leScanner.stopScan(scanCallbackV21);
        }
    }

    private void deviceDiscovered(BluetoothDevice device, byte[] data) {
        if (sCallback != null && device.getAddress().equals(context.getString(R.string.neopenda_address))) {
            sCallback.deviceDiscovered(data);
        }
    }

    private boolean checkIfBleEnabled() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager.getAdapter().isEnabled();
    }
}