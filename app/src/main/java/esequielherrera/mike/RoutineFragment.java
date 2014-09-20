package esequielherrera.mike;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;



/**
 * Created by esequielherrera-ortiz on 9/17/14.
 */

public class RoutineFragment extends Fragment {

    private static final String FILENAME = "Mike_Routine_File";

    public RoutineFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_routine, container, false);

        //MAP XML with java
        final EditText routineName = (EditText) rootView.findViewById(R.id.routineName);
        final EditText currentWeight = (EditText) rootView.findViewById(R.id.currentWeight);
        final EditText weightGoal = (EditText) rootView.findViewById(R.id.weightGoal);
        final DatePicker endDate = (DatePicker) rootView.findViewById(R.id.endDate);
        final ImageView beforePic = (ImageView) rootView.findViewById(R.id.beforePic);
        final Button saveButton = (Button) rootView.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                createNewRoutine(getActivity(), routineName,  currentWeight, weightGoal, endDate, beforePic);
            }
        });

        return rootView;
    }


    /*
        Description: Takes in the application context and the EditText field and stored the
        routine in the internal storage. If successfully created changes the save button onClick
        function.
     */
    private boolean createNewRoutine(Context context, EditText fName, EditText cWeight,
                                     EditText wGoal, DatePicker endDate, ImageView beforePic){

        String fileName = fName.getText().toString().trim();
        FileOutputStream fos = obtainStream(context);
        String currentWeight = cWeight.getText().toString().trim();
        String weightGoal = wGoal.getText().toString().trim();



        if(fileName == null || fileName.equals("")){
            Toast.makeText(context, "Please insert a routine name", Toast.LENGTH_LONG).show();
            fName.requestFocus();
            return false;
        }
        if(currentWeight.equals("")){
            cWeight.setText("N/A");
        }

        if(weightGoal.equals("")){
            wGoal.setText("N/A");
        }

        if(fos != null ) {
            try {
                fos.write(fileName.getBytes());
                fos.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private FileOutputStream obtainStream(Context context){
        FileOutputStream fos = null;
        try {
            fos =  context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return fos;
    }
}

