package esequielherrera.mike;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class ListExerciseAdapter extends ArrayAdapter<Workout> {
    private final Context context;
    private final List<Workout> workouts;


    public ListExerciseAdapter(Context context, List<Workout> workouts) {
        super(context, R.layout.exercise_list_item, workouts);
        this.context = context;
        this.workouts = workouts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView name, reps, sets, restTime;

        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.exercise_list_item, null);
            convertView.setTag(workouts.get(position));
        }

        name = (TextView)convertView.findViewById(R.id.name);
        reps = (TextView)convertView.findViewById(R.id.reps);
        sets = (TextView)convertView.findViewById(R.id.sets);
        restTime = (TextView)convertView.findViewById(R.id.restTime);
        name.setText(String.valueOf((workouts.get(position).getExerciseName())));
        sets.setText(String.valueOf(workouts.get(position).getSets()));
        reps.setText(workouts.get(position).getReps());
        restTime.setText(String.valueOf(workouts.get(position).getRestTime()));

        return convertView;
    }
}