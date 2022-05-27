package com.example.huacaolu.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBasePlantHelper extends SQLiteOpenHelper {

    public static final String TB_NAME = "plant";
    public static final String IMAGE_PATH = "imagePath";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String URL = "url";
    public static final String FABULOUS = "fabulous";
    public static final String COLLECTION = "collection";

    public SQLiteDatabase mSQLiteDatabase ;

    public DataBasePlantHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                TB_NAME + " ( _id integer primary key autoincrement," +
                ID + " TEXT," +
                IMAGE_PATH + " TEXT," +
                NAME +" TEXT," +
                URL + " TEXT," +
                FABULOUS + " integer," +
                COLLECTION + " integer" +
                ") ");
        mSQLiteDatabase = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

}
