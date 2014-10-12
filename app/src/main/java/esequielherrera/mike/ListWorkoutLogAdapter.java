package esequielherrera.mike;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 10/4/14.
 */
public class ListWorkoutLogAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Workout> workouts;
    private ArrayList<List<LogEntry>> logs;
    private int childCount;

    public ListWorkoutLogAdapter(Context context, Workout workout) {
        this.context = context;
        DBWorkoutHelper db = new DBWorkoutHelper(context);
        this.workouts = db.getRoutineWorkouts(workout.getRoutineId(), workout.getName());
    }

    @Override
    public int getGroupCount() {
        if(workouts != null) {
            return workouts.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int i) {
        if(workouts != null){
            return workouts.get(i).getSets();
        }
        return -1;
    }

    @Override
    public Object getGroup(int i) {
        return workouts.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return workouts.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return workouts.get(i).getId();
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
        DBLogHelper db = new DBLogHelper(context);
        Workout workout = workouts.get(i);

        if(view == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(R.layout.workout_log_goup_item, null);
        }
        exerciseName = (TextView)view.findViewById(R.id.exerciseName);
        setsRepsRest = (TextView)view.findViewById(R.id.setsRepsRest);
        exerciseName.setText(workout.getExerciseName());
        setsRepsRest.setText("Sets: " + workout.getSets() + " Reps: " + workout.getReps() +
                " Rest: " + workout.getRestTime());
        view.setTag(workout);
        return view;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
        LogEntry logEntry;
        DBLogHelper db = new DBLogHelper(context);
        LogEntry lastEntry = db.getMostRecentLog(workouts.get(i).getId(), i2);

        if(view == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(R.layout.workout_log_child_item, null);
        }

        ((TextView)view.findViewById(R.id.setNumber)).setText(i2 + "");

        logEntry = logs.get(i).get(i2);
        TextView entry = (TextView)view.findViewById(R.id.entry);
        if(logEntry.isSet()) {
            entry.setText("Weight: " + logEntry.getWeight() + "Reps: " + logEntry.getReps() + "\nNotes: " + logEntry.getNotes());
        }

        TextView previous = (TextView)view.findViewById(R.id.previousEntry);
        if(lastEntry == null)
            previous.setText("Previous: No Data");
        else
            previous.setText("Previous Weight:" + lastEntry.getWeight() + " Reps: " + lastEntry.getReps()
                + "\nNotes: " + lastEntry.getNotes());


        logEntry = logs.get(i).get(i2);
        logEntry.setWorkoutId(workouts.get(i).getId());
        logEntry.setSetNum(i2);
        view.setTag(logEntry);
        return view;
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

    public ArrayList<List<LogEntry>> getLogs() {
        return logs;
    }

    public void setLogs(ArrayList<List<LogEntry>> logs) {
        this.logs = logs;
    }
}
