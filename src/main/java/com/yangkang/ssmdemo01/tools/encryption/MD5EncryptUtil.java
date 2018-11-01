package com.yangkang.ssmdemo01.tools.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * MD5EncryptUtil
 *
 * @author yangkang
 * @date 2018/10/31
 */
public class MD5EncryptUtil {

    public static String encode(String data) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.getEncoder().encodeToString(md5.digest(data.getBytes()));
    }
}
