package esequielherrera.mike;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class ListWorkoutAdapter extends ArrayAdapter<Workout> {
    private final Context context;
    private final List<Workout> workouts;


    public ListWorkoutAdapter(Context context, List<Workout> workouts) {
        super(context, R.layout.workout_list_item, workouts);
        this.context = context;
        this.workouts = workouts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        TextView name;


        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.workout_list_item, null);
            convertView.setTag(workouts.get(position));
        }

        name = (TextView)convertView.findViewById(R.id.name);
        name.setText(workouts.get(position).getName());

        return convertView;
    }

    private static class ViewHolder{
        public String id;
    }

}
