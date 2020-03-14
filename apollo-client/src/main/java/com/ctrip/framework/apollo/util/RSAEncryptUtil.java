package com.ctrip.framework.apollo.util;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class RSAEncryptUtil {

    private static final String ENCRYPTED_VALUE_PREFIX = "ENC(";
    private static final String ENCRYPTED_VALUE_SUFFIX = ")";

    public static String decryptValue(String value) {
        return decrypt(value, getPriKeyString());
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
        if (privateKey == null) {
            return null;
        }
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

    private static String getPriKeyString() {
        File file = new File("/secret/private.key");
        if (!file.exists()) {
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

    private static String getInnerEncryptedValue(final String value) {
        return value.substring(ENCRYPTED_VALUE_PREFIX.length(), (value.length() - ENCRYPTED_VALUE_SUFFIX.length()));
    }
}

