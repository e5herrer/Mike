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
 * Created by esequielherrera-ortiz on 12/17/14.
 */
public class DBWorkoutHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myRoutines.db",
            TABLE_WORKOUT = "workout",
            KEY_ID = "id ",
            KEY_ROUTINE_ID = "routineID ",
            KEY_NAME = "name ",
            KEY_LAST_MODIFIED = "lastModified ",
            KEY_TIME_STAMP = "timeStamp ",
            TAG = "WorkoutDBHelper",
            QUERY_CREATE_TABLE = "CREATE TABLE " + TABLE_WORKOUT + " (" + KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_ROUTINE_ID + "INTEGER," + KEY_NAME  + "TEXT," +
                    KEY_LAST_MODIFIED + "TEXT," +  KEY_TIME_STAMP + "DATETIME DEFAULT CURRENT_TIMESTAMP)";

    private final Context context;

    public DBWorkoutHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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

    public List<Workout> getRoutineWorkouts(Routine routine){

        List<Workout> workouts = new ArrayList<Workout>();
        Workout workout;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WORKOUT + " WHERE " + KEY_ROUTINE_ID +
                " = " + routine.getId(), null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for( int i =0 ; i < cursor.getCount(); i++) {
                workout = new Workout(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3));
                workouts.add(workout);
                cursor.moveToNext();
            }
        }
        db.close();
        return workouts;
    }

    public void addWorkout(Workout workout){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ROUTINE_ID, workout.getRoutineId());
        values.put(KEY_NAME, workout.getName());
        values.put(KEY_LAST_MODIFIED, new Date().toString());

        workout.setId((int)db.insert(TABLE_WORKOUT, null, values));
        db.close();
    }

    public int updateWorkout(Workout workout){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, workout.getName());
        values.put(KEY_LAST_MODIFIED, new Date().toString());

        return db.update(TABLE_WORKOUT, values, KEY_ID + "=?", new String[] {String.valueOf(workout.getId())});
    }

    public void deleteWorkout(Workout workout){
        DBExerciseHelper exerciseDB = new DBExerciseHelper(context);
        exerciseDB.deleteWorkoutExercises(workout);

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_WORKOUT, KEY_ID + "=?", new String[] {String.valueOf(workout.getId())});
        db.close();
    }

    private void resetTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT);
        db.execSQL(QUERY_CREATE_TABLE);
        db.close();
    }

}
