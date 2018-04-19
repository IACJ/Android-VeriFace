package com.scut.veriface.util;

/**
 * Created by kidd on 2017/12/21.
 */

public class Person {
    private String uid;
    private String user_info;
    private String class_id;
    private int imageId;

    public Person(String uid, String user_info, String class_id, int imageId) {
        this.uid = uid;
        this.user_info = user_info;
        this.class_id = class_id;
        this.imageId = imageId;
    }

    public String getUid() {
        return uid;
    }

    public String getUser_info() {
        return user_info;
    }

    public String getClass_id() {
        return class_id;
    }

    public int getImageId() {
        return imageId;
    }
}
