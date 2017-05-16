package seungchan.com.tobaccoach_5_2.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017-05-11.
 */

public class Record implements Serializable{
    private Date date;
    private double nicotine;
    private int user_id;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getNicotine() {
        return nicotine;
    }

    public void setNicotine(double nicotine) {
        this.nicotine = nicotine;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
