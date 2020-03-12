package com.ctrip.framework.apollo.common.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAEncryptUtil {

    private static final String ENCRYPTED_VALUE_PREFIX = "ENC(";
    private static final String ENCRYPTED_VALUE_SUFFIX = ")";
    private static String PRIVATE_KEY = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAwszz1lBhRxnoHB61GRpmKp5agZCwQoixxzbaih2RoyEDpoZiCmAGd8Ay"
            + "ILI1QXfbkHQHvdth8BYR230MUjGJTQIDAQABAkAxu9nVMZhkarzT0RMzYYYMA3nf8mzNz9BzqBGLiZkRKH5SHGV1EjhvKWA1T9gpp+vBCKGFUXTMo5Sr/VOLCfoBAiEA76Rk"
            + "aW65wU6bNbhyiLkAfitT6FoziP6twxRnsctkpCECIQDQGPre8VWlh5wXS0ze4LcsyZwyg9zQdx+0ULEdcXq/rQIgaeQrVodN34Q7g0Zonc+ZzyaYIiDRiuR2pa/7jg3A/+EC"
            + "ICr6Lb2je/u+wRbyf0K8iDggvziTkSQgphSYYaviBubVAiEAgR1YqUQcnzGRr77LZ9+QXokfovP0aXZ9zeWR/V+w4Pg=";

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
        keyPairGen.initialize(512,new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        keyMap.put("publicKey",publicKeyString);
        keyMap.put("privateKey",privateKeyString);
        return keyMap;
    }

    public static String decryptValue(String value) {
        return decrypt(value,PRIVATE_KEY);
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

    private static String getInnerEncryptedValue(final String value) {
        return value.substring(ENCRYPTED_VALUE_PREFIX.length(), (value.length() - ENCRYPTED_VALUE_SUFFIX.length()));
    }
}

