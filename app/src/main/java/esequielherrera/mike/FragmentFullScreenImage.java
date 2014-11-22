package esequielherrera.mike;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by esequielherrera-ortiz on 11/21/14.
 */
public class FragmentFullScreenImage extends Fragment {

    private String imagePath;

    public FragmentFullScreenImage(String imagePath){
        this.imagePath = imagePath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bitmap image = BitmapFactory.decodeFile(imagePath);
        ImageView imgView = new ImageView(getActivity());
        imgView.setImageBitmap(image);

        return imgView;

    }


}
