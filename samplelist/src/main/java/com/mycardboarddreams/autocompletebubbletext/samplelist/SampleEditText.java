package com.mycardboarddreams.autocompletebubbletext.samplelist;

import android.content.Context;
import android.util.AttributeSet;

import com.mycardboarddreams.autocompletebubbletext.MultiSelectEditText;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Hando on 1/26/2015.
 */
public class SampleEditText extends MultiSelectEditText<SampleItem> {

    List<SampleItem> sampleItems;

    public SampleEditText(Context context) {
        super(context);
    }

    public SampleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SampleEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void filterData(String lastCommaValue) {

        sampleItems = Arrays.asList(
                new SampleItem("Aaron LastName"),
                new SampleItem("Cameron Chimes"),
                new SampleItem("Tim Gibbons"),
                new SampleItem("Gary Styles")
        );

        clearAllItems();

        addAllItems(sampleItems);
    }
}
