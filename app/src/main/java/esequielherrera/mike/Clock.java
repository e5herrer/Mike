package esequielherrera.mike;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by esequielherrera-ortiz on 11/18/14.
 */
public class Clock {

    private Context context;
    private android.widget.TextView display;
    private CountDownTimer timer;
    private MediaPlayer alarm;

    private Runnable stopWatch;
    private long elapsedTime;
    private long startTime;
    private Handler mHandler = new Handler();
    private final int REFRESH_RATE = 100;
    private int displayWidth;



    public Clock(Context context, TextView display){
        this.context = context;
        this.display = display;
        this.alarm = getAlarm();
        this.display.setText("00:00:00");
    }



    /**
     * startTimer - creates a timer with the provided seconds. When time finishes rings an alarm.
     * @param seconds - seconds before timer finishes.
     */
    public void startTimer(int seconds){

        if(displayWidth == 0)
            displayWidth = this.display.getWidth();


        if(!onStandby()){
            reset();
        }

        final int milSec = seconds  * 1000;

        timer =  new CountDownTimer(milSec, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                display.setText(timerFormat(millisUntilFinished));

            }

            @Override
            public void onFinish() {
                display.setGravity(Gravity.CENTER_HORIZONTAL);
                display.setText("Stop");
                display.setWidth(displayWidth);

                if (alarm != null)
                    alarm.start();
            }
        };

        timer.start();
    }

    /**
     *  startStopWatch - Begins a stop watch and refreshes the display
     */
    public void startStopWatch() {

        if(displayWidth == 0)
            displayWidth = this.display.getWidth();


        if(!onStandby()){
            reset();
        }

        startTime = System.currentTimeMillis();

        stopWatch = new Runnable() {
            public void run() {
                elapsedTime = System.currentTimeMillis() - startTime;
                display.setText(timerFormat(elapsedTime));
                mHandler.postDelayed(this, REFRESH_RATE);
            }
        };
        stopWatch.run();
    }

    /**
     * Resets any stopwatch or timer we have created. Also shut off the alarm if activated.
     */
    public void reset(){
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        if(stopWatch != null){
            mHandler.removeCallbacks(stopWatch);
            stopWatch = null;
        }

        if(alarm.isPlaying()) {
            alarm.stop();
            try {
                alarm.prepare();
            } catch (IOException e) {
                alarm.release();
                alarm = null;
            }
        }

        display.setText("00:00:00");
    }

    /**
     * Takes in time in milliseconds and formats to the specified hh:mm:ss
     * @param time - time in milliseconds
     * @return - String representation of milliseconds in the form of hh:mm:ss
     */
    private String timerFormat (float time){
        long secs = (long)(time/1000);
        long mins = (long)((time/1000)/60);
        long hrs = (long)(((time/1000)/60)/60);

        String seconds;
        String minutes;
        String hours;

		/* Convert the seconds to String
		 * and format to ensure it has
		 * a leading zero when required
		 */
        secs = secs % 60;
        seconds=String.valueOf(secs);
        if(secs == 0){
            seconds = "00";
        }
        if(secs <10 && secs > 0){
            seconds = "0"+seconds;
        }

		/* Convert the minutes to String and format the String */

        mins = mins % 60;
        minutes=String.valueOf(mins);
        if(mins == 0){
            minutes = "00";
        }
        if(mins <10 && mins > 0){
            minutes = "0"+minutes;
        }

    	/* Convert the hours to String and format the String */

        hours=String.valueOf(hrs);
        if(hrs == 0){
            hours = "00";
        }
        if(hrs <10 && hrs > 0){
            hours = "0"+hours;
        }

		/* Setting the timer text to the elapsed time */
        return hours+ ":" + minutes + ":" + seconds;
    }

    /**
     * @return MediaPlayer - an alarm ready to be run
     */
    private MediaPlayer getAlarm(){

        MediaPlayer mMediaPlayer = new MediaPlayer();

        try
        {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mMediaPlayer.setDataSource( context, notification);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
            }
        }
        catch (IOException e)
        {
            // oops!
        }
        return mMediaPlayer;
    }

    public void setDisplay(TextView display){
        String time = this.display.getText().toString();
        this.display = display;
        display.setGravity(Gravity.CENTER_HORIZONTAL);
        this.display.setText(time);
        this.display.setWidth(displayWidth);
    }

    /**
     * @return - Returns true if no timer and alarm are currently running else returns false
     */
    public boolean onStandby(){
        return timer == null && stopWatch == null;
    }

}
