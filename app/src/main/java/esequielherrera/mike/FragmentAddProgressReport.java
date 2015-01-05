package esequielherrera.mike;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
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

import java.io.File;

/**
 * Created by esequielherrera-ortiz on 11/24/14.
 */
public class FragmentAddProgressReport extends Fragment {
    private static final int MAX_TITLE_LENGTH = 30;
    //   private static final int SELECT_FILE = 0;
    private static final int REQUEST_CAMERA = 1;
    private String selectedImagePath;
    private Routine routine;
    private LinearLayout gallery;
    private EditText currentWeight;
    private ProgressReport progressReport = new ProgressReport();

    private String afterPic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //MAP XML with java
        View rootView = inflater.inflate(R.layout.fragment_add_progress_report, container, false);
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
                DBProgressReportHandler db = new DBProgressReportHandler(getActivity());
                progressReport.setRoutineId(routine.getId());
                progressReport.setWeight(currentWeight.getText().toString());
                db.addProgressReport(progressReport);

                if(routine.getBeforePic() == null)
                    routine.setBeforePic(afterPic);
                else
                    routine.setAfterPic(afterPic);

                DBRoutineHelper routineDB = new DBRoutineHelper(getActivity());
                routineDB.updateRoutine(routine);

                ((MainActivity)getActivity()).finishFragment();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

                    selectedImagePath = ProgressPic.createImageFile();

                    imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(selectedImagePath)));
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

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case REQUEST_CAMERA:

                    progressReport.addPhoto(new ProgressPic(selectedImagePath));

                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(selectedImagePath)));

                    ProgressPic.fixOrientationToPortrait(getActivity(), selectedImagePath);

                    Bitmap pic = BitmapFactory.decodeFile(selectedImagePath);

                    Bitmap picScaled = Bitmap.createScaledBitmap(pic, 200, 250, true);

                    ImageView takenThumbnail = new ImageView(getActivity());
                    takenThumbnail.setImageBitmap(picScaled);
                    takenThumbnail.setPadding(5,5,5,5);
                    takenThumbnail.setTag(selectedImagePath);

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
                    progressReport.getAlbum().remove(picture.getTag());
                    if (picture.isSelected()) {
                        if (list.getChildCount() > 0) {
                            //if selected pic was selected change selected pic to default index 0
                            View view = list.getChildAt(0);
                            view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                            view.setSelected(true);
                            afterPic = (String) view.getTag();
                        } else { //No Pic to select as album cover
                            afterPic = null;
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
                afterPic = (String) view.getTag();
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
            afterPic = (String)thumbnail.getTag();
        }
    }




    public void setRoutine(Routine routine){
        this.routine = routine;
    }

}

