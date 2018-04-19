package com.scut.veriface.baiduface.entity;

import java.util.Arrays;

public class IdentifyMvNResult {
    public Result[] result;
    public int result_num;


    @Override
    public String toString() {
        return "IdentifyMvNResult{" +
                "result=" + Arrays.toString(result) +
                ", result_num=" + result_num +
                '}';
    }

    public class Result {
        public String uid;
        public double[] scores;
        public String group_id;
        public String user_info;
        public Location position;

        @Override
        public String toString() {
            return "Result{" +
                    "uid='" + uid + '\'' +
                    ", scores=" + Arrays.toString(scores) +
                    ", group_id='" + group_id + '\'' +
                    ", user_info='" + user_info + '\'' +
                    ", position=" + position +
                    '}';
        }

        public class Location {
            public double left;
            public double top;
            public double width;
            public double height;

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
