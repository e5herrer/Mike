package esequielherrera.mike;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

public class CreateRoutineFragment extends Fragment {
    private static final int SELECT_FILE = 0;
    private static final int REQUEST_CAMERA = 1;
    private String mCurrentPhotoPath;
    Routine routine = null;
    List<Uri> pics;


    public CreateRoutineFragment() {
        this.routine =  new Routine();
        this.pics = new ArrayList<Uri>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_routine, container, false);

        //MAP XML with java
        final EditText routineName = (EditText) rootView.findViewById(R.id.routineName);
        final EditText currentWeight = (EditText) rootView.findViewById(R.id.currentWeight);
        final EditText weightGoal = (EditText) rootView.findViewById(R.id.weightGoal);
        final DatePicker endDate = (DatePicker) rootView.findViewById(R.id.endDate);
        final ImageView beforePic = (ImageView) rootView.findViewById(R.id.beforePic);
        final Button saveButton = (Button) rootView.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                routine = createNewRoutine(getActivity(), routineName, currentWeight, endDate);
                if(routine != null) {
                    ProgressPicDBHandler db = new ProgressPicDBHandler(getActivity());
                    ProgressPic pic;
                    String date = new Date().toString();
                    for (int i = 0; i < pics.size(); i++) {
                        pic = new ProgressPic(routine.getId(), pics.get(i).toString(), date);
                        db.addProgressPic(pic);
                    }
                    startAddDaysFragment();
                }
            }
        });

        beforePic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                selectImage(beforePic);
            }
        });

        return rootView;
    }


    /*
        Description: Takes in the application context and the EditText field and stored the
        routine in the internal storage. If successfully created changes the save button onClick
        function.
     */
    private Routine createNewRoutine(Context context, EditText fName, EditText cWeight,
                                     DatePicker eDate){

        RoutineDBHandler db = new RoutineDBHandler(context);
        String fileName = fName.getText().toString().trim();
        String startWeight = cWeight.getText().toString().trim();
        String endDate = Integer.toString(eDate.getDayOfMonth()) + "/" +
                         Integer.toString(eDate.getMonth()) + "/" +
                         Integer.toString(eDate.getYear());


        if(fileName == null || fileName.equals("")){
            Toast.makeText(context, "Please insert a routine name", Toast.LENGTH_LONG).show();
            fName.requestFocus();
            return null;
        }
        if(startWeight.equals("")){
            startWeight = "0";
        }
        Routine routine = new Routine(fileName, endDate, Integer.decode(startWeight));
        db.addRoutine(routine);
        return routine;
    }

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

    private void selectImage(ImageView beforePic) {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Photo Source");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File file = null;
                    try {
                        file = createImageFile();
                    }
                    catch(IOException e){
                        //error while creating file
                    }

                    if(file != null) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        //This check is needed for intents to see if you have the appropriate component
                        //to handle the intent or else the app will crash
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(intent, REQUEST_CAMERA);
                            //Check if user exceeded max of 5 photos per album
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
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void startAddDaysFragment() {
        Fragment newFragment = new AddDaysFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("routineId", routine.getId());
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

    // Replace whatever is in the fragment_container view with this fragment,
    // and add the transaction to the back stack
        transaction.replace(R.id.container, newFragment);

    // Commit the transaction
        transaction.commit();
    }
}

