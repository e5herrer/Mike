package esequielherrera.mike;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by esequielherrera-ortiz on 9/23/14.
 */
public class Routine implements Serializable {
    private int id;
    private String name;
    private String startDate;
    private String lastModified;
    private String beforePic;
    private String afterPic;

    public Routine(String name){
        this.name = name;
    }

    public Routine(int id, String name, String startDate, String lastModified, String beforePic, String afterPic){
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.lastModified = lastModified;
        this.beforePic = beforePic;
        this.afterPic = afterPic;
    }

    /**
     * Description - Calculates how many days have passed since this routine was created
     * @return - String representation of the age of routine in days.
     */
    public  String ageInDays(){
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        try {
            if(startDate == null){
                return "0";
            }
            Date date1 = myFormat.parse(startDate);
            Date date2 = new Date();
            long diff = date2.getTime() - date1.getTime();
            return Long.toString(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        }
        catch(ParseException e){
            return "0";
        }
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
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

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getBeforePic() {
        return beforePic;
    }

    public void setBeforePic(String beforePic) {
        this.beforePic = beforePic;
    }

    public String getAfterPic() {
        return afterPic;
    }

    public void setAfterPic(String afterPic) {
        this.afterPic = afterPic;
    }
}
