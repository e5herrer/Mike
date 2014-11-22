package esequielherrera.mike;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by esequielherrera-ortiz on 11/20/14.
 */
public class FragmentProgressPicGallery extends Fragment{
    private ArrayList<ProgressPic> gallery;
    private Routine routine;
    private final int REQUEST_CAMERA = 1;
    private ListView galleryView;
    private String newImage;
    String date = new Date().toString();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryView = (ListView)rootView.findViewById(R.id.gallery);

        gallery = getGallery();

        ListGalleryAdapter adapter = new ListGalleryAdapter(getActivity(), gallery);
        galleryView.setAdapter(adapter);

        setHasOptionsMenu(true);

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
        inflater.inflate(R.menu.fragment_gallery_actions, menu);
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
            case R.id.new_album:

                Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                newImage = ProgressPic.createImageFile();

                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(newImage)));
                startActivityForResult(imageIntent, REQUEST_CAMERA);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case REQUEST_CAMERA:

                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(newImage)));
                    ProgressPic.fixOrientationToPortrait(getActivity(), newImage);

                    //add new photo to db
                    DBProgressPicHandler db = new DBProgressPicHandler(getActivity());
                    ProgressPic pic = new ProgressPic(routine.getId(), newImage, date);
                    gallery.add(pic);

                    db.addProgressPic(pic);

                    ((ListGalleryAdapter) galleryView.getAdapter()).orderPhotos(gallery);
                    ((ListGalleryAdapter) galleryView.getAdapter()).notifyDataSetChanged();

                    break;

            }
        }
    }

    private ArrayList<ProgressPic> getGallery(){
        DBProgressPicHandler db = new DBProgressPicHandler(getActivity());
        return db.getAllRoutinePics(routine.getId());
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }
}
