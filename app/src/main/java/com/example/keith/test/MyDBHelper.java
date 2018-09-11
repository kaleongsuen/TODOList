package com.example.keith.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Keith on 2/11/2017.
 */

public class MyDBHelper extends SQLiteOpenHelper{

    String PERIOD_TABLE_NAME = "period";
    String PERIOD_ID = "_id";
    String LAST_PERIOD_DATE = "last_period_date";
    String PERIOD = "period";
    String TASK_TABLE_NAME = "task";
    String TASK_ID = "_id";
    String TASK_NAME = "task_name";
    String TASK_DESC = "task_desc";
    String TASK_DUE_DATE = "task_due_date";
    String TASK_STATUS = "task_status";
    String TASK_CAT = "task_cat";
    String TASK_DONE_DATE = "task_done_date";

    String CAT_TABLE_NAME = "cat";
    String CAT_ID = "_id";
    String CAT_NAME = "cat_name";

    String[] columns={TASK_ID,TASK_NAME,TASK_DESC,TASK_DUE_DATE,TASK_STATUS,TASK_CAT,TASK_DONE_DATE};

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TASK_TABLE_NAME+
            "("+TASK_ID+" INTEGER PRIMARY KEY  NOT NULL, "+
                TASK_NAME+" STRING NOT NULL , "+
                TASK_DESC+" STRING, "+
                TASK_DUE_DATE+" LONG NOT NULL ,"+
                TASK_STATUS+" INTEGER,"+
                TASK_CAT+" INTEGER, "+
                TASK_DONE_DATE+" LONG )");
        db.execSQL("CREATE TABLE "+CAT_TABLE_NAME+
                "("+CAT_ID+" INTEGER PRIMARY KEY  NOT NULL, "+
                CAT_NAME+" STRING NOT NULL )");
        db.execSQL("insert into "+CAT_TABLE_NAME+" (cat_name) values ('Work')");
        db.execSQL("insert into "+CAT_TABLE_NAME+"(cat_name) values ('Family')");
        db.execSQL("insert into "+CAT_TABLE_NAME+"(cat_name) values ('Friends')");
        db.execSQL("insert into "+CAT_TABLE_NAME+"(cat_name) values ('MISC')");
        db.execSQL("CREATE TABLE "+PERIOD_TABLE_NAME+
        "("+PERIOD_ID+" INTEGER PRIMARY KEY NOT NUL, "+
        LAST_PERIOD_DATE+" LONG NOT NULL"+"" +
                PERIOD+" INTEGER NOT NULL )");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(oldVersion == 1 && newVersion == 2){
                db.execSQL("ALTER TABLE "+TASK_TABLE_NAME+" ADD "+TASK_DONE_DATE+" LONG");
            }
            if(oldVersion == 2 && newVersion == 3){
                db.execSQL("CREATE TABLE "+PERIOD_TABLE_NAME+
                        "("+PERIOD_ID+" INTEGER PRIMARY KEY NOT NUL, "+
                        LAST_PERIOD_DATE+" LONG NOT NULL"+"" +
                        PERIOD+" INTEGER NOT NULL )");
            }
    }

    private static MyDBHelper instance;
    public static MyDBHelper getInstance(Context ctx){
        if (instance==null){
            instance = new MyDBHelper(ctx, "finna.db", null, 3);
        }
        return instance;
    }

    public ArrayList<Model> listTodoTask(SQLiteDatabase db){
        ArrayList<Model> result = new ArrayList<>();
        //String[] columns={TASK_ID,TASK_NAME,TASK_DESC,TASK_DUE_DATE,TASK_STATUS,TASK_CAT};
        Cursor cursor = db.query(TASK_TABLE_NAME,
                                columns,
                                TASK_STATUS +"= 0",
                                null,
                                null,
                                null,
                                TASK_DUE_DATE);
        while (cursor.moveToNext()){
            result.add(new Model(
                    cursor.getInt(cursor.getColumnIndex(TASK_ID)),
                    cursor.getString(cursor.getColumnIndex(TASK_NAME)),
                    cursor.getString(cursor.getColumnIndex(TASK_DESC)),
                    cursor.getLong(cursor.getColumnIndex(TASK_DUE_DATE)),
                    cursor.getInt(cursor.getColumnIndex(TASK_STATUS)),
                    cursor.getInt(cursor.getColumnIndex(TASK_CAT)),
                    cursor.getLong(cursor.getColumnIndex(TASK_DONE_DATE)))
                    );
        }
        cursor.close();
        return result;
    }
    public ArrayList<Model> listDoneTask(SQLiteDatabase db){
        ArrayList<Model> result = new ArrayList<>();
        //String[] columns={TASK_ID,TASK_NAME,TASK_DESC,TASK_DUE_DATE,TASK_STATUS,TASK_CAT};
        Cursor cursor = db.query(TASK_TABLE_NAME,
                columns,
                TASK_STATUS +"= 1",
                null,
                null,
                null,
                TASK_DUE_DATE);
        while (cursor.moveToNext()){
            result.add(new Model(
                    cursor.getInt(cursor.getColumnIndex(TASK_ID)),
                    cursor.getString(cursor.getColumnIndex(TASK_NAME)),
                    cursor.getString(cursor.getColumnIndex(TASK_DESC)),
                    cursor.getLong(cursor.getColumnIndex(TASK_DUE_DATE)),
                    cursor.getInt(cursor.getColumnIndex(TASK_STATUS)),
                    cursor.getInt(cursor.getColumnIndex(TASK_CAT)),
                    cursor.getLong(cursor.getColumnIndex(TASK_DONE_DATE)))
            );
        }
        cursor.close();
        return result;
    }
    public ArrayList<Model> listDeadLine(SQLiteDatabase db){
        ArrayList<Model> result = new ArrayList<>();
        //String[] columns={TASK_ID,TASK_NAME,TASK_DESC,TASK_DUE_DATE,TASK_STATUS,TASK_CAT};
        Cursor cursor = db.query(TASK_TABLE_NAME,
                columns,
                TASK_STATUS +"= 0",
                null,
                null,
                null,
                TASK_DUE_DATE);
        while (cursor.moveToNext()){
            Long dateLong = cursor.getLong(cursor.getColumnIndex(TASK_DUE_DATE));
            LocalDate date = new LocalDate(dateLong);
            LocalDate today = new LocalDate();
            int diffdays = Days.daysBetween(today,date).getDays();
            if(diffdays <= 1){
                result.add(new Model(
                        cursor.getInt(cursor.getColumnIndex(TASK_ID)),
                        cursor.getString(cursor.getColumnIndex(TASK_NAME)),
                        cursor.getString(cursor.getColumnIndex(TASK_DESC)),
                        cursor.getLong(cursor.getColumnIndex(TASK_DUE_DATE)),
                        cursor.getInt(cursor.getColumnIndex(TASK_STATUS)),
                        cursor.getInt(cursor.getColumnIndex(TASK_CAT)),
                        cursor.getLong(cursor.getColumnIndex(TASK_DONE_DATE)))
                );
            }
        }
        cursor.close();
        return result;
    }
    public ArrayList<Model> listByCat(SQLiteDatabase db, int cat){
        ArrayList<Model> result = new ArrayList<>();
        //String[] columns={TASK_ID,TASK_NAME,TASK_DESC,TASK_DUE_DATE,TASK_STATUS,TASK_CAT};
        Cursor cursor = db.query(TASK_TABLE_NAME,
                columns,
                TASK_STATUS +"= 0 and "+TASK_CAT+"="+cat,
                null,
                null,
                null,
                TASK_DUE_DATE);
        while (cursor.moveToNext()){
            result.add(new Model(
                    cursor.getInt(cursor.getColumnIndex(TASK_ID)),
                    cursor.getString(cursor.getColumnIndex(TASK_NAME)),
                    cursor.getString(cursor.getColumnIndex(TASK_DESC)),
                    cursor.getLong(cursor.getColumnIndex(TASK_DUE_DATE)),
                    cursor.getInt(cursor.getColumnIndex(TASK_STATUS)),
                    cursor.getInt(cursor.getColumnIndex(TASK_CAT)),
                    cursor.getLong(cursor.getColumnIndex(TASK_DONE_DATE)))
            );
        }
        cursor.close();
        return result;
    }
    public void updateTask(SQLiteDatabase db, Model target){
        ContentValues values = new ContentValues();
        values.put(TASK_NAME,target.getName());
        values.put(TASK_DESC,target.getDesc());
        values.put(TASK_DUE_DATE, target.getDueDate());
        values.put(TASK_STATUS,target.getStatus());
        values.put(TASK_CAT,target.getCat());
        values.put(TASK_DONE_DATE,target.getDoneDate());
        db.update(TASK_TABLE_NAME,values,TASK_ID+"="+target.getID(),null);

    }
    public void markTaskDone(SQLiteDatabase db, Model target) {
        ContentValues values = new ContentValues();
        values.put(TASK_NAME,target.getName());
        values.put(TASK_DESC,target.getDesc());
        values.put(TASK_DUE_DATE, target.getDueDate());
        values.put(TASK_STATUS,1);
        values.put(TASK_CAT,target.getCat());
        LocalDate localDate = new LocalDate();
        long date = localDate.toDate().getTime();
        values.put(TASK_DONE_DATE,date);
        db.update(TASK_TABLE_NAME,values,TASK_ID+"="+target.getID(),null);

    }
    public void markTaskTodo(SQLiteDatabase db, Model target){
        ContentValues values = new ContentValues();
        values.put(TASK_NAME,target.getName());
        values.put(TASK_DESC,target.getDesc());
        values.put(TASK_DUE_DATE, target.getDueDate());
        values.put(TASK_STATUS,0);
        values.put(TASK_CAT,target.getCat());
        values.put(TASK_DONE_DATE,-1);
        db.update(TASK_TABLE_NAME,values,TASK_ID+"="+target.getID(),null);

    }
    public void dropTable(SQLiteDatabase db){
        db.delete(TASK_TABLE_NAME,null,null);
    }

    public void deleteTask(SQLiteDatabase db, Model target){
        db.delete(TASK_TABLE_NAME,TASK_ID+"="+target.getID(),null);
    }

    public void removeDoneTask(SQLiteDatabase db){
        db.delete(TASK_TABLE_NAME,TASK_STATUS+" =1 ",null);
    }

    public void autoRemoveDoneTask(SQLiteDatabase db){
        int removeAfterDay = 5;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH,removeAfterDay*-1);
        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        long refDate = calendar.getTimeInMillis();
        db.delete(TASK_TABLE_NAME,TASK_STATUS+" =1 AND "+TASK_DONE_DATE+" < "+ refDate,null);
    }

    public ArrayList<CatModel> getAllCat(SQLiteDatabase db){
        ArrayList<CatModel> result = new ArrayList<>();
        String[] columns={CAT_ID, CAT_NAME};
        Cursor cursor = db.query(CAT_TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null);
        while (cursor.moveToNext()){
            result.add(new CatModel(
                    cursor.getInt(cursor.getColumnIndex(CAT_ID)),
                    cursor.getString(cursor.getColumnIndex(CAT_NAME))
                    ));
        }
        cursor.close();
        return result;
    }

    public int getLastRecordId(SQLiteDatabase db){
        String[] columns={TASK_ID};
        int id=-1;
        Cursor cursor =  db.rawQuery("SELECT MAX("+TASK_ID+") FROM "+TASK_TABLE_NAME,null);
        while (cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        return  id;
    }

    public PeriodModel getPeriodModel(SQLiteDatabase db){
        PeriodModel periodModel = null;
        Cursor cursor =  db.rawQuery("SELECT MAX("+PERIOD_ID+") FROM "+PERIOD_TABLE_NAME,null);
        while (cursor.moveToNext()){
            periodModel = new PeriodModel(
                    cursor.getInt(cursor.getColumnIndex(PERIOD_ID)),
                    cursor.getLong(cursor.getColumnIndex(LAST_PERIOD_DATE)),
                    cursor.getInt(cursor.getColumnIndex(PERIOD))
            );
        }
        return periodModel;
    }

    public long updatePeriod(SQLiteDatabase db, PeriodModel periodModel){
        //clear all data
        db.delete(PERIOD_TABLE_NAME,null,null);
        //insert new value
        ContentValues values = new ContentValues();
        values.put(LAST_PERIOD_DATE, periodModel.getLastPeriodDate());
        values.put(PERIOD, periodModel.getPeriod());
        long insertId = db.insert(PERIOD_TABLE_NAME, null, values);
        return insertId;
    }

}
