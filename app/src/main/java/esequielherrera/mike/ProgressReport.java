package esequielherrera.mike;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by esequielherrera-ortiz on 11/22/14.
 */
public class ProgressReport {
    int id;
    int routineId;
    String date;
    ArrayList<ProgressPic> album = new ArrayList<ProgressPic>();
    String weight;

    public ProgressReport(){
        DateFormat df = DateFormat.getDateInstance();
        this. date = df.format( new Date());
        this.weight = "0";
    }
    public ProgressReport(String weight, String date) {
        this.date = date;
        this.weight = weight;
    }

    public void addPhoto(ProgressPic pic){
        album.add(pic);
    }

    public void deletePhoto(ProgressPic pic){
        album.remove(pic);
    }

    public String getDate() {
        return date;
    }

    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    public int getId() {

        return id;
    }

    public ArrayList<ProgressPic> getAlbum() {
        return album;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getRoutineId() {
        return routineId;
    }


}
