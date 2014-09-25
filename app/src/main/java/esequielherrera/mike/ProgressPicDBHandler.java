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
public class ProgressPicDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myProgressPics",
    TABLE_PROGRESS_PIC = "ProgressPic",
    KEY_ID = "id",
    KEY_ROUTINE_ID = "picId",
    KEY_URI = "uri",
    KEY_DATE = "date";


    public ProgressPicDBHandler (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PROGRESS_PIC + "(" + KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_ROUTINE_ID + "INTEGER," + KEY_URI + "TEXT," + KEY_DATE + "TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //db.execSQL("DROP TABLE IF EXIST " + TABLE_ROUTINE);
        onCreate(db);
    }

    public void addProgressPic(ProgressPic pic){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ROUTINE_ID, pic.getroutineId());
        values.put(KEY_URI, pic.getUri());
        values.put(KEY_DATE, pic.getDate());

        db.insert(TABLE_PROGRESS_PIC, null, values);
        db.close();
    }

    public List<ProgressPic> getAllProgressPics(){
        List<ProgressPic> pics = new ArrayList<ProgressPic>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROGRESS_PIC, null);

        if (cursor != null || cursor.moveToFirst()) {
            do {
                ProgressPic pic = new ProgressPic(cursor.getInt(0), cursor.getInt(1),
                        cursor.getString(2), cursor.getString(3));
                pics.add(pic);
            } while (cursor.moveToNext());
        }
        return pics;
    }

    public ProgressPic getProgressPic(int id){
        ProgressPic pic = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROGRESS_PIC, new String[] {KEY_ID, KEY_ROUTINE_ID, KEY_URI, KEY_DATE},
                KEY_ID + "=?", new String[] { String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            pic = new ProgressPic(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3));
        }
        db.close();
        return pic;
    }

    public void deleteProgressPic(ProgressPic pic){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PROGRESS_PIC, KEY_ID + "=?", new String[] {String.valueOf(pic.getProgressPicId())});
        db.close();
    }

    public int getProgressPicCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROGRESS_PIC, null);
        cursor.close();
        db.close();
        return cursor.getCount();
    }

    public int updateProgressPic(ProgressPic pic){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ROUTINE_ID, pic.getroutineId());
        values.put(KEY_URI, pic.getUri());
        values.put(KEY_DATE, pic.getDate());

        return db.update(TABLE_PROGRESS_PIC, values, KEY_ID + "=?", new String[] {String.valueOf(pic.getProgressPicId())});
    }
}
