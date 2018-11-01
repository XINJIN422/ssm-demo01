package com.yangkang.ssmdemo01.tools.encryption;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * RSAEncryptUtil
 *
 * @author yangkang
 * @date 2018/10/26
 */
public class RSAEncryptUtil {

    private Logger logger = LoggerFactory.getLogger(RSAEncryptUtil.class);

    private final static String algorithm = "RSA";

    private final static PrivateKey serverPrivateKey;

    private final static PublicKey serverPublicKey;

    /**
     * 初始化服务器端的公私钥,随机生成,每次服务器启动后都不一样
     */
    static {
        KeyPairGenerator keyGen = null;
        KeyPair keyPair = null;
        try {
            keyGen = KeyPairGenerator.getInstance(algorithm);
            keyGen.initialize(1024);
            keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
//        privateKey = keyPair.getPrivate().getEncoded();
//        publicKey = keyPair.getPublic().getEncoded();
        serverPrivateKey = keyPair.getPrivate();
        serverPublicKey = keyPair.getPublic();
    }

    /**
     * 加密方法,入参出参为字节数组
     * @param data
     * @param clientPublicKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] encrypt(byte[] data, PublicKey clientPublicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(cipher.ENCRYPT_MODE, clientPublicKey);
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
        cipher.init(Cipher.DECRYPT_MODE, serverPrivateKey);
        return cipher.doFinal(data);
    }

    /**
     * 加密方法,入参出参为字符串,其中出参是加密后的字节数组Base64编码成的字符串
     * @param data
     * @param clientPublicKey
     * @return
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static String encrypt(String data, PublicKey clientPublicKey) throws UnsupportedEncodingException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] byteMing = data.getBytes("UTF8");
        byte[] byteMi = encrypt(byteMing, clientPublicKey);
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
    public static PublicKey getServerPublicKey(){
        return serverPublicKey;
    }

    /**
     * 获取服务器端Base64编码的公钥字符串
     * @return
     */
    public static String getServerPublicKeyStr(){
        return Base64.getEncoder().encodeToString(serverPublicKey.getEncoded());
    }

    /**
     * 根据客户端传来的Base64编码的公钥字符串获取客户端公钥
     * @param clientPublicKeyStr
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getClientPublicKeyFromStr(String clientPublicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(clientPublicKeyStr)));
    }

    /**
     * 根据客户端传来的Base64编码的私钥字符串获取客户端私钥(这个方法用不到)
     * @param clientPrivateKeyStr
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static PrivateKey getClientPrivateKeyFromStr(String clientPrivateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(clientPrivateKeyStr)));
    }

    /**
     * 签名
     * @param data
     * @param clientPrivateKeyStr
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws SignatureException
     */
    public static String sign(String data, String clientPrivateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initSign(getClientPrivateKeyFromStr(clientPrivateKeyStr));
        signature.update(data.getBytes("UTF8"));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 验签
     * @param data
     * @param signStr
     * @param clientPublicKeyStr
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws SignatureException
     */
    public static boolean checkSign(String data, String signStr, String clientPublicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initVerify(getClientPublicKeyFromStr(clientPublicKeyStr));
        signature.update(data.getBytes("UTF8"));
        return signature.verify(Base64.getDecoder().decode(signStr));
    }

    public static void main(String[] args) throws UnsupportedEncodingException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeySpecException, SignatureException {
//        System.out.println(new String(serverPrivateKey, "UTF8"));
//        System.out.println(Base64.getEncoder().encodeToString(serverPrivateKey));

//        System.out.println(decrypt(encrypt("hello", serverPublicKey)));


//        //单独用RSA算法加解密
//        //加密
//        String originalStr = "hello";
//        String encryptedStr = encrypt(originalStr, getClientPublicKeyFromStr(getServerPublicKeyStr()));
//        //解密
//        String decryptStr = decrypt(encryptedStr);
//        System.out.println(decryptStr);


        //混合使用AES和RSA算法加解密;因为AES加密快,RSA加密慢,所以用AES对要传输的数据加密,RSA对随机的AES密钥加密;
        //先生成客户端私钥公钥
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
        keyGen.initialize(1024);
        KeyPair keyPair = keyGen.generateKeyPair();
        String clientPublicKeyStr = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String clientPrivateKeyStr = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        //签名
        TreeMap<String, String> originalParam = new TreeMap<>();    //有序键值对
        originalParam.put("param1","hello1");
        originalParam.put("param2","hello2");
//        long millisStart = new Date().getTime();
        String signStr = sign(JSON.toJSONString(originalParam), clientPrivateKeyStr);
//        System.out.println("签名耗时:" + (new Date().getTime() - millisStart) + "ms");
        originalParam.put("sign", signStr);
        String originalStr = JSON.toJSONString(originalParam);
        //AES算法对数据加密
//        long millisStart2 = new Date().getTime();
        String encryptedData = AESEncryptUtil.encrypt(originalStr);
//        long millisStart3 = new Date().getTime() - millisStart2;
//        System.out.println("AES算法对数据加密耗时:" + millisStart3 + "ms");
        //RSA算法对AES公钥加密
        String encryptedAesKey = RSAEncryptUtil.encrypt(AESEncryptUtil.getPublicSecretKeyStr(), RSAEncryptUtil.getClientPublicKeyFromStr(RSAEncryptUtil.getServerPublicKeyStr()));
//        long millisStart4 = (new Date().getTime() - millisStart2 - millisStart3);
//        System.out.println("RSA算法对AES公钥加密耗时:" + millisStart4 + "ms");
        //真正传输的键值对
        HashMap<String, String> transParam = new HashMap<>();
        transParam.put("data", encryptedData);
        transParam.put("key", encryptedAesKey);

        //RSA算法对AES公钥解密
//        long millisStart5 = new Date().getTime();
        String aesKey = RSAEncryptUtil.decrypt(transParam.get("key"));
//        long millisStart6 = (new Date().getTime() - millisStart5);
//        System.out.println("RSA算法对AES公钥解密耗时:" + millisStart6 + "ms");
        //AES算法解密数据
        TreeMap<String, String> decryptedParam = JSON.parseObject(AESEncryptUtil.decrypt(transParam.get("data")), new TypeReference<TreeMap<String, String>>() {});
//        long millisStart7 = (new Date().getTime() - millisStart5 - millisStart6);
//        System.out.println("AES算法解密数据耗时:" + millisStart7 + "ms");
        //验签
        String decryptedSignStr = decryptedParam.get("sign");
        decryptedParam.remove("sign");
//        long millisStart8 = new Date().getTime();
        boolean checkSign = checkSign(JSON.toJSONString(decryptedParam), decryptedSignStr, clientPublicKeyStr);
//        long millisStart9 = (new Date().getTime() - millisStart8);
//        System.out.println("验签耗时:" + millisStart9 + "ms");
        if (checkSign){
            //验证通过则解析数据
            for (String paramKey : decryptedParam.keySet())
                System.out.println(paramKey + " : " + decryptedParam.get(paramKey));
        }
    }
}
