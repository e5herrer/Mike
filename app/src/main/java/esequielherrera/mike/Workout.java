package esequielherrera.mike;

/**
 * Created by esequielherrera-ortiz on 9/24/14.
 */
public class Workout {

    private int id;
    private int routineId;
    private String name;
    private String exerciseName;
    private int reps;
    private int sets;
    private int restTime;
    private int position;

    public Workout() {

    }

    public Workout(int id, int routineId, String name, String workoutName, int reps, int sets,
                   int restTime, int position){
        this.id = id;
        this.routineId = routineId;
        this.name = name;
        this.exerciseName = workoutName;
        this.reps = reps;
        this.sets = sets;
        this.restTime = restTime;
        this.position = position;
    }

    public int getId() {
        return id;
    }

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

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
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
