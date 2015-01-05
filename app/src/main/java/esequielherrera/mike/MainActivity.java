package esequielherrera.mike;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class MainActivity extends Activity {
    public enum FragmentTags{
        FragmentAddExercise, FragmentAddRoutine, FragmentRoutine, FragmentWorkout, FragmentWorkoutLog,
        FragmentProgresssPicGallery, FragmentFullScreenImage, FragmentAddProgressReport, FragmentAllExerciseLogs
    }

    ArrayList<FragmentTags> fragmentStack = new ArrayList<FragmentTags>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Main", "onCreate");

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Routine routine = getCurrentRoutine();
            if(routine == null) {
                startAddRoutineFragment();
            }
            else{
                startRoutineFragment();
            }
        }

    }


    @Override
    public void onBackPressed(){
        switch(fragmentStack.get(fragmentStack.size() - 1 )) {
            case FragmentWorkoutLog:
            case FragmentAddExercise:

                Dialog.confirmation(this).show();
                break;

            case FragmentRoutine:
                finish();
                break;

            default:
                finishFragment();
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }

    /**
     * @return - Method returns the last modified routine by the user or null of no existing routine.
     */
    public Routine getCurrentRoutine(){
        DBRoutineHelper db = new DBRoutineHelper(this);
        return db.getLastModified();
    }



    public void startAddWorkoutFragment(Routine routine) {
        FragmentWorkout fragment = new FragmentWorkout();
        fragment.setRoutine(routine);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragmentStack.add(FragmentTags.FragmentWorkout);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void startAddExerciseFragment(Routine routine, Workout workout) {
        FragmentAddExercise fragment = new FragmentAddExercise();
        fragment.setRoutine(routine);
        fragment.setWorkout(workout);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragmentStack.add(FragmentTags.FragmentAddExercise);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void startRoutineFragment() {
        Fragment newFragment = new FragmentRoutine();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragmentStack.add(FragmentTags.FragmentRoutine);
        transaction.replace(R.id.container, newFragment);
        transaction.commit();
    }

    public void startAddRoutineFragment() {
        Fragment newFragment = new FragmentAddRoutine();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragmentStack.add(FragmentTags.FragmentAddRoutine);
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void startWorkoutLogFragment(Routine routine, Workout workout){
        FragmentWorkoutLog newFragment = new FragmentWorkoutLog();
        newFragment.setWorkout(workout);
        newFragment.setRoutine(routine);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragmentStack.add(FragmentTags.FragmentWorkoutLog);
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void startGalleryFragment(Routine routine){
        FragmentProgressReports newFragment = new FragmentProgressReports();
        newFragment.setRoutine(routine);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragmentStack.add(FragmentTags.FragmentProgresssPicGallery);
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void startFragmentFullScreenImage(String imgPath){
        FragmentFullScreenImage newFragment = new FragmentFullScreenImage(imgPath);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragmentStack.add(FragmentTags.FragmentFullScreenImage);
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void startAddProgressReportFragment(Routine routine){
        FragmentAddProgressReport newFragment = new FragmentAddProgressReport();
        newFragment.setRoutine(routine);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragmentStack.add(FragmentTags.FragmentAddProgressReport);
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void startAllExerciseLogsFragment(Exercise exercise){
        FragmentAllExerciseLogs fragment = new FragmentAllExerciseLogs();
        fragment.setExercise(exercise);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragmentStack.add(FragmentTags.FragmentAllExerciseLogs);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void finishFragment() {
        getFragmentManager().popBackStack();
        fragmentStack.remove(fragmentStack.size()-1);
    }


}
