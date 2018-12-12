package cn.liuxiang.security.util;

import java.io.UnsupportedEncodingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * 字节流与16进制字符串转化工具
 *
 * @author tianfeng
 */
public class HexUtil {
    /**
     * 字节流转十六进制字符串
     *
     * @param bytes
     * @return
     */
    public static String hexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; ++i) {
            char hi = Character.forDigit(bytes[i] >> 4 & 0xF, 16);
            char lo = Character.forDigit(bytes[i] & 0xF, 16);
            sb.append(Character.toUpperCase(hi));
            sb.append(Character.toUpperCase(lo));
        }
        return sb.toString();
    }

    /**
     * 十六进制转字节流字符串
     *
     * @return
     */
    public static byte[] hex2byte(String hexString) {
        if (hexString.length() % 2 == 0) {
            return hex2byte(hexString.getBytes(), 0, hexString.length() >> 1);
        }
        return hex2byte("0" + hexString);
    }

    private static byte[] hex2byte(byte[] bytes, int offset, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length * 2; ++i) {
            int shift = (i % 2 == 1) ? 0 : 4;
            int index = (i >> 1);
            result[index] = (byte) (result[index] | Character.digit(
                    (char) bytes[(offset + i)], 16) << shift);
        }
        return result;
    }

    /**
     * 把字节数组转换成16进制字符串
     *
     * @param bArray
     * @return
     */
    public static final String bytes2HexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }



    /**
     * 16进制转字节数组
     *
     * @param src
     * @return
     */
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        if ((src.length() % 2) != 0)
            src = "0" + src;
        int bytesLen = src.length() / 2;
        //System.out.println(l);
        byte[] ret = new byte[bytesLen];
        for (int i = 0; i < bytesLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = Integer.decode(
                    "0x" + src.substring(i * 2, m) + src.substring(m, n))
                    .byteValue();
        }
        return ret;
    }

    /**
     * 从byte[]类型转换到int类型，数组的0索引对应int的高位，数组的末尾对应int的低位
     */
    public static int bytesToInt(byte[] data) {
        if (data.length == 1) {
            return data[0];
        }
        if (data.length == 2) {
            return (data[0] << 8 & 0x0000FF00) | (data[1] & 0x000000FF);
        }
        if (data.length == 4) {
            return (data[0] << 24 & 0xFF000000) | (data[1] << 16 & 0x00FF0000) | (data[2] << 8 & 0x0000FF00) | (data[3] & 0x000000FF);
        }
        throw new IllegalArgumentException("unsupported array length");
    }

    public static byte[] intToAsciiBytes(int value) {
        byte[] src = new byte[2];
        src[0] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 异或，调用处保证两个参数长度一样
     *
     * @param data1 待异或字节数组1
     * @param data2 待异或字节数组2
     * @return
     */
    public static byte[] xor(byte[] data1, byte[] data2) {
        byte[] src = new byte[16];

        for (int i = 0; i < 16; i++) {
            src[i] = (byte) (data1[i] ^ data2[i]);
        }

        return src;
    }

    /**
     * @param number
     * @return
     */
    public static byte[] stringToAsciiBytes(String number) {
        byte[] b = new byte[number.length()];
        for (int i = 0; i < number.length(); i++) {
            b[i] = (byte) number.charAt(i);
        }
        return b;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null) {
            throw new IllegalArgumentException(hexString);
        }
        hexString = hexString.trim();
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }
        if (hexString.startsWith("0x") || hexString.startsWith("0X")) {
            hexString = hexString.substring(2);
        }
        hexString = hexString.toUpperCase();
        byte[] b = new byte[hexString.length() / 2];
        for (int i = 0; i < b.length; i++) {
            char h = hexString.charAt(i * 2);
            char l = hexString.charAt(i * 2 + 1);
            b[i] = (byte) ((hexCharToInt(h) << 4) + hexCharToInt(l));
        }
        return b;

    }

    /**
     * 此函数用户将用户的访问密码，由字符串格式转换为其ascii码对应的16进制格式的表示，
     *
     * @param data   需要进行转换的数据
     * @param length 总的长度，表示的是hexString的总长度
     * @return
     */
    public static String stringFormatter(String data, int length) {
        StringBuffer assembler = new StringBuffer();
        int numOfLeftZero = length - (data.length() * 2);
        for (int i = 0; i < numOfLeftZero; i++) {
            assembler.append("0");
        }
        for (int i = 0; i < data.length(); i++) {
            assembler.append(Integer.toHexString((byte) data.charAt(i)));
        }
        String field = assembler.toString();
        return field;
    }


    /**
     * 为字符串左补0到目标长度
     *
     * @param data
     * @param length
     * @return
     */
    public static String leftAdd0Tostring(String data, int length) {
        StringBuffer assembler = new StringBuffer();
        int numOfLeftZero = length - data.length();
        for (int i = 0; i < numOfLeftZero; i++) {
            assembler.append("0");
        }
        assembler.append(data);
        return assembler.toString();
    }

    private static byte hexCharToInt(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }
        return sb.toString();
    }

    public static String convertStringToHex(String str) {
        try {
            return bytes2HexString(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * base64加密
     */

    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    /**
     * base64解密
     *
     * @param s
     * @return
     */
    public static String getFromBase64(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    public static String stringToAscii(String value) {
		StringBuffer sbu = new StringBuffer();
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			sbu.append(Integer.toHexString((int) chars[i]));
		}
		return sbu.toString();
	}
}

