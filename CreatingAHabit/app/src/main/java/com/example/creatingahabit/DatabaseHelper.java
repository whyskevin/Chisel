package com.example.creatingahabit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Habits.db";
    public static final String TABLE_NAME = "Habit_Table";
//    public static final String COL_0 = "ID";
    public static final String COL_1 = "NAME";
    public static final String COL_2 = "DESCRIPTION";
    public static final String COL_3 = "FREQUENCY";
    public static final String COL_4 = "TIME_PERIOD";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " ( NAME TEXT PRIMARY KEY, DESCRIPTION TEXT, FREQUENCY INTEGER, TIME_PERIOD TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate(sqLiteDatabase);
    }

    public boolean insertData( String name, String description, String frequency, String spinnerFrequency){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, name);
        contentValues.put(COL_2, description);
        contentValues.put(COL_3, Integer.parseInt(frequency));
        contentValues.put(COL_4, spinnerFrequency);

        long result =  db.insert(TABLE_NAME, null, contentValues); //Returns -1 if data is not inserted
        if(result == -1)
            return false;
        else {
            System.out.println("Inserted:" + contentValues.toString());
            return true;
        }

    }

    public void updateData(String name, String description, String frequency, String spinnerFrequency){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, name);
        contentValues.put(COL_2, description);
        contentValues.put(COL_3, Integer.parseInt(frequency));
        contentValues.put(COL_4, spinnerFrequency);

        String where = COL_1 + "= '" + name + "'";
        db.update(TABLE_NAME, contentValues, where,null );
    }


    public boolean deleteData(String key)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_1 + " = '" + key + "'", null) > 0;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    public Cursor viewData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getItem(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_1 + " = '" + name + "'";
        Cursor rowData = db.rawQuery(query, null);
        return rowData;
    }
}