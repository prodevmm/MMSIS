package com.jlib.mmsis.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NavigationModel implements Parcelable {
    private String name, code_code;

    public NavigationModel(String name, String url) {
        this.name = name;
        this.code_code = url;
    }

    protected NavigationModel(Parcel in) {
        name = in.readString();
        code_code = in.readString();
    }

    public static final Creator<NavigationModel> CREATOR = new Creator<NavigationModel>() {
        @Override
        public NavigationModel createFromParcel(Parcel in) {
            return new NavigationModel(in);
        }

        @Override
        public NavigationModel[] newArray(int size) {
            return new NavigationModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getCode_code() {
        return code_code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(code_code);
    }
}
