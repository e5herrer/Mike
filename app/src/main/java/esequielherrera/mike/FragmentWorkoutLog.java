package esequielherrera.mike;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/30/14.
 */
public class FragmentWorkoutLog extends Fragment {
    private List<Workout> myExercises = new ArrayList<Workout>();
    private ArrayList<List<LogEntry>> logs = new ArrayList<List<LogEntry>>();
    private Routine routine;
    private Workout workout;
    private LogEntry logEntry;
    private ExpandableListView exerciseList;
    private ListWorkoutLogAdapter adapter;
    private int groupNum;
    private int childNum;
    private Clock clock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_workout_log, container, false);
        exerciseList = (ExpandableListView) rootView.findViewById(R.id.exerciseList);
        final TextView workoutName = (TextView) rootView.findViewById(R.id.workoutTitle);
        final TextView weightNum = (TextView) rootView.findViewById(R.id.weight);
        final TextView repsNum = (TextView) rootView.findViewById(R.id.reps);
        final TextView notes = (TextView) rootView.findViewById(R.id.notes);
        final Button logButton = (Button) rootView.findViewById(R.id.logButton);


        //Obtaining our exercise list and setting our adapter
        DBWorkoutHelper db = new DBWorkoutHelper(getActivity());
        myExercises = db.getWorkoutExercises(workout.getRoutineId(), workout.getName());
        logs = createLogList(myExercises);
        adapter = new ListWorkoutLogAdapter(getActivity(), workout, logs);
        exerciseList.setAdapter(adapter);

        //setting title of workout
        workoutName.setText(workout.getName());


        exerciseList.setOnGroupClickListener( new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                groupNum = i;
                childNum = 0;
                logEntry = logs.get(i).get(0);
                return false;
            }
        });

        exerciseList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                groupNum = i;
                childNum = i2;
                logEntry = logs.get(i).get(i2);

                //Highlighs selected child
                ((ListWorkoutLogAdapter)exerciseList.getExpandableListAdapter()).setFocus(i, i2);
                ((ListWorkoutLogAdapter)exerciseList.getExpandableListAdapter()).notifyDataSetChanged();

                //populate entry area when an exercise is revisited.
                if(logEntry.isSet()) {
                    weightNum.setText(""+ logEntry.getWeight());
                    repsNum.setText("" + logEntry.getReps());
                    notes.setText(logEntry.getNotes());
                }
                return true;
            }
        });

        logButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weightNum.getText().toString().equals("")){
                    weightNum.setText("0");
                }
                if(repsNum.getText().toString().equals("")){
                    repsNum.setText("0");
                }
                logEntry.setWeight(Integer.decode(weightNum.getText().toString()));
                logEntry.setNotes(notes.getText().toString().trim());
                logEntry.setReps(repsNum.getText().toString().trim());

                weightNum.setText("");
                notes.setText("");
                repsNum.setText("");

                //LAUNCH TIMER IF Exercise has Rest Time
                if(myExercises.get(groupNum).getRestTime() > 0) {
                    //If not the last set in an exercise launch rest timer
                    if (childNum < logs.get(groupNum).size() - 1) {
                        //if its a new log then start timer
                        if(!logs.get(groupNum).get(childNum).isSet())
                            clock.startTimer(myExercises.get(groupNum).getRestTime());
                    } else {
                        clock.startStopWatch();
                    }
                }

                logEntry.setSet(true);
                focusNextChild();
                weightNum.requestFocus();
                adapter.notifyDataSetChanged();
            }
        });


        //Exapdn all groups
        for(int i = 0; i < exerciseList.getExpandableListAdapter().getGroupCount(); i++){
            exerciseList.expandGroup(i);
        }

        //selecting first routine
        logEntry = logs.get(groupNum).get(childNum);

        //Need to change the way keyboard to carry input field up
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Signaling Activity to call onCreateOptionMenu to setup action bar buttons
        setHasOptionsMenu(true);


        return rootView;
    }

    /**
     * Needed to destroy any ongoing timers
     */
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if(clock != null){
            clock.reset();
        }
    }

    /**
     * Description: Sets custom action buttons to the action bar
     * @param menu - Given by hosting activity
     * @param inflater - Given by hosting activity
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.workout_log_fragment_actions, menu);


        //creating our timer box
        MenuItem timerItem = menu.findItem(R.id.timer);
        TextView timerDisplay = (TextView)timerItem.getActionView();
        timerDisplay.setPadding(20, 0, 20, 0);

        timerDisplay.setTextSize(24);

        clock = new Clock(getActivity(), timerDisplay);

        timerDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clock.onStandby())
                    clock.startStopWatch();
                else
                    clock.reset();

            }
        });


    }

    /**
     * Description - Sets the actionListeners to the action buttons in the action title bar
     * @param item - The menu item that was selected.
     * @return - boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.saveButton:

                DBLogHelper db = new DBLogHelper(getActivity());
                for(List<LogEntry> exercise : logs){
                    for(LogEntry log : exercise){
                        db.addLog(log);
                    }
                }
                ((MainActivity)getActivity()).startAddWorkoutFragment(routine);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Description: When called, focuses on the next available child in expandable list view.
     */
    public void focusNextChild(){
        if(childNum < adapter.getChildrenCount(groupNum) -1)
            childNum++;
        else if(groupNum < adapter.getGroupCount() - 1){
            exerciseList.collapseGroup(groupNum++);
            childNum = 0;
        }
        else{
            return;
        }

        //If user submits data on collapsed group then expand it
        if(!exerciseList.isGroupExpanded(groupNum)){
            exerciseList.expandGroup(groupNum);
        }

        logEntry = logs.get(groupNum).get(childNum);

        if(logEntry.isSet())
            focusNextChild();

        //Highlight and scroll to next exercise
        ((ListWorkoutLogAdapter)exerciseList.getExpandableListAdapter()).setFocus(groupNum, childNum);
        ((ListWorkoutLogAdapter)exerciseList.getExpandableListAdapter()).notifyDataSetChanged();
        exerciseList.smoothScrollToPosition(exerciseList.getFlatListPosition(exerciseList.getPackedPositionForChild(groupNum,childNum)));
    }


    /**
     * Creates a list of blank logs
     * @param workouts - Used to calculate how many blank logs are needed
     * @return 2d ArrayList where first level arrays represent the workout with the inner array representing the exercise sets
     */
    public ArrayList<List<LogEntry>> createLogList(List<Workout> workouts){

        ArrayList<List<LogEntry>> logs = new ArrayList<List<LogEntry>>();

        for( Workout workout : workouts){
            ArrayList<LogEntry> exercise = new ArrayList<LogEntry>();

            for(int j = 0; j < workout.getSets(); j++){
                    exercise.add(new LogEntry());
            }
            logs.add(exercise);
        }
        return logs;
    }



    public void setWorkout(Workout myWorkout) {
        this.workout = myWorkout;
    }
    public void setRoutine(Routine routine) { this.routine = routine; }
}
