package esequielherrera.mike;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/29/14.
 */
public class RoutineFragment extends Fragment {
    private List<Routine> routines;

    public RoutineFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_routine, container, false);
        final ListView routinesList = (ListView) rootView.findViewById(R.id.routinesList);
        final Button addButton = (Button) rootView.findViewById(R.id.newButton);

        DBRoutineHelper db = new DBRoutineHelper(getActivity());
        this.routines = db.getAllRoutines();

        addButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                startRoutineFragment(getActivity());
            }
        });

        setListAdapter(routinesList);

        return rootView;

    }

    private void setListAdapter(ListView list){
        final ListRoutinesAdapter adapter = new ListRoutinesAdapter(getActivity(), this.routines);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> l, View v, int position,
                                    long id) {
                AddWorkoutFragment fragment = new AddWorkoutFragment();
                fragment.setRoutine((Routine)v.getTag());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.commit();
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



    public void optionBuilder(Routine rtn, ArrayAdapter adpt){
        final String [] items = new String [] { "Duplicate", "Delete" };
        final DBRoutineHelper db = new DBRoutineHelper(getActivity());
        final Routine routine = rtn;
        final ArrayAdapter adapter = adpt;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What would you like to do?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Delete")) {
                    db.deleteRoutine(routine);
                    adapter.remove(routine);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void startRoutineFragment(Context context){
        Fragment newFragment = new AddRoutineFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.commit();
    }
}
