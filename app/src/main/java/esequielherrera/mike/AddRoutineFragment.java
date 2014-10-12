package esequielherrera.mike;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import java.io.FileNotFoundException;
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


        //Used for modifying a routine to prepopulate the fields
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
                    for (int i = 0; i < pics.size(); i++) {
                        pic = new ProgressPic(routine.getId(), pics.get(i).toString(), date);
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
                selectImage(gallery);
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

        if(fileName == null || fileName.equals("")){
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
     * @param g gallery the images will be inerted into
     */
    private void selectImage(LinearLayout g) {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        final LinearLayout gallery = g;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Photo Source");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = null;
                    try {
                        file = createImageFile();
                    }
                    catch(IOException e){
                        //error while creating file
                    }

                    if(file != null) {
                        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        intent.putExtra(MediaStore.ACTION_IMAGE_CAPTURE, Uri.fromFile(file));

                        //This check is needed for intents to see if you have the appropriate component
                        //to handle the intent or else the app will crash
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(intent, REQUEST_CAMERA);
                            pics.add(Uri.fromFile(file));
                        }
                    }
                    else{
                        //tell the user they can't capture images.
                    }
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

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CAMERA:
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    Bitmap largerImage=Bitmap.createScaledBitmap(image, 200, 250, true);
                    ImageView thumbnail = new ImageView(getActivity());
                    thumbnail.setImageBitmap(largerImage);
                    thumbnail.setPadding(5,5,5,5);
                    gallery.addView(thumbnail);
            }
        }
        else{
            Toast.makeText(getActivity(), "File upload failed", Toast.LENGTH_SHORT).show();
        }
    }

    /* Input:
       @filePath: The absolute path of where the image is stored
       Description:To display the pictures in the android gallery you need to add the meta data into the
       MediaStore
     */
    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File storageDir = new File(root.getAbsolutePath() + "/ProgressPics/");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    public void setRoutine(Routine routine){
        this.routine = routine;
    }

        /*
    private FileOutputStream obtainStream(Context context, String dest){
        FileOutputStream fos;
        try {
            fos =  context.openFileOutput(dest, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return fos;
    }
    */
}

