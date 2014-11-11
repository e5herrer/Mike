package esequielherrera.mike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/24/14.
 * Description- Class used to handle database CRUD related to progressPics
 */
public class DBProgressPicHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myProgressPics ",
    TABLE_PROGRESS_PIC = "ProgressPic ",
    KEY_ID = "id ",
    KEY_ROUTINE_ID = "picId ",
    KEY_URI = "uri ",
    KEY_DATE = "date ",
    KEY_CREATE_TABLE = "CREATE TABLE " + TABLE_PROGRESS_PIC + "(" + KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_ROUTINE_ID + "INTEGER," + KEY_URI + "TEXT," + KEY_DATE + "TEXT)";


    public DBProgressPicHandler(Context context){
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
     * @param pic - Pic to be added to the database
     */
    public void addProgressPic(ProgressPic pic){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ROUTINE_ID, pic.getroutineId());
        values.put(KEY_URI, pic.getPath());
        values.put(KEY_DATE, getCurrentDate());

        db.insert(TABLE_PROGRESS_PIC, null, values);
        db.close();
    }


    /**
     * @return Returns every photo in the database
     */
    public List<ProgressPic> getAllProgressPics(){
        List<ProgressPic> pics = new ArrayList<ProgressPic>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROGRESS_PIC, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ProgressPic pic = new ProgressPic(cursor.getInt(0), cursor.getInt(1),
                        cursor.getString(2), cursor.getString(3));
                pics.add(pic);
            } while (cursor.moveToNext());
        }
        return pics;
    }

    /**
     * @param id - The routine id
     * @return - List of a routines progress pics in desc order
     */
    public ArrayList<ProgressPic> getAllRoutinePics(int id){
        ArrayList<ProgressPic> pics = new ArrayList<ProgressPic>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROGRESS_PIC, new String[] {KEY_ID, KEY_ROUTINE_ID, KEY_URI, KEY_DATE},
                KEY_ROUTINE_ID + "=?", new String[] { String.valueOf(id)}, null, null, KEY_DATE + " ASC", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                pics.add(new ProgressPic(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3)));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return pics;
    }

    /**
     * @param id - The routine id
     * @return - Oldest photo belonging to routine id
     */
    public ProgressPic getBeforePic(int id){
        ProgressPic pic = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROGRESS_PIC, new String[] {KEY_ID, KEY_ROUTINE_ID, KEY_URI, KEY_DATE},
                KEY_ROUTINE_ID + "=?", new String[] { String.valueOf(id)}, null, null, KEY_DATE + " DESC", " 1");

        if (cursor != null) {
            cursor.moveToFirst();
            try {
                pic = new ProgressPic(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3));
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
        Cursor cursor = db.query(TABLE_PROGRESS_PIC, new String[] {KEY_ID, KEY_ROUTINE_ID, KEY_URI, KEY_DATE},
                KEY_ROUTINE_ID + "=?", new String[] { String.valueOf(id)}, null, null, KEY_DATE + " ASC", " 1");

        if (cursor != null) {
            cursor.moveToFirst();
            try {
                pic = new ProgressPic(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3));
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
     * @param pic - Pic to be deleted from the database
     */
    public void deleteProgressPic(ProgressPic pic){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PROGRESS_PIC, KEY_ID + "=?", new String[] {String.valueOf(pic.getProgressPicId())});
        db.close();
    }

    /**
     *
     * @return - Returns a string representation of the date of the form "dd MM yyyy"
     */
    private String getCurrentDate(){
        DateFormat df = new SimpleDateFormat("dd MM yyyy");
        return df.format(new Date());
    }

    private void resetTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRESS_PIC);
        db.execSQL(KEY_CREATE_TABLE);
        db.close();
    }

}
