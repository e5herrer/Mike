package esequielherrera.mike;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/30/14.
 */
public class WorkoutLogFragment extends Fragment {
    private List<Workout> myExercises = new ArrayList<Workout>();
    private Routine routine;
    private Workout workout;
    private ExpandableListView exerciseList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_workout_log, container, false);
        exerciseList = (ExpandableListView) rootView.findViewById(R.id.exerciseList);
        final TextView workoutName = (TextView) rootView.findViewById(R.id.workoutTitle);

        DBWorkoutHelper db = new DBWorkoutHelper(getActivity());
        myExercises = db.getRoutineWorkouts(workout.getRoutineId() ,workout.getName());
        ListWorkoutLogAdapter adapter = new ListWorkoutLogAdapter(getActivity(), workout);
        exerciseList.setAdapter(adapter);

        workoutName.setText(workout.getName());

        exerciseList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                Toast.makeText(getActivity(), (String)view.getTag(), Toast.LENGTH_LONG).show();
                return false;
            }
        });


        return rootView;
    }

    public void setWorkout(Workout myWorkout) {
        this.workout = myWorkout;
    }
    public void setRoutine(Routine routine) { this.routine = routine; }
}
