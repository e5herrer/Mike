package esequielherrera.mike;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 10/4/14.
 */
public class ListWorkoutLogAdapter implements ExpandableListAdapter {

    private Context context;
    private List<Workout> workouts;
    private int childCount;

    public ListWorkoutLogAdapter(Context context, Workout workout) {
        this.context = context;
        DBWorkoutHelper db = new DBWorkoutHelper(context);
        this.workouts = db.getRoutineWorkouts(workout.getRoutineId(), workout.getName());
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

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
        if(view == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(R.layout.workout_log_child_item, null);
        }
        ((TextView)view.findViewById(R.id.setNumber)).setText(i2 + "");
        view.setTag(i + "-" + i2 );
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
}
