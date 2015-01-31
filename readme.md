## Notes

AutoCompleteBubbleText allows you to add and remove items from an EditText using a drawable as a background.

The benefit of AutoCompleteBubbleText is that you can position the ListView anywhere in the layout instead of just under the EditText.

You can also use the autocomplete filtering function of the EditText to filter items in the list.

An example use is in contact lists that need to be filtered and keep checked states.

![Sample image 1](https://github.com/FrederickRider/AutoCompleteBubbleText/blob/master/images/Screenshot_1.png)
![Sample image 2](https://github.com/FrederickRider/AutoCompleteBubbleText/tree/master/images/Screenshot_2.png)

## Usage

The sample activity shows a basic usage.

````````````````
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

### You must override:

getListView() - Once the ListView is created, you must fetch it and add it as a child of some other layout.


## Customization

You can customize most parts of the view.

### You may also override the following methods:

onCreateListView() - To return a custom ListView.

onCreateAdapter() - The return a custom Adapter, which (currently) must be an ArrayAdapter.

getBubbleResource() - The resource representing the bubble drawable.

getDelimiter() - Override this to return a different delimiter string.

filterData(String lastCommaValue) - Gives you the last value after the delimiter (default is ','), return the filtered results.
