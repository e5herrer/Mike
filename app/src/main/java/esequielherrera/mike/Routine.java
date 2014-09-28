package esequielherrera.mike;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

/**
 * Created by esequielherrera-ortiz on 9/23/14.
 */
public class Routine implements Serializable {
    private int id;
    private String name;
    private String startDate;
    private String endDate;
    private int startWeight;
    private int endWeight;
    private String lastModified;
    private String timeStamp;

    public Routine(){
    }

    public Routine(String name, String endDate, int startWeight){
        this.name = name;
        this.endDate = endDate;
        this.startWeight = startWeight;
    }

    public Routine(int id, String name, String startDate, String endDate, int startWeight,
                   int endWeight, String lastModified, String timeStamp){
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startWeight = startWeight;
        this.endWeight = endWeight;
    }


    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setStartDate(String date){
        this.startDate = date;
    }

    public void setEndDate(String date){
        this.endDate = date;
    }

    public void setStartWeight(int weight){
        this.startWeight = weight;
    }

    public void setEndWeight(int weight){
        this.endWeight = weight;
    }

    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }

    public String getStartDate(){
        return this.startDate;
    }

    public String getEndDate(){
        return this.endDate;
    }

    public int getStartWeight(){
        return this.startWeight;
    }

    public int getEndWeight(){
        return this.endWeight;
    }





}
