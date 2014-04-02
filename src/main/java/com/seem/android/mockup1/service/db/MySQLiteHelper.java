package com.seem.android.mockup1.service.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.seem.android.mockup1.util.Utils;

import java.util.Date;

/**
 * Created by igbopie on 02/04/14.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ITEMS_ID = "_id";
    public static final String COLUMN_ITEMS_CAPTION = "caption";
    public static final String COLUMN_ITEMS_MEDIA_ID = "mediaId";
    public static final String COLUMN_ITEMS_CREATED = "created";
    public static final String COLUMN_ITEMS_REPLY_TO = "replyTo";
    public static final String COLUMN_ITEMS_SEEM_ID = "seemId";
    public static final String COLUMN_ITEMS_DEPTH = "depth";
    public static final String COLUMN_ITEMS_REPLY_COUNT = "replyCount";


    public static final String TABLE_SEEMS = "seems";
    public static final String COLUMN_SEEMS_ID = "_id";
    public static final String COLUMN_SEEMS_TITLE = "title";
    public static final String COLUMN_SEEMS_ITEM_ID = "itemId";
    public static final String COLUMN_SEEMS_CREATED = "created";
    public static final String COLUMN_SEEMS_UPDATED = "updated";
    public static final String COLUMN_SEEMS_ITEM_COUNT = "itemCount";


    private static final String DATABASE_NAME = "seem.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String CREATE_ITEMS_TABLE = "CREATE TABLE "
            + TABLE_ITEMS + "("
            + COLUMN_ITEMS_ID + " TEXT PRIMARY KEY, "
            + COLUMN_ITEMS_CAPTION + " TEXT NULL, "
            + COLUMN_ITEMS_MEDIA_ID + " TEXT NULL, "
            + COLUMN_ITEMS_CREATED + " LONG null, "
            + COLUMN_ITEMS_REPLY_TO + " TEXT null, "
            + COLUMN_ITEMS_SEEM_ID + " TEXT null, "
            + COLUMN_ITEMS_DEPTH + " INTEGER null, "
            + COLUMN_ITEMS_REPLY_COUNT + " INTEGER null);";

    // Database creation sql statement
    private static final String CREATE_SEEMS_TABLE = "CREATE TABLE "
            + TABLE_SEEMS + "("
            + COLUMN_SEEMS_ID + " TEXT PRIMARY KEY, "
            + COLUMN_SEEMS_TITLE + " TEXT NULL, "
            + COLUMN_SEEMS_ITEM_ID + " TEXT NULL, "
            + COLUMN_SEEMS_CREATED + " LONG null, "
            + COLUMN_SEEMS_UPDATED + " LONG null, "
            + COLUMN_SEEMS_ITEM_COUNT + " INTEGER null);";


    private static final String CREATE_ITEMS_INDEX =
            "CREATE INDEX "+TABLE_ITEMS+"_"+ COLUMN_ITEMS_REPLY_TO +"_idx ON "+TABLE_ITEMS+"("+ COLUMN_ITEMS_REPLY_TO +");";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ITEMS_TABLE);
        sqLiteDatabase.execSQL(CREATE_ITEMS_INDEX);
        sqLiteDatabase.execSQL(CREATE_SEEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Utils.debug(MySQLiteHelper.class,
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEEMS);
        onCreate(db);
    }
}
