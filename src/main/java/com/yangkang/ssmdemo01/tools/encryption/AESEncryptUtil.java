package com.yangkang.ssmdemo01.tools.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * AESEncryptUtil
 *
 * @author yangkang
 * @date 2018/10/26
 */
public class AESEncryptUtil {
    private Logger logger = LoggerFactory.getLogger(AESEncryptUtil.class);

    private final static String algorithm = "AES";      //"AES/ECB/PKCS5Padding"->"算法/模式/补码方式"(默认)

    private final static SecretKey publicSecretKey;

    static {
        //第一种,随机生成,前台第一次来取后存在全局变量里
//        KeyGenerator keyGen = null;
//        try {
//            keyGen = KeyGenerator.getInstance(algorithm);
//            keyGen.init(128);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        publicSecretKey = keyGen.generateKey();

        //第二种,按照约定的密码生成,前台第一次来取后存在全局变量里
//        String password = "mypassword";
//        byte[] key = null;
//        try {
//            MessageDigest sha = MessageDigest.getInstance("SHA-1");
//            key = Arrays.copyOf(sha.digest(password.getBytes("UTF-8")), 16);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e2) {
//            e2.printStackTrace();
//        }
//        publicSecretKey = new SecretKeySpec(key, algorithm);

        //第三种,写死在后台与前台代码里
        publicSecretKey = getPublicSecretKeyFromStr("GXyq3KiXdoulN/JJx7dDOQ==");
    }

    /**
     * 加密方法,入参出参为字节数组
     * @param data
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] encrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, publicSecretKey);
        return cipher.doFinal(data);
    }

    /**
     * 解密方法,入参出参为字节数组
     * @param data
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, publicSecretKey);
        return cipher.doFinal(data);
    }

    /**
     * 加密方法,入参出参为字符串,其中出参是加密后的字节数组Base64编码成的字符串
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static String encrypt(String data) throws UnsupportedEncodingException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] byteMing = data.getBytes("UTF8");
        byte[] byteMi = encrypt(byteMing);
        return Base64.getEncoder().encodeToString(byteMi);
    }

    /**
     * 解密方法,入参出参为字符串,其中入参是加密后的字节数组Base64编码成的字符串
     * @param data
     * @return
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws UnsupportedEncodingException
     */
    public static String decrypt(String data) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        byte[] byteMi = Base64.getDecoder().decode(data);
        byte[] byteMing = decrypt(byteMi);
        return new String(byteMing, "UTF8");
    }

    /**
     * 获取服务器端的公钥
     * @return
     */
    public static SecretKey getPublicSecretKey(){
        return publicSecretKey;
    }

    /**
     * 获取服务器端Base64编码的公钥字符串
     * @return
     */
    public static String getPublicSecretKeyStr(){
        return Base64.getEncoder().encodeToString(publicSecretKey.getEncoded());
    }

    /**
     * 根据Base64编码的公钥字符串获取公钥(这个方法用不到)
     * @param publicSecretKeyStr
     * @return
     */
    public static SecretKey getPublicSecretKeyFromStr(String publicSecretKeyStr){
        return new SecretKeySpec(Base64.getDecoder().decode(publicSecretKeyStr), algorithm);
    }

    public static void main(String[] args) throws BadPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        //单独用AES算法加解密
        //加密
        String originalStr = "hello";
        String encryptedStr = encrypt(originalStr);
        //解密
        String decryptedStr = decrypt(encryptedStr);
        System.out.println(decryptedStr);
    }
}
