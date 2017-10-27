package com.sagar.android_projects.contentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.sagar.android_projects.contentprovider.contentprovider.ContentProvider;

public class MainActivity extends AppCompatActivity {

    static final Uri CONTENT_URL =
            Uri.parse("content://com.csmpl.android.contentprovider.ContactProvider/cpcontacts");
    ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addName();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void addName() {

        resolver = getContentResolver();

        String name = "sagar";
        ContentValues values = new ContentValues();
        values.put(ContentProvider.KEY_NAME, name);

        Uri uri = resolver.insert(Uri.parse(ContentProvider.URL + "/" + ContentProvider.TABLE_ONE), values);

        if (uri != null) {
            Toast.makeText(getBaseContext(), "New Contact Added" + uri, Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(MainActivity.this, "New Contact not Added", Toast.LENGTH_LONG)
                    .show();
        }

        String mobile = "8093329914";
        values = new ContentValues();
        values.put(ContentProvider.KEY_MOBILE, mobile);

        uri = resolver.insert(Uri.parse(ContentProvider.URL + "/" + ContentProvider.TABLE_TWO), values);

        if (uri != null) {
            Toast.makeText(getBaseContext(), "New Contact Added" + uri, Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(MainActivity.this, "New Contact not Added", Toast.LENGTH_LONG)
                    .show();
        }

//        getContacts();
    }

    public void getContacts() {

        // Projection contains the columns we want
        String[] projection = new String[]{ContentProvider.KEY_ID_TABLE_ONE, ContentProvider.KEY_NAME};

        // Pass the URL, projection and I'll cover the other options below
        Cursor cursor = resolver.query(ContentProvider.CONTENT_URL, projection, null, null, null);

        String contactList = "";

        if (cursor == null)
            return;

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContentProvider.KEY_ID_TABLE_ONE));
                String name = cursor.getString(cursor.getColumnIndex(ContentProvider.KEY_NAME));
                Log.i("sfgdsfgb", "" + id + ":" + name);
                contactList = contactList + id + " : " + name + "\n";
            } while (cursor.moveToNext());
        }

        cursor.close();

    }
}
