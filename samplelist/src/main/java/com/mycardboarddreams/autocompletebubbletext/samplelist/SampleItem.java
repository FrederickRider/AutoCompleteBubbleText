package com.mycardboarddreams.autocompletebubbletext.samplelist;

import com.mycardboarddreams.autocompletebubbletext.MultiSelectItem;

import java.util.UUID;

public class SampleItem implements MultiSelectItem {

    private final String mReadableName;
    private final String mId;

    public SampleItem(String readableName){
        mReadableName = readableName;
        mId = String.valueOf(readableName.hashCode());
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
}
