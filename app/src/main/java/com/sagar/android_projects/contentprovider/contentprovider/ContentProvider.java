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
 */
public class ContentProvider extends android.content.ContentProvider {

    private static HashMap<String, String> values;

    private SQLiteDatabase sqlDB;
    static final String DATABASE_NAME = "sqlite_database";
    static final int DATABASE_VERSION = 1;

    public static final String TABLE_ONE = "table_one";
    public static final String KEY_ID_TABLE_ONE = "ID";
    public static final String KEY_NAME = "NAME";
    static final String CREATE_DB_TABLE_ONE = " CREATE TABLE " + TABLE_ONE
            + " (" + KEY_ID_TABLE_ONE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT NOT NULL"
            + ");";

    public static final String TABLE_TWO = "table_two";
    public static final String KEY_ID_TABLE_TWO = "ID";
    public static final String KEY_MOBILE = "MOBILE";
    static final String CREATE_DB_TABLE_TWO = " CREATE TABLE " + TABLE_TWO
            + " (" + KEY_ID_TABLE_TWO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_MOBILE + " TEXT NOT NULL"
            + ");";

    static final String PROVIDER_NAME = "com.sagar.android_projects.contentprovider";

    public static final String URL = "content://" + PROVIDER_NAME + "/database";
    public static final Uri CONTENT_URL = Uri.parse(URL);
    static final int URI_TABLE_ONE = 1;
    static final int URI_TABLE_TWO = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "database/" + TABLE_ONE, URI_TABLE_ONE);
        uriMatcher.addURI(PROVIDER_NAME, "database/" + TABLE_TWO, URI_TABLE_TWO);
    }

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        sqlDB = dbHelper.getWritableDatabase();
        return sqlDB != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_ONE);
        switch (uriMatcher.match(uri)) {
            case URI_TABLE_ONE:
                queryBuilder.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor cursor = queryBuilder.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

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

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
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
