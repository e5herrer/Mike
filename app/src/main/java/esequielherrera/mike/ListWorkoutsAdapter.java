package esequielherrera.mike;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class ListWorkoutsAdapter extends ArrayAdapter<Workout> {
    private final Context context;
//    private final Workout [] workouts;
    private final List<Workout> workouts;


    public ListWorkoutsAdapter(Context context, List<Workout> workouts) {
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
            convertView = inflater.inflate(R.layout.day_list_item, null);
            holder = new ViewHolder();
            name = (TextView)convertView.findViewById(R.id.name);
            name.setText(String.valueOf((workouts.get(position).getName())));
            holder.id = String.valueOf(workouts.get(position).getId());
            convertView.setTag(workouts.get(position));
        }

        name = (TextView)convertView.findViewById(R.id.name);
        name.setText(workouts.get(position).getName());

        return convertView;
    }

    private static class ViewHolder{
        public String id;
    }

//    public ListWorkoutsAdapter(Context context, Workout [] workouts) {
//        super(context, R.layout.workout_list_item, workouts);
//        this.context = context;
//        this.workouts = workouts;
//    }

    /*
     * @position - position of list item
     * @convertView - recycledview. if not null modify it rather than infalting a new one.
     * @parent - the parent listview
     */
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        TextView name;
//
//
//        if(convertView == null){
//            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
//            convertView = inflater.inflate(R.layout.day_list_item, null);
//            holder = new ViewHolder();
//            name = (TextView)convertView.findViewById(R.id.name);
//            name.setText(String.valueOf((workouts[position].getName())));
//            holder.id = String.valueOf(workouts[position].getId());
//            convertView.setTag(workouts[position]);
//        }
//
//        name = (TextView)convertView.findViewById(R.id.name);
//        name.setText(workouts[position].getName());
//
//        return convertView;
//    }
//
//    private static class ViewHolder{
//        public String id;
//    }
//}


}
