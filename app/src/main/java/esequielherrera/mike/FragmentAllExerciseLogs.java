package esequielherrera.mike;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 1/4/15.
 */
public class FragmentAllExerciseLogs extends Fragment {
    Exercise exercise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.all_exercise_logs, container, false);
        LinearLayout listView = (LinearLayout)rootView.findViewById(R.id.list);
        TextView exerciseName = (TextView)listView.findViewById(R.id.exerciseName);
        TextView setsRepsRest = (TextView)listView.findViewById(R.id.setsRepsRest);


        exerciseName.setText(exercise.getName());
        setsRepsRest.setText("Sets: " + exercise.getSets() + " Reps: " + exercise.getReps() +
                " Rest: " + exercise.getRestTime());

        DBLogHelper db = new DBLogHelper(getActivity());
        List<LogEntry> logs = db.getExerciseLogs(exercise.getId());

        String date = "";
        for(LogEntry log : logs){

            if(!date.equals(log.getTimeStamp())){
                date = log.getTimeStamp();
                TextView dateView = new TextView(getActivity());
                dateView.setBackgroundColor(Color.GRAY);
                dateView.setTextColor(Color.WHITE);
                dateView.setText(log.getDate());
                listView.addView(dateView);
            }
            TextView entry = new TextView(getActivity());

            String data = "Weight: " + log.getWeight() + "\t\tReps: " + log.getReps();

            if(log.getNotes().equals("")){
                entry.setText(log.getSetNum() + "\t\t" + data);
            }
            else
                entry.setText(log.getSetNum() + "\t\t" + data + "\t\tNotes: " + log.getNotes());


            listView.addView(entry);
        }


        return rootView;

    }

    public void setExercise(Exercise exercise){
        this.exercise = exercise;
    }
}
