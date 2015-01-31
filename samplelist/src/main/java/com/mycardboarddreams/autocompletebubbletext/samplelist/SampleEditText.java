package com.mycardboarddreams.autocompletebubbletext.samplelist;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.mycardboarddreams.autocompletebubbletext.MultiSelectEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SampleEditText extends MultiSelectEditText<SampleItem> {

    List<SampleItem> sampleItems;

    public SampleEditText(Context context) {
        super(context);
        addSampleItems();
    }

    public SampleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addSampleItems();
    }

    public SampleEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addSampleItems();
    }

    protected void addSampleItems() {

        sampleItems = Arrays.asList(
                new SampleItem("Aaron LastName"),
                new SampleItem("Cameron Chimes"),
                new SampleItem("Tim Gibbons"),
                new SampleItem("Gary Styles")
        );

        addAllItems(sampleItems);
    }
}
