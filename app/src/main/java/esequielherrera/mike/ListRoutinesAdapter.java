package esequielherrera.mike;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 9/29/14.
 */
public class ListRoutinesAdapter extends ArrayAdapter<Routine> {

    private final Context context;
    private final List<Routine> routines;


    public ListRoutinesAdapter(Context context, List<Routine> routines) {
        super(context, R.layout.routine_main_list_item, routines);
        this.context = context;
        this.routines = routines;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView name;


        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();

            if (position == 0)
                convertView = inflater.inflate(R.layout.routine_main_list_item, parent, false);
            else {
                convertView = inflater.inflate(R.layout.routine_list_item, parent, false);
            }
        }

        //First routine has a custom list view
        if (position == 0) {
            Routine routine = routines.get(position);

            TextView days = (TextView) convertView.findViewById(R.id.dayTextView);
            ImageView beforePicHolder = (ImageView) convertView.findViewById(R.id.beforePic);
            ImageView afterPicHolder = (ImageView) convertView.findViewById(R.id.afterPic);

            //Set routine view progress pictures
            if (routine.getBeforePic() != null) {
                Bitmap pic = BitmapFactory.decodeFile(routine.getBeforePic());
                if (pic != null) {
                    Bitmap picScaled = Bitmap.createScaledBitmap(pic, 200, 250, true);
                    beforePicHolder.setImageBitmap(picScaled);
                } else {
                    //image was deleted so remove from database
                    routine.setBeforePic(null);
                    DBRoutineHelper db = new DBRoutineHelper(context);
                    db.updateRoutine(routine);
                }

                //Taking care of the after pic
                if (routine.getAfterPic() != null) {
                    Bitmap pic2 = BitmapFactory.decodeFile(routine.getAfterPic());
                    if (pic2 != null) {
                        Bitmap picScaled2 = Bitmap.createScaledBitmap(pic2, 250, 200, true);
                        afterPicHolder.setImageBitmap(picScaled2);
                    } else {
                        routine.setAfterPic(null);
                        DBRoutineHelper db = new DBRoutineHelper(context);
                        db.updateRoutine(routine);
                    }
                }

            }
            //Needed because before pic would carry over when main view was deleted
            else if(beforePicHolder.getWidth() != 0){
                beforePicHolder.setImageBitmap(null);
            }

            //Set days on the routine
            days.setText(routine.ageInDays());
        }

        //sotring routine in view
        convertView.setTag(routines.get(position));

        //Setup title bar of the routines
        name = (TextView) convertView.findViewById(R.id.name);
        name.setText(routines.get(position).getName());

        return convertView;
    }


    //Required getViewTypeCount and getItemView Type because cycle would crash interchanging layouts
    /**
     * Description lets adapter know we have multiple layouts
     * @return - number of difference layouts
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Method to determine which layout to choose based on the position provided
     * @param position - Position in the cycle
     * @return - index of resource you want depending on position
     */
    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return 0;
        else
            return 1;
    }

}
