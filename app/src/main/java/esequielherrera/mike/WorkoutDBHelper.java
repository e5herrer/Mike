package esequielherrera.mike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/24/14.
 */
public class WorkoutDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myRoutines",
    TABLE_WORKOUT = "workout",
            KEY_ID = "id",
            KEY_NAME = "name",
            KEY_ROUTINE_ID = "routineID",
            KEY_EXERCISE_NAME = "excerciseName",
            KEY_REPS = "reps",
            KEY_SETS = "sets",
            KEY_REST_TIME = "restTime",
            KEY_POSITION = "position";

    public WorkoutDBHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_WORKOUT + "(" + KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_ROUTINE_ID + "INTEGER," + KEY_NAME + "TEXT," + KEY_EXERCISE_NAME + "TEXT," +
                KEY_REPS + "INTEGER," + KEY_SETS + "INTEGER," + KEY_REST_TIME + "INTEGER," + KEY_POSITION +
                "INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //db.execSQL("DROP TABLE IF EXIST " + TABLE_ROUTINE);
        onCreate(db);
    }

    public void addWorkout(Workout workout){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ROUTINE_ID, workout.getRoutineId());
        values.put(KEY_NAME, workout.getName());
        values.put(KEY_EXERCISE_NAME, workout.getExerciseName());
        values.put(KEY_REPS, workout.getReps());
        values.put(KEY_SETS, workout.getSets());
        values.put(KEY_REST_TIME, workout.getRestTime());
        values.put(KEY_POSITION, workout.getPosition());


        db.insert(TABLE_WORKOUT, null, values);
        db.close();
    }

    public List<Workout> getAllWorkouts(){
        List<Workout> workouts = new ArrayList<Workout>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WORKOUT, null);

        if (cursor != null || cursor.moveToFirst()) {
            do {
                Workout workout = new Workout(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3),
                        cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getInt(7));
                workouts.add(workout);
            } while (cursor.moveToNext());
        }
        return workouts;
    }

    public Workout getRoutine(int id){
        Workout workout = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUT, new String[] {KEY_ID, KEY_ROUTINE_ID, KEY_NAME, KEY_EXERCISE_NAME,
                KEY_REPS, KEY_SETS, KEY_REST_TIME, KEY_POSITION}, KEY_ID + "=?", new String[] { String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            workout = new Workout(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3),
                    cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getInt(7));
        }
        db.close();
        return workout;
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

    public int updateRoutine(Workout workout){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ROUTINE_ID, workout.getRoutineId());
        values.put(KEY_NAME, workout.getName());
        values.put(KEY_EXERCISE_NAME, workout.getExerciseName());
        values.put(KEY_REPS, workout.getReps());
        values.put(KEY_SETS, workout.getSets());
        values.put(KEY_REST_TIME, workout.getRestTime());
        values.put(KEY_POSITION, workout.getPosition());

        return db.update(TABLE_WORKOUT, values, KEY_ID + "=?", new String[] {String.valueOf(workout.getId())});
    }
}
