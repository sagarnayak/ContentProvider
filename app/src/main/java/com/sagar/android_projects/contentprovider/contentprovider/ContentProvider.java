package com.sagar.android_projects.contentprovider.contentprovider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by sagar on 10/27/2017.
 * this is the custom content provider created for the application.
 * it has a sqlite db with 2 tables from which it gets its data. the tables are named table one &
 * table two. both have a simple structure. onw with id and name and another with id and mobile number.
 * the main aim is to show a demo how to use the content provider in an android app.
 * it uses two tables TABLE ONE and TABLE TWO.
 * both have same structure. one has the id and name and another has id and mobile number.
 * the aim is to show working with multiple tables in content provider.
 * the SQLite database is used by the content provider for storing the data.
 */
public class ContentProvider extends android.content.ContentProvider {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    params for the sqlite.
     */
    private SQLiteDatabase sqlDB;
    static final String DATABASE_NAME = "sqlite_database";
    static final int DATABASE_VERSION = 1;

    //table one
    public static final String TABLE_ONE = "table_one";
    public static final String KEY_ID_TABLE_ONE = "ID";
    public static final String KEY_NAME = "NAME";
    static final String CREATE_DB_TABLE_ONE = " CREATE TABLE " + TABLE_ONE
            + " (" + KEY_ID_TABLE_ONE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT NOT NULL"
            + ");";

    //table two
    public static final String TABLE_TWO = "table_two";
    public static final String KEY_ID_TABLE_TWO = "ID";
    public static final String KEY_MOBILE = "MOBILE";
    static final String CREATE_DB_TABLE_TWO = " CREATE TABLE " + TABLE_TWO
            + " (" + KEY_ID_TABLE_TWO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_MOBILE + " TEXT NOT NULL"
            + ");";
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    params for the content provider
     */
    static final String PROVIDER_NAME = "com.sagar.android_projects.contentprovider";

    public static final String URL = "content://" + PROVIDER_NAME + "/database";
    public static final Uri CONTENT_URL = Uri.parse(URL);
    static final int URI_TABLE_ONE = 1;
    static final int URI_TABLE_TWO = 2;

    /*
    uri matcher us used to distinguish between different uri sent to the content provider and perform
    the intended task.
     */
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "database/" + TABLE_ONE, URI_TABLE_ONE);
        uriMatcher.addURI(PROVIDER_NAME, "database/" + TABLE_TWO, URI_TABLE_TWO);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        sqlDB = dbHelper.getWritableDatabase();
        return sqlDB != null;
    }

    /**
     * the method to override when using ContentProvider.
     * this will be fired when the query() is called.
     *
     * @param uri           uri for the operation
     * @param projection    rows to select
     * @param selection     selection params (where clause)
     * @param selectionArgs values for the where args
     * @param sortOrder     order by
     * @return cursor for the result
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        HashMap<String, String> values = null;
        switch (uriMatcher.match(uri)) {
            case URI_TABLE_ONE:
                queryBuilder.setTables(TABLE_ONE);
                break;
            case URI_TABLE_TWO:
                queryBuilder.setTables(TABLE_TWO);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        queryBuilder.setProjectionMap(values);
        Cursor cursor = queryBuilder.query(sqlDB, projection, selection, selectionArgs,
                null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * the method to override when using ContentProvider.
     * this will be fired when the getType() is called.
     * it is used when we are using any MIME type operation in our code.
     * for this example we are not using any MIME type related operation.
     *
     * @param uri uri
     * @return type
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_TABLE_ONE:
                return "vnd.android.cursor.dir/database";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    /**
     * the method to override when using ContentProvider.
     * this will be fired when the insert() is called.
     *
     * @param uri    uri for the operation
     * @param values values to insert
     * @return row of insertion
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case URI_TABLE_ONE:
                return insertToTableOne(values);
            case URI_TABLE_TWO:
                return insertToTableTwo(values);
        }
        return null;
    }

    /**
     * the method to override when using ContentProvider.
     * this will be fired when the delete() is called.
     *
     * @param uri           uri for the operation
     * @param selection     selection params (where clause)
     * @param selectionArgs values for the selection args
     * @return number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;
        switch (uriMatcher.match(uri)) {
            case URI_TABLE_ONE:
                rowsDeleted = sqlDB.delete(TABLE_ONE, selection, selectionArgs);
                break;
            case URI_TABLE_TWO:
                rowsDeleted = sqlDB.delete(TABLE_TWO, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * the method to override when using ContentProvider.
     * this will be fired when the update() is called.
     *
     * @param uri           uri for the operation
     * @param values        values to update
     * @param selection     where clause
     * @param selectionArgs values for the where clause
     * @return number of rows updated
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int rowsUpdated;

        switch (uriMatcher.match(uri)) {
            case URI_TABLE_ONE:
                rowsUpdated = sqlDB.update(TABLE_ONE, values, selection, selectionArgs);
                break;
            case URI_TABLE_TWO:
                rowsUpdated = sqlDB.update(TABLE_TWO, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    /**
     * SQLite database class.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqlDB) {
            sqlDB.execSQL(CREATE_DB_TABLE_ONE);
            sqlDB.execSQL(CREATE_DB_TABLE_TWO);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqlDB, int oldVersion, int newVersion) {
            sqlDB.execSQL("DROP TABLE IF EXISTS " + TABLE_ONE);
            sqlDB.execSQL("DROP TABLE IF EXISTS " + TABLE_TWO);
            onCreate(sqlDB);
        }
    }

    /**
     * method that will perform the insert operation for the TABLE ONE.
     * this is called from the insert().
     *
     * @param values values to insert
     * @return row in which the new data is inserted
     */
    private Uri insertToTableOne(ContentValues values) {
        long rowID = sqlDB.insert(TABLE_ONE, null, values);
        try {
            if (rowID > 0) {
                Uri _uri = ContentUris.withAppendedId(Uri.parse(URL + "/" + TABLE_ONE), rowID);
                getContext().getContentResolver().notifyChange(_uri, null);
                return _uri;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(), "Row Insert Failed", Toast.LENGTH_LONG).show();
        return null;
    }

    /**
     * method that will perform the insert operation for the TABLE TWO.
     * this is called from the insert().
     *
     * @param values values to insert
     * @return row in which the new data is inserted
     */
    private Uri insertToTableTwo(ContentValues values) {
        long rowID = sqlDB.insert(TABLE_TWO, null, values);
        try {
            if (rowID > 0) {
                Uri _uri = ContentUris.withAppendedId(Uri.parse(URL + "/" + TABLE_TWO), rowID);
                getContext().getContentResolver().notifyChange(_uri, null);
                return _uri;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(), "Row Insert Failed", Toast.LENGTH_LONG).show();
        return null;
    }
}
