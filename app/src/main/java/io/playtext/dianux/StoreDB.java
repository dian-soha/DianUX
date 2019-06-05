package io.playtext.dianux;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StoreDB extends SQLiteOpenHelper {
    public static final String TAG = "StoreDB";
    private Context context;

    public static String DB_NAME = "Store.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "Store";
    private static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String LANGUAGE = "language";
    public static final String TAGS = "tags";
    private static final String PRICE = "price";
    private static final String SPLITTER = "splitter";
    private static final String URL = "url";
    private static final String STATUS = "status";

    private SQLiteDatabase db;
    private String[] allColumns = {
            ID,
            TITLE,
            AUTHOR,
            LANGUAGE,
            TAGS,
            PRICE,
            SPLITTER,
            URL,
            STATUS};

    public StoreDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME
                + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TITLE + " TEXT ,"
                + AUTHOR + " TEXT ,"
                + LANGUAGE + " TEXT ,"
                + TAGS + " TEXT ,"
                + PRICE + " INTEGER ," // unite = cents
                + SPLITTER + " TEXT ,"
                + URL + " TEXT ,"
                + STATUS + " INTEGER "
                + ")";
        db.execSQL(CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "Updating table from " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    public void open() throws SQLException {
        db = this.getWritableDatabase();
        db = SQLiteDatabase.openDatabase(databaseFolderPath(context)+ File.separator  + DB_NAME, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
    }

    public static String databaseFolderPath(Context context) {
        return java.io.File.separator +
                context.getString(R.string.data) + java.io.File.separator +
                context.getString(R.string.data) + java.io.File.separator +
                context.getPackageName() + java.io.File.separator +
                context.getString(R.string.databases);
    }

    @Override
    public synchronized void close() {
        if (db != null)
            db.close();
        super.close();
    }

    public void delete() throws SQLException {
        db.execSQL("delete from "+ TABLE_NAME);
    }

    private Store cursorToItem(Cursor cursor) {
        Store store = new Store();

        store.setId(cursor.getInt(cursor.getColumnIndex(ID)));
        store.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
        store.setAuthor(cursor.getString(cursor.getColumnIndex(AUTHOR)));
        store.setLanguage(cursor.getString(cursor.getColumnIndex(LANGUAGE)));
        store.setTags(cursor.getString(cursor.getColumnIndex(TAGS)));
        store.setPrice(cursor.getInt(cursor.getColumnIndex(PRICE)));
        store.setSplitter(cursor.getString(cursor.getColumnIndex(SPLITTER)));
        store.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
        store.setStatus(cursor.getLong(cursor.getColumnIndex(STATUS)));

        return store;
    }

    public Store createItem(String title, String author, String language, String tags, int price, String splitter, String url, long status) {
        ContentValues cv = new ContentValues();
        cv.put(TITLE, title);
        cv.put(AUTHOR, author);
        cv.put(LANGUAGE, language);
        cv.put(TAGS, tags);
        cv.put(PRICE, price);
        cv.put(SPLITTER, splitter);
        cv.put(URL, url);
        cv.put(STATUS, status);

        long insertId = db.insert(TABLE_NAME, null, cv);
        Cursor cursor = db.query(TABLE_NAME, allColumns, ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Store store = cursorToItem(cursor);

        cursor.close();
        return store;
    }

    public List<Store> getAllItems() {
        List<Store> storeList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, allColumns, null , null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Store store = cursorToItem(cursor);
            storeList.add(store);
            cursor.moveToNext();
        }
        cursor.close();
        return storeList;
    }
    public int getItemsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    List<Store> getFilteredItems(String filter) {
        List<Store> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(filter, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Store store = cursorToItem(cursor);
            list.add(store);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public int getFilteredItemsCount(String filter) {
        Cursor cursor = db.rawQuery(filter, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
}