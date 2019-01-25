package com.peihou.willgood.pojo;

public class TimerTask {

    private boolean open;
    private String name;
    private String timer;

    public TimerTask(boolean open, String name, String timer) {
        this.open = open;
        this.name = name;
        this.timer = timer;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }
}
