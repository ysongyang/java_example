package cn.liuxiang.security.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.TreeMap;
import com.alibaba.fastjson.JSONObject;
import sun.misc.BASE64Decoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

public class SecurityUtils {

    // private static final Logger logger = Logger.getLogger(SecurityUtils.class);

    /**
     * 公钥
     */
    private static final String PUBLIC_KEY = "";

    /**
     * 私钥
     */
    private static final String PRIVATE_KEY = "";

    /**
     * 第三方平台公钥
     */
    private static final String UNIONPAY_PUBLIC_KEY = "";


    /**
     * 私钥签名<br>
     *
     * @param data
     * @return
     */
    public static String sign(String data) {
        try {
            //传化为der格式的秘钥，加密机验证必须这样，否则验签失败
            String privateKeyDer = getDerKey(SecurityUtils.PRIVATE_KEY);
            DerInputStream derReader = new DerInputStream(hexStr2Bytes(privateKeyDer));
            DerValue[] seq = derReader.getSequence(0);
            if (seq.length < 9) {
                throw new GeneralSecurityException(
                        "Could not parse a PKCS1 private key.");
            }
            BigInteger version = seq[0].getBigInteger();
            BigInteger modulus = seq[1].getBigInteger();
            BigInteger publicExponent = seq[2].getBigInteger();
            BigInteger privateExponent = seq[3].getBigInteger();
            BigInteger prime1 = seq[4].getBigInteger();
            BigInteger prime2 = seq[5].getBigInteger();
            BigInteger exponent1 = seq[6].getBigInteger();
            BigInteger exponent2 = seq[7].getBigInteger();
            BigInteger coefficient = seq[8].getBigInteger();

            RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(modulus,
                    publicExponent, privateExponent, prime1, prime2, exponent1,
                    exponent2, coefficient);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = factory.generatePrivate(keySpec);
            // 用私钥对信息生成数字签名
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            // 签名
            byte[] result = signature.sign();
            // 签名转换为HexString形式
            String hexStringSign = bytes2HexString(result);
            return hexStringSign;
        } catch (Exception e) {
            //logger.error("加签发生异常",e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 传入pkcs8原始公钥或者私钥，返回der格式的key<br>
     *
     * @param key 秘钥做处理，如何获取der格式的key，base64然后转16进制，再去除第一个308开头的字符串<br>
     *            前置系统入库的publicKey全部都是der格式的<br>
     * @return
     */
    public static String getDerKey(String key) {
        byte[] data = com.alibaba.fastjson.util.Base64.decodeFast(key);
        String value = HexUtil.hexString(data).toLowerCase();
        String lastValue = value.substring(value.lastIndexOf("308"), value.length());
        return lastValue;
    }


    /**
     * 把字节数组转换成16进制字符串<br>
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
     * 16进制转字节数组<br>
     * @param src
     * @return
     */
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        if ((src.length() % 2) != 0)
            src = "0" + src;
        int bytesLen = src.length() / 2;
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

    /***
     * 数据验签<br>
     * @param content
     * @param sign
     * @param pubKey
     * @return
     */
    public static boolean verify(String content, String sign,
                                 RSAPublicKey pubKey) throws SignatureException {
        try {
           
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes("UTF-8"));
            boolean result = signature.verify(hexStr2Bytes(sign));
            //System.out.println("验签结果为:" + result);
            return result;
        } catch (Exception e) {
            throw new SignatureException("RSA验证签名[content = " + content
                    + "; charset = " + "; signature = " + sign + "]发生异常!", e);
        }
    }


    /**
     * 验证银联签名
     * @param publicKeyStr  银联公钥的数据库格式
     * @param signature
     * @param mapStr  入参的字符串排序后的形式   其中不应包括signature
     * @return
     */
    public static boolean verifySign(String publicKeyStr, String signature, String mapStr) {
        String publicExponent = "10001";
        String HexData = HexUtil.convertStringToHex(mapStr);
        //System.out.println(HexData);
        boolean signedSuccess = false;
        try {
            RSAPublicKey publicKey = genRSAPublicKey(publicKeyStr, publicExponent);
            Signature verifySign = Signature.getInstance("SHA256withRSA");
            verifySign.initVerify(publicKey);
            verifySign.update(HexUtil.hex2byte(HexData));
            signedSuccess = verifySign.verify(HexUtil.hex2byte(signature));
            //System.out.println("验签结果为:" + signedSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signedSuccess;
    }



    /***
     * 实例化公钥<br>
     * @param pubkey
     * @return
     */
    public static RSAPublicKey getPubKey(String pubkey) throws Exception {
        RSAPublicKey pubKey = null;
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] buffer = base64Decoder.decodeBuffer(pubkey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            pubKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (IOException e) {
            throw new Exception("公钥数据内容读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
        return pubKey;
    }

    /**
     * 验签<br>
     * @param mapStr
     * @param sign
     * @return
     * @throws Exception
     */
    public static boolean checkSign(String mapStr, String sign) throws Exception {
        return SecurityUtils.verifySign(SecurityUtils.UNIONPAY_PUBLIC_KEY,sign,mapStr);
    }


    public static RSAPublicKey genRSAPublicKey(String module,String publicExponent) throws Exception {
    
        RSAPublicKey pubKey;
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            BigInteger modulesBig = new BigInteger(module, 16);
            BigInteger exponentBig = new BigInteger(publicExponent, 16);
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulesBig,exponentBig);
            pubKey = (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
            //System.out.println(pubKey);
            return pubKey;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("生成公钥错误，无效算法！");
            throw new Exception(e.getMessage());
        } catch (InvalidKeySpecException e) {
            throw new Exception(e.getMessage());
        }
    }


    public static void convertPublicKey() throws Exception {
        String modulus = "";
        String publicExponent = "10001";
        RSAPublicKey publicKey = genRSAPublicKey(modulus, publicExponent);
        System.out.println(HexUtil.bytes2HexString(publicKey.getEncoded()));
    }


    public static void main(String[] args) throws Exception {

        
    }

}

