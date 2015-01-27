package com.mycardboarddreams.autocompletebubbletext.samplelist;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SampleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        SampleEditText editText = (SampleEditText)findViewById(R.id.auto_text_complete);

        ListView list = editText.getListView();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        list.setLayoutParams(params);

        FrameLayout frame = (FrameLayout)findViewById(R.id.auto_list_container);
        frame.addView(list);
    }
}
