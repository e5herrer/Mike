package esequielherrera.mike;


/**
 * Created by esequielherrera-ortiz on 9/24/14.
 */
public class ProgressPic {

    private int id;
    private int routineId;
    private String uri;
    private String date;

    public ProgressPic(){
    }

    public ProgressPic(int id, int routineId, String uri, String date) {
        this.id = id;
        this.routineId = routineId;
        this.uri = uri;
        this.date = date;
    }

    public ProgressPic(int routineId, String uri, String date){
        this.routineId = routineId;
        this.uri = uri;
        this.date = date;
    }

    public int getProgressPicId() {
        return id;
    }

    public int getroutineId() {
        return routineId;
    }


    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
