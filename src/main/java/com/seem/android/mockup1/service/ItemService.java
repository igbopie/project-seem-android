package com.seem.android.mockup1.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.seem.android.mockup1.MyApplication;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.service.db.MySQLiteHelper;
import com.seem.android.mockup1.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by igbopie on 26/03/14.
 */
public class ItemService {

    //TODO implement paging properly
    private final int MAX_ITEMS_PER_QUERY = 100;

    private static ItemService singleton = null;

    public static synchronized ItemService getInstance(){
        if(singleton == null){
            Utils.debug(ItemService.class,"Creating a new singleton... ");
            singleton = new ItemService(MyApplication.getAppContext());
        }
        return singleton;
    }

    private ItemService(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ITEMS_ID,
            MySQLiteHelper.COLUMN_ITEMS_REPLY_COUNT,
            MySQLiteHelper.COLUMN_ITEMS_DEPTH,
            MySQLiteHelper.COLUMN_ITEMS_SEEM_ID,
            MySQLiteHelper.COLUMN_ITEMS_REPLY_TO,
            MySQLiteHelper.COLUMN_ITEMS_CAPTION,
            MySQLiteHelper.COLUMN_ITEMS_CREATED,
            MySQLiteHelper.COLUMN_ITEMS_MEDIA_ID};

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private Item cursorToItem(Cursor cursor) {
        Item item = new Item();
        item.setId(cursor.getString(0));
        item.setReplyCount(cursor.getInt(1));
        item.setDepth(cursor.getInt(2));
        item.setSeemId(cursor.getString(3));
        item.setReplyTo(cursor.getString(4));
        item.setCaption(cursor.getString(5));
        Date d = new Date();
        d.setTime(cursor.getLong(6));
        item.setCreated(d);
        item.setMediaId(cursor.getString(7));
        return item;
    }

    public Item findItemById(String id) {
        return findItemById(id,false);
    }
    public Item findItemById(String id,boolean refresh){
        Item item = null;
        if(!refresh){
            open();
            Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS,
                    allColumns, MySQLiteHelper.COLUMN_ITEMS_ID + " = ? ", new String[]{id}, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                item = cursorToItem(cursor);
            }
            cursor.close();
            close();
        }

        if(item == null || refresh){
            Utils.debug(this.getClass(),"Cache miss item: "+id);
            item = Api.getItem(id);
            if(item == null){
                return null;
            }
            open();
            this.saveItem(item);
            close();
        }
        return item;
    }

    public List<Item> findItemReplies(String parentItemId){
        return this.findItemReplies(parentItemId,0,false);
    }
    public List<Item> findItemReplies(String parentItemId,boolean refresh){
        return this.findItemReplies(parentItemId,0,refresh);
    }
    public List<Item> findItemReplies(String parentItemId, int page,boolean refresh){

        Item parentItem = findItemById(parentItemId,refresh);
        if(parentItem == null){
            Utils.debug(this.getClass(),"Parent not found");
            return null;
        }
        int replyCount = parentItem.getReplyCount();

        int fromItem = page * MAX_ITEMS_PER_QUERY;
        int toItem = Math.min(fromItem + MAX_ITEMS_PER_QUERY,replyCount);

        if(replyCount < fromItem){
            Utils.debug(this.getClass(),"Invalid page number");
            return null;
        }

        int returnSize = 1;

        //TODO NO PAGE FOR NOW
        page = 0;
        if(refresh){
            List<Item> items = Api.getReplies(parentItemId, page);
            this.saveItems(items);
        }
        if(replyCount > this.countItemRepliesDb(parentItemId)){
            List<Item> apiReplies = Api.getReplies(parentItemId,page);
            saveItems(apiReplies);
        }

        List<Item>replies = findItemRepliesDb(parentItemId);

        Collections.sort(replies, new Comparator<Item>() {
            @Override
            public int compare(Item item, Item item2) {
                return item.getCreated().compareTo(item2.getCreated()) * -1;
            }
        });
        //We have to discard
        //return replies.subList(fromItem,Math.min(toItem, replies.size()));
        return replies;
    }

    private List<Item> findItemRepliesDb(String parentItemId){
        //TODO Paging...
        List<Item>allDbReplies = new ArrayList<Item>();
        open();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS,
                allColumns,
                MySQLiteHelper.COLUMN_ITEMS_REPLY_TO + " = ? ",
                new String[]{parentItemId},
                null, null,
                MySQLiteHelper.COLUMN_ITEMS_CREATED +" desc");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            allDbReplies.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return allDbReplies;
    }
    private int countItemRepliesDb(String parentItemId){
        open();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS,
                allColumns,
                MySQLiteHelper.COLUMN_ITEMS_REPLY_TO + " = ? ",
                new String[]{parentItemId},
                null, null,
                MySQLiteHelper.COLUMN_ITEMS_CREATED +" desc");

        int dbCount = cursor.getCount();
        close();
        return dbCount;
    }

    public Item reply(Item item){
        Item parentReply = findItemById(item.getReplyTo());
        if(parentReply == null){
            return null;
        }
        //It is important to do this before the call, that way,
        // either the parent item was loaded locally or remotelly
        // it will be the same
        open();
        parentReply.setReplyCount(parentReply.getReplyCount() + 1);
        this.saveItem(parentReply);

        Item reply = Api.reply(item.getCaption(),item.getMediaId(),item.getReplyTo());
        this.saveItem(reply);
        close();
        return reply;
    }

    private void saveItems(List<Item> items){
        this.open();
        for(Item item:items){
            this.saveItem(item);
        }
        this.close();
    }
    private void saveItem(Item item){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ITEMS_ID, item.getId());
        values.put(MySQLiteHelper.COLUMN_ITEMS_CAPTION, item.getCaption());
        values.put(MySQLiteHelper.COLUMN_ITEMS_CREATED, item.getCreated().getTime());
        values.put(MySQLiteHelper.COLUMN_ITEMS_DEPTH, item.getDepth());
        values.put(MySQLiteHelper.COLUMN_ITEMS_MEDIA_ID, item.getMediaId());
        values.put(MySQLiteHelper.COLUMN_ITEMS_REPLY_COUNT, item.getReplyCount());
        values.put(MySQLiteHelper.COLUMN_ITEMS_REPLY_TO, item.getReplyTo());
        values.put(MySQLiteHelper.COLUMN_ITEMS_SEEM_ID, item.getSeemId());
        database.replace(MySQLiteHelper.TABLE_ITEMS, null, values);

    }
}
