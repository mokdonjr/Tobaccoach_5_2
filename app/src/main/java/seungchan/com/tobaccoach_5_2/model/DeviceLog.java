package seungchan.com.tobaccoach_5_2.model;

import java.io.Serializable;

/**
 * Created by USER on 2017-05-07.
 */

public class DeviceLog implements Serializable {

    // Fields
    private String dateTime;

    // constructors
    public DeviceLog(){}
    public DeviceLog(String dateTime){
        this.dateTime = dateTime;
    }

    // getters and setters
    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
