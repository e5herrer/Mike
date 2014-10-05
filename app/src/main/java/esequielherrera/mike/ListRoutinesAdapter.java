package esequielherrera.mike;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/29/14.
 */
public class ListRoutinesAdapter extends ArrayAdapter<Routine>{

    private final Context context;
    private final List<Routine> routines;


    public ListRoutinesAdapter(Context context, List<Routine> routines) {
        super(context, R.layout.routine_list_item, routines);
        this.context = context;
        this.routines = routines;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView name, startDate, endDate;

        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.routine_list_item, null);
            convertView.setTag(routines.get(position));
        }

        name = (TextView)convertView.findViewById(R.id.name);
        startDate = (TextView)convertView.findViewById(R.id.startDate);
        endDate = (TextView)convertView.findViewById(R.id.endDate);
        name.setText(routines.get(position).getName());
        startDate.setText(String.valueOf(routines.get(position).getStartDate()));
        endDate.setText(routines.get(position).getEndDate());

        return convertView;
    }

}
