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
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.TextKeyListener;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiSelectEditText<T extends MultiSelectItem> extends EditText {
    private static final String TAG = MultiSelectEditText.class.getSimpleName();

    private int bubbleDrawableResource;
    private ListView listView;
    private ArrayAdapter<T> adapter;

    protected BubbleClickListener<T> listener;

    final private List<T> originalItems = new ArrayList<T>();
    final private Set<String> checkedIds = new HashSet<String>();

    private BubbleWatcher watcher;

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
        setMovementMethod(LinkMovementMethod.getInstance());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                if(checked.get(position))
                    addItemChecked(adapter.getItem(position));
                else
                    removeItemChecked(adapter.getItem(position));

                setString();
            }
        });

        watcher = new BubbleWatcher();

        addTextChangedListener(watcher);

        setMinHeight(getPaddingBottom() + getPaddingTop() + calculateLineHeight());
    }

    private void addItemChecked(T item){
        checkedIds.add(item.getId());
    }

    private void removeItemChecked(T item){
        checkedIds.remove(item.getId());
    }

    private void setCheckedItems() {
        for(int i = 0; i < adapter.getCount(); i++){
            T item = adapter.getItem(i);
            if(checkedIds.contains(item.getId()))
                listView.setItemChecked(i, true);
            else
                listView.setItemChecked(i, false);
        }
    }

    private void updateFilteredItems(String lastValue){
        if(originalItems != null) {
            List<T> filtered = filterData(originalItems, lastValue);

            adapter.clear();

            for (T item : filtered) {
                adapter.add(item);
            }

            adapter.notifyDataSetChanged();

            setCheckedItems();
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

    private String getLastDelineatedValue() {
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

    public void addAllItems(List<T> allItems){
        clearAllItems();

        originalItems.addAll(allItems);
        adapter.addAll(allItems);

        updateFilteredItems(getLastDelineatedValue());
    }

    public void clearAllItems(){
        originalItems.clear();
        adapter.clear();
    }

    public void removeItem(String itemName){

        for(int i = 0; i < adapter.getCount(); i++) {
            T item = adapter.getItem(i);

            if(TextUtils.equals(item.getId(), itemName)){
                listView.setItemChecked(i, false);
                removeItemChecked(item);
            }
        }

        setString();

        updateFilteredItems(getLastDelineatedValue());
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

    /**
     * Call this to set a listener for bubble clicks
     * @param listener
     */
    public void setBubbleClickListener(BubbleClickListener<T> listener){
        this.listener = listener;
    }

    public void setString(){
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        SparseBooleanArray checked = listView.getCheckedItemPositions();

        for (int i = 0; i < adapter.getCount(); i++) {
            if(!checked.get(i))
                continue;

            final T item = adapter.getItem(i);
            String name = item.getReadableName();

            TextView tv = createItemTextView(name);
            tv.setTextColor(getCurrentTextColor());

            BitmapDrawable bd = convertViewToDrawable(tv);

            sb.append(name);

            final int start = sb.length() - name.length();
            final int end = sb.length();
            sb.setSpan(new BubbleSpan(bd, item.getId()), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ClickableSpan(){

                @Override
                public void onClick(View view) {
                    if(listener != null) listener.onClick(item);
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            sb.append(getDelimiter());
        }
        setText(sb);

        int position = getText().length() - 1;
        if (position < 0) position = 0;
        setSelection(position);

        Editable editable = getText();
        editable.setSpan(watcher, 0, editable.length(), 0);

        updateFilteredItems(getLastDelineatedValue());
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

    private class BubbleWatcher implements SpanWatcher, TextWatcher {

        @Override
        public void onSpanAdded(Spannable text, Object what, int start, int end) {
        }

        @Override
        public void onSpanRemoved(Spannable text, Object what, int start, int end) {
        }

        @Override
        public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
            if (what instanceof ImageSpan){
                if(ostart != nstart && (ostart - nstart) == (oend - nend))
                    return;

                String readableName = ((ImageSpan) what).getSource();
                removeItem(readableName);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateFilteredItems(getLastDelineatedValue());
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

    public interface BubbleClickListener<T>{
        void onClick(T item);
    }

}
