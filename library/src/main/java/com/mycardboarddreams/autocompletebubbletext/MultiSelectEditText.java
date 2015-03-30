package com.mycardboarddreams.autocompletebubbletext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectEditText<T extends MultiSelectItem> extends EditText {
    private static final String TAG = MultiSelectEditText.class.getSimpleName();

    List<String> checkedIds = new ArrayList<String>();

    private int bubbleDrawableResource;
    private ListView listView;
    private ArrayAdapter<T> adapter;

    private List<T> originalItems;

    public MultiSelectEditText(Context context) {
        super(context);
    }

    public MultiSelectEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiSelectEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    protected void init(){
        setInitialComponents();

        setFreezesText(true);

        final BubbleWatcher watcher = new BubbleWatcher(this, TextKeyListener.Capitalize.NONE, false);
        setKeyListener(watcher);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final T item = (T) parent.getItemAtPosition(position);
                if (listView.isItemChecked(position)) {
                    addCheckedItem(item);
                } else {
                    removeCheckedItem(item);
                }
                setString();

                updateListViewCheckState();
            }
        });

        updateFilteredItems("");

        setMinHeight(getPaddingBottom() + getPaddingTop() + calculateLineHeight());
    }

    private void updateFilteredItems(String lastValue){
        if(originalItems != null) {
            List<T> filtered = filterData(originalItems, lastValue);

            adapter.clear();

            for (T item : filtered) {
                adapter.add(item);
            }

            adapter.notifyDataSetChanged();
        }
    }

    private void setInitialComponents() {
        listView = onCreateListView();

        if(listView == null)
            throw new IllegalStateException("The ListView cannot be null");

        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        adapter = onCreateAdapter();

        if(adapter == null)
            throw new IllegalStateException("The Adapter cannot be null");

        listView.setAdapter(adapter);
        bubbleDrawableResource = getBubbleResource();

        if(bubbleDrawableResource == 0)
            throw new IllegalStateException("The resource drawable for the bubble cannot be null");
    }

    private String getLastCommaValue() {
        String fullText = getText().toString();

        if(TextUtils.isEmpty(fullText))
            return "";

        final String[] commaDelineated = fullText.split(getDelimiter().trim());

        Editable spannedText = getEditableText();

        ImageSpan[] spans = spannedText.getSpans(0, fullText.length(), ImageSpan.class);
        String lastString = commaDelineated[commaDelineated.length - 1].trim();

        if(spans.length == 0)
            return lastString;

        int spanEndPoint = spannedText.getSpanEnd(spans[spans.length - 1]);
        if(spanEndPoint == spannedText.length() - 1)
            return "";

        return lastString;
    }

    /**
     * Override this the replace the filtering of list items.
     * @param originalItems Original full list of items
     * @param lastCommaValue text after the last delimiter
     * @return a filtered list of the same items
     */
    protected List<T> filterData(final List<T> originalItems, final String lastCommaValue){

        if(TextUtils.isEmpty(lastCommaValue)){
            return originalItems;
        }

        List<T> filtered = new ArrayList<T>();
        for(T item : originalItems){
            if(item.getReadableName().toLowerCase().startsWith(lastCommaValue.toLowerCase()))
                filtered.add(item);
        }

        return filtered;
    }

    /**
     * Override this to customize the adapter
     * @return a custom ArrayAdapter
     */
    protected ArrayAdapter<T> onCreateAdapter(){
        return new ArrayAdapter<T>(getContext(), getListItemLayout()){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setBackgroundResource(R.drawable.list_item_background);
                return v;
            }
        };
    }

    /**
     * Override this to customize the ListView
     * @return a custom ListView
     */
    protected ListView onCreateListView(){
        ListView lv = new ListView(getContext());
        lv.setSelector(R.drawable.selector_list_checked);
        return lv;
    }

    protected int getListItemLayout(){
        return android.R.layout.simple_list_item_1;
    }

    /**
     * Override this to customize the drawable resource behind the individual items
     * @return the resource id of the bubble drawable
     */
    protected int getBubbleResource(){
        return R.drawable.sample_bubble;
    }

    protected int calculateLineHeight(){
        Drawable bubbleDrawable = getResources().getDrawable(getBubbleResource());

        int lineHeight = getLineHeight();

        Rect rect = new Rect();
        if(bubbleDrawable.getPadding(rect)){
            return lineHeight + rect.top + rect.bottom;
        }
        return lineHeight;
    }

    /**
     * Fetch the ListView associated with this MultiSelectEditText, that was created in onCreateListView()
     * @return the ListView associated with this MultiSelectEditText
     */
    public final ListView getListView(){
        return listView;
    }


    private void updateListViewCheckState() {
        final int count = adapter.getCount();

        for (int i = 0; i < count; i++){
            final T listItem = adapter.getItem(i);
            if (listItem != null) {
                final String listChatId = listItem.getId();
                if (checkedIds.contains(listChatId)) {
                    listView.setItemChecked(i, true);
                } else {
                    listView.setItemChecked(i, false);
                }
            }
        }
    }

    public void addCheckedItem(T item){
        final String id = item.getId();
        checkedIds.add(id);
    }

    public void removeCheckedItem(T item){
        final String id = item.getId();
        removeCheckedItem(id);
    }

    public void removeCheckedItem(String id){
        checkedIds.remove(id);
    }

    public void addAllItems(List<T> allItems){
        originalItems = allItems;
        updateFilteredItems(getLastCommaValue());
    }

    public void clearAllItems(){
        originalItems.clear();
    }

    public void removeItem(String itemName){

        List<T> newOriginalItems = new ArrayList<T>();

        for(int i = 0; i < originalItems.size(); i++) {
            String name = originalItems.get(i).getReadableName();

            if(!TextUtils.equals(name, itemName))
                newOriginalItems.add(originalItems.get(i));
        }

        originalItems = newOriginalItems;

        updateFilteredItems(getLastCommaValue());
    }

    public int getCheckedItemsCount(){
        return listView.getCheckedItemPositions().size();
    }

    /**
     * Override this to choose a different delimiter between the items
     * @return the delimiter string
     */
    protected String getDelimiter(){
        return ", ";
    }

    public void setString(){
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        for (String itemId : checkedIds) {
            final T item = findById(itemId);
            String name = item.getReadableName();

            TextView tv = createItemTextView(name);
            tv.setTextColor(getResources().getColor(android.R.color.black));

            BitmapDrawable bd = convertViewToDrawable(tv);

            sb.append(name);

            final int start = sb.length() - name.length();
            final int end = sb.length();
            sb.setSpan(new BubbleSpan(bd, itemId), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            sb.append(getDelimiter());
        }
        setText(sb);

        int position = getText().length() - 1;
        if (position < 0) position = 0;
        setSelection(position);

        updateFilteredItems(getLastCommaValue());
    }

    private T findById(String id){
        if(originalItems == null || originalItems.size() == 0)
            return null;

        for(T item : originalItems){
            if(TextUtils.equals(id, item.getId()))
                return item;
        }
        return null;
    }

    protected TextView createItemTextView(String text){
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setBackgroundResource(bubbleDrawableResource);
        return tv;
    }

    protected BitmapDrawable convertViewToDrawable(View textView) {
        int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        textView.measure(spec, spec);
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(textView.getMeasuredWidth(), textView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate(-textView.getScrollX(), -textView.getScrollY());
        textView.draw(c);
        textView.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = textView.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        textView.destroyDrawingCache();
        BitmapDrawable bd = new BitmapDrawable(getContext().getResources(), viewBmp);
        bd.setBounds(0, 0, viewBmp.getWidth(), viewBmp.getHeight());

        return bd;
    }

    private class BubbleWatcher extends TextKeyListener implements TextWatcher {
        private final ArrayList<ImageSpanContainer> mBubblesToRemove = new ArrayList<ImageSpanContainer>();

        public BubbleWatcher(MultiSelectEditText editText, Capitalize cap, boolean autotext) {
            super(cap, autotext);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (count > 0) {
                int end = start + count;
                Editable message = getEditableText();
                ImageSpan[] list = message.getSpans(start, end, ImageSpan.class);

                for (ImageSpan span : list) {
                    int spanStart = message.getSpanStart(span);
                    int spanEnd = message.getSpanEnd(span);
                    if ((spanStart < end) && (spanEnd > start)) {
                        mBubblesToRemove.add(new ImageSpanContainer(span, spanStart, spanEnd));
                    }
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final String lastCommaValue = getLastCommaValue();
            updateFilteredItems(lastCommaValue);

            Editable message = getEditableText();

            List<ImageSpanContainer> removeList = new ArrayList<ImageSpanContainer>();
            for (ImageSpanContainer container : mBubblesToRemove){
                ImageSpan span = container.getSpan();
                int spanStart =  container.getSpanStart();
                int spanEnd = container.getSpanEnd();

                if (message.length() >= spanEnd && spanStart != spanEnd){
                    String id = span.getSource();
                    removeCheckedItem(id);
                    setString();
                    removeList.add(container);
                }
            }

            mBubblesToRemove.removeAll(removeList);
        }

        @Override
        public void afterTextChanged(Editable s) {
            updateListViewCheckState();
        }

        @Override
        public void onSpanAdded(Spannable text, Object what, int start, int end) {
        }

        @Override
        public void onSpanRemoved(Spannable text, Object what, int start, int end) {
            if (what instanceof ImageSpan){
                String readableName = ((ImageSpan) what).getSource();
                removeItem(readableName);
            }
        }

        @Override
        public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
        }

        @Override
        public int getInputType() {
            return InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        }
    }

    private static class BubbleSpan extends ImageSpan {

        public BubbleSpan(Drawable d, String source) {
            super(d, source);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            if(text instanceof Spannable){
                Spannable spanned = ((Spannable)text);
                ImageSpan[] includingSpans = spanned.getSpans(0, end, ImageSpan.class);
                if(includingSpans.length != 0){
                    ImageSpan lastSpan = includingSpans[includingSpans.length-1];
                    int endPoint = spanned.getSpanEnd(lastSpan);
                    if(end == endPoint)
                        super.draw(canvas, text, start, end, x, top, y, bottom, paint);
                }
            }
            else
                super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        }
    }

}
