package esequielherrera.mike;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.io.IOException;
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
        myExercises = db.getRoutineWorkouts(workout.getRoutineId() ,workout.getName());
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
                logEntry = (LogEntry)view.getTag();

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
                logEntry.setSet(true);
                weightNum.setText("");
                notes.setText("");
                repsNum.setText("");
                focusNextChild();
                adapter.notifyDataSetChanged();

                //LAUNCH TIMER IF Exercise has Rest Time
                if(myExercises.get(groupNum).getRestTime() > 0)
                    launchTimer(myExercises.get(groupNum).getRestTime() * 1000);
            }
        });

        //Exapdn all groups
        for(int i = 0; i < exerciseList.getExpandableListAdapter().getGroupCount(); i++){
            exerciseList.expandGroup(i);
        }

        //selecting first routine
        logEntry = logs.get(groupNum).get(childNum);
//        selectedExerciseTitle.setText(myExercises.get(groupNum).getExerciseName());


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

        //Highlight next exercise
        ((ListWorkoutLogAdapter)exerciseList.getExpandableListAdapter()).setFocus(groupNum, childNum);
        ((ListWorkoutLogAdapter)exerciseList.getExpandableListAdapter()).notifyDataSetChanged();
//        selectedExerciseTitle.setText(myExercises.get(groupNum).getExerciseName());

    }


    /**
     * Creates a list of blank logs
     * @param workouts - Used to calculate how many blank logs are needed
     * @return 2d ArrayList where first level arrays represent the workout with the inner array representing the exercise sets
     */
    public ArrayList<List<LogEntry>> createLogList(List<Workout> workouts){
        ArrayList<List<LogEntry>> logs = new ArrayList<List<LogEntry>>();
        for(int i = 0; i < workouts.size(); i++){
            ArrayList<LogEntry> exercise = new ArrayList<LogEntry>();
            for(int j = 0; j < workouts.get(i).getSets(); j++){
                    exercise.add(new LogEntry());
            }
            logs.add(exercise);
        }
        return logs;
    }

    /**
     * Description- Called after a workouts data is entered to start the rest time timer.
     * @param milSec - an exercise rest time in milliseconds
     */
    public void launchTimer(int milSec){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final MediaPlayer mMediaPlayer = new MediaPlayer();

        builder.setTitle("Rest Time");
        builder.setMessage("");
        builder.setPositiveButton("Ready!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mMediaPlayer.reset();
                ((AlertDialog)dialog).hide();
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        new CountDownTimer(milSec, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                dialog.setMessage( (millisUntilFinished/1000) + " sec");
            }

            @Override
            public void onFinish() {
                dialog.setMessage(0 + " sec");
                try
                {
                    if(dialog.isShowing()) {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        mMediaPlayer.setDataSource(getActivity(), notification);
                        final AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                            mMediaPlayer.setLooping(true);
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                        }
                    }
                }
                catch (IOException e)
                {
                    // oops!
                }
            }
        }.start();
    }

    public void setWorkout(Workout myWorkout) {
        this.workout = myWorkout;
    }
    public void setRoutine(Routine routine) { this.routine = routine; }
}
