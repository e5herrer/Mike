package esequielherrera.mike;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Main", "onCreate");

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Routine routine = getCurrentRoutine();
            if(routine == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new CreateRoutineFragment())
                        .commit();
            }
            else if(new WorkoutDBHelper(this).getRoutineWorkouts(routine.getId()).size() <= 0){
                Bundle bundle = new Bundle();
                bundle.putInt("routineId", routine.getId());
                AddDaysFragment fragment = new AddDaysFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            }
            else{
                Bundle bundle = new Bundle();
                bundle.putInt("routineId", routine.getId());
                AddDaysFragment fragment = new AddDaysFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
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

    public Routine getCurrentRoutine(){
        RoutineDBHandler db = new RoutineDBHandler(this);
        return db.getLastModified();
    }

    public void startAddDaysFragment(Bundle bundle) {
        AddDaysFragment fragment = new AddDaysFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }

}
