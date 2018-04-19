package com.scut.veriface.baiduface.entity;

import java.util.Arrays;

public class IdentifyResult {
    public Result[] result;
    public int result_num;


    @Override
    public String toString() {
        return "IdentifyResult{" +
                "result=" + Arrays.toString(result) +
                ", result_num=" + result_num +
                '}';
    }

    public class Result {
        public String uid;
        public double[] scores;
        public String group_id;
        public String user_info;

        @Override
        public String toString() {
            return "Result{" +
                    "uid='" + uid + '\'' +
                    ", scores=" + Arrays.toString(scores) +
                    ", group_id='" + group_id + '\'' +
                    ", user_info='" + user_info + '\'' +
                    '}';
        }
    }

}
