package com.scut.veriface.baiduface;

import com.scut.veriface.baiduface.utils.HttpUtil;

import java.net.URLEncoder;

/**
 * 人脸识别—— M vs N
 */
public class IdentifyMvN {


    public static String identify(String accessToken,String group_id,String imgStr) throws Exception {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v2/multi-identify";
        try {

            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param =   "group_id=" + group_id +  "&images=" + imgParam +"&detect_top_num=10" ;

            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}