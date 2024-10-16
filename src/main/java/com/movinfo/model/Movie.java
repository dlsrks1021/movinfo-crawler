package com.movinfo.model;

import java.util.Date;

public class Movie {
    private String name;
    private String date;
    private String screentype;
    private Date expireAt;

    public Movie(String name, String date, String screentype, Date expireAt){
        this.name = name;
        this.date = date;
        this.screentype = screentype;
        this.expireAt = expireAt;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getScreentype() {
        return screentype;
    }
    public void setScreentype(String screentype) {
        this.screentype = screentype;
    }
    public Date getExpireAt() {
        return expireAt;
    }
    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }
}
