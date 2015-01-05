package esequielherrera.mike;

/**
 * Created by esequielherrera-ortiz on 9/24/14.
 */
public class Exercise {

    private int id;
    private int workoutId;
    private String name;
    private int sets;
    private String reps;
    private int restTime;
    private int position;

    public Exercise() {

    }

    public Exercise(int id, int workoutId, String name, int sets, String reps,
                    int restTime, int position){
        this.id = id;
        this.workoutId = workoutId;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.restTime = restTime;
        this.position = position;
    }

    public Exercise(String name, int sets, String reps, int restTime){
        this.id = -1;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.restTime = restTime;
    }

    @Override
    public String toString(){
        return this.name + " reps:" + this.sets + " sets:" + this.reps;
    }

    public void setId(int id) {this.id = id;}

    public int getId() { return id;}

    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId( int workoutId ){
        this.workoutId = workoutId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
