package esequielherrera.mike;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by esequielherrera-ortiz on 9/29/14.
 * Description: Fragment that lists all the routines. Allowing CRUD functionality for routines.
 */
public class FragmentRoutine extends Fragment {
    private List<Routine> routines;

    public FragmentRoutine(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_routine, container, false);
        final ListView routinesList = (ListView) rootView.findViewById(R.id.routinesList);
        final TextView noRoutines = (TextView) rootView.findViewById(R.id.noRoutine);


        routines = new DBRoutineHelper(getActivity()).getAllRoutines();

        setListAdapter(routinesList, routines);

        //Signaling Activity to call onCreateOptionMenu
        setHasOptionsMenu(true);

        if(routines.size() != 0){
            noRoutines.setTextSize(0);
        }


        return rootView;

    }

    /**
     * Description - Sets the actionListeners to the action buttons in the action title bar
     * @param item Our action view item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.new_routine:
                ((MainActivity)getActivity()).startAddRoutineFragment();
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
        inflater.inflate(R.menu.fragment_routine_actions, menu);
    }


    /**
     * Description: Handles setting up the routine list's adapter
     * @param list - routine list
     */
    private void setListAdapter(ListView list, List<Routine> routines){
        final ListRoutinesAdapter adapter = new ListRoutinesAdapter(getActivity(), routines);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> l, View v, int position,
                                    long id) {
                ((MainActivity)getActivity()).startAddWorkoutFragment((Routine)v.getTag());
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> l, View v, int position,
                                           long id) {
                optionBuilder((Routine) v.getTag(), adapter);
                return true;
            }


        });

    }


    /**
     * Called upon when a list item is held.
     * @param routine - routine associated to the list item selected
     * @param adapter - List adapter used signal of data change.
     */
    public void optionBuilder(final Routine routine, final ArrayAdapter adapter){
        final String [] items = new String [] { "Rename", "Delete" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Delete")) {
                    deleteConfirmation(getActivity(), routine, adapter);
                } else if (items[item].equals("Rename")) {
                    renameBuilder(routine, adapter);
                }
            }
        });
        builder.show();
    }

    /**
     * Description: renameBuilder prompts the DialogueBox with a textfield allowing users to rename
     *              a routine.
     * @param rtn: The routine to be renames
     * @param adpt: Adapter to notify that the rtn is modified
     */
    public void renameBuilder(final Routine rtn, final ArrayAdapter adpt){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        builder.setTitle("Rename");
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();

                if( !value.equals("") ) {
                    rtn.setName(value);
                    DBRoutineHelper db = new DBRoutineHelper(getActivity());
                    db.updateRoutine(rtn);
                    adpt.notifyDataSetChanged();
                }
            }
        });
        builder.show();
    }

    /**
     * Returns an ArrayList with routines ordered by when tehy were last modified
     * @return
     */
    private List<Routine> getRoutinesByLastModified(){
        List<Routine> routineList = new ArrayList<Routine>();
        List<Integer> routineIds = new ArrayList<Integer>();

        //All the Database helpers being used
        DBLogHelper logDB = new DBLogHelper(getActivity());
        DBWorkoutHelper workoutDB = new DBWorkoutHelper(getActivity());
        DBRoutineHelper routineDB = new DBRoutineHelper(getActivity());

        //Getting log lists in descending order by timestamp removing duplicates of matching workout id
        List<LogEntry> logList = logDB.getWorkoutsByLastModified();

        if(logList != null && logList.size() > 0){

            //Iterate through logs and build routine id list and make sure the workouts they belong to wasn't deleted
            for(LogEntry log : logList){
                Workout workout = workoutDB.getWorkout(log.getWorkoutId());
                if(workout != null) {
                    routineIds.add(workout.getRoutineId());
                }

                //Remove duplicate routine id's while preserving order
                LinkedHashSet<Integer> tempSet = new LinkedHashSet<Integer>(routineIds);
                ArrayList<Integer> uniqueRoutines = new ArrayList<Integer>(tempSet);

                //Obtain routines using routine ids
                for(int i : uniqueRoutines){
                    Routine routine = routineDB.getRoutine(i);
                    if(routine != null) {
                        routineList.add(routine);
                    }
                }
            }


        }
        return routineList;
    }

    /**
     * deleteConfirmation - Promps the user witha  dialogue box to confirm action of delete a routine
     * @param context
     * @param routine
     * @param adapter
     */
    public void deleteConfirmation(final Context context, final Routine routine, final ArrayAdapter adapter){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.lose_related_data)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final DBRoutineHelper db = new DBRoutineHelper(getActivity());
                        db.deleteRoutine(routine);
                        routines.remove(routine);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, null);
        // Create the AlertDialog object and return it
        builder.create().show();
    }

}
