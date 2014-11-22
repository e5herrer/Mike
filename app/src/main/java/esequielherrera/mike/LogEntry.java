package esequielherrera.mike;

import java.util.Date;

/**
 * Created by esequielherrera-ortiz on 10/5/14.
 */
public class LogEntry {

    private int id, workoutId, setNum, weight, restTime;
    private boolean isSet = false;
    private String reps, notes, timeStamp;

    public LogEntry(){
    }

    public LogEntry(int id, int workoutId, int setNum, int weight, String reps, String notes, int restTime, String timeStamp) {
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean isSet) {
        this.isSet = isSet;
    }
}
