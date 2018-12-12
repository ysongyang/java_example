package cn.liuxiang.security.util;


import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;


public class BeanUtil {
    //private static Logger logger = Logger.getLogger(BeanUtil.class);

    /**
     * Map转K-V字符串
     * 为null的字段不转,key为"class"的不转
     *
     * @param dataMap
     * @return
     */
    public static String map2kvStr(Map<String, String> dataMap) {
        if (dataMap == null) {
            throw new IllegalArgumentException();
        }
        StringBuffer sb = new StringBuffer();
        for (String key : dataMap.keySet()) {
            String k = dataMap.get(key);
            if (!"class".equalsIgnoreCase(key) && (k != null && k != "")) {
                sb.append(key).append("=").append(dataMap.get(key)).append("&");
            }
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }


    /**
     * K-V字符串转Map
     *
     * @param kvStr
     * @return
     */
    public static Map<String, String> kvStr2Map(String kvStr) {
        Map<String, String> dataMap = new TreeMap<String, String>();
        if (kvStr == null || "".equals(kvStr)) {
            throw new IllegalArgumentException();
        }
        String[] kvStrArray = kvStr.split("&");
        for (String kvStrTemp : kvStrArray) {
            String[] temp = kvStrTemp.split("=");
            dataMap.put(temp[0], temp[1]);
        }
        return dataMap;
    }


    /**
     * UUID生成指定位数的订单ID，不足长度补0处理
     *
     * @param num 位数
     * @return
     */
    public static String getOrderIdByUUId(int num) {
        String ipAddress = "";
        try {
            //获取服务器IP地址
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //获取UUID
        String uuid = ipAddress + "$" + UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        //生成后缀
        long suffix = Math.abs(uuid.hashCode() % 100000000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String time = sdf.format(new Date());
        String userId = String.valueOf(time + suffix);
        int strLen = userId.length();
        if (strLen < num) {
            while (strLen < num) {
                StringBuffer sb = new StringBuffer();
                //左边补0
                //sb.append("0").append(userId);
                //右边补0
                sb.append(userId).append("0");
                userId = sb.toString();
                strLen = userId.length();
            }
        }
        return userId;
    }
}
