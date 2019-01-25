package com.peihou.willgood.pojo;

public class SwtichState {
    private int type;
    private String name;
    private int state;

    public SwtichState(int type, String name, int state) {
        this.type = type;
        this.name = name;
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
