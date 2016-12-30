package com.example.tranleduy.aahome.items;

import java.io.Serializable;

/**
 * Created by ${DUY} on 18-Jun-16.
 */
public class ChartItem implements Serializable {
    public static final long serialVersionUID = 2L;

    private int valueTemp = 0;
    private int valueHumi = 0;
    private int color = 0;
    private int width = 0;
    private long millisSecond = 0;

    public ChartItem(int valueTemp, int valueHumi, int color, int width, long millisSecond) {

        this.valueTemp = valueTemp;
        this.valueHumi = valueHumi;
        this.color = color;
        this.width = width;
        this.millisSecond = millisSecond;
    }

    public ChartItem() {

    }

    public int getValueTemp() {
        return valueTemp;
    }

    public void setValueTemp(int valueTemp) {
        this.valueTemp = valueTemp;
    }

    public int getValueHumi() {
        return valueHumi;
    }

    public void setValueHumi(int valueHumi) {
        this.valueHumi = valueHumi;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public long getMillisSecond() {
        return millisSecond;
    }

    public void setMillisSecond(long millisSecond) {
        this.millisSecond = millisSecond;
    }
}
