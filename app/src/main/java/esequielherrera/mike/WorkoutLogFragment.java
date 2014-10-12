package esequielherrera.mike;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
        final Button saveButton = (Button) rootView.findViewById(R.id.saveButton);

        DBWorkoutHelper db = new DBWorkoutHelper(getActivity());
        myExercises = db.getRoutineWorkouts(workout.getRoutineId() ,workout.getName());
        adapter = new ListWorkoutLogAdapter(getActivity(), workout);
        exerciseList.setAdapter(adapter);
        createLogList();
        adapter.setLogs(logs);


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
                if(logEntry.isSet()) {
                    weightNum.setText(""+ logEntry.getWeight());
                    repsNum.setText("" + logEntry.getReps());
                    notes.setText(logEntry.getNotes());
                }
                return false;
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
                launchTimer(5000);
            }
        });

        saveButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBLogHelper db = new DBLogHelper(getActivity());
                for(List<LogEntry> exercise : logs){
                    for(LogEntry log : exercise){
                        db.addLog(log);
                    }
                }
                ((MainActivity)getActivity()).startAddWorkoutFragment(routine);
            }
        });




        return rootView;
    }

    /**
     * Description: Used to focus on the next available child in expandable listview
     */
    public void focusNextChild(){
        if(childNum < adapter.getChildrenCount(groupNum) -1)
            childNum++;
        else if(groupNum < adapter.getGroupCount() - 1){
            exerciseList.collapseGroup(groupNum++);
            exerciseList.expandGroup(groupNum);
            childNum = 0;
        }
        else{
            return;
        }

        logEntry = logs.get(groupNum).get(childNum);

        if(logEntry.isSet())
            focusNextChild();
    }


    public void createLogList(){
        logs = new ArrayList<List<LogEntry>>();
        for(int i = 0; i < adapter.getGroupCount(); i++){
            ArrayList<LogEntry> workout = new ArrayList<LogEntry>();
            logs.add(workout);
            for(int j = 0; j < adapter.getChildrenCount(i); j++){
                    workout.add(new LogEntry());
            }

        }
    }

    public void launchTimer(int milSec){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);

        builder.setTitle("Rest Time");
        builder.setMessage("00:10");
        builder.setPositiveButton("Ready!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ((AlertDialog)dialog).hide();
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        new CountDownTimer(milSec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                dialog.setMessage(""+ (millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                while(dialog.isShowing()) {
                    r.play();
                }
            }
        }.start();
    }

    public void setWorkout(Workout myWorkout) {
        this.workout = myWorkout;
    }
    public void setRoutine(Routine routine) { this.routine = routine; }
}
