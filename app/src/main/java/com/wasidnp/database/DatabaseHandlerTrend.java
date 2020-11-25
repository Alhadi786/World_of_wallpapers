package com.wasidnp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wasidnp.models.ItemRecent;
import com.wasidnp.models.ItemTrend;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandlerTrend extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "mwp_trend";

    private static final String TABLE_NAME = "trend";
    private static final String KEY_ID = "id";
    private static final String KEY_IMAGE_URL = "imageurl";


    public DatabaseHandlerTrend(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_IMAGE_URL + " TEXT,"
                + "UNIQUE(" + KEY_IMAGE_URL + ")"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    //Adding Record in Database

    public void AddtoFavoriteLatest(ItemTrend pj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_IMAGE_URL, pj.getImageurl());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection

    }

    // Getting All Data
    public List<ItemTrend> getAllData() {
        List<ItemTrend> dataList = new ArrayList<ItemTrend>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemTrend contact = new ItemTrend();
                contact.setImageurl(cursor.getString(1));

                // Adding contact to list
                dataList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return dataList;
    }

    //getting single row

    public List<ItemRecent> getFavRow(String id) {
        List<ItemRecent> dataList = new ArrayList<ItemRecent>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE imageurl=" + "'" + id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemRecent contact = new ItemRecent();
                //contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setCategoryName(cursor.getString(1));
                contact.setImageurl(cursor.getString(2));

                // Adding contact to list
                dataList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return dataList;
    }

    //for remove favorite

    public void RemoveFav(ItemRecent contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_IMAGE_URL + " = ?",
                new String[]{String.valueOf(contact.getImageurl())});
        db.close();
    }

    public enum DatabaseManager {
        INSTANCE;
        private SQLiteDatabase db;
        private boolean isDbClosed = true;
        DatabaseHandlerTrend dbHelper;

        public void init(Context context) {
            dbHelper = new DatabaseHandlerTrend(context);
            if (isDbClosed) {
                isDbClosed = false;
                this.db = dbHelper.getWritableDatabase();
            }

        }


        public boolean isDatabaseClosed() {
            return isDbClosed;
        }

        public void closeDatabase() {
            if (!isDbClosed && db != null) {
                isDbClosed = true;
                db.close();
                dbHelper.close();
            }
        }
    }
}
