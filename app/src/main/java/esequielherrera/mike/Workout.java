package esequielherrera.mike;

/**
 * Created by esequielherrera-ortiz on 12/17/14.
 */
public class Workout {
    private int id;
    private int routineId;
    private String name;
    private String lastModified;

    private Exercise [] exercises;

    public Workout(int routineId, String name){
        this.routineId = routineId;
        this.name = name;
    }
    public Workout(int id, int routineId, String name, String lastModified) {
        this.id = id;
        this.routineId = routineId;
        this.name = name;
        this.lastModified = lastModified;
    }

    public String getLastModified() {
        return lastModified;
    }

    public int getRoutineId() {
        return routineId;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id){
        this.id = id;

    }

    public String getName() {

        return name;
    }
}
