package com.example.kit;

public class History {
    private String name;

    private String time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIfSaved() {
        return ifSaved;
    }

    public void setIfSaved(String ifSaved) {
        this.ifSaved = ifSaved;
    }

    private String ifSaved;
}
