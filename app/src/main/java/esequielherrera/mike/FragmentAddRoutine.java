package esequielherrera.mike;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by esequielherrera-ortiz on 9/17/14.
 */

public class FragmentAddRoutine extends Fragment {
    private static final int MAX_TITLE_LENGTH = 30;
 //   private static final int SELECT_FILE = 0;
    private static final int REQUEST_CAMERA = 1;
    private String selectedImage;
    private Routine routine;
    private List<String> pics = new ArrayList<String>();
    private LinearLayout gallery;
    private EditText routineName;
    private EditText currentWeight;

    String beforePic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //MAP XML with java
        View rootView = inflater.inflate(R.layout.fragment_add_routine, container, false);
        routineName = (EditText) rootView.findViewById(R.id.routineName);
        currentWeight = (EditText) rootView.findViewById(R.id.currentWeight);
        gallery = (LinearLayout) rootView.findViewById(R.id.horizontalGallery);

        final Button addButton = (Button) rootView.findViewById(R.id.addButton);

        //Signaling Activity to call onCreateOptionMenu to setup action bar buttons
        setHasOptionsMenu(true);

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                selectImage();
            }
        });

        //Need to change the way keyboard is handled because it covers a textfield
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return rootView;
    }

    /**
     * Description: Sets custom action buttons to the action bar
     * @param menu - Given by hosting activity
     * @param inflater - Given by hosting activity
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.add_routine_fragment_actions, menu);
    }

    /**
     * Description - Sets the actionListeners to the action buttons in the action title bar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.accept:

                routine = createNewRoutine(getActivity(), routineName, currentWeight, beforePic);
                if (routine != null) {
                    DBProgressPicHandler db = new DBProgressPicHandler(getActivity());
                    ProgressPic pic;
                    String date = new Date().toString();
                    for (String p : pics) {
                        pic = new ProgressPic(routine.getId(), p, date);
                        db.addProgressPic(pic);
                    }
                    //Start activity but don't add self to backstack
                    ((MainActivity)getActivity()).finishFragment();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
        Description: Takes in the application context and the EditText field and stored the
        routine in the internal storage. If successfully created changes the save button onClick
        function.
     **/
    private Routine createNewRoutine(Context context, EditText fName, EditText cWeight, String beforePic){

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
            routine = new Routine(fileName);
            routine.setBeforePic(beforePic);
        }
        catch(NumberFormatException e){
            Toast.makeText(context, "Current Weight only accepts integers", Toast.LENGTH_LONG).show();
            cWeight.requestFocus();
            return null;
        }
        db.addRoutine(routine);
        return routine;
    }


    /**
     * Name: selectImage
     * Description: This method builds the dialogue box for selecting an image.
     */
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Photo Source");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                    Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    try {
                        selectedImage = ProgressPic.createImageFile();
                    }
                    catch(IOException e){
                        return;
                    }

                    imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(selectedImage)));
                    startActivityForResult(imageIntent, REQUEST_CAMERA);

                }
//                else if (items[item].equals("Choose from Library")) {
//                    Intent intent = new Intent(
//                            Intent.ACTION_PICK,
//                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    intent.setType("image/*");
//                    startActivityForResult(
//                            Intent.createChooser(intent, "Select File"),
//                            SELECT_FILE);
//                }
                else if (items[item].equals("Cancel")) {
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

                    pics.add(selectedImage);

                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(selectedImage)));

                    ProgressPic.fixOrientationToPortrait(getActivity(), selectedImage);

                    Bitmap pic = BitmapFactory.decodeFile(selectedImage);

                    Bitmap picScaled = Bitmap.createScaledBitmap(pic, 200, 250, true);

                    ImageView takenThumbnail = new ImageView(getActivity());
                    takenThumbnail.setImageBitmap(picScaled);
                    takenThumbnail.setPadding(5,5,5,5);
                    takenThumbnail.setTag(selectedImage);

                    setThumbnailActions(takenThumbnail);

                    gallery.addView(takenThumbnail,0);

                    break;

//                case SELECT_FILE:
//                    selectedURI = data.getData();
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                    Cursor cursor = getActivity().getContentResolver().query(
//                            selectedURI, filePathColumn, null, null, null);
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String filePath = cursor.getString(columnIndex);
//                    cursor.close();
//
//                    //Storing Copy of image into application folder
//                    String path = ProgressPic.copyToApplicationFolder(getActivity(), filePath);
//                    ProgressPic.fixOrientationToPortrait(getActivity(), path);
//
//                    pics.add(path);
//
//                    Bitmap selectedImage = BitmapFactory.decodeFile(path);
//
//                    //Create selected Image thumbnail
//                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(selectedImage, 250, 200, true);
//
//                    ImageView selectedThumbnail = new ImageView(getActivity());
//                    selectedThumbnail.setImageBitmap(scaledBitmap);
//                    selectedThumbnail.setPadding(5,5,5,5);
//                    gallery.addView(selectedThumbnail,0);
//                    break;
            }

        }
        else{
            //Toast.makeText(getActivity(), "File upload canceled", Toast.LENGTH_SHORT).show();
        }
    }

    public void optionBuilder(final LinearLayout list, final View picture){
        final String [] items = new String [] { "Delete" };
        final DBRoutineHelper db = new DBRoutineHelper(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Delete")) {
                    list.removeView(picture);
                    pics.remove(picture.getTag());
                    if (picture.isSelected()) {
                        if (list.getChildCount() > 0) {
                            //if selected pic was selected change selected pic to default index 0
                            View view = list.getChildAt(0);
                            view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                            view.setSelected(true);
                            beforePic = (String) view.getTag();
                        } else { //No Pic to select as album cover
                            beforePic = null;
                        }
                    }

                    //Delete Photo from gallery
                    ProgressPic.deleteGallaryImage(getActivity(), (String) picture.getTag());

                }
            }
        });
        builder.show();
    }

    /**
     * Takes the thumbnail and adds onclick and onlongclick actions to select or delete thumbnail.
     * @param thumbnail
     */
    public void setThumbnailActions(View thumbnail){
        //Allows users to select which image they want as their album cover
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < gallery.getChildCount(); i++){
                    View img = gallery.getChildAt(i);
                    img.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    img.setSelected(false);
                }
                beforePic = (String) view.getTag();
                view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                view.setSelected(true);
            }
        });

        thumbnail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                optionBuilder(gallery, view);
                return false;
            }
        });

        if(gallery.getChildCount() == 0){
            thumbnail.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            thumbnail.setSelected(true);
            beforePic = (String)thumbnail.getTag();
        }
    }




    public void setRoutine(Routine routine){
        this.routine = routine;
    }

}

