package com.yangkang.ssmdemo01.tools.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * SHAEncryptUtil
 *
 * @author yangkang
 * @date 2018/10/31
 */
public class SHAEncryptUtil {

    private final static String algorithm = "SHA-1";   //SHA-256 (加密速度:MD5>SHA1>SHA256;加密后长度(即碰撞难度)MD5<SHA1<SHA256)

    public static String encode(String data) throws NoSuchAlgorithmException {
        MessageDigest SHA = MessageDigest.getInstance(algorithm);
        return Base64.getEncoder().encodeToString(SHA.digest(data.getBytes()));
    }
}
