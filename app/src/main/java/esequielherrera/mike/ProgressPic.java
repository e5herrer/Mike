package esequielherrera.mike;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by esequielherrera-ortiz on 9/24/14.
 */
public class ProgressPic {

    private String path;


    public ProgressPic(String path){
        this.path = path;
    }

    public static int getPicOrientation(Context context, String path){
        int rotate = 90;
        try {
            Uri uri = Uri.parse(path);
            context.getContentResolver().notifyChange(uri, null);
            File imageFile = new File(path);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static String copyToApplicationFolder(Context context, String path){

        Bitmap selectedImage = BitmapFactory.decodeFile(path);

        //Image needs to be rotated to portrait view
        Matrix matrix = new Matrix();
        matrix.postRotate(-ProgressPic.getPicOrientation(context, path));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(selectedImage, selectedImage.getWidth(), selectedImage.getHeight() , true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);

        String imageFile = createImageFile();

        FileOutputStream fos;
        try {
            // fos = openFileOutput(filename, Context.MODE_PRIVATE);

            fos = new FileOutputStream(imageFile);

            // Use the compress method on the BitMap object to write image to the OutputStream
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageFile;
    }

    public static void fixOrientationToPortrait(Context context, String path) {

        Bitmap pic = BitmapFactory.decodeFile(path);

        Matrix picMatrix = new Matrix();
        picMatrix.postRotate(ProgressPic.getPicOrientation(context, path));
        Bitmap rotatedPic = Bitmap.createBitmap(pic, 0, 0, pic.getWidth(), pic.getHeight(), picMatrix, true);

        FileOutputStream fos;
        try {
            // fos = openFileOutput(filename, Context.MODE_PRIVATE);

            fos = new FileOutputStream(new File(path));

            // Use the compress method on the BitMap object to write image to the OutputStream
            rotatedPic.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap(int width, int height){
        Bitmap pic = BitmapFactory.decodeFile(path);
        if(pic != null) {
            return Bitmap.createScaledBitmap(pic, width, height, true);
        }
        return null;
    }

    /**
     * deleteGallaryImage - Used to delete Images in the Mike gallery folder.
     * @param context - Current context of the application. Used to get content resolver
     * @param path - Complete path to the directory
     */
    public static void deleteGallaryImage(Context context, String path){
        File file = new File(path);
        // request scan
        // Set up the projection (we only need the ID)
        String[] projection = { MediaStore.Images.Media._ID };

        // Match on the file path
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[] { file.getAbsolutePath() };

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
        } else {
            // File not found in media store DB
        }
        c.close();
    }


    /**
     * @return the absolute path of a new file
     * @throws IOException
     */

    public static String createImageFile() {
        //camera stuff
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //folder stuff
        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES), "Mike");
        imagesFolder.mkdirs();

        return new File(imagesFolder, "QR_" + timeStamp + ".png").getAbsolutePath();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String uri) {
        this.path = uri;
    }

}
