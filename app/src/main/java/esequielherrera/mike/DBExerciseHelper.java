package esequielherrera.mike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/24/14.
 */
public class DBExerciseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myRoutines.db",
    TABLE_EXERCISE = "exercise",
            KEY_ID = "id ",
            KEY_WORKOUT_ID = "workoutID ",
            KEY_NAME = "name ",
            KEY_SETS = "sets ",
            KEY_REPS = "reps ",
            KEY_REST_TIME = "restTime ",
            KEY_POSITION = "position ",
            KEY_TIME_STAMP = "timeStamp ",
            TAG = "ExerciseDBHelper",
            QUERY_CREATE_TABLE = "CREATE TABLE " + TABLE_EXERCISE + " (" + KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_WORKOUT_ID + "INTEGER," + KEY_NAME  + "TEXT," +
                    KEY_SETS + "INTEGER," + KEY_REPS + "TEXT," + KEY_REST_TIME + "INTEGER," + KEY_POSITION +
                    "INTEGER," + KEY_TIME_STAMP + "DATETIME DEFAULT CURRENT_TIMESTAMP)";


    public DBExerciseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        Log.i(TAG, QUERY_CREATE_TABLE);
        db.execSQL(QUERY_CREATE_TABLE);
        Log.i(TAG, "DB CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //db.execSQL("DROP TABLE IF EXISTS " + WORKOUT_ROUTINE);
        onCreate(db);
    }

    public void addExercise(Exercise exercise){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WORKOUT_ID, exercise.getWorkoutId());
        values.put(KEY_NAME, exercise.getName());
        values.put(KEY_SETS, exercise.getSets());
        values.put(KEY_REPS, exercise.getReps());
        values.put(KEY_REST_TIME, exercise.getRestTime());
        values.put(KEY_POSITION, exercise.getPosition());

        int id = (int)db.insert(TABLE_EXERCISE, null, values);
        exercise.setId(id);
        db.close();
    }



    public Exercise getExercise(int id){
        Exercise exercise = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_EXERCISE, new String[] {KEY_ID, KEY_WORKOUT_ID, KEY_NAME,
                KEY_REPS, KEY_SETS, KEY_REST_TIME, KEY_POSITION}, KEY_ID + "=?",
                new String[] { String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                exercise = new Exercise(cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6));
            }
        }
        db.close();
        return exercise;
    }


    public List<Exercise> getWorkoutExercises(Workout workout){

        int workoutID = workout.getId();
        Exercise exercise;
        List<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_EXERCISE, null, KEY_WORKOUT_ID + "=?",
                new String[] { String.valueOf(workoutID)}, null, null, KEY_POSITION + " ASC", null);


        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for( int i =0 ; i < cursor.getCount(); i++) {
                exercise = new Exercise(cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6));
                exercises.add(exercise);
                cursor.moveToNext();
            }
        }

        db.close();
        return exercises;
    }


    public void deleteExercise(Exercise exercise){
        SQLiteDatabase db = getWritableDatabase();
        String id = String.valueOf(exercise.getWorkoutId());
        db.delete(TABLE_EXERCISE, KEY_ID + "=?", new String[] {id});
        db.close();
    }

    public void deleteWorkoutExercises(Workout workout){
        SQLiteDatabase db = getWritableDatabase();
        String workoutID = String.valueOf(workout.getId());

        db.delete(TABLE_EXERCISE, KEY_WORKOUT_ID + "=?", new String[] {workoutID});
        db.close();
    }

    public int updateExercise(Exercise exercise){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_WORKOUT_ID, exercise.getWorkoutId());
        values.put(KEY_NAME, exercise.getName());
        values.put(KEY_SETS, exercise.getSets());
        values.put(KEY_REPS, exercise.getReps());
        values.put(KEY_REST_TIME, exercise.getRestTime());
        values.put(KEY_POSITION, exercise.getPosition());

        return db.update(TABLE_EXERCISE, values, KEY_ID + "=?", new String[] {String.valueOf(exercise.getId())});
    }



    private void resetTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        db.execSQL(QUERY_CREATE_TABLE);
        db.close();
    }
}

