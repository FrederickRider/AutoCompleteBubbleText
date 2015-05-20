package com.mycardboarddreams.autocompletebubbletext.samplelist;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mycardboarddreams.autocompletebubbletext.MultiSelectEditText;

import java.util.Arrays;
import java.util.List;

public class SampleActivity extends Activity {

    private static final String TAG = SampleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        MultiSelectEditText editText = (MultiSelectEditText)findViewById(R.id.auto_text_complete);

        //Add some sample items
        List<SampleItem> sampleItems = Arrays.asList(
                new SampleItem("Aaron LastName"),
                new SampleItem("Cameron Chimes"),
                new SampleItem("Tim Gibbons"),
                new SampleItem("Gary Styles"),
                new SampleItem("Bart Thompson"),
                new SampleItem("Abagail B.D.E.")
        );

        editText.addAllItems(sampleItems);

        //Get the ListView associated with the MultiSelectEditText
        ListView list = editText.getListView();

        //Add it to a FrameLayout somewhere in the activity
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        list.setLayoutParams(params);

        FrameLayout frame = (FrameLayout)findViewById(R.id.auto_list_container);
        frame.addView(list);

        //Set a listener on bubble clicks
        editText.setBubbleClickListener(new MultiSelectEditText.BubbleClickListener<SampleItem>() {

            @Override
            public void onClick(SampleItem item) {
                Log.d(TAG, "Item: " + item.getReadableName());
            }
        });
    }
}
