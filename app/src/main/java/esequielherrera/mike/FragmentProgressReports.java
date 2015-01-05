package esequielherrera.mike;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by esequielherrera-ortiz on 11/20/14.
 */
public class FragmentProgressReports extends Fragment{
    private ArrayList<ProgressReport> reports;
    private Routine routine;
    private final int REQUEST_CAMERA = 1;
    private ListView galleryView;
    private String newImage;
    private ProgressReport todaysReport = new ProgressReport();
    String date = new Date().toString();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_progress_reports, container, false);
        galleryView = (ListView)rootView.findViewById(R.id.gallery);

        reports = getReports();

        ListProgressReportAdapter adapter = new ListProgressReportAdapter(getActivity(), reports);
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

                ((MainActivity)getActivity()).startAddProgressReportFragment(routine);

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
                    DBProgressReportHandler db = new DBProgressReportHandler(getActivity());
                    ProgressPic pic = new ProgressPic(newImage);
                    if(reports.get(0).getDate().equals(todaysReport.getDate())){
                        reports.get(0).addPhoto(pic);
                    }
                    else{
                        todaysReport.addPhoto(pic);
                        todaysReport.setRoutineId(routine.getId());
                        reports.add(0,todaysReport);
                    }

                    ((ListProgressReportAdapter) galleryView.getAdapter()).notifyDataSetChanged();

                    break;

            }
        }
    }

    private ArrayList<ProgressReport> getReports(){
        DBProgressReportHandler db = new DBProgressReportHandler(getActivity());
        return db.getAllRoutineReports(routine.getId());
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }
}
