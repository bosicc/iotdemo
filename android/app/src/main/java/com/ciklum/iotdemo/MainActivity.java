package com.ciklum.iotdemo;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ciklum.iotdemo.backend.ApiService;
import com.ciklum.iotdemo.backend.Urls;
import com.ciklum.iotdemo.connectivity.Connection;
import com.ciklum.iotdemo.connectivity.ConnectionHelper;

import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_LOCATION = 11;
    private ConnectionHelper connectionHelper;
    private boolean isScanning;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.temperature_view)
    TextView temperatureView;
    private Connection.ScanCallback scanCallback = new Connection.ScanCallback() {
        @Override
        public void deviceDiscovered(@NonNull byte[] scanRecord) {
            if (scanRecord.length > 14) {
                if (scanRecord[13] == (byte) 0x4E && scanRecord[14] == (byte) 0xFF) {
                    byte[] data = Arrays.copyOfRange(scanRecord, 15, 23);
                    double temperature = (convertToUnsigned(data[7]) << 8) + convertToUnsigned(data[6]);
                    float res = (float) (temperature / 10);

                    temperatureView.setText(String.format(Locale.getDefault(), "%.2f", res));

                    sendTemperature(res);
                }
            }
        }

        private int convertToUnsigned(byte b) {
            return b & 0xFF;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        connectionHelper = ConnectionHelper.getInstance(getApplicationContext());
        connectionHelper.setScanningCallback(scanCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkIfBleEnabled() && !isGpsPermissionNeed() && !isScanning) {
            isScanning = true;
            connectionHelper.startScan();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isScanning) {
            isScanning = false;
            connectionHelper.stopScan();
        }
    }

    private void sendTemperature(float value) {
        Log.i(TAG, "deviceDiscovered: " + value);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Urls.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<ResponseBody> call = apiService.post(String.valueOf(value));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "onResponse: onPost: " + response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: onPost: " + t.getMessage());
            }
        });
    }

    private boolean checkIfBleEnabled() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        boolean isEnabled = bluetoothManager.getAdapter().isEnabled();
        if (!isEnabled) {
            showMessage(getString(R.string.error_bluetooth));
        }
        return isEnabled;
    }

    public boolean isGpsPermissionNeed() {
        boolean res = false;
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "isGpsPermissionNeed: ");
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            res = true;
        }
        return res;
    }

    private void showMessage(String message) {
        if (container != null) {
            Snackbar.make(container, message, Snackbar.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}