package esequielherrera.mike;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/26/14.
 */
public class AddWorkoutsFragment extends Fragment {
    List<Workout> allWorkouts = new ArrayList<Workout>();
    List<Workout> workouts = new ArrayList<Workout>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_workouts, container, false);
        final EditText dayName = (EditText) rootView.findViewById(R.id.dayName);
        final ListView listWorkouts = (ListView) rootView.findViewById(R.id.listWorkouts);
        final EditText workoutName  = (EditText) rootView.findViewById(R.id.workoutName);
        final EditText sets = (EditText) rootView.findViewById(R.id.sets);
        final EditText reps = (EditText) rootView.findViewById(R.id.reps);
        final EditText restTime = (EditText) rootView.findViewById(R.id.restTime);

        final Button cancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        final Button addButton = (Button) rootView.findViewById(R.id.addButton);
        final Button saveButton = (Button) rootView.findViewById(R.id.saveButton);


        addButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addWorkout(dayName, workoutName, sets, reps, restTime);
                ((ArrayAdapter)listWorkouts.getAdapter()).notifyDataSetChanged();
            }
        });

        saveButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkoutDBHelper db = new WorkoutDBHelper(getActivity());
                for(int i = 0; i < workouts.size(); i++){
                    db.addWorkout(workouts.get(i));
                }
                closeFragment();
                ((MainActivity)getActivity()).startAddDaysFragment(getArguments());
            }
        });

        cancelButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment();
                ((MainActivity)getActivity()).startAddDaysFragment(getArguments());
            }
        });

        setListAdapter(listWorkouts);

        return rootView;
    }

    public void addWorkout(EditText iDayName, EditText iWorkoutName,
                           EditText iSets, EditText iReps, EditText iRestTime){
        int routineId = getArguments().getInt("routineId");
        String dayName = iDayName.getText().toString().trim();
        String workoutName = iWorkoutName.getText().toString().trim();
        String sets = iSets.getText().toString().trim();
        String reps = iReps.getText().toString().trim();
        String restTime = iRestTime.getText().toString().trim();
        WorkoutDBHelper db = new WorkoutDBHelper(getActivity());
        Workout workout;

        if(dayName.equals("")){
            Toast.makeText(getActivity(), "Please include a day name", Toast.LENGTH_LONG).show();
            iDayName.requestFocus();
            return;
        }
        if(workoutName.equals("")){
            Toast.makeText(getActivity(), "Please include a workout name", Toast.LENGTH_LONG).show();
            iWorkoutName.requestFocus();
            return;
        }
        if(!sets.equals("") && !isInteger(sets)){
            Toast.makeText(getActivity(), "Sets only accepts integers or leave field empty", Toast.LENGTH_LONG).show();
            iSets.requestFocus();
            return;
        }
        if(!reps.equals("")){
            String repParts [] = reps.split(",");

            for(int i = 0; i < repParts.length; i++) {
                String temp = repParts[i].trim();
                if(!temp.equals("") && !isInteger(temp)) {
                    Toast.makeText(getActivity(), "Reps is in the wrong format separate using commas", Toast.LENGTH_LONG).show();
                    iReps.requestFocus();
                    return;
                }
            }
        }

        if(!restTime.equals("") && !isInteger(restTime)){
                Toast.makeText(getActivity(), "Sets only accepts integers accepted of leave blank", Toast.LENGTH_LONG).show();
                iReps.requestFocus();
                return;
        }
        Workout temp = new Workout(routineId, dayName, workoutName, Integer.decode(sets), reps, Integer.decode(restTime));
        this.workouts.add(temp);
        this.allWorkouts.add(temp);
        /*
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        */
    }

    private boolean isInteger(String string) {
        try{
            Integer.parseInt(string);
        }
        catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    private void closeFragment(){
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }

    private void setListAdapter(ListView list){
        WorkoutDBHelper db = new WorkoutDBHelper(getActivity());
        //List<Workout> dbWorkouts = db.getAllWorkouts();
        allWorkouts.addAll(workouts);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, allWorkouts);
        list.setAdapter(adapter);
    }


}
