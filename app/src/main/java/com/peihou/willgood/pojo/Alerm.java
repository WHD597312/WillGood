package com.peihou.willgood.pojo;

public class Alerm {
    private String name;
    private int type;
    private boolean open;

    public Alerm(String name, int type, boolean open) {
        this.name = name;
        this.type = type;
        this.open = open;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
