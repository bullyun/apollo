package com.ctrip.framework.apollo.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAEncryptUtil {

    private static final Logger logger = LoggerFactory.getLogger(RSAEncryptUtil.class);

    private static final String ENCRYPTED_VALUE_PREFIX = "ENC(";
    private static final String ENCRYPTED_VALUE_SUFFIX = ")";

    /**
     * 生成密钥对
     * @return map
     */
    public static Map<String, String> genKeyPair() {
        Map<String, String> keyMap = new HashMap<String, String>();
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(512, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        keyMap.put("publicKey", new String(Base64.encodeBase64(publicKey.getEncoded())));
        keyMap.put("privateKey", new String(Base64.encodeBase64((privateKey.getEncoded()))));
        return keyMap;
    }

    /**
     * 判断值是否需要解密
     * @param value
     *        加密字符串
     * @return
     *        是否需要解密
     */
    public static boolean isEncryptedValue(String value) {
        if (value == null) {
            return false;
        }
        final String trimmedValue = value.trim();
        return (trimmedValue.startsWith(ENCRYPTED_VALUE_PREFIX) &&
                trimmedValue.endsWith(ENCRYPTED_VALUE_SUFFIX));
    }

    /**
     * RSA私钥解密
     *
     * @param value
     *         加密字符串
     * @param privateKey
     *         私钥
     * @return 铭文
     * @throws Exception
     *         解密过程中的异常信息
     */
    public static String decrypt(String value, String privateKey) {
        try {
            byte[] inputByte = Base64.decodeBase64(getInnerEncryptedValue(value).getBytes("UTF-8"));
            byte[] decoded = Base64.decodeBase64(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return new String(cipher.doFinal(inputByte));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * RSA公钥加密
     *
     * @param str
     *        加密字符串
     * @param publicKey
     *        公钥
     * @return 密文
     * @throws Exception
     * 加密过程中的异常信息
     */
    public static String encrypt( String str, String publicKey ) {
        try {
            //base64编码的公钥
            byte[] decoded = Base64.decodeBase64(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
            return String.format("ENC(%s)", outStr);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取prikey
     * @return
     */
    public static String getPriKeyString() {
        File file = new File("/secret/private.key");
        if (!file.exists()) {
            logger.info("private.key is not exists");
            return null;
        }
        try {
            FileReader reader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String s = "";
            while ((s =bReader.readLine()) != null) {
                sb.append(s + "\n");
            }
            bReader.close();
            String str = sb.toString();
            return str.replaceAll("\u0000","")
                    .replaceAll("-----BEGIN PRIVATE KEY-----","")
                    .replaceAll("-----END PRIVATE KEY-----","");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(getPriKeyString());
    }

    private static String getInnerEncryptedValue(final String value) {
        return value.substring(ENCRYPTED_VALUE_PREFIX.length(), (value.length() - ENCRYPTED_VALUE_SUFFIX.length()));
    }
}

