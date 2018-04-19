package com.scut.veriface.aliface;

import java.util.Arrays;

public class Result {
    /* 实体类，用于把 json 字符串 转换为该类对象 */
    public int face_num;
    public int[] face_rect;
    public double[] face_prob;
    public double[] pose;
    public int landmark_num;
    public double[] landmark;
    public double[] iris;
    public int errno;

    @Override
    public String toString() {
        return "Result{" +
                "face_num=" + face_num +
                ", face_rect=" + Arrays.toString(face_rect) +
                ", face_prob=" + Arrays.toString(face_prob) +
                ", pose=" + Arrays.toString(pose) +
                ", landmark_num=" + landmark_num +
                ", landmark=" + Arrays.toString(landmark) +
                ", iris=" + Arrays.toString(iris) +
                ", errno=" + errno +
                '}';
    }
}
