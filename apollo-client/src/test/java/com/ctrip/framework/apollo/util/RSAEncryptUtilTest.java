package com.ctrip.framework.apollo.util;

import org.junit.Test;

/**
 * @author bbb
 * @since 2020-03-11
 */
public class RSAEncryptUtilTest {
    @Test
    public void testGetDetailMessageWithNoCause() throws Exception {
        //data明文 manniu@#2020
        String data = "ENC(iG60mB5eCGHslzcrPTAcHL+y+qW67AYyfAuzkuPIfYDQtDuas0wTog/kIrqKH5qOvQnu5sZrHsfIxGvqpzVarA==)";
        System.out.println(RSAEncryptUtil.decryptValue(data));
    }
}
