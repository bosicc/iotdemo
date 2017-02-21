package com.ciklum.iotdemo.connectivity.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceData implements Parcelable {
    private String address;
    private String name;

    public DeviceData(String mac, String name) {
        this.address = mac;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(name);
    }
    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<DeviceData> CREATOR = new Creator<DeviceData>() {
        public DeviceData createFromParcel(Parcel in) {
            return new DeviceData(in);
        }

        public DeviceData[] newArray(int size) {
            return new DeviceData[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private DeviceData(Parcel in) {
        address = in.readString();
        name = in.readString();
    }

    @Override
    public String toString() {
        return "DeviceData{" +
                "address='" + address + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}