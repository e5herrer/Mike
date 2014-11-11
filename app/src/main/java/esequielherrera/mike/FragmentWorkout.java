package esequielherrera.mike;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/25/14.
 */
public class FragmentWorkout extends Fragment {
    private Routine routine;
    private Workout workout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_workouts, container, false);
        final ListView workoutList = (ListView)rootView.findViewById(R.id.daysList);

        setListAdapter(workoutList);

        //Signaling Activity to call onCreateOptionMenu
        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * Description - Sets the actionListeners to the action buttons in the action title bar
     * @param item - The action bar item that was selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.new_workout:
                ((MainActivity)getActivity()).startAddExerciseFragment(routine, workout);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Description: Sets custom action buttons the action bar
     * @param menu - Given by hosting activity
     * @param inflater - Given by hosting activity
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.fragment_workout_actions, menu);
    }

    private void setListAdapter(ListView list){
        DBWorkoutHelper db = new DBWorkoutHelper(getActivity());
        List<Workout> workouts = db.getRoutineWorkouts(this.routine.getId());

        final ArrayAdapter adapter = new ListWorkoutAdapter(getActivity(), workouts);
        list.setAdapter(adapter);

        list.setOnItemClickListener( new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View v, int position,
                                    long id) {
                ((MainActivity)getActivity()).startWorkoutLogFragment(routine, ((Workout)v.getTag()));
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> l, View v, int position,
                                           long id) {
                optionBuilder((Workout) v.getTag(), adapter);
                return true;
            }
        });

    }

    public void optionBuilder(final Workout workout, ArrayAdapter adpt){
        final String [] items = new String [] { "Edit", "Delete" };
        final DBWorkoutHelper db = new DBWorkoutHelper(getActivity());
        final ArrayAdapter adapter = adpt;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What would you like to do?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Edit")) {
                    ((MainActivity)getActivity()).startAddExerciseFragment(routine, workout);
                    dialog.dismiss();
                }
                else if (items[item].equals("Delete")) {
                    dialog.dismiss();
                    deleteConfirmation(getActivity(), workout, adapter);
                }
            }
        });
        builder.show();
    }

    public void deleteConfirmation(final Context context, final Workout workout, final ArrayAdapter adapter){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.lose_related_data)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final DBWorkoutHelper db = new DBWorkoutHelper(getActivity());
                        db.deleteWorkout(workout);
                        adapter.remove(workout);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, null);
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    public void setRoutine(Routine routine){
        this.routine = routine;
    }
    public void setWorkout(Workout workout) { this.workout = workout; }


}
