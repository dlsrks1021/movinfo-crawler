package com.movinfo.model;

import java.util.Date;

public class Movie {
    private String name;
    private Date dateOpen;
    private String poster;
    private Date expireAt;

    public Movie(String name, Date dateOpen, String poster){
        this.name = name;
        this.dateOpen = dateOpen;
        this.poster = poster;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Date getDateOpen() {
        return dateOpen;
    }
    public void setDateOpen(Date dateOpen) {
        this.dateOpen = dateOpen;
    }
    public String getPoster() {
        return poster;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }
    public Date getExpireAt() {
        return expireAt;
    }
    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }
}
