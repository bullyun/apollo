package com.ctrip.framework.apollo.util;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class RSAEncryptUtil {

    private static final String ENCRYPTED_VALUE_PREFIX = "ENC(";
    private static final String ENCRYPTED_VALUE_SUFFIX = ")";
    private static String PRIVATE_KEY = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAwszz1lBhRxnoHB61GRpmKp5agZCwQoixxzbaih2RoyEDpoZiCmAGd8Ay"
            + "ILI1QXfbkHQHvdth8BYR230MUjGJTQIDAQABAkAxu9nVMZhkarzT0RMzYYYMA3nf8mzNz9BzqBGLiZkRKH5SHGV1EjhvKWA1T9gpp+vBCKGFUXTMo5Sr/VOLCfoBAiEA76Rk"
            + "aW65wU6bNbhyiLkAfitT6FoziP6twxRnsctkpCECIQDQGPre8VWlh5wXS0ze4LcsyZwyg9zQdx+0ULEdcXq/rQIgaeQrVodN34Q7g0Zonc+ZzyaYIiDRiuR2pa/7jg3A/+EC"
            + "ICr6Lb2je/u+wRbyf0K8iDggvziTkSQgphSYYaviBubVAiEAgR1YqUQcnzGRr77LZ9+QXokfovP0aXZ9zeWR/V+w4Pg=";

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

    private static String getInnerEncryptedValue(final String value) {
        return value.substring(ENCRYPTED_VALUE_PREFIX.length(), (value.length() - ENCRYPTED_VALUE_SUFFIX.length()));
    }
}

