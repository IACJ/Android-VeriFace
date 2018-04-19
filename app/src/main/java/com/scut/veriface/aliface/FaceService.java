package com.scut.veriface.aliface;

import com.scut.veriface.util.PasswdUtil;

import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.InputStream;

public class FaceService {


    public static String getImageStr(InputStream inputStream) {

        byte[] data = null;
        try {
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    public static String doFaceAPI(String strImg) throws Exception {

        String ak_id = PasswdUtil.getAliyunPasswd__ak_id(); //保密ak
        String ak_secret = PasswdUtil.getAliyunPasswd__ak_secret(); // 保密ak_secret
        String url = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/detect";

        String postBody = "{\"type\": \"1\", \"content\":\"";
        postBody += strImg;
        postBody += "\"}";

        String jsonRep = null;

        System.out.println("花钱啦！");
        System.out.println("sendPost...");
        jsonRep = AESDecode.sendPost(url, postBody, ak_id, ak_secret);
        System.out.println("done!");

        return jsonRep;
    }
}
