package esequielherrera.mike;

import android.net.Uri;
import java.util.Date;

/**
 * Created by esequielherrera-ortiz on 9/24/14.
 */
public class ProgressCheckPoint {
    private Uri[] gallery;
    private Date day;

    public ProgressCheckPoint() {
        this.day = new Date();
        this.gallery = new Uri[5];
    }

    public boolean addPhoto(Uri photoLocation){
        for(int i = 0; i < 5; i++){
            if(gallery[i] == null){
                gallery[i] = photoLocation;
                return true;
            }
        }
        return false;
    }

    public boolean deletePhoto(Uri photoLocation){
        for(int i = 0; i < 5; i++) {
            if (gallery[i].equals(photoLocation)) {
                gallery[i] = null;
                return true;
            }
        }
        return false;
    }

}
