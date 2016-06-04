package com.egeye.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Octavio on 2016/2/2.
 */
public class MD5Tool {


    /**
     * md5加密方法
     * @param password
     * @return
     */
    public static String md5password(String password) {
        try {
            //得到一个信息摘要器
            MessageDigest md = MessageDigest.getInstance("md5");

            byte[] result = md.digest(password.getBytes());

            StringBuffer sbuffer = new StringBuffer();

            for (byte b : result) {
                //与运算
                int number = b & 0xff;
                String str = Integer.toHexString(number);

                if (str.length() == 1) {
                    sbuffer.append("0");
                }
                sbuffer.append(str);

            }

            //将标识的md5加密后的结果返回
            return sbuffer.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }


    }

}
