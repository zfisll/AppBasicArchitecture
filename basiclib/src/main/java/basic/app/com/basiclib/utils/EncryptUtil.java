package basic.app.com.basiclib.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import basic.app.com.basiclib.utils.logger.LogUtil;

/**
 * author : user_zf
 * date : 2018/8/29
 * desc : 加密工具，MD5，SHA1，SHA256
 */
public class EncryptUtil {
    /**
     * @param strSrc
     * @return
     * @author
     * @Title getEncrypt
     * @Description: SHA256位加密
     * @return: String
     * @throws:
     */
    public static String getSHA256Encrypt(String strSrc) {
        MessageDigest md;
        String strDes;

        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(bt);
            strDes = bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(e, e.getMessage());
            return null;
        }
        return strDes;
    }

    /**
     * @param strSrc
     * @return
     * @author
     * @Title getEncrypt
     * @Description: SHA1位加密
     * @return: String
     * @throws:
     */
    public static String getSHA1Encrypt(String strSrc) {
        MessageDigest md = null;
        String strDes = null;

        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(bt);
            strDes = bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(e, e.getMessage());
            return null;
        }
        return strDes;
    }

    /**
     * @param strSrc
     * @return
     * @author
     * @Title getMD5Encrypt
     * @Description: MD5加密
     * @return: String
     * @throws:
     */
    public static String getMD5Encrypt(String strSrc) {
        MessageDigest md = null;
        String strDes = null;

        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(bt);
            strDes = bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(e, e.getMessage());
            return null;
        }
        return strDes;
    }

    /**
     * MD5算法32位小写;
     * <hr/>
     * 16位小写加密只需getMd5Value("xxx").substring(8, 24);即可
     *
     * @param sSecret
     * @return
     */
    public static String getMd5Value(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(e, e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param bts
     * @return
     * @author
     * @Title bytes2Hex
     * @Description:
     * @return: String
     * @throws:
     */
    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    /**
     * 小写字母转大写
     *
     * @param str
     */
    public static String toUpperCase(String str) {
        StringBuffer sb = new StringBuffer("");
        byte[] bt = str.getBytes();
        int dist = 'A' - 'a';
        for (int i = 0; i < bt.length; i++) {
            if (bt[i] >= 'a' && bt[i] <= 'z') {
                bt[i] += dist;
            }
            sb.append((char) bt[i]);
        }
        return sb.toString();
    }

}
