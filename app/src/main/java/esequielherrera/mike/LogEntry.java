package esequielherrera.mike;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by esequielherrera-ortiz on 10/5/14.
 */
public class LogEntry {

    private int id, workoutId, setNum, restTime;
    private boolean isSet = false;


    private String weight, reps, notes, timeStamp;

    public LogEntry(){
    }

    public LogEntry(int id, int workoutId, int setNum, String weight, String reps, String notes, int restTime, String timeStamp) {
        this.id = id;
        this.workoutId = workoutId;
        this.setNum = setNum;
        this.weight = weight;
        this.restTime = restTime;
        this.reps = reps;
        this.notes = notes;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public int getSetNum() {
        return setNum;
    }

    public void setSetNum(int setNum) {
        this.setNum = setNum;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public String getReps() {

        if(reps == null){
            return "0";
        }
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getNotes() {

        if(notes == null){
            return "";
        }
        else{
            return notes;
        }
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean isSet) {
        this.isSet = isSet;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getDate() {
          SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date;
        try {
            date = timeStampFormat.parse(getTimeStamp());
        }
        catch(ParseException e){
            return null;
        }
        SimpleDateFormat newFormat = new SimpleDateFormat( "MMM dd yyy");
        return newFormat.format(date);
    }
}
