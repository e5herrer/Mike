package esequielherrera.mike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/24/14.
 */
public class DBRoutineHelper extends SQLiteOpenHelper {

    private Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myRoutines.db",
    TABLE_ROUTINE = "routine",
    KEY_ID = "id ",
    KEY_NAME = "name ",
    KEY_START_DATE = "startDate ",
    KEY_LAST_MODIFIED = "lastModified ",
    KEY_BEFORE_PIC = "beforePic ",
    KEY_AFTER_PIC = "afterPic ",
    KEY_TIME_STAMP = "timeStamp ",
    KEY_CREATE_TABLE = "CREATE TABLE " + TABLE_ROUTINE + " (" + KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_NAME + "TEXT," + KEY_START_DATE + "TEXT," + KEY_LAST_MODIFIED + "TEXT DEFAULT CURRENT_TIMESTAMP," + KEY_BEFORE_PIC + "TEXT," +
            KEY_AFTER_PIC + "TEXT," + KEY_TIME_STAMP +  "DATETIME DEFAULT CURRENT_TIMESTAMP)",
    TAG = "RoutineDBHandler";


    public DBRoutineHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        Log.i(TAG, KEY_CREATE_TABLE);
        db.execSQL(KEY_CREATE_TABLE);
        Log.i(TAG, "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTINE);
        onCreate(db);
    }

    public void addRoutine(Routine routine){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, routine.getName());
        values.put(KEY_BEFORE_PIC, routine.getBeforePic());
        values.put(KEY_AFTER_PIC, routine.getAfterPic());

        routine.setId((int)db.insert(TABLE_ROUTINE, null, values));
        db.close();
    }

    /**
     *
     * @return - list of routines in descending order of time they were created
     */
    public List<Routine> getAllRoutines(){

        List<Routine> routines = new ArrayList<Routine>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROUTINE, null, null, null, null, null, KEY_TIME_STAMP + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                do {
                    Routine routine = new Routine(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            cursor.getString(4), cursor.getString(5));
                    routines.add(routine);
                } while (cursor.moveToNext());
            }
            catch(Exception e){
                //The empty set was found
            }
        }
        return routines;
    }


    public Routine getRoutine(int id){
        Routine routine = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROUTINE, new String[] {KEY_ID, KEY_NAME, KEY_START_DATE,
                KEY_LAST_MODIFIED, KEY_BEFORE_PIC, KEY_AFTER_PIC}, KEY_ID + "=?", new String[] { String.valueOf(id)}, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routine = new Routine(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5));
        }
        db.close();
        return routine;
    }

    public void deleteRoutine(Routine routine){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ROUTINE, KEY_ID + "=?", new String[] {String.valueOf(routine.getId())});
        db.close();
        DBWorkoutHelper workoutDB = new DBWorkoutHelper(context);
        List<Workout> workouts = workoutDB.getRoutineWorkouts(routine.getId());
        for(Workout w : workouts){
            workoutDB.deleteWorkout(w);
        }
        DBProgressPicHandler picDB = new DBProgressPicHandler(context);
        List<ProgressPic> pics = picDB.getAllRoutinePics(routine.getId());
        for(ProgressPic p : pics){
            picDB.deleteProgressPic(p);
            ProgressPic.deleteGallaryImage(context, p.getPath());
        }
    }

    public int updateRoutine(Routine routine){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, routine.getName());
        values.put(KEY_START_DATE, routine.getStartDate());
        values.put(KEY_LAST_MODIFIED, getCurrentDate());

        return db.update(TABLE_ROUTINE, values, KEY_ID + "=?", new String[] {String.valueOf(routine.getId())});
    }

    public Routine getLastModified(){

        SQLiteDatabase db = getReadableDatabase();
        Routine routine = null;
        Cursor cursor = db.query(TABLE_ROUTINE, new String[] {KEY_ID, KEY_NAME, KEY_START_DATE, KEY_LAST_MODIFIED,
                KEY_BEFORE_PIC, KEY_AFTER_PIC, "MAX( " + KEY_LAST_MODIFIED + ")", KEY_TIME_STAMP}, null, null, null, null, null);
        if(cursor!=null && cursor.getCount() > 0){
            cursor.moveToFirst();
            routine =  new Routine(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5));

            cursor.close();

        }
        db.close();
        return routine;

    }

//    private void resetTable(){
//        SQLiteDatabase db = getWritableDatabase();
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTINE);
//        db.execSQL(KEY_CREATE_TABLE);
//        db.close();
//    }

    private String getCurrentDate(){
        DateFormat df = new SimpleDateFormat("dd MM yyyy");
        return df.format(new Date());
    }

}
