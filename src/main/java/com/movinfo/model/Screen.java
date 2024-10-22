package com.movinfo.model;

import java.util.LinkedList;
import java.util.List;

public class Screen {
    private String movieName;
    private String screenDate;
    private List<String> screentypes;

    public Screen(String movieName, String screenDate){
        this.movieName = movieName;
        this.screenDate = screenDate;
        screentypes = new LinkedList<>();
    }

    public String getMovieName() {
        return movieName;
    }
    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
    public String getScreenDate() {
        return screenDate;
    }
    public void setScreenDate(String screenDate) {
        this.screenDate = screenDate;
    }
    public List<String> getScreentypes() {
        return screentypes;
    }
    public void addScreentype(String screentype) {
        screentypes.add(screentype);
    }
}
