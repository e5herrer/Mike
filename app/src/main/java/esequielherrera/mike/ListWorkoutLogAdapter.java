package esequielherrera.mike;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 10/4/14.
 */
public class ListWorkoutLogAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Exercise> exercises;
    private ArrayList<List<LogEntry>> logs;
    private final ArrayList<List<LogEntry>> previousLogs;


    private int focusGroup;
    private int focusChild;

    public ListWorkoutLogAdapter(Context context, Workout workout, ArrayList<List<LogEntry>> logs) {
        this.context = context;
        DBExerciseHelper db = new DBExerciseHelper(context);
        this.exercises = db.getWorkoutExercises(workout);
        this.logs = logs;

        previousLogs = getPreviousLogs(exercises);
    }

    @Override
    public int getGroupCount() {
        if(exercises != null) {
            return exercises.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int i) {
        if(exercises != null){
            return exercises.get(i).getSets();
        }
        return -1;
    }

    @Override
    public Object getGroup(int i) {
        return exercises.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return exercises.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return exercises.get(i).getId();
    }

    @Override
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        TextView exerciseName;
        TextView setsRepsRest;
        Exercise exercise = exercises.get(i);

        if(view == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(R.layout.workout_log_goup_item, null);
        }

        exerciseName = (TextView)view.findViewById(R.id.exerciseName);
        setsRepsRest = (TextView)view.findViewById(R.id.setsRepsRest);
        exerciseName.setText(exercise.getName());
        setsRepsRest.setText("Sets: " + exercise.getSets() + " Reps: " + exercise.getReps() +
                " Rest: " + exercise.getRestTime());
        view.setTag(exercise);
        return view;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
        LogEntry logEntry;
        LogEntry lastEntry = previousLogs.get(i).get(i2);

        if(view == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(R.layout.workout_log_child_item, null);
        }

        if(focusGroup == i && focusChild == i2){
            view.setBackground(context.getResources().getDrawable(R.drawable.rectangle));
        }
        else{
            view.setBackgroundColor(Color.WHITE);
        }

        ((TextView)view.findViewById(R.id.setNumber)).setText(i2 + 1 + "");

        logEntry = logs.get(i).get(i2);
        TextView entry = (TextView)view.findViewById(R.id.entry);

        //set current input
        if(logEntry.isSet()) {

            String data = "Weight: " + logEntry.getWeight() + "\t\tReps: " + logEntry.getReps();

            if(logEntry.getNotes().equals("")){
                entry.setText(data);
            }
            else
                entry.setText(data + "\t\tNotes: " + logEntry.getNotes());
        }
        else{
            entry.setText("");
        }

        //Set previous data
        TextView previous = (TextView)view.findViewById(R.id.previousEntry);
        if(lastEntry != null) {
            String previousData = "Previous -\tWeight: " + lastEntry.getWeight() + "\t\t Reps: " + lastEntry.getReps();
            if(lastEntry.getNotes().equals(""))
                previous.setText( previousData);
            else
                previous.setText( previousData +  "\t\t Notes: " + lastEntry.getNotes());
        }
        else{
            previous.setText("");
        }


        logEntry = logs.get(i).get(i2);
        logEntry.setWorkoutId(exercises.get(i).getId());
        logEntry.setSetNum(i2);
        view.setTag(logEntry);
        return view;
    }

    public ArrayList<List<LogEntry>> getPreviousLogs(List<Exercise> exercises){
        ArrayList<List<LogEntry>> previousLogs = new ArrayList<List<LogEntry>>();
        DBLogHelper db = new DBLogHelper(context);

        for(int i = 0; i < exercises.size(); i++){
            ArrayList<LogEntry> exercise = new ArrayList<LogEntry>();
            for(int j = 0; j < exercises.get(i).getSets(); j++){
                exercise.add(db.getMostRecentLog(exercises.get(i).getId(), j));
            }
            previousLogs.add(exercise);
        }
        return previousLogs;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int i) {
        //list.performItemClick(list.getAdapter().getView(2, null, null), 2, 2);
    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l2) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }

    public void setFocus(int group, int child){
        this.focusGroup = group;
        this.focusChild = child;
    }
}
