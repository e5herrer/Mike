package esequielherrera.mike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by esequielherrera-ortiz on 10/10/14.
 */
public class DBLogHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myWorkoutLog.db",
            TABLE_LOG_ENTRY = "logEntry",
            KEY_ID = "id ",
            KEY_WORKOUT_ID = "logEntryID ",
            KEY_SET_NUM = "setsNum ",
            KEY_WEIGHT = "weight ",
            KEY_REPS = "reps ",
            KEY_NOTES = "notes ",
            KEY_REST_TIME = "restTime ",
            KEY_TIME_STAMP = "timeStamp ",
            TAG = "WorkoutDBHelper",
            KEY_CREATE_TABLE = "CREATE TABLE " + TABLE_LOG_ENTRY + " (" + KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_WORKOUT_ID + "INTEGER," + KEY_SET_NUM + "INTEGER," + KEY_WEIGHT + "TEXT," +
                    KEY_REPS + " INTEGER," + KEY_NOTES + "TEXT," + KEY_REST_TIME + "INTEGER," +
                    KEY_TIME_STAMP + "DATETIME DEFAULT CURRENT_TIMESTAMP)";


    public DBLogHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        Log.i(TAG, KEY_CREATE_TABLE);
        db.execSQL(KEY_CREATE_TABLE);
        Log.i(TAG, "DB CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onCreate(db);
    }

    public void addLog(LogEntry logEntry){
        int id;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WORKOUT_ID, logEntry.getWorkoutId());
        values.put(KEY_SET_NUM, logEntry.getSetNum());
        values.put(KEY_WEIGHT, logEntry.getWeight());
        values.put(KEY_REPS, logEntry.getReps());
        values.put(KEY_NOTES, logEntry.getNotes());
        values.put(KEY_REST_TIME, logEntry.getRestTime());

        id = (int)db.insert(TABLE_LOG_ENTRY, null, values);
        db.close();
        logEntry.setId(id);
    }


    public List<LogEntry> getExerciseLogs(int id){

        List<LogEntry> logs = new ArrayList<LogEntry>();
        LogEntry logEntry;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOG_ENTRY, new String[] {KEY_ID, KEY_WORKOUT_ID, KEY_SET_NUM, KEY_WEIGHT,
                KEY_REPS, KEY_NOTES, KEY_REST_TIME, KEY_TIME_STAMP}, KEY_WORKOUT_ID + "=?", new String[] { String.valueOf(id)}, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for( int i =0 ; i < cursor.getCount(); i++) {
                logEntry = new LogEntry(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
                        cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7));
                logs.add(logEntry);
                cursor.moveToNext();
            }
        }
        db.close();
        return logs;
    }

    public LogEntry getMostRecentLog(int id, int setNum){

        LogEntry logEntry = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOG_ENTRY + " WHERE " + KEY_WORKOUT_ID +
        "=? AND " + KEY_SET_NUM + " =? ORDER BY " + KEY_TIME_STAMP + " DESC LIMIT 1", new String [] {id+"",setNum+""});

        if (cursor != null) {
            if(cursor.moveToFirst()) {

                //Because we use Order by Desc cursor can return the empty set resulting in null pointer exception.
                try {
                    logEntry = new LogEntry(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
                            cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7));
                }
                catch(Exception e){
                    return null;
                }
            }
        }

        db.close();
        return logEntry;
    }

    /**
     * return: Returns an ArrayList<LogEntry> with 1 LogEntry per workout. The list is ordered in descending order submitted.
     *          Used to order workouts by last modified.
     */
    public List<LogEntry> getWorkoutsByLastModified(){

        List<LogEntry> workoutLogs = new ArrayList<LogEntry>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOG_ENTRY, new String[]{KEY_ID, KEY_WORKOUT_ID, KEY_SET_NUM, KEY_WEIGHT,
                KEY_REPS, KEY_NOTES, KEY_REST_TIME, KEY_TIME_STAMP}, null, null, KEY_WORKOUT_ID, null, KEY_TIME_STAMP + " DESC");

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                //Because we use Order by Desc cursor can return the empty set resulting in null pointer exception.
                try {
                    do {
                        workoutLogs.add(new LogEntry(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
                                cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7)));
                    } while(cursor.moveToNext());

                }
                catch(Exception e){
                    return null;
                }
            }
        }

        db.close();
        return workoutLogs;
    }


    private void resetTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG_ENTRY);
        db.execSQL(KEY_CREATE_TABLE);
        db.close();
    }

}
