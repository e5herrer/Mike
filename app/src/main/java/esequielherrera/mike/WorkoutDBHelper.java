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
public class WorkoutDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myRoutines.db",
    TABLE_WORKOUT = "workout",
            KEY_ID = "id ",
            KEY_ROUTINE_ID = "routineID ",
            KEY_NAME = "name ",
            KEY_EXERCISE_NAME = "exerciseName ",
            KEY_SETS = "sets ",
            KEY_REPS = "reps ",
            KEY_REST_TIME = "restTime ",
            KEY_POSITION = "position ",
            KEY_TIME_STAMP = "timeStamp ",
            TAG = "WorkoutDBHelper",
            KEY_CREATE_TABLE = "CREATE TABLE " + TABLE_WORKOUT + " (" + KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_ROUTINE_ID + "INTEGER," + KEY_NAME + "TEXT," + KEY_EXERCISE_NAME + "TEXT," +
                    KEY_SETS + "INTEGER," + KEY_REPS + "TEXT," + KEY_REST_TIME + "INTEGER," + KEY_POSITION +
                    "INTEGER," + KEY_TIME_STAMP + "TEXT)";


    public WorkoutDBHelper (Context context){
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
        //db.execSQL("DROP TABLE IF EXISTS " + WORKOUT_ROUTINE);
        onCreate(db);
    }

    public void addWorkout(Workout workout){
        int id;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ROUTINE_ID, workout.getRoutineId());
        values.put(KEY_NAME, workout.getName());
        values.put(KEY_EXERCISE_NAME, workout.getExerciseName());
        values.put(KEY_SETS, workout.getSets());
        values.put(KEY_REPS, workout.getReps());
        values.put(KEY_REST_TIME, workout.getRestTime());
        values.put(KEY_POSITION, workout.getPosition());
        values.put(KEY_TIME_STAMP, new Date().toString());

        id = (int)db.insert(TABLE_WORKOUT, null, values);
        db.close();
        workout.setId(id);
    }

    public List<Workout> getAllWorkouts(){

        List<Workout> workouts = new ArrayList<Workout>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WORKOUT, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Workout workout = new Workout(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3),
                        cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getInt(7), cursor.getString(8));
                workouts.add(workout);
            } while (cursor.moveToNext());
        }
        return workouts;
    }

    public Workout getWorkout(int id){
        Workout workout = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUT, new String[] {KEY_ID, KEY_ROUTINE_ID, KEY_NAME, KEY_EXERCISE_NAME,
                KEY_REPS, KEY_SETS, KEY_REST_TIME, KEY_POSITION, KEY_TIME_STAMP}, KEY_ID + "=?", new String[] { String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            workout = new Workout(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3),
                    cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getInt(7), cursor.getString(8));
        }
        db.close();
        return workout;
    }

    public List<Workout> getRoutineDays(int id){

        List<Workout> days = new ArrayList<Workout>();
        Workout workout;
        SQLiteDatabase db = getReadableDatabase();
        //Gets distinct entries
        Cursor cursor = db.query(true, TABLE_WORKOUT, new String[] {KEY_ID, KEY_ROUTINE_ID, KEY_NAME, KEY_EXERCISE_NAME,
                        KEY_SETS, KEY_REPS, KEY_REST_TIME, KEY_POSITION, KEY_TIME_STAMP}, KEY_ROUTINE_ID + "=?",
                new String[] { String.valueOf(id)}, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for( int i =0 ; i < cursor.getCount(); i++) {
                workout = new Workout(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3),
                cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getInt(7), cursor.getString(8));
                days.add(workout);
                cursor.moveToNext();
            }
        }
        db.close();
        return days;
    }
    public List<Workout> getRoutineWorkouts(int id){

        Workout workout = null;
        List<Workout> workouts = new ArrayList<Workout>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUT, new String[] {KEY_ID, KEY_ROUTINE_ID, KEY_NAME, KEY_EXERCISE_NAME,
                KEY_SETS, KEY_REPS, KEY_REST_TIME, KEY_POSITION, KEY_TIME_STAMP}, KEY_ROUTINE_ID + "=?", new String[] { String.valueOf(id)}, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for( int i =0 ; i < cursor.getCount(); i++) {
                workout = new Workout(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3),
                        cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getInt(7), cursor.getString(8));
                workouts.add(workout);
                cursor.moveToNext();
            }
        }
        db.close();
        return workouts;
    }

    public void deleteWorkout(Workout workout){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_WORKOUT, KEY_ID + "=?", new String[] {String.valueOf(workout.getId())});
        db.close();
    }

    public int getWorkoutCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WORKOUT, null);
        cursor.close();
        db.close();
        return cursor.getCount();
    }

    public int updateWorkout(Workout workout){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ROUTINE_ID, workout.getRoutineId());
        values.put(KEY_NAME, workout.getName());
        values.put(KEY_EXERCISE_NAME, workout.getExerciseName());
        values.put(KEY_SETS, workout.getSets());
        values.put(KEY_REPS, workout.getReps());
        values.put(KEY_REST_TIME, workout.getRestTime());
        values.put(KEY_POSITION, workout.getPosition());

        return db.update(TABLE_WORKOUT, values, KEY_ID + "=?", new String[] {String.valueOf(workout.getId())});
    }

    private void restartTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT);
        db.execSQL(KEY_CREATE_TABLE);
        db.close();
    }
}
