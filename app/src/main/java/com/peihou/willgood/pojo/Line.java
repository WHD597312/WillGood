package com.peihou.willgood.pojo;

public class Line {
    private boolean onClick;
    private String name;

    public Line(boolean onClick, String name) {
        this.onClick = onClick;
        this.name = name;
    }

    public boolean isOnClick() {
        return onClick;
    }

    public void setOnClick(boolean onClick) {
        this.onClick = onClick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
