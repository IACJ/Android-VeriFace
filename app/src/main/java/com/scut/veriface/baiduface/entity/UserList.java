package com.scut.veriface.baiduface.entity;

import java.util.Arrays;

public class UserList {
    public Result[] result;
    int result_num;

    public class Result {
        public String uid;
        public String user_info;

        @Override
        public String toString() {
            return "Result{" +
                    "uid='" + uid + '\'' +
                    ", user_info='" + user_info + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UserList{" +
                "results=" + Arrays.toString(result) +
                ", result_num=" + result_num +
                '}';
    }
}
