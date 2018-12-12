package com.ysongyang.antifinapi.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.fc.csplatform.common.crypto.Base64Util;
import com.alipay.fc.csplatform.common.crypto.CustomerInfoCryptoUtil;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class SecruityUtils {

    // 公钥
    private static String PUB_KEY = "";


    /**
     * 还原出RSA公钥对象
     *
     * @return
     * @throws Exception
     */
    public static PublicKey getPubKey() throws Exception {

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64Util.decode(PUB_KEY));
        KeyFactory keyFactory;
        keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = null;
        key = keyFactory.generatePublic(keySpec);
        return key;
    }

    public static String getCinfoKey(JSONObject cinfo) {
        Map<String, Object> json = new HashMap<String, Object>();
        try {
            PublicKey pubKey = getPubKey();
            Map<String, String> map = CustomerInfoCryptoUtil.encryptByPublicKey(cinfo.toString(), pubKey);
            json.put("key", map.get("key"));
            json.put("cinfo", map.get("text"));
            return JSON.toJSONString(json);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        JSONObject extInfo = new JSONObject();

        extInfo.put("userId", "123456");
        extInfo.put("userName", "张三");

        JSONObject cinfo = new JSONObject();
        cinfo.put("userId", "123456");
        cinfo.put("extInfo", extInfo);
        System.out.println("返回数据：");
        String json = getCinfoKey(cinfo);
        System.out.println(json);
    }

}
