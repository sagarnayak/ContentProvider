package com.sagar.android_projects.contentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.sagar.android_projects.contentprovider.adapter.Adapter;
import com.sagar.android_projects.contentprovider.contentprovider.ContentProvider;
import com.sagar.android_projects.contentprovider.pojo.DataForRecyclerview;

import java.util.ArrayList;

/**
 * created by SAGAR KUMAR NAYAK on 28 OCT 2017.
 * this is the app to demonstrate the use of content provider in android.
 * the full post can be found here -
 * content provider is very useful in case we want to share our app data with other apps in the device.
 * and also if want the database operations to take place in another thread and do not occupy the main
 * thread then the content provider is very useful.
 *
 * this app uses a SQLite database to store the data. the content provider access the SQLite database
 * in another thread and gives the result to the calling app or activity.
 * in this app i have implemented all the CRUD operations.
 * just look into the respective methods for more explanation.
 */
public class MainActivity extends AppCompatActivity implements Adapter.Callback {

    //views
    @SuppressWarnings("FieldCanBeLocal")
    private RadioButton radioButtonTableOne;
    @SuppressWarnings("FieldCanBeLocal")
    private RadioButton radioButtonTableTwo;
    private EditText editTextAdd;
    @SuppressWarnings("FieldCanBeLocal")
    private Button buttonAdd;
    private EditText editTextUpdateOrDelete;
    @SuppressWarnings("FieldCanBeLocal")
    private Button buttonUpdate;
    @SuppressWarnings("FieldCanBeLocal")
    private Button buttonDelete;
    private RecyclerView recyclerView;

    //content resolver to access the convert provider
    ContentResolver resolver;

    /*
    enum for the selected table. as i have used two tables for string data. and the ui have radio
    buttons for the two tables. this enum will save which table is selected.
     */
    enum CurrentSelection {
        TABLE_ONE,
        TABLE_TWO
    }

    //variable of the enum discussed above.
    private CurrentSelection currentSelection = CurrentSelection.TABLE_ONE;

    //arraylist for data of the recyclerview
    ArrayList<DataForRecyclerview> dataForRecyclerviews;
    //adapter
    Adapter adapter;
    //selected index in the recyclerview
    @SuppressWarnings("FieldCanBeLocal,unused")
    private int selectedIndex;
    //selected id of the element selected in the recyclerview the database.
    private String dbId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide the keyboard at start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //view
        radioButtonTableOne = findViewById(R.id.radio_button_table_one);
        radioButtonTableTwo = findViewById(R.id.radio_button_table_two);
        editTextAdd = findViewById(R.id.edittext_add);
        buttonAdd = findViewById(R.id.button_add);
        editTextUpdateOrDelete = findViewById(R.id.edittext_update_or_delete);
        buttonUpdate = findViewById(R.id.button_update);
        buttonDelete = findViewById(R.id.button_delete);
        recyclerView = findViewById(R.id.recyclerview);
        ////////////////////////////////////////////////////////////////////////////////////////////

        /*
        on check change of the radio button show the data from the respective table.
        also update the currently selected table in the enum.
         */
        radioButtonTableOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                    return;
                editTextUpdateOrDelete.setText("");
                resetRecyclerview();
                currentSelection = CurrentSelection.TABLE_ONE;
                getAllDataAndSetToRecyclerview();
            }
        });

        /*
        on check change of the radio button show the data from the respective table.
        also update the currently selected table in the enum.
         */
        radioButtonTableTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                    return;
                editTextUpdateOrDelete.setText("");
                resetRecyclerview();
                currentSelection = CurrentSelection.TABLE_TWO;
                getAllDataAndSetToRecyclerview();
            }
        });

        /*
        on click for the add data button.
        this will check if the data to add is blank or not. if yes then return. and if the data is
        present in the edittext then send the data to the add function.
        after finished from the add function do clear the text in the edittext.
         */
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextAdd.getText().length() == 0)
                    return;
                hideSoftKB();
                addValue(editTextAdd.getText().toString());
                editTextAdd.setText("");
            }
        });

        /*
        if user clicks on a item in the recyclerview and callback will send the selected id and index
        of the data from the adapter to the activity. then the data will be set to the edittext.
        user can then change the data and click the update button.
        this will update the data from the database with the new data provided by the user.
         */
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextUpdateOrDelete.getText().length() == 0)
                    return;
                hideSoftKB();
                update(editTextUpdateOrDelete.getText().toString());
                editTextUpdateOrDelete.setText("");
            }
        });

        /*
        if user clicks on a item in the recyclerview and callback will send the selected id and index
        of the data from the adapter to the activity. then the data will be set to the edittext.
        user can then click the delete button.
        this will delete the data from the database.
         */
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextUpdateOrDelete.getText().length() == 0)
                    return;
                hideSoftKB();
                delete();
                editTextUpdateOrDelete.setText("");
            }
        });

        //initialise the content resolver object.
        resolver = getContentResolver();

        //layout manager for the recyclerview.
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        //get the data from the database for the first time.
        getAllDataAndSetToRecyclerview();
    }

    /**
     * method to get the data from the content provider and set to the recyclerview in the activity.
     * as the structure of the 2 tables used are similar we are using a single pojo and adapter for
     * the same.
     * this will first check which is the table that is selected for the operation. this is done by
     * the enum we have created and the the radio button.
     * as the content provider works with uri. we have to create different  uris for both tables.
     * this will distinguish the READ operation for tables.
     * after the table uri is created send the request to the content resolver and we will get the
     * data form the database.
     * after getting the data set the data to the adapter of the recyclerview.
     */
    private void getAllDataAndSetToRecyclerview() {
        dataForRecyclerviews = new ArrayList<>();
        switch (currentSelection) {
            case TABLE_ONE:
                String[] projection = new String[]{ContentProvider.KEY_ID_TABLE_ONE, ContentProvider.KEY_NAME};
                Cursor cursor = resolver.query(Uri.parse(ContentProvider.URL + "/" +
                        ContentProvider.TABLE_ONE), projection, null, null, null);
                if (cursor == null)
                    return;
                if (cursor.moveToFirst()) {
                    do {
                        String id = cursor.getString(cursor.getColumnIndex(ContentProvider.KEY_ID_TABLE_ONE));
                        String name = cursor.getString(cursor.getColumnIndex(ContentProvider.KEY_NAME));
                        dataForRecyclerviews.add(new DataForRecyclerview(id, name));
                        adapter = new Adapter(dataForRecyclerviews, MainActivity.this);
                        recyclerView.setAdapter(adapter);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                break;
            case TABLE_TWO:
                String[] projectionn = new String[]{ContentProvider.KEY_ID_TABLE_TWO, ContentProvider.KEY_MOBILE};
                Cursor cursorr = resolver.query(Uri.parse(ContentProvider.URL + "/" +
                        ContentProvider.TABLE_TWO), projectionn, null, null, null);
                if (cursorr == null)
                    return;
                if (cursorr.moveToFirst()) {
                    do {
                        String id = cursorr.getString(cursorr.getColumnIndex(ContentProvider.KEY_ID_TABLE_TWO));
                        String mobile = cursorr.getString(cursorr.getColumnIndex(ContentProvider.KEY_MOBILE));
                        dataForRecyclerviews.add(new DataForRecyclerview(id, mobile));
                        adapter = new Adapter(dataForRecyclerviews, MainActivity.this);
                        recyclerView.setAdapter(adapter);
                    } while (cursorr.moveToNext());
                }
                cursorr.close();
                break;
        }
    }

    /**
     * method to add a new row to the database.
     * after getting a new value this method will distinguish what is the selected tables.
     * according to that it will create the uri.
     * along with the uri the new value is sent to content provider.
     * in return we can get a row id in which the new data is inserted into.
     * @param value value to insert
     */
    public void addValue(String value) {
        ContentValues values = new ContentValues();
        switch (currentSelection) {
            case TABLE_ONE:
                values.put(ContentProvider.KEY_NAME, value);
                Uri uri = resolver.insert(Uri.parse(ContentProvider.URL + "/" + ContentProvider.TABLE_ONE), values);
                if (uri != null) {
                    Toast.makeText(getBaseContext(), "New Contact Added" + uri, Toast.LENGTH_LONG).show();
                    getAllDataAndSetToRecyclerview();
                } else {
                    Toast.makeText(MainActivity.this, "New Contact not Added", Toast.LENGTH_LONG).show();
                }
                break;
            case TABLE_TWO:
                values.put(ContentProvider.KEY_MOBILE, value);
                uri = resolver.insert(Uri.parse(ContentProvider.URL + "/" + ContentProvider.TABLE_TWO), values);
                if (uri != null) {
                    Toast.makeText(getBaseContext(), "New Contact Added" + uri, Toast.LENGTH_LONG).show();
                    getAllDataAndSetToRecyclerview();
                } else {
                    Toast.makeText(MainActivity.this, "New Contact not Added", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * method to update the row in a table.
     * when user clicks on an element in the recyclerview ca callback from the adapter will send the
     * clicked item position and its id in the database to the activity.
     * this method will send those data to the content provider for updating the given row in the
     * selected table.
     * @param value new value to set
     */
    private void update(String value) {
        ContentValues values = new ContentValues();
        String where;
        String[] args;
        int rows;
        switch (currentSelection) {
            case TABLE_ONE:
                values.put(ContentProvider.KEY_NAME, value);
                where = ContentProvider.KEY_ID_TABLE_ONE + " = ?";
                args = new String[]{dbId};
                rows = resolver.update(Uri.parse(ContentProvider.URL + "/" + ContentProvider.TABLE_ONE), values, where, args);
                if (rows > 0)
                    getAllDataAndSetToRecyclerview();
                break;
            case TABLE_TWO:
                values.put(ContentProvider.KEY_MOBILE, value);
                where = ContentProvider.KEY_ID_TABLE_TWO + " = ?";
                args = new String[]{dbId};
                rows = resolver.update(Uri.parse(ContentProvider.URL + "/" + ContentProvider.TABLE_TWO), values, where, args);
                if (rows > 0)
                    getAllDataAndSetToRecyclerview();
                break;
        }
    }

    /**
     * method to delete an element selected from the recyclerview.
     * when user clicks on an element in the recyclerview ca callback from the adapter will send the
     * clicked item position and its id in the database to the activity.
     * after getting the selected of the element in the recyclerview we can just send that id to the
     * content provider and this will delete the row form the database,
     */
    private void delete() {
        String where;
        String[] args;
        int rows = 0;
        switch (currentSelection) {
            case TABLE_ONE:
                where = ContentProvider.KEY_ID_TABLE_ONE + " = ?";
                args = new String[]{dbId};
                rows = resolver.delete(Uri.parse(ContentProvider.URL + "/" + ContentProvider.TABLE_ONE), where, args);
                break;
            case TABLE_TWO:
                where = ContentProvider.KEY_ID_TABLE_TWO + " = ?";
                args = new String[]{dbId};
                rows = resolver.delete(Uri.parse(ContentProvider.URL + "/" + ContentProvider.TABLE_TWO), where, args);
                break;
        }
        if (rows > 0) {
            resetRecyclerview();
            getAllDataAndSetToRecyclerview();
        }
    }

    /**
     * method will just reset a new blank adapter to the recyclerview.
     */
    private void resetRecyclerview() {
        adapter = new Adapter(new ArrayList<DataForRecyclerview>(), MainActivity.this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * method to hide the soft keyboard.
     */
    private void hideSoftKB() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null)
                return;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * callback from the recyclerview adapter.
     * this will give the index and id of the selected item from the recyclerview.
     * @param position index of the selected item
     * @param id id of the selected item
     */
    @Override
    public void clickedOnItem(int position, String id) {
        selectedIndex = position;
        editTextUpdateOrDelete.setText(dataForRecyclerviews.get(position).getValue());
        editTextUpdateOrDelete.requestFocus();
        editTextUpdateOrDelete.setSelection(editTextUpdateOrDelete.getText().length());
        dbId = id;
    }
}
