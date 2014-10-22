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


public class MainActivity extends Activity {

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

            else if(new DBWorkoutHelper(this).getRoutineWorkouts(routine.getId()).size() <= 0){
                startRoutineFragment();
            }
            else{
                startRoutineFragment();
            }
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

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
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
        AddWorkoutFragment fragment = new AddWorkoutFragment();
        fragment.setRoutine(routine);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void startAddExerciseFragment(Routine routine, Workout workout) {
        AddExerciseFragment fragment = new AddExerciseFragment();
        fragment.setRoutine(routine);
        fragment.setWorkout(workout);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void startRoutineFragment() {
        Fragment newFragment = new RoutineFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.commit();
    }

    public void startAddRoutineFragment() {
        Fragment newFragment = new AddRoutineFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
