package com.mycardboarddreams.autocompletebubbletext.samplelist;

import android.os.Parcel;
import android.os.Parcelable;

import com.mycardboarddreams.autocompletebubbletext.MultiSelectItem;

import java.util.UUID;

public class SampleItem implements MultiSelectItem {

    private final String mReadableName;
    private final String mId;

    public SampleItem(String readableName){
        mReadableName = readableName;
        mId = String.valueOf(readableName.hashCode());
    }

    public SampleItem(Parcel in) {
        mReadableName = in.readString();
        mId = in.readString();
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public String getReadableName() {
        return mReadableName;
    }

    @Override
    public String toString() {
        return mReadableName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mReadableName);
        dest.writeString(mId);
    }

    public static final Parcelable.Creator<SampleItem> CREATOR = new Parcelable.Creator<SampleItem>() {
        public SampleItem createFromParcel(Parcel in) {
            return new SampleItem(in);
        }

        public SampleItem[] newArray(int size) {
            return new SampleItem[size];
        }
    };
}
