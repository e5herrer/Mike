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
import java.util.List;

/**
 * Created by esequielherrera-ortiz on 11/20/14.
 */
public class ListGalleryAdapter extends ArrayAdapter<ProgressPic> {

    Context context;
    ArrayList<List<ProgressPic>> gallery;

    public ListGalleryAdapter(Context context, ArrayList<ProgressPic> pics){
        super(context, R.layout.fragment_gallery, pics);
        this.context = context;
        orderPhotos(pics);
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

        if(gallery.get(position).size() != 0)
            date.setText(gallery.get(position).get(0).getDate());

        for (ProgressPic pic : gallery.get(position)) {

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

    public void orderPhotos(ArrayList<ProgressPic> allPics){
        ArrayList<List<ProgressPic>> gallery = new ArrayList<List<ProgressPic>>();
        ArrayList<ProgressPic> album = new ArrayList<ProgressPic>();

        String date;

        date = allPics.get(0).getDate();

        for (ProgressPic pic : allPics) {
            if (date.equals(pic.getDate())) {
                album.add(pic);
            }
            else{
                gallery.add(album);
                album = new ArrayList<ProgressPic>();
                date = pic.getDate();
            }

        }
        gallery.add(album);

        this.gallery = gallery;
    }

    public void addImage(ProgressPic pic){
        gallery.get(0).add(pic);
    }
    @Override
    public int getCount(){
        return gallery.size();
    }

}
