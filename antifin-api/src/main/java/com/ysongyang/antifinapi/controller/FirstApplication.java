package com.ysongyang.antifinapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.fc.csplatform.common.crypto.CustomerInfoCryptoUtil;
import com.ysongyang.antifinapi.util.SecruityUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class FirstApplication {

    @RequestMapping("/hello")
    private String index() {
        return "Hello World";
    }


    /**
     * 解密
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/decrypt")
    public String decryption(HttpServletRequest request) {

        Map<String, Object> map = new HashMap<>();

        if (request.getMethod().equals("GET")) {
            map.put("code", "1");
            map.put("msg", "请通过POST方式提交");
            return JSON.toJSONString(map);
        }

        //解密时需要提交的参数
        String params = request.getParameter("params");
        String key = request.getParameter("key");

        if (params == null || key == null) {
            map.put("code", "1");
            map.put("msg", "重要参数不能为空");
            return JSON.toJSONString(map);
        } else {

            try {
                params = params.trim().replaceAll(" ", "+"); //将空格替换为+号
                key = key.trim().replaceAll(" ", "+");
                PublicKey publicKey = SecruityUtils.getPubKey();
                //String cinfo = CustomerInfoCryptoUtil.decryptByPublicKey(params, key, publicKey);
                String cinfo = CustomerInfoCryptoUtil.decryptByPublicKey(URLEncoder.encode(params, "UTF-8"), URLEncoder.encode(key, "UTF-8"), publicKey);
                //JSONObject dataJsonObject = JSON.parseObject(cinfo);
                return cinfo;
            } catch (IllegalBlockSizeException e) {
                map.put("code", "1");
                map.put("msg", "请求参数有误");
                return JSON.toJSONString(map);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        map.put("code", "1");
        map.put("msg", "解密失败");
        return JSON.toJSONString(map);
    }

    /**
     * 加密
     *
     * @param req
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/encryption") //, method = RequestMethod.POST
    public String encryption(HttpServletRequest req) throws Exception {
        Map<String, Object> map = new HashMap<>();

        if (req.getMethod().equals("GET")) {
            map.put("code", "1");
            map.put("msg", "请通过POST方式提交");
            return JSON.toJSONString(map);
        }
        // 加密时需要提交的参数
        // 最重要的是 3 个字段：extInfo,userId,timestamp
        String extInfo = req.getParameter("extInfo");
        String userId = req.getParameter("userId");
        String uname = req.getParameter("uname");
        String browser = req.getParameter("browser");
        String network = req.getParameter("network");
        String appVersion = req.getParameter("appVersion");
        String os = req.getParameter("os");
        String device = req.getParameter("device");
        String resolution = req.getParameter("resolution");
        String timestamp = req.getParameter("timestamp");

        if (extInfo == null || userId == null || timestamp == null) {
            map.put("code", "1");
            map.put("msg", "重要参数不能为空");
            return JSON.toJSONString(map);

        } else {
            JSONObject cInfo = new JSONObject();
            JSONObject jsonObject = JSON.parseObject(extInfo);
            cInfo.put("userId", userId);
            cInfo.put("uname", uname);
            cInfo.put("timestamp", timestamp);
            cInfo.put("extInfo", jsonObject);
            if (browser != null) {
                cInfo.put("browser", browser);
            }
            if (network != null) {
                cInfo.put("network", network);
            }
            if (appVersion != null) {
                cInfo.put("appVersion", appVersion);
            }
            if (os != null) {
                cInfo.put("os", os);
            }

            if (device != null) {
                cInfo.put("device", device);
            }

            if (resolution != null) {
                cInfo.put("resolution", resolution);
            }
            return SecruityUtils.getCinfoKey(cInfo);
        }
    }
}
