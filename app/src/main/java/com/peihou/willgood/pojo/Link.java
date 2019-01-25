package com.peihou.willgood.pojo;

public class Link {
    private int type;
    private boolean check;

    public Link(int type, boolean check, String name) {
        this.type = type;
        this.check = check;
        this.name = name;
    }

    private String name;




    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
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
}
