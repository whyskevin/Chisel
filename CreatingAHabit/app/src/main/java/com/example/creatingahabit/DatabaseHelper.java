package com.example.creatingahabit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper
{
    //Database Name
    public static final String DATABASE_NAME = "Habits.db";

    //Table Names
    public static final String HT_NAME = "Habit_Table";
    public static String HR_NAME = "Habit_Record"; //Name will act as a placeholder as each new Habit will use an ID as its name

    //Habit_Table Column Names
    public static final String HABIT_ID = "ID";
    public static final String HABIT_NAME = "NAME";
    public static final String HABIT_DESCRIPTION = "DESCRIPTION";
    public static final String HABIT_FREQUENCY = "FREQUENCY";
    public static final String HABIT_TIME_PERIOD = "TIME_PERIOD";

    //Habit_Record Column Names
    public static final String HR_ID = "HR_ID"; //Don't know the significance of having this. Each record will have an unique date that can be used as a key
    public static final String DATE = "DATE";
    public static final String COMPLETE = "COMPLETE";
    public static final String NOTE = "NOTE";

    //Create Table Statements
    public static final String CREATE_TABLE_HABIT_TABLE = "CREATE TABLE " + HT_NAME + " ( " + HABIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + HABIT_NAME + " TEXT UNIQUE, "
                                                                                            + HABIT_DESCRIPTION + " TEXT, " + HABIT_FREQUENCY + " INTEGER, " + HABIT_TIME_PERIOD + " TEXT )";

    public static String CREATE_TABLE_HABIT_RECORD = "CREATE TABLE " + HR_NAME + " ( " + HR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " TEXT, " + COMPLETE + " INTEGER, " + NOTE + " TEXT )";


    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 5);}

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
        sqLiteDatabase.execSQL(CREATE_TABLE_HABIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HT_NAME );
        onCreate(sqLiteDatabase);
    }

    public void dropTable(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + tableName + " ");
    }

    //-----Habit_Table methods-----

    public boolean insertDataToHT( String name, String description, String frequency, String spinnerFrequency){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(name.equals("") || frequency.equals("")) {
            return false;
        }
        contentValues.put(HABIT_NAME, name);
        contentValues.put(HABIT_DESCRIPTION, description);
        contentValues.put(HABIT_FREQUENCY, Integer.parseInt(frequency));
        contentValues.put(HABIT_TIME_PERIOD, spinnerFrequency);

        long result =  db.insert(HT_NAME, null, contentValues); //Returns -1 if data is not inserted
        if(result == -1)
            return false;
        else {
            System.out.println("Inserted:" + contentValues.toString());
            //Once the Habit is inserted into the Habit_Table, we need to create a Habit_Record for it.
            HR_NAME = "Table_" + Integer.toString(returnIDFromHT(name)); //Given the habit's name, we query for the ID PK. This will be the name of the new table.
            //Creates a Habit_Record of the habit name
            CREATE_TABLE_HABIT_RECORD = "CREATE TABLE " + HR_NAME + " ( " + HR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " TEXT UNIQUE, " + COMPLETE + " INTEGER, " + NOTE + " TEXT )";
            db.execSQL(CREATE_TABLE_HABIT_RECORD);
            Date currentTime = Calendar.getInstance().getTime();
            //Inserts first row. This is holds the current date
            Log.d("I", "INSERT_HT: TODAY'S DATE IS " + currentTime.toString());
            insertDataToHR(HR_NAME, currentTime.toString(), "0", "");
            return true;
        }

    }

    public void updateHT(Integer ID, String name, String description, String frequency, String spinnerFrequency){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HABIT_NAME, name);
        contentValues.put(HABIT_DESCRIPTION, description);
        contentValues.put(HABIT_FREQUENCY, Integer.parseInt(frequency));
        contentValues.put(HABIT_TIME_PERIOD, spinnerFrequency);

        String where = HABIT_ID + "= '" + ID + "'";
        db.update(HT_NAME, contentValues, where,null );
    }

    public boolean deleteFromHT(String ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(HT_NAME, HABIT_ID + " = '" + ID + "'", null); //Returns # of affected rows. Checks > 0 to see if SQL worked
        if(result > 0){ //The row was deleted. Now we must delete the Habit_Records table
            Log.d("E", "Success! ID" + ID + " was deleted!");
            dropTable("Table_" + ID);
        }else{
            Log.d("E", "Error: " + ID + " was not deleted. Check corresponding Habit_Record.");
        }
        return (result > 0);
    }

    //Habit_Table Read Functions
    public Cursor getAllDataHT(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + HT_NAME, null);
    }

    public Cursor readAllDataHT() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + HT_NAME;
        Cursor cursor =  db.rawQuery(query, null);
        return cursor;
    }

    //Habit_Table
    //Returns the ROWID of a given habit name from Habit_Table
    public int returnIDFromHT(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + HABIT_ID + " FROM " + HT_NAME + " WHERE " + HABIT_NAME  + " = '" + name + "'";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() == 0){ //A table with empty rows will not have any data
            Log.d("E", "Error: No ID found to be returned!");
        }
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex("ID"));
    }

    //Habit_Table Accessor Function
    public Cursor getRecordFromHT(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + HT_NAME + " WHERE " + HABIT_ID + " = '" + ID + "'";
        return db.rawQuery(query, null);
//        return rowData;
    }


    //-----Habit_Record Functions-----

    //Changes variable HR_NAME.
    public void changeTableName(String newTableName){
        HR_NAME = newTableName;
    }

    public boolean insertDataToHR(String tableName, String date, String completed, String note){
//        String habitRecordName = habit_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(tableName.equals("") || date.equals("")) {
            return false;
        }
        contentValues.put(DATE, date);
        contentValues.put(COMPLETE, Integer.parseInt(completed));
        contentValues.put(NOTE, note);

        long result =  db.insert(tableName, null, contentValues); //Returns -1 if data is not inserted
        if(result == -1)
            return false;
        else {
            Log.d("I", "Inserted:" + contentValues.toString());
            return true;
        }
    }

    public void checkCompletion(int habit_ID, String date, boolean completed){
//        String tableName = "Table_" + String.valueOf(habit_ID);
//        String check;
//        SQLiteDatabase db = this.getWritableDatabase();
//        if(completed) {
//            check = "IF EXISTS (SELECT * FROM " + tableName + " WHERE DATE = " + date + " ) "
//                    + "BEGIN " +
//                    " UPDATE  SET COMPLETED = 1 WHERE DATE = " + date + " END "
//            + "  ELSE\n" +
//                    "      BEGIN\n" +
//                    "        INSERT INTO " + tableName + " (DATE, COMPLETE, NOTE) VALUES (" + date + ", 1, \" \") " +
//                    "       END";
//        }else{
//            check = "IF EXISTS (SELECT * FROM " + tableName + " WHERE DATE = " + date + " ) "
//                    + "BEGIN " +
//                    " UPDATE  SET COMPLETED = 0 WHERE DATE = " + date + " END ";
//                    + "  ELSE\n" +
//                    "      BEGIN\n" +
//                    "        INSERT INTO " + tableName + " (DATE, COMPLETE, NOTE) VALUES (" + date + ", 0, \" \") " +
//                    "       END";
//        }
//        db.execSQL(check);
    }

    public boolean deleteFromHR(String habitName, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        String habitRecordName = String.valueOf(returnIDFromHT(habitName));
        return (db.delete(habitRecordName, DATE + " = '" + date + "'", null) > 0);
    }

    public Cursor getAllDataHR(String habitName){
        SQLiteDatabase db = this.getWritableDatabase();
        String habitRecordName = String.valueOf(returnIDFromHT(habitName));
        return db.rawQuery("SELECT * FROM " + habitRecordName, null);
    }

    public Cursor getRecordFromHR(String habitName, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        String habitRecordName = String.valueOf(returnIDFromHT(habitName));
        return db.rawQuery("SELECT * FROM " + habitRecordName + " WHERE DATE = " + date, null );
    }

}