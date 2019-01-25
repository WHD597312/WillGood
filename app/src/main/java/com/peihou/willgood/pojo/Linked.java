package com.peihou.willgood.pojo;

import android.support.annotation.NonNull;

import java.util.Comparator;

public class Linked{
    private boolean open;//开关
    private int type;//联动的类型
    private String lines;//联动的线路
    private String name;//联动的名称

    public Linked(boolean open, int type, String lines, String name) {
        this.open = open;
        this.type = type;
        this.lines = lines;
        this.name = name;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Linked)){
            return false;
        }
        Linked linked= (Linked) obj;
        if (this.type==linked.type){
            return true;
        }else {
            return false;
        }
    }


}
