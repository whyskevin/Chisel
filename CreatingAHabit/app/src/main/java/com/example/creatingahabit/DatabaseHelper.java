package com.example.creatingahabit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Habits.db";
    public static final String TABLE_NAME = "Habit_Table";
    public static final String COL_0 = "ID";
    public static final String COL_1 = "NAME";
    public static final String COL_2 = "DESCRIPTION";
    public static final String COL_3 = "FREQUENCY";
    public static final String COL_4 = "TIME_PERIOD";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);

    }


    /*
    Purpose: Create a DB Helper object to manage a SQLITE database.
    Habit_Table is a table within the Habits.db
    Things to note about the ID INTEGER PK AUTOINCREMENT:
    The ID chosen for a new row is at least one larger than the largest ID that has ever before existed in that same table.
    If the table has never before contained any data, then a ROWID of 1 is used.
    If the largest possible ROWID has previously been inserted, then new INSERTs are not allowed and any attempt to insert a new row will fail with an SQLITE_FULL error
    [1, 9223372036854775807]
    */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " ( ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT UNIQUE, DESCRIPTION TEXT, FREQUENCY INTEGER, TIME_PERIOD TEXT )");
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

    public void updateData(Integer ID, String name, String description, String frequency, String spinnerFrequency){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, name);
        contentValues.put(COL_2, description);
        contentValues.put(COL_3, Integer.parseInt(frequency));
        contentValues.put(COL_4, spinnerFrequency);

        String where = COL_0 + "= '" + ID + "'";
        db.update(TABLE_NAME, contentValues, where,null );
    }

    public boolean deleteData(String ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(TABLE_NAME, COL_0 + " = '" + ID + "'", null) > 0);
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

    //Returns the ROWID of a given habit name
    public int returnID(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_0 + " FROM " + TABLE_NAME + " WHERE " + COL_1  + " = '" + name + "'";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() == 0){ //A table with empty rows will not have any data
            Log.d("E", "Error: No ID found to be returned!");
        }
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex("ID"));

    }

    public Cursor getItem(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_0 + " = '" + ID + "'";
        Cursor rowData = db.rawQuery(query, null);
        return rowData;
    }
}