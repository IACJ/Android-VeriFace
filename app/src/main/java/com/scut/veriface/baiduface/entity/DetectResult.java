package com.scut.veriface.baiduface.entity;

import java.util.Arrays;

public class DetectResult {
    public int result_num;
    public Result[] result;


    @Override
    public String toString() {
        return "DetectResult{" +
                "result_num=" + result_num +
                ", result=" + Arrays.toString(result) +
                '}';
    }

    public class Result{
        public Location location;
        public double age;
        public double beauty;
        public double face_probability;

        @Override
        public String toString() {
            return "Result{" +
                    "location=" + location +
                    ", age=" + age +
                    ", beauty=" + beauty +
                    ", face_probability=" + face_probability +
                    '}';
        }

        public class Location {
            public int left;
            public int top;
            public int width;
            public int height;

            @Override
            public String toString() {
                return "Location{" +
                        "left=" + left +
                        ", top=" + top +
                        ", width=" + width +
                        ", height=" + height +
                        '}';
            }
        }

    }
}
