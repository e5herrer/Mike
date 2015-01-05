package esequielherrera.mike;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.List;

public class ListExerciseAdapter extends ArrayAdapter<Exercise> {
    private final Context context;
    private final List<Exercise> exercises;
    private  int selected = -1;


    public ListExerciseAdapter(Context context, List<Exercise> exercises) {
        super(context, R.layout.exercise_list_item, exercises);
        this.context = context;
        this.exercises = exercises;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView name, reps, sets, restTime;

        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.exercise_list_item, parent, false);
        }


        //Alternate row color
        //if selected highlight in blue
        if(selected == position){
            //seting blue boarder around selected
            int sdk = android.os.Build.VERSION.SDK_INT;

            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN)
                convertView.setBackgroundDrawable( context.getResources().getDrawable(R.drawable.rectangle));
            else
                convertView.setBackground( context.getResources().getDrawable(R.drawable.rectangle));

        }
        else if(position % 2 ==1)
            convertView.setBackgroundColor(Color.rgb(230, 230, 230));
        else
            convertView.setBackgroundColor(Color.rgb(215, 215, 215));

        //Set up the reorder onclick listeners
        final ImageButton upButton = (ImageButton)convertView.findViewById(R.id.upButton);
        final ImageButton downButton = (ImageButton)convertView.findViewById(R.id.downButton);

        upButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(position > 0){
                    Exercise selected = exercises.remove(position);
                    exercises.add(position - 1, selected);
                    notifyDataSetChanged();
                }

            }
        });



        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position < exercises.size() - 1) {
                    Exercise selected = exercises.remove(position + 1);
                    exercises.add(position, selected);
                    notifyDataSetChanged();
                }
            }
        });


        name = (TextView)convertView.findViewById(R.id.name);
        reps = (TextView)convertView.findViewById(R.id.reps);
        sets = (TextView)convertView.findViewById(R.id.sets);
        restTime = (TextView)convertView.findViewById(R.id.restTime);

        name.setText(String.valueOf((exercises.get(position).getName())));
        sets.setText("Sets: " + String.valueOf(exercises.get(position).getSets()));
        reps.setText("Reps: " + exercises.get(position).getReps());
        restTime.setText("Rest: " + String.valueOf(exercises.get(position).getRestTime()) + "s");

        return convertView;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }


}