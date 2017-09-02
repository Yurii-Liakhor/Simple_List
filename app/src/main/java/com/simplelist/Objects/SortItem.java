package com.simplelist.Objects;

/**
 * Created by Yurii on 03.08.2017.
 */

public class SortItem {
    private int imageRes;
    private Integer textRes;

    public SortItem(int imageRes, int textRes){
        this.imageRes = imageRes;
        this.textRes = textRes;
    }

    public int getImageRes() {
        return imageRes;
    }

    public int getTextRes() {
        return textRes;
    }
}