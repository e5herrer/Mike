package esequielherrera.mike;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/25/14.
 */
public class AddWorkoutFragment extends Fragment {
    private Routine routine;
    private Workout workout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_add_workouts, container, false);
        final ListView workoutList = (ListView)rootView.findViewById(R.id.daysList);
        final Button addButton = (Button)rootView.findViewById(R.id.addButton);
        final Button backButton = (Button)rootView.findViewById(R.id.backButton);
        Routine temp = routine;

        addButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                ((MainActivity)getActivity()).startAddExerciseFragment(routine, workout);
            }
        });
        backButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                ((MainActivity)getActivity()).startRoutineFragment();
            }
        });

        setListAdapter(workoutList);

        return rootView;
    }

    private void setListAdapter(ListView list){
        DBWorkoutHelper db = new DBWorkoutHelper(getActivity());
        List<Workout> workouts = db.getRoutineWorkouts(this.routine.getId());

        final ArrayAdapter adapter = new ListWorkoutAdapter(getActivity(), workouts);
        list.setAdapter(adapter);

        list.setOnItemClickListener( new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View v, int position,
                                    long id) {
                WorkoutLogFragment fragment = new WorkoutLogFragment();
                fragment.setWorkout((Workout)v.getTag());
                fragment.setRoutine(routine);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.commit();

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> l, View v, int position,
                                           long id) {
                optionBuilder((Workout) v.getTag(), adapter, position);
                return true;
            }
        });

    }

    public void optionBuilder(Workout wrk, ArrayAdapter adpt, int position){
        final String [] items = new String [] { "Edit", "Delete" };
        final DBWorkoutHelper db = new DBWorkoutHelper(getActivity());
        final Workout workout = wrk;
        final ArrayAdapter adapter = adpt;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What would you like to do?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Edit")) {
                    ((MainActivity)getActivity()).startAddExerciseFragment(routine, workout);
                    dialog.dismiss();
                }
                else if (items[item].equals("Delete")) {
                    db.deleteDay(workout);
                    adapter.remove(workout);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void setRoutine(Routine routine){
        this.routine = routine;
    }
    public void setWorkout(Workout workout) { this.workout = workout; }


}
