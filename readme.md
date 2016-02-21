## Notes

AutoCompleteBubbleText allows you to add and remove items from an EditText using a drawable as a background.

The benefit of AutoCompleteBubbleText is that you can position the ListView anywhere in the layout instead of just under the EditText.

You can also use the autocomplete filtering function of the EditText to filter items in the list.

An example use is in contact lists that need to be filtered and keep checked states.

![Sample image 1](https://github.com/FrederickRider/AutoCompleteBubbleText/blob/master/images/Screenshot_1.png)

You can also filter the list by text after the last delimiter:

![Sample image 2](https://github.com/FrederickRider/AutoCompleteBubbleText/tree/master/images/Screenshot_2.png)

## Usage

The sample activity shows a basic usage.

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.mycardboarddreams.autocompletebubbletext.MultiSelectEditText
        android:id="@+id/auto_text_complete"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textColor="@android:color/black"
        android:padding="5dp"
        android:background="#FFEEEEEE"/>

    <!-- Layout where the list will go -->
    <FrameLayout
        android:id="@+id/auto_list_container"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="#FF666666"/>

</LinearLayout>
````````````````

Create some object that implements MultiSelectItem

```java
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
```

Once you've set up the layout, pull the ListView out of the EditText, and put it wherever in the layout you like:

```java
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sample);

    //Find the MultiSelectEditText in the layout
    MultiSelectEditText editText = (MultiSelectEditText)findViewById(R.id.auto_text_complete);

    /**
     * Add some sample items
     * The type of item can be anything that implements MultiSelectItem
     */
    List<SampleItem> sampleItems = Arrays.asList(
            new SampleItem("Aaron LastName"),
            new SampleItem("Cameron Chimes"),
            new SampleItem("Tim Gibbons"),
            new SampleItem("Gary Styles"),
            new SampleItem("Bart Thompson"),
            new SampleItem("Abagail B.D.E.")
    );

    editText.addAllItems(sampleItems);

    //Pull out the ListView from the MultiSelectEditText
    ListView list = editText.getListView();

    //Add it to a ViewGroup somewhere else in the layout
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
```

### You must call getListView():

```java
/**
 * Once the ListView is created, you must fetch
 * it and add it as a child of some other layout.
 */
ListView getListView()
```


## Customization

You can customize most parts of the view.

### You may also override the following methods:

```java
/**
 * Override this to return a custom ListView.
 */
ListView onCreateListView()
```

```java
/**
 * Override this to return a custom Adapter,
 * which (currently) must be an ArrayAdapter.
 */
ArrayAdapter<T> onCreateAdapter()

/**
 * Override this to return the resource
 * representing the bubble drawable.
 */
int getBubbleResource()


/**
 * Override this to return a different delimiter string.
 */
String getDelimiter()

/**
 * Override this to return filtered results given the last value
 * after the delimiter (default is ','), lastCommaValue.
 */
filterData(String lastCommaValue)

/**
 * Call this to pass a listener for clicks on bubbles
 * Pass in a class that implements BubbleClickListener
 */
setBubbleClickListener(BubbleClickListener<T> listener)
```
