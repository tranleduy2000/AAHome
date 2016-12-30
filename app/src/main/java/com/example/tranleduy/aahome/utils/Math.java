package com.example.tranleduy.aahome.utils;

/**
 * Created by tranleduy on 24-May-16.
 */
public class Math {
    public int convertToF(String C) {
        int mF = Integer.parseInt(C);
        return (int) (mF * 1.8 + 32);
    }

}
