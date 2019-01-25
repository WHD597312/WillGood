package com.peihou.willgood.pojo;

public class AlermLog {
    private String name;
    private String type;
    private String timer;

    public AlermLog(String name, String type, String timer) {
        this.name = name;
        this.type = type;
        this.timer = timer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }
}
