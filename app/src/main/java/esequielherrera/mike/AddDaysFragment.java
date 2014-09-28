package esequielherrera.mike;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/25/14.
 */
public class AddDaysFragment extends Fragment {
    Routine routine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_days, container, false);
        final ListView daysList = (ListView)rootView.findViewById(R.id.daysList);
        final Button addButton = (Button)rootView.findViewById(R.id.addButton);

        int routineId = getArguments().getInt("routineId");
        RoutineDBHandler db = new RoutineDBHandler(getActivity());
        this.routine = db.getRoutine(routineId);

        addButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                startAddWorkoutsFragment();
            }
        });

        setListAdapter(daysList);

        return rootView;
    }

    private void startAddWorkoutsFragment() {
        Fragment newFragment = new AddWorkoutsFragment();
        Bundle bundle = getArguments();
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.commit();
    }

    private void setListAdapter(ListView list){
        WorkoutDBHelper db = new WorkoutDBHelper(getActivity());
        List<Workout> workouts = db.getRoutineDays(getArguments().getInt("routineId"));

        final ArrayAdapter adapter = new ListWorkoutsAdapter(getActivity(), workouts);
        list.setAdapter(adapter);

        list.setOnItemClickListener( new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View v, int position,
                                    long id) {
                Workout temp = (Workout)v.getTag();
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> l, View v, int position,
                                           long id) {
                optionBuilder((Workout) v.getTag(), adapter, position);
                return true;
            }


        });

    }

    public void optionBuilder(Workout wrk, ArrayAdapter adpt, int position){
        final String [] items = new String [] { "Edit", "Delete" };
        final WorkoutDBHelper db = new WorkoutDBHelper(getActivity());
        final Workout workout = wrk;
        final ArrayAdapter adapter = adpt;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What would you like to do?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Edit")) {
                    dialog.dismiss();
                }
                else if (items[item].equals("Delete")) {
                    db.deleteWorkout(workout);
                    ((ListWorkoutsAdapter)adapter).remove(workout);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


}
