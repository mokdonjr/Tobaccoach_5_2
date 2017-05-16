package seungchan.com.tobaccoach_5_2.model;

import java.io.Serializable;

import seungchan.com.tobaccoach_5_2.model.Tobacco;

/**
 * Created by Administrator on 2017-05-03.
 */
public class User implements Serializable {
    private String nick;
    private String password;
    private Tobacco tobac;

    public User(String nick, String password, Tobacco tobacco) {
        this.nick = nick;
        this.password = password;
        this.tobac = tobacco;
    }

    public User() {

    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Tobacco getTobac(){
        return tobac;
    }

    public void setTobac(Tobacco tobac){
        this.tobac = tobac;
    }
}
