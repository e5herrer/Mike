package esequielherrera.mike;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/26/14.
 */
public class FragmentAddExercise extends Fragment {
    private List<Workout> allExercises = new ArrayList<Workout>();
    private List<Workout> workouts = new ArrayList<Workout>();
    private EditText workoutName;
    private EditText sets;
    private EditText reps;
    private EditText restTime;
    private EditText exerciseName;
    private Routine routine;
    private Workout workout;
    private Workout editExercise;
    private ListView listWorkouts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_exercises, container, false);
        workoutName = (EditText) rootView.findViewById(R.id.workoutName);
        exerciseName  = (EditText) rootView.findViewById(R.id.exerciseName);
        sets = (EditText) rootView.findViewById(R.id.sets);
        reps = (EditText) rootView.findViewById(R.id.reps);
        restTime = (EditText) rootView.findViewById(R.id.restTime);

        listWorkouts = (ListView) rootView.findViewById(R.id.listWorkouts);
        final ImageButton addButton = (ImageButton) rootView.findViewById(R.id.addButton);

        addButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addExercise(workoutName, exerciseName, sets, reps, restTime);

                ((ArrayAdapter)listWorkouts.getAdapter()).notifyDataSetChanged();


                //Reset data entry fields
                exerciseName.setText("");
                sets.setText("");
                reps.setText("");
                exerciseName.requestFocus();

                //Keeps list focused on last element
                listWorkouts.smoothScrollToPosition(allExercises.size() - 1);
            }
        });

        //Since rest time saves previous entered if click then clear so user doesn't have to
        restTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (restTime.isFocused()) {
                    restTime.setText("");
                }
            }
        });


        if(workout != null){
            workoutName.setText(workout.getName());
            allExercises = new DBWorkoutHelper(getActivity()).getWorkoutExercises(workout.getRoutineId(), workout.getName());
        }

        //Signaling Activity to call onCreateOptionMenu to setup action bar buttons
        setHasOptionsMenu(true);

        setListAdapter(listWorkouts);


        //Need to change the way keyboard to carry input field up
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);



        workoutName.requestFocus();

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
        inflater.inflate(R.menu.add_exercise_fragment_actions, menu);
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
            case R.id.saveButton:

                DBWorkoutHelper db = new DBWorkoutHelper(getActivity());
                String name = workoutName.getText().toString().trim();

                if(name.equals("")){
                    Toast.makeText(getActivity(), "Please include a workout name", Toast.LENGTH_LONG).show();
                    workoutName.requestFocus();
                    return false;
                }
                if(allExercises.size() == 0){
                    Toast.makeText(getActivity(), "Please include an exercise", Toast.LENGTH_LONG).show();
                    exerciseName.requestFocus();
                    return false;
                }
                for(Workout wrk : workouts){
                    wrk.setName(name);
                    db.addWorkout(wrk);
                }
                //Changed workout name so need to update the ones the exercises that were previously inserted to db
                db.updateWorkoutNames(allExercises, name);

                //Update p

                ((MainActivity)getActivity()).finishFragment();
                ((MainActivity)getActivity()).startAddWorkoutFragment(routine);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Description - This method extracts the information provided in the text fields, checks the inputs
     *               for validity, and stores it in our exercise buffer.
     * @param wName - Workout Name
     * @param e - Exercise Name
     * @param iSets - Number of sets
     * @param iReps - Number of Reps
     * @param iRestTime - Rest Time in seconds
     * return - true if new exercise was successfully created else return false
     */
    public boolean addExercise(EditText wName, EditText e,
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
            return false;
        }
        if(sets.equals("")){
            sets = "1";
        }
        else if(!isInteger(sets)){
            Toast.makeText(getActivity(), "Sets only accepts integers or leave field empty", Toast.LENGTH_LONG).show();
            iSets.requestFocus();
            return false;
        }
        if(reps.equals("")){
            reps = "0";
        }
        if(restTime.equals("")){
            restTime = "0";
        }
        else if(!isInteger(restTime)){
                Toast.makeText(getActivity(), "Rest time only accepts integers accepted of leave blank", Toast.LENGTH_LONG).show();
                iReps.requestFocus();
                return false;
        }

        //Checking if we're editing an exisiting field or creating new
        if(editExercise == null) {
            Workout temp = new Workout(routineId, workoutName, exerciseName, Integer.decode(sets), reps, Integer.decode(restTime));
            this.workouts.add(temp);
            this.allExercises.add(temp);
        }
        else{
            editExercise.setExerciseName(exerciseName);
            editExercise.setReps(reps);
            editExercise.setSets(Integer.decode(sets));
            editExercise.setRestTime(Integer.decode(restTime));
            //Remove selected screen
            ((ListExerciseAdapter)listWorkouts.getAdapter()).setSelected(-1);
            editExercise = null;
            return false;

        }
        return true;

    }


    /**
     * @param string - String representatino of an int
     * @return - true if string is a valid string representation else return false
     */

    private boolean isInteger(String string) {
        try{
            Integer.parseInt(string);
        }
        catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    /**
     * Description - Sets the adapter of our list of exercises. Also sets an onItemLongClick listener to every
     *              child.
     * @param list - List to contain our exercises
     */
    private void setListAdapter(final ListView list){
        final ArrayAdapter adapter = new ListExerciseAdapter(getActivity(), allExercises);

        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> l, View v, int position,
                                           long id) {
                optionBuilder(allExercises.get(position), adapter);
                return true;
            }


        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Workout clickedExercise = allExercises.get(index);

                //If selected is the same then clear selection
                if(clickedExercise == editExercise) {
                    editExercise = null;
                    ((ListExerciseAdapter)list.getAdapter()).setSelected(-1);
                    exerciseName.setText("");
                    sets.setText("");
                    reps.setText("");
                    restTime.setText("");
                }
                else { //fill the input area
                    editExercise = clickedExercise;
                    ((ListExerciseAdapter)list.getAdapter()).setSelected(index);
                    exerciseName.setText(editExercise.getExerciseName());
                    sets.setText(""+editExercise.getSets());
                    reps.setText(editExercise.getReps());
                    restTime.setText(""+editExercise.getRestTime());
                }

                ((ListExerciseAdapter) list.getAdapter()).notifyDataSetChanged();
            }
        });
    }


    /**
     * Description - Called when an exercise is long clicked. Method prompts an alert dialogue for options.
     * @param workout - item that was long clicked
     * @param adapter - reference passed to notify of data change
     */
    public void optionBuilder(final Workout workout, final ArrayAdapter adapter){
        final String [] items = new String [] { "Delete" };
        final DBWorkoutHelper db = new DBWorkoutHelper(getActivity());

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
