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

public class MainActivity extends AppCompatActivity implements Adapter.Callback {

    private RadioButton radioButtonTableOne;
    private RadioButton radioButtonTableTwo;
    private EditText editTextAdd;
    private Button buttonAdd;
    private EditText editTextUpdateOrDelete;
    private Button buttonUpdate;
    private Button buttonDelete;
    private RecyclerView recyclerView;

    ContentResolver resolver;

    enum CurrentSelection {
        TABLE_ONE,
        TABLE_TWO
    }

    private CurrentSelection currentSelection = CurrentSelection.TABLE_ONE;

    ArrayList<DataForRecyclerview> dataForRecyclerviews;
    Adapter adapter;
    private int selectedIndex;
    private String dbId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        radioButtonTableOne = (RadioButton) findViewById(R.id.radio_button_table_one);
        radioButtonTableTwo = (RadioButton) findViewById(R.id.radio_button_table_two);
        editTextAdd = (EditText) findViewById(R.id.edittext_add);
        buttonAdd = (Button) findViewById(R.id.button_add);
        editTextUpdateOrDelete = (EditText) findViewById(R.id.edittext_update_or_delete);
        buttonUpdate = (Button) findViewById(R.id.button_update);
        buttonDelete = (Button) findViewById(R.id.button_delete);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

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

        resolver = getContentResolver();

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        getAllDataAndSetToRecyclerview();
    }

    private void getAllDataAndSetToRecyclerview() {
        dataForRecyclerviews = new ArrayList<>();
        switch (currentSelection) {
            case TABLE_ONE:
                String[] projection = new String[]{ContentProvider.KEY_ID_TABLE_ONE, ContentProvider.KEY_NAME};
                Cursor cursor = resolver.query(Uri.parse(ContentProvider.URL + "/" + ContentProvider.TABLE_ONE), projection, null, null, null);
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
                Cursor cursorr = resolver.query(Uri.parse(ContentProvider.URL + "/" + ContentProvider.TABLE_TWO), projectionn, null, null, null);
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

    private void resetRecyclerview() {
        adapter = new Adapter(new ArrayList<DataForRecyclerview>(), MainActivity.this);
        recyclerView.setAdapter(adapter);
    }

    private void hideSoftKB() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void clickedOnItem(int position, String id) {
        selectedIndex = position;
        editTextUpdateOrDelete.setText(dataForRecyclerviews.get(position).getValue());
        editTextUpdateOrDelete.requestFocus();
        editTextUpdateOrDelete.setSelection(editTextUpdateOrDelete.getText().length());
        dbId = id;
    }
}
