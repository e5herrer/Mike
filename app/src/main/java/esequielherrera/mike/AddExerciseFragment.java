package esequielherrera.mike;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class AddExerciseFragment extends Fragment {
    List<Workout> allWorkouts = new ArrayList<Workout>();
    List<Workout> storedWorkouts = new ArrayList<Workout>();
    List<Workout> workouts = new ArrayList<Workout>();
    Routine routine;
    Workout workout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_exercises, container, false);
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
                workoutName.setText("");
                sets.setText("");
                reps.setText("");
                restTime.setText("");
            }
        });

        saveButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBWorkoutHelper db = new DBWorkoutHelper(getActivity());
                String workoutName = dayName.getText().toString().trim();
                if(workoutName.equals("")){
                    Toast.makeText(getActivity(), "Please include a workout name", Toast.LENGTH_LONG).show();
                    dayName.requestFocus();
                    return;
                }
                for(int i = 0; i < workouts.size(); i++){
                    workouts.get(i).setName(workoutName);
                    db.addWorkout(workouts.get(i));
                }
                closeFragment();
                updateDayName(dayName);
                ((MainActivity)getActivity()).startAddWorkoutFragment(routine);
            }
        });

        cancelButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment();
                ((MainActivity)getActivity()).startAddWorkoutFragment(routine);
            }
        });


        if(workout != null){
            dayName.setText(workout.getName());
            storedWorkouts = new DBWorkoutHelper(getActivity()).getRoutineWorkouts(workout.getRoutineId(), workout.getName());
            allWorkouts.addAll(storedWorkouts);
        }

        setListAdapter(listWorkouts);

        return rootView;
    }

    public void addWorkout(EditText wName, EditText e,
                           EditText iSets, EditText iReps, EditText iRestTime){
        int routineId = routine.getId();
        String workoutName = wName.getText().toString().trim();
        String exerciseName = e.getText().toString().trim();
        String sets = iSets.getText().toString().trim();
        String reps = iReps.getText().toString().trim();
        String restTime = iRestTime.getText().toString().trim();

        if(exerciseName.equals("")){
            Toast.makeText(getActivity(), "Please include a exercise name", Toast.LENGTH_LONG).show();
            e.requestFocus();
            return;
        }
        if(sets.equals("")){
            sets = "0";
        }
        else if(!isInteger(sets)){
            Toast.makeText(getActivity(), "Sets only accepts integers or leave field empty", Toast.LENGTH_LONG).show();
            iSets.requestFocus();
            return;
        }


        if(restTime.equals("")){
            restTime = "0";
        }
        else if(!isInteger(restTime)){
                Toast.makeText(getActivity(), "Rest time only accepts integers accepted of leave blank", Toast.LENGTH_LONG).show();
                iReps.requestFocus();
                return;
        }

        Workout temp = new Workout(routineId, workoutName, exerciseName, Integer.decode(sets), reps, Integer.decode(restTime));
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
        final ArrayAdapter adapter = new ListExerciseAdapter(getActivity(), allWorkouts);

        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> l, View v, int position,
                                           long id) {
                optionBuilder((Workout) v.getTag(), adapter, position);
                return true;
            }


        });
    }

    private void updateDayName(EditText newName){
        if(workout != null && !newName.getText().toString().equals(workout.getName())){
            new DBWorkoutHelper(getActivity()).updateDayNames(this.storedWorkouts, newName.getText().toString());
        }
    }


    public void optionBuilder(Workout wrk, ArrayAdapter adpt, int position){
        final String [] items = new String [] { "Delete" };
        final DBWorkoutHelper db = new DBWorkoutHelper(getActivity());
        final Workout workout = wrk;
        final ArrayAdapter adapter = adpt;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What would you like to do?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Delete")) {
                    db.deleteWorkout(workout);
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
    public void setWorkout(Workout workout){
        this.workout = workout;
    }

}
