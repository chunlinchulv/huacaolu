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
        values.put("id",id);
        values.put("imagePath",plant.getImagePath());
        values.put("name",plant.getName());
        values.put("url",plant.getUrl());
        return writableDatabase.insert(DataBasePlantHelper.TB_NAME, null, values);
    }

    public int delete(String id){
        return writableDatabase.delete(DataBasePlantHelper.TB_NAME, "id = ?", new String[]{id});
    }

    public Cursor query(String id){
        String[] columns = new String[]{"id","imagePath","name","url"};
        // 参数为空则为返回所有列
//        String[] columns = null;
        String selection = "id = ?";
        String[] selectionArgs = new String[]{id};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        return writableDatabase.query(DataBasePlantHelper.TB_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public void update(String id,ExplorePlantBean.Result plant){
        ContentValues values = new ContentValues();
        values.put("id",id);
        values.put("imagePath",plant.getImagePath());
        values.put("name",plant.getName());
        values.put("url",plant.getUrl());
        writableDatabase.update(DataBasePlantHelper.TB_NAME,values,"id = ?",new String[]{id});
    }


}
