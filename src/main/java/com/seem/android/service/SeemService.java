package com.seem.android.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.seem.android.MyApplication;
import com.seem.android.model.Seem;
import com.seem.android.service.db.MySQLiteHelper;
import com.seem.android.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by igbopie on 26/03/14.
 */
public class SeemService {

    private static SeemService singleton = null;

    public static synchronized SeemService getInstance(){
        if(singleton == null){
            Utils.debug(SeemService.class,"Creating a new singleton... ");
            singleton = new SeemService(MyApplication.getAppContext());
        }
        return singleton;
    }
    private SeemService(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_SEEMS_ID,
            MySQLiteHelper.COLUMN_SEEMS_ITEM_COUNT,
            MySQLiteHelper.COLUMN_SEEMS_TITLE,
            MySQLiteHelper.COLUMN_SEEMS_CREATED,
            MySQLiteHelper.COLUMN_SEEMS_ITEM_ID,
            MySQLiteHelper.COLUMN_SEEMS_UPDATED};



    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Seem findSeemById(String id){
        Seem seem = findSeemByIdDB(id);
        if(seem == null){
            Utils.debug(this.getClass(),"Cache miss item: "+id);
            findSeems(true);
            seem = findSeemByIdDB(id);
        }
        return seem;
    }

    private Seem findSeemByIdDB(String id) {
        Seem seem = null;

        open();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SEEMS,
                allColumns, MySQLiteHelper.COLUMN_SEEMS_ID + " = ? ", new String[]{id}, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            seem = cursorToSeem(cursor);
        }
        cursor.close();
        close();
        return seem;
    }

    public List<Seem> findSeems() {
        return this.findSeems(false);
    }

    public List<Seem> findSeems(boolean refresh) {
        if(countSeemsDb() == 0 || refresh){
            saveSeems(Api.getSeems());
        }
        List<Seem> collection = new ArrayList<Seem>();

        open();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SEEMS,
                allColumns,
                null,
                null,
                null, null,
                MySQLiteHelper.COLUMN_SEEMS_UPDATED +" desc");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Seem seem = cursorToSeem(cursor);
            collection.add(seem);
            cursor.moveToNext();
        }
        cursor.close();
        close();

        Collections.sort(collection, new Comparator<Seem>() {
            @Override
            public int compare(Seem item, Seem item2) {
                return item.getUpdated().compareTo(item2.getUpdated()) * -1;
            }
        });
        return collection;
    }

    private int countSeemsDb(){
        open();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SEEMS,
                allColumns,
                null,
                null,
                null, null,
                MySQLiteHelper.COLUMN_SEEMS_UPDATED +" desc");

        int dbCount = cursor.getCount();
        close();
        return dbCount;
    }

    public Seem save(String title,String caption, String mediaId){
        Seem seem = Api.createSeem(title,caption,mediaId);
        open();
        this.saveSeem(seem);
        close();
        return seem;
    }

    private void saveSeems(List<Seem> seems){
        open();
        for(Seem seem:seems){
            this.saveSeem(seem);
        }
        close();
    }
    private void saveSeem(Seem seem){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_SEEMS_ID, seem.getId());
        values.put(MySQLiteHelper.COLUMN_SEEMS_CREATED,seem.getCreated().getTime() );
        values.put(MySQLiteHelper.COLUMN_SEEMS_UPDATED,seem.getUpdated().getTime() );
        values.put(MySQLiteHelper.COLUMN_SEEMS_ITEM_COUNT,seem.getItemCount() );
        values.put(MySQLiteHelper.COLUMN_SEEMS_ITEM_ID,seem.getItemId() );
        values.put(MySQLiteHelper.COLUMN_SEEMS_TITLE,seem.getTitle());
        database.replace(MySQLiteHelper.TABLE_SEEMS, null, values);
    }

    private Seem cursorToSeem(Cursor cursor) {
        Seem seem = new Seem();
        seem.setId(cursor.getString(0));
        seem.setItemCount(cursor.getInt(1));
        seem.setTitle(cursor.getString(2));
        seem.setCreated(new Date(cursor.getLong(3)));
        seem.setItemId(cursor.getString(4));
        seem.setUpdated(new Date(cursor.getLong(5)));
        return seem;
    }

}
