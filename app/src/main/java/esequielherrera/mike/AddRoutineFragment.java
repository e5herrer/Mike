package esequielherrera.mike;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by esequielherrera-ortiz on 9/17/14.
 */

public class AddRoutineFragment extends Fragment {
    private static final int MAX_TITLE_LENGTH = 30;
    private static final int SELECT_FILE = 0;
    private static final int REQUEST_CAMERA = 1;
    private Uri selectedImage;
    private Routine routine = null;
    private List<Uri> pics = new ArrayList<Uri>();
    private LinearLayout gallery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_routine, container, false);

        //MAP XML with java
        final EditText routineName = (EditText) rootView.findViewById(R.id.routineName);
        final EditText currentWeight = (EditText) rootView.findViewById(R.id.currentWeight);
        final Button saveButton = (Button) rootView.findViewById(R.id.saveButton);
        final Button cancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        final Button addButton = (Button) rootView.findViewById(R.id.addButton);
        gallery = (LinearLayout) rootView.findViewById(R.id.horizontalGallery);


        //Used for modifying a routine to pre-populate the fields
        if(this.routine != null){
            routineName.setText(this.routine.getName());
            currentWeight.setText(this.routine.getStartWeight());
            ((RelativeLayout)addButton.getParent()).removeView(addButton);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                routine = createNewRoutine(getActivity(), routineName, currentWeight);
                if (routine != null) {
                    DBProgressPicHandler db = new DBProgressPicHandler(getActivity());
                    ProgressPic pic;
                    String date = new Date().toString();
                    for (Uri p : pics) {
                        pic = new ProgressPic(routine.getId(), p.toString(), date);
                        db.addProgressPic(pic);
                    }
                    ((MainActivity)getActivity()).startAddWorkoutFragment(routine);
                }
            }
        });
        cancelButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                ((MainActivity)getActivity()).startRoutineFragment();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                selectImage();
            }
        });

        return rootView;
    }


    /*
        Description: Takes in the application context and the EditText field and stored the
        routine in the internal storage. If successfully created changes the save button onClick
        function.
     */
    private Routine createNewRoutine(Context context, EditText fName, EditText cWeight){

        DBRoutineHelper db = new DBRoutineHelper(context);
        String fileName = fName.getText().toString().trim();
        String startWeight = cWeight.getText().toString();
        Routine routine;

        fileName = fileName.substring(0, Math.min(fileName.length(), MAX_TITLE_LENGTH));

        if(fileName.equals("")){
            Toast.makeText(context, "Please insert a routine name", Toast.LENGTH_LONG).show();
            fName.requestFocus();
            return null;
        }
        if(startWeight.equals("")){
            startWeight = "0";
        }
        try {
            routine = new Routine(fileName, null, Integer.decode(startWeight));
        }
        catch(NumberFormatException e){
            Toast.makeText(context, "Current Weight only accepts integers", Toast.LENGTH_LONG).show();
            cWeight.requestFocus();
            return null;
        }
        db.addRoutine(routine);
        return routine;
    }


    /** Name: selectImage
     * Description: This method builds the dialogue box for selecting an image.
     */
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Photo Source");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                    Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    try {
                        selectedImage = Uri.fromFile(createImageFile());
                    }
                    catch(IOException e){
                        return;
                    }

                    imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
                    startActivityForResult(imageIntent, REQUEST_CAMERA);

                }
                else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedURI;

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CAMERA:
                    selectedURI = selectedImage;

                    pics.add(selectedURI);

                    Bitmap pic = BitmapFactory.decodeFile(selectedURI.getPath());

                    //Image needs to be rotated to portrait view
                    Matrix picMatrix = new Matrix();
                    picMatrix.postRotate(getCameraPhotoOrientation(getActivity(), selectedURI, selectedURI.getPath()));
                    Bitmap picScaled = Bitmap.createScaledBitmap(pic, 250, 200 , true);
                    Bitmap rotatedPic = Bitmap.createBitmap(picScaled , 0, 0, picScaled .getWidth(), picScaled .getHeight(), picMatrix, true);

                    ImageView selectedThumbnail4 = new ImageView(getActivity());
                    selectedThumbnail4.setImageBitmap(rotatedPic);
                    selectedThumbnail4.setPadding(5,5,5,5);
                    gallery.addView(selectedThumbnail4,0);

                    break;

                case SELECT_FILE:
                    selectedURI = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(
                            selectedURI, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);

                    //Storing Copy of image into application folder
                    pics.add(saveToInternalSorage(selectedImage));

                    //Image needs to be rotated to portrait view
                    Matrix matrix = new Matrix();
                    matrix.postRotate(getCameraPhotoOrientation(getActivity(), selectedURI, filePath));
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(selectedImage, 250, 200 , true);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);

                    ImageView selectedThumbnail = new ImageView(getActivity());
                    selectedThumbnail.setImageBitmap(rotatedBitmap);
                    selectedThumbnail.setPadding(5,5,5,5);
                    gallery.addView(selectedThumbnail,0);
                    break;
            }



        }
        else{
            //Toast.makeText(getActivity(), "File upload canceled", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Description: Calculates photo orientation and returns degree that needs to be turned for portrait.
     * @param context - activity context
     * @param imageUri - uri to be stored
     * @param imagePath - absolute path
     * @return - returns degree that image is rotated
     */
    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

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

    /**
     * @return temp file to store camera captured image
     * @throws IOException
     */

    private File createImageFile() throws IOException {
        //camera stuff
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //folder stuff
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Mike");
        imagesFolder.mkdirs();

        return new File(imagesFolder, "QR_" + timeStamp + ".png");
    }

    private Uri saveToInternalSorage(Bitmap bitmapImage){
        File imageFile;
        try {
            imageFile = createImageFile();
        }
        catch(IOException e){
            return null;
        }

        FileOutputStream fos;
        try {
            // fos = openFileOutput(filename, Context.MODE_PRIVATE);

            fos = new FileOutputStream(imageFile);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Uri.fromFile(imageFile);
    }


    public void setRoutine(Routine routine){
        this.routine = routine;
    }

}

