package com.scut.veriface.util;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by IACJ on 2018/4/20.
 */

public class PasswdUtil {

    static private String aliyunPasswd__ak_id;
    static private String aliyunPasswd__ak_secret;

    static private String  baiduyun__API_Key;
    static private String  baiduyun__Secret_Key;

    public static Properties init(Context context) {
        Properties properties = new Properties();
        try {
            InputStream in = context.getAssets().open("passwd.properties");
            properties.load(in);
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        setAliyunPasswd__ak_id(properties.getProperty("aliyunPasswd__ak_id"));
        setAliyunPasswd__ak_secret(properties.getProperty("aliyunPasswd__ak_secret"));

        setBaiduyun__API_Key(properties.getProperty("baiduyun__API_Key"));
        setBaiduyun__Secret_Key(properties.getProperty("baiduyun__Secret_Key"));

        return properties;
    }

    //////////////////////////////////////////////////////
    public static String getBaiduyun__API_Key() {
        return baiduyun__API_Key;
    }

    public static void setBaiduyun__API_Key(String baiduyun__API_Key) {
        PasswdUtil.baiduyun__API_Key = baiduyun__API_Key;
    }

    public static String getBaiduyun__Secret_Key() {
        return baiduyun__Secret_Key;
    }

    public static void setBaiduyun__Secret_Key(String baiduyun__Secret_Key) {
        PasswdUtil.baiduyun__Secret_Key = baiduyun__Secret_Key;
    }

    public static String getAliyunPasswd__ak_id() {
        return aliyunPasswd__ak_id;
    }

    public static void setAliyunPasswd__ak_id(String aliyunPasswd__ak_id) {
        PasswdUtil.aliyunPasswd__ak_id = aliyunPasswd__ak_id;
    }

    public static String getAliyunPasswd__ak_secret() {
        return aliyunPasswd__ak_secret;
    }

    public static void setAliyunPasswd__ak_secret(String aliyunPasswd__ak_secret) {
        PasswdUtil.aliyunPasswd__ak_secret = aliyunPasswd__ak_secret;
    }
}
