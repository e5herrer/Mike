package esequielherrera.mike;

/**
 * Created by esequielherrera-ortiz on 9/24/14.
 */
public class Workout {

    private int id;
    private int routineId;
    private String name;
    private String exerciseName;
    private int sets;
    private String reps;
    private int restTime;
    private int position;
    private String timeStamp;

    public Workout() {

    }

    public Workout(int id, int routineId, String name, String workoutName, int sets, String reps,
                   int restTime, int position, String timeStamp){
        this.id = id;
        this.routineId = routineId;
        this.name = name;
        this.exerciseName = workoutName;
        this.sets = sets;
        this.reps = reps;
        this.restTime = restTime;
        this.position = position;
        this.timeStamp = timeStamp;
    }

    public Workout(int routineId, String name, String workoutName, int sets, String reps, int restTime){
        this.routineId = routineId;
        this.name = name;
        this.exerciseName = workoutName;
        this.sets = sets;
        this.reps = reps;
        this.restTime = restTime;
    }

    @Override
    public String toString(){
        return this.exerciseName + " reps:" + this.sets + " sets:" + this.reps;
    }

    public void setId(int id) {this.id = id;}

    public int getId() { return id;}

    public int getRoutineId() {
        return routineId;
    }

    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String workoutName) {
        this.exerciseName = workoutName;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
