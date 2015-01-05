package esequielherrera.mike;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by esequielherrera-ortiz on 11/20/14.
 */
public class ListProgressReportAdapter extends ArrayAdapter<ProgressReport> {

    Context context;
    ArrayList<ProgressReport> reports;
    public ListProgressReportAdapter(Context context, ArrayList<ProgressReport> reports){
        super(context, R.layout.fragment_progress_reports, reports);
        this.context = context;
        this.reports = reports;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.gallery_list_item, parent, false);
        }

        TextView date = (TextView) convertView.findViewById(R.id.date);
        LinearLayout album = (LinearLayout) convertView.findViewById(R.id.album);
        album.removeAllViews();

        ProgressReport report = reports.get(position);

        if(report.getWeight().equals(""))
            date.setText(report.getDate());
        else
            date.setText(report.getDate() + " - " + report.getWeight());


        for (ProgressPic pic : report.getAlbum()) {

            Bitmap picBitmap = pic.getBitmap(200, 250);
            if (picBitmap != null) {
                ImageView imgView = new ImageView(context);
                imgView.setPadding(2,2,2,2);
                imgView.setImageBitmap(picBitmap);

                final String imgPath = pic.getPath();
                imgView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity)context).startFragmentFullScreenImage(imgPath);
                    }
                });
                album.addView(imgView);
            }
        }
        return convertView;

    }

}
