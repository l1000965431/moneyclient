package dao;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by fisher on 2015/6/29.
 */
@Entity
public class Spitter {
    private long id;
    private String userName, passWord, fullName;

    public Spitter(long id, String n, String p, String f){
        this.id = id;
        this.userName = n;
        this.passWord = p;
        this.fullName = f;
    }
    public Spitter(){}
    public void setId(long id){
        this.id = id;
    }
    @Id
    public long getId(){
        return id;
    }
    public String getUserName(){
        return this.userName;
    }
    public void setUserName(String n){
        this.userName = n;
    }
    public String getPassWord(){
        return this.passWord;
    }
    public void setPassWord(String p){
        this.passWord = p;
    }
    public String getFullName(){
        return this.fullName;
    }
    public void setFullName(String f){
        this.fullName = f;
    }
}
