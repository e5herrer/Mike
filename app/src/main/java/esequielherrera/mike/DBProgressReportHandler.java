package esequielherrera.mike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by esequielherrera-ortiz on 9/24/14.
 * Description- Class used to handle database CRUD related to progressPics
 */
public class DBProgressReportHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myProgressPics ",
    TABLE_PROGRESS_PIC = "ProgressPic ",
    KEY_ID = "id ",
    KEY_ROUTINE_ID = "routineID ",
    KEY_WEIGHT = "weight ",
    KEY_PATH = "path ",
    KEY_DATE = "date ",
    KEY_TIME_STAMP = "timeStamp ",
    KEY_CREATE_TABLE = "CREATE TABLE " + TABLE_PROGRESS_PIC + "(" + KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_ROUTINE_ID + "INTEGER," + KEY_WEIGHT + "TEXT," + KEY_PATH + "TEXT," + KEY_DATE + "TEXT," + KEY_TIME_STAMP + "DATETIME DEFAULT CURRENT_TIMESTAMP)";


    public DBProgressReportHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL(KEY_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //db.execSQL("DROP TABLE IF EXIST " + TABLE_ROUTINE);
        onCreate(db);
    }

    /**
     *
     * @param report - Report to be added to the database
     */
    public void addProgressReport(ProgressReport report){
        SQLiteDatabase db = getWritableDatabase();
        //delete old entry if it exists
        db.delete(TABLE_PROGRESS_PIC, KEY_DATE + "=?", new String[] { report.getDate()});
        ContentValues values = new ContentValues();

        if(report.getAlbum().size() == 0 && !report.getWeight().equals("")){
            values.put(KEY_ROUTINE_ID, report.getRoutineId());
            values.put(KEY_WEIGHT, report.getWeight());
            values.put(KEY_DATE, report.getDate());
            db.insert(TABLE_PROGRESS_PIC, null, values);
        }

        for (ProgressPic pic : report.getAlbum()) {
            values.put(KEY_ROUTINE_ID, report.getRoutineId());
            values.put(KEY_WEIGHT, report.getWeight());
            values.put(KEY_PATH, pic.getPath());
            values.put(KEY_DATE, report.getDate());
            db.insert(TABLE_PROGRESS_PIC, null, values);
        }
        db.close();
    }

    /**
     * @param routineID - The routine id
     * @return - List of routine ProgressReports in desc order
     */
    public ArrayList<ProgressReport> getAllRoutineReports(int routineID){
        ArrayList<ProgressReport> reports = new ArrayList<ProgressReport>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROGRESS_PIC, new String[] {KEY_ID, KEY_ROUTINE_ID, KEY_WEIGHT, KEY_PATH, KEY_DATE},
                KEY_ROUTINE_ID + "=?", new String[] { String.valueOf(routineID)}, null, null, KEY_DATE + " DESC", null);

        if (cursor != null && cursor.moveToFirst()) {
            String date = cursor.getString(4);
            ProgressReport report = new ProgressReport(cursor.getString(2), date);
            do {
                if(date.equals(cursor.getString(4))){
                    report.addPhoto(new ProgressPic(cursor.getString(3)));
                }
                else {
                    reports.add(report);
                    date = cursor.getString(4);
                    report = new ProgressReport(cursor.getString(2), date);
                    report.addPhoto(new ProgressPic(cursor.getString(3)));
                }
            } while(cursor.moveToNext());
            reports.add(report);
        }
        cursor.close();
        db.close();
        return reports;
    }

    /**
     * @param id - The routine id
     * @return - Oldest photo belonging to routine id
     */
    public ProgressPic getBeforePic(int id){
        ProgressPic pic = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROGRESS_PIC, new String[] {KEY_PATH},
                KEY_ROUTINE_ID + "=?", new String[] { String.valueOf(id)}, null, null, KEY_DATE + " DESC", " 1");

        if (cursor != null) {
            cursor.moveToFirst();
            try {
                pic = new ProgressPic(cursor.getString(0));
                cursor.close();
            }
            catch(Exception e){
                return null;
            }
        }
        db.close();
        return pic;
    }


    /**
     * @param id - The routine id
     * @return - Most recent photo associated with the routine id
     */
    public ProgressPic getAfterPic(int id){
        ProgressPic pic = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROGRESS_PIC, new String[] {KEY_PATH},
                KEY_ROUTINE_ID + "=?", new String[] { String.valueOf(id)}, null, null, KEY_DATE + " ASC", " 1");

        if (cursor != null) {
            cursor.moveToFirst();
            try {
                pic = new ProgressPic(cursor.getString(0));
                cursor.close();
            }
            catch(Exception e){
                    return null;
            }
        }
        db.close();
        return pic;
    }

    /**
     *
     * @param report - Pic to be deleted from the database
     */
    public void deleteProgressReport(ProgressReport report){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PROGRESS_PIC, KEY_ID + "=?", new String[] {String.valueOf(report.getId())});
        db.close();
    }

    /**
     *
     * @return - Returns a string representation of the date of the form "dd MM yyyy"
     */
    private String getCurrentDate(){
        DateFormat df = DateFormat.getDateInstance();
        return df.format(new Date());
    }

    private void resetTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRESS_PIC);
        db.execSQL(KEY_CREATE_TABLE);
        db.close();
    }

}
