package com.example.huacaolu.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.huacaolu.bean.ExplorePlantBean;
import com.example.huacaolu.helper.DataBasePlantHelper;

public class DataBaseUtil {
    private static SQLiteDatabase writableDatabase;
    private static DataBaseUtil instance;
    private static String[] columns = new String[]{
            DataBasePlantHelper.ID,
            DataBasePlantHelper.IMAGE_PATH,
            DataBasePlantHelper.NAME,
            DataBasePlantHelper.URL,
            DataBasePlantHelper.FABULOUS,
            DataBasePlantHelper.COLLECTION};
    public static DataBaseUtil getInstance(){
        if (instance == null) {
            instance = new DataBaseUtil();
        }
        return instance;
    }

    private DataBaseUtil(){

    }

    public void createDataBase(Context context) {
        DataBasePlantHelper dbHelper = new DataBasePlantHelper(context, "huacaolu.db", null, 1);
        writableDatabase = dbHelper.getWritableDatabase();
    }

    public long insert(String id, ExplorePlantBean.Result plant){
        ContentValues values = new ContentValues();
        values.put(columns[0],id);
        values.put(columns[1],plant.getImagePath());
        values.put(columns[2],plant.getName());
        values.put(columns[3],plant.getUrl());
        values.put(columns[4],plant.getFabulous());
        values.put(columns[5],plant.getCollection());
        return writableDatabase.insert(DataBasePlantHelper.TB_NAME, null, values);
    }

    public int delete(String id){
        return writableDatabase.delete(DataBasePlantHelper.TB_NAME, "id = ?", new String[]{id});
    }
    public Cursor queryAll(){
        // 参数为空则为返回所有列
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;
        return writableDatabase.query(DataBasePlantHelper.TB_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
    }
    public Cursor queryByID(String id){
        // 参数为空则为返回所有列
        String selection = "id = ?";
        String[] selectionArgs = new String[]{id};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        return writableDatabase.query(DataBasePlantHelper.TB_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public Cursor queryByName(String name){
        // 参数为空则为返回所有列
//        String[] columns = null;
        String selection = "name = ?";
        String[] selectionArgs = new String[]{name};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        return writableDatabase.query(DataBasePlantHelper.TB_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public int update(String id,ExplorePlantBean.Result plant){
        ContentValues values = new ContentValues();
        values.put(columns[0],id);
        values.put(columns[1],plant.getImagePath());
        values.put(columns[2],plant.getName());
        values.put(columns[3],plant.getUrl());
        values.put(columns[4],plant.getFabulous());
        values.put(columns[5],plant.getCollection());
        return writableDatabase.update(DataBasePlantHelper.TB_NAME,values,"id = ?",new String[]{id});
    }


}
