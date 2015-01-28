package com.mycardboarddreams.autocompletebubbletext;

import android.text.style.ImageSpan;

public class ImageSpanContainer {
    private int mSpanStart;
    private int mSpanEnd;
    private ImageSpan mSpan;

    public ImageSpanContainer(ImageSpan span, int start, int end) {
        mSpan = span;
        mSpanStart = start;
        mSpanEnd = end;
    }

    public ImageSpan getSpan() {
        return mSpan;
    }

    public int getSpanStart() {
        return mSpanStart;
    }

    public int getSpanEnd() {
        return mSpanEnd;
    }
}
