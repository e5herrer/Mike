package esequielherrera.mike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/24/14.
 */
public class DBRoutineHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myRoutines.db",
    TABLE_ROUTINE = "routine",
    TABLE_WORKOUT = "workout",
    KEY_ID = "id ",
    KEY_NAME = "name ",
    KEY_START_DATE = "startDate ",
    KEY_END_DATE = "endDate ",
    KEY_START_WEIGHT = "startWeight ",
    KEY_END_WEIGHT = "endWeight ",
    KEY_LAST_MODIFIED = "lastModified ",
    KEY_TIME_STAMP = "timeStamp ",
    CREATE_TABLE = "CREATE TABLE " + TABLE_ROUTINE + " (" + KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_NAME + "TEXT," + KEY_START_DATE + "TEXT," + KEY_END_DATE + "TEXT," +
            KEY_START_WEIGHT + "INTEGER," + KEY_END_WEIGHT + "INTEGER," + KEY_LAST_MODIFIED + "TEXT," +
            KEY_TIME_STAMP + "TEXT)",
    KEY_ROUTINE_ID = "routineID ",
    TAG = "RoutineDBHandler";


    public DBRoutineHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        Log.i(TAG, CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
        Log.i(TAG, "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTINE);
        onCreate(db);
    }

    public void addRoutine(Routine routine){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        String date = new Date().toString();

        values.put(KEY_NAME, routine.getName());
        values.put(KEY_START_DATE, routine.getStartDate());
        values.put(KEY_END_DATE, routine.getEndDate());
        values.put(KEY_START_WEIGHT, routine.getStartWeight());
        values.put(KEY_END_WEIGHT, routine.getEndWeight());
        values.put(KEY_LAST_MODIFIED, date);
        values.put(KEY_TIME_STAMP, date);

        routine.setId((int)db.insert(TABLE_ROUTINE, null, values));
        db.close();
    }

    public List<Routine> getAllRoutines(){

        List<Routine> routines = new ArrayList<Routine>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ROUTINE, null);

        int temp = cursor.getCount();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Routine routine = new Routine(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getInt(4), cursor.getInt(5), cursor.getString(6), cursor.getString(7));
                routines.add(routine);
            } while (cursor.moveToNext());
        }
        return routines;
    }


    public Routine getRoutine(int id){
        Routine routine = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROUTINE, new String[] {KEY_ID, KEY_NAME, KEY_START_DATE, KEY_END_DATE,
                KEY_START_WEIGHT, KEY_END_WEIGHT, KEY_LAST_MODIFIED, KEY_TIME_STAMP}, KEY_ID + "=?", new String[] { String.valueOf(id)}, null, null, null, null);

        int temp = cursor.getCount();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routine = new Routine(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getInt(4), cursor.getInt(5), cursor.getString(6), cursor.getString(7));
        }
        db.close();
        return routine;
    }

    public void deleteRoutine(Routine routine){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ROUTINE, KEY_ID + "=?", new String[] {String.valueOf(routine.getId())});
        db.delete(TABLE_WORKOUT, KEY_ROUTINE_ID + "=?", new String[] {String.valueOf(routine.getId())});
        db.close();

    }

    public int getRoutineCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ROUTINE, null);
        cursor.close();
        db.close();
        return cursor.getCount();
    }

    public int updateRoutine(Routine routine){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, routine.getName());
        values.put(KEY_START_DATE, routine.getStartDate());
        values.put(KEY_END_DATE, routine.getEndDate());
        values.put(KEY_START_WEIGHT, routine.getStartWeight());
        values.put(KEY_END_WEIGHT, routine.getEndWeight());
        values.put(KEY_LAST_MODIFIED, new Date().toString());

        return db.update(TABLE_ROUTINE, values, KEY_ID + "=?", new String[] {String.valueOf(routine.getId())});
    }

    public Routine getLastModified(){

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROUTINE, new String[] {KEY_ID, KEY_NAME, KEY_START_DATE, KEY_END_DATE,
                KEY_START_WEIGHT, KEY_END_WEIGHT, "MAX( " + KEY_LAST_MODIFIED + ")", KEY_TIME_STAMP}, null, null, null, null, null);
        if(cursor!=null && cursor.getCount() > 0){
            cursor.moveToFirst();
            return new Routine(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getInt(4), cursor.getInt(5), cursor.getString(6), cursor.getString(7));

        }
        cursor.close();
        db.close();
        return null;

    }

}
