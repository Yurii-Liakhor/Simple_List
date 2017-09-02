package com.simplelist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.simplelist.Objects.Item;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Yurii on 01.07.2017.
 */

public class ListDataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "list.sqlite";
    private static final int VERSION = 4;
    private static final String TABLE_LIST = "list";
    private static final String COLUMN_LIST_ID = "_id";
    private static final String COLUMN_LIST_ITEM_NAME = "item_name";
    private static final String COLUMN_LIST_ITEM_DESCRIPTION = "item_description";
    private static final String COLUMN_LIST_ITEM_DATE = "item_date";
    private static final String COLUMN_LIST_ITEM_IMAGE = "item_image";

    private static final String SORT_BY_ASC = COLUMN_LIST_ITEM_NAME + " " + "ASC";
    private static final String SORT_BY_DESC = COLUMN_LIST_ITEM_NAME + " " + "DESC";
    private static final String SORT_BY_DATE_ASC = COLUMN_LIST_ITEM_DATE + " " + "TIMESTAMP ASC";
    private static final String SORT_BY_DATE_DESC = COLUMN_LIST_ITEM_DATE + " " + "TIMESTAMP DESC";

    public ListDataBaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE list " +
                "(_id, item_name, item_description, item_date DATETIME, item_image);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        if(i != 4){
            db.execSQL("ALTER TABLE " + TABLE_LIST + " ADD COLUMN " + COLUMN_LIST_ITEM_IMAGE);
            db.execSQL("UPDATE " + TABLE_LIST + " SET " + COLUMN_LIST_ITEM_IMAGE + " = '';");
            //onCreate(db);
        }
    }

    public void insertItem(Item item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LIST_ITEM_NAME, item.getTitle());
        cv.put(COLUMN_LIST_ID, item.getUuid());
        cv.put(COLUMN_LIST_ITEM_DESCRIPTION, item.getDescription());
        cv.put(COLUMN_LIST_ITEM_IMAGE, item.getImage());
        cv.put(COLUMN_LIST_ITEM_DATE, item.getTimestamp().toString());
        getWritableDatabase().insert(TABLE_LIST, null, cv);
    }

    public void insertItems(ArrayList<Item> items){
        for(int i = 0; i < items.size(); i++){
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_LIST_ITEM_NAME, items.get(i).getTitle());
            cv.put(COLUMN_LIST_ID, items.get(i).getUuid());
            cv.put(COLUMN_LIST_ITEM_DESCRIPTION, items.get(i).getDescription());
            cv.put(COLUMN_LIST_ITEM_IMAGE, items.get(i).getImage());
            cv.put(COLUMN_LIST_ITEM_DATE, items.get(i).getTimestamp().toString());
            getWritableDatabase().insert(TABLE_LIST, null, cv);
        }
    }

    public void updateItem(Item item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LIST_ITEM_NAME, item.getTitle());
        cv.put(COLUMN_LIST_ITEM_DESCRIPTION, item.getDescription());
        cv.put(COLUMN_LIST_ITEM_IMAGE, item.getImage());
        //getWritableDatabase().update(TABLE_LIST, cv, "_id" + "=" + item.getId(), null);
        getWritableDatabase().update(TABLE_LIST, cv, "_id" + "=?", new String[] {item.getUuid()});
    }

    public void deleteItem(Item item) {
        //getWritableDatabase().delete(TABLE_LIST, "_id" + "=" + item.getId(), null);
        getWritableDatabase().delete(TABLE_LIST, "_id" + "=?", new String[] {item.getUuid()});
    }

    public void deleteAllItems(){
        getWritableDatabase().delete(TABLE_LIST, null, null);
    }

    public ArrayList<Item> queryItems(){
        ArrayList<Item> list = new ArrayList<Item>();
        Cursor cursor = getReadableDatabase().query(TABLE_LIST, null,
                null, null, null, null, null);
        if (cursor.moveToFirst()){
            do{
                Item item = new Item(cursor.getString(0), cursor.getString(1), cursor.getString(2), Timestamp.valueOf(cursor.getString(3)), cursor.getString(4));
                list.add(item);
            } while(cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<Item> queryItems(String Sort){
        ArrayList<Item> list = new ArrayList<Item>();
        Cursor cursor = getReadableDatabase().query(TABLE_LIST, null,
                null, null, null, null, Sort);
        if (cursor.moveToFirst()){
            do{
                Item item = new Item(cursor.getString(0), cursor.getString(1), cursor.getString(2), Timestamp.valueOf(cursor.getString(3)), cursor.getString(4));
                list.add(item);
            } while(cursor.moveToNext());
        }
        return list;
    }

    /*public Item queryItem(int id) {
        id++; //autoincrement starts from 1
        Cursor cursor = getReadableDatabase().query(TABLE_LIST, new String[] {"_id", "item_name"}, "_id" + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor.moveToFirst()){
            Item item = new Item(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
            return item;
        }
        return null;
    }*/
}
