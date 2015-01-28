package com.mycardboarddreams.autocompletebubbletext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public abstract class MultiSelectEditText<T extends MultiSelectItem> extends EditText {
    private static final String TAG = MultiSelectEditText.class.getSimpleName();

    private final Hashtable<String, T> mSelectedItems = new Hashtable<String, T>();

    private int bubbleDrawableResource;
    private ListView listView;
    private String fullText;
    private ArrayAdapter<T> adapter;

    public MultiSelectEditText(Context context) {
        super(context);
        init();
    }

    public MultiSelectEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiSelectEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init(){
        setInitialComponents();

        final BubbleWatcher watcher = new BubbleWatcher(this, TextKeyListener.Capitalize.NONE, false);
        setKeyListener(watcher);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fullText = s.toString();
                final String lastCommaValue = getLastCommaValue();
                filterData(lastCommaValue);
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateListViewCheckState();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final T item = (T) parent.getItemAtPosition(position);
                if (listView.isItemChecked(position)) {
                    addSelectedItem(item);
                } else {
                    removeSelectedItem(item);
                }
                setString();

                updateListViewCheckState();
            }
        });

        filterData("");
        adapter.notifyDataSetChanged();
    }

    private void setInitialComponents() {
        listView = new ListView(getContext());

        if(listView == null)
            throw new IllegalStateException("The ListView cannot be null");

        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        adapter = onCreateAdapter();

        if(adapter == null)
            throw new IllegalStateException("The Adapter cannot be null");

        listView.setAdapter(adapter);
        bubbleDrawableResource = getBubbleLayout();

        if(bubbleDrawableResource == 0)
            throw new IllegalStateException("The resource drawable for the bubble cannot be null");
    }

    private String getLastCommaValue() {
        final String[] commaDelineated = fullText.split(",");
        return commaDelineated.length == 0 ? "" : commaDelineated[commaDelineated.length - 1].trim();
    }

    protected abstract void filterData(String lastCommaValue);

    protected ArrayAdapter<T> onCreateAdapter(){
        return new ArrayAdapter<T>(getContext(), getListItemLayout());
    }

    protected int getListItemLayout(){
        return android.R.layout.simple_list_item_1;
    }

    protected int getBubbleLayout(){
        return R.drawable.contact_bubble;
    }

    public ListView getListView(){
        return listView;
    }


    private void updateListViewCheckState() {
        final int count = adapter.getCount();
        final Hashtable<String, T> checkedContacts = getCheckedItems();
        Set<String> ids = checkedContacts.keySet();

        for (int i = 0; i < count; i++){
            final T listItem = adapter.getItem(i);
            if (listItem != null) {
                final String listChatId = listItem.getId();
                if (ids.contains(listChatId)) {
                    listView.setItemChecked(i, true);
                } else {
                    listView.setItemChecked(i, false);
                }
            }
        }
    }

    public void addSelectedItem(T item){
        final String chatId = item.getId();
        mSelectedItems.put(chatId, item);
    }

    public void removeSelectedItem(T item){
        final String id = item.getId();
        mSelectedItems.remove(id);
    }

    public void addAllItems(List<T> allItems){

        for(T item : allItems)
            adapter.add(item);
    }

    public void clearAllItems(){
        adapter.clear();
    }

    public void removeItem(String itemName){

        for(int i = 0; i < adapter.getCount(); i++) {
            String name = adapter.getItem(i).getReadableName();

            if(TextUtils.equals(name, itemName))
                removeSelectedItem(adapter.getItem(i));
        }

        adapter.notifyDataSetChanged();
    }

    public int getCheckedItemsCount(){
        return mSelectedItems.size();
    }

    public void setString(){
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        for (String chatId : mSelectedItems.keySet()) {
            final T item = mSelectedItems.get(chatId);
            String name = item.getReadableName();

            TextView tv = createItemTextView(name);
            tv.setTextColor(getResources().getColor(android.R.color.black));

            BitmapDrawable bd = convertViewToDrawable(tv);
            bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());

            sb.append(name);

            final int start = sb.length() - name.length();
            final int end = sb.length();
            sb.setSpan(new ImageSpan(bd, chatId), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            sb.append(", ");
        }
        setText(sb);

        int position = getText().length() - 1;
        if (position < 0) position = 0;
        setSelection(position);
    }

    public Hashtable<String, T> getCheckedItems(){
        return mSelectedItems;
    }

    public TextView createItemTextView(String text){
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setBackgroundResource(bubbleDrawableResource);
        return tv;
    }

    public BitmapDrawable convertViewToDrawable(View textView) {
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
        return new BitmapDrawable(getContext().getResources(), viewBmp);
    }

    public static class BubbleWatcher extends TextKeyListener implements TextWatcher {
        private final ArrayList<ImageSpanContainer> mBubblesToRemove = new ArrayList<ImageSpanContainer>();
        private final MultiSelectEditText editText;

        public BubbleWatcher(MultiSelectEditText editText, Capitalize cap, boolean autotext) {
            super(cap, autotext);
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (count > 0) {
                int end = start + count;
                Editable message = editText.getEditableText();
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
            Editable message = editText.getEditableText();

            List<ImageSpanContainer> removeList = new ArrayList<ImageSpanContainer>();
            for (ImageSpanContainer container : mBubblesToRemove){
                ImageSpan span = container.getSpan();
                int spanStart =  container.getSpanStart();
                int spanEnd = container.getSpanEnd();

                if (message.length() >= spanEnd && spanStart != spanEnd){
                    String readableName = span.getSource();
                    editText.removeItem(readableName);
                    editText.setString();
                    removeList.add(container);
                }
            }

            mBubblesToRemove.removeAll(removeList);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void onSpanAdded(Spannable text, Object what, int start, int end) {
        }

        @Override
        public void onSpanRemoved(Spannable text, Object what, int start, int end) {
            if (what instanceof ImageSpan){
                String readableName = ((ImageSpan) what).getSource();
                editText.removeItem(readableName);
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
}
