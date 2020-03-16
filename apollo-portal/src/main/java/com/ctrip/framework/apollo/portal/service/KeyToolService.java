package com.ctrip.framework.apollo.portal.service;

import com.ctrip.framework.apollo.common.utils.RSAEncryptUtil;
import org.springframework.stereotype.Service;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author bbb
 * @since 2020-03-13
 */
@Service
public class KeyToolService {

    public void createKeys(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> keyPair = RSAEncryptUtil.genKeyPair();
        File prifile = createFile(getKeyStr(keyPair, KeyEnum.PRIVATE_KEY.type), KeyEnum.PRIVATE_KEY.type);
        File pubfile = createFile(getKeyStr(keyPair, KeyEnum.PUBLIC_KEY.type), KeyEnum.PUBLIC_KEY.type);
        String[] files = new String[] {prifile.getAbsolutePath(), pubfile.getAbsolutePath()};
        down(files, request, response);
    }

    /**
     * 字符串美化
     * @param keyPair
     * @return
     */
    private String getKeyStr(Map<String, String> keyPair, int type) {
        if (type == KeyEnum.PRIVATE_KEY.type) {
            StringBuffer priKeyBuffer = new StringBuffer("-----BEGIN PRIVATE KEY-----");
            priKeyBuffer.append("\n").append(keyPair.get("privateKey")).append("\n").append("-----END PRIVATE KEY-----");
            return priKeyBuffer.toString();
        }
        StringBuffer pubKeyBuffer = new StringBuffer("-----BEGIN PUBLIC KEY-----");
        pubKeyBuffer.append("\n").append(keyPair.get("publicKey")).append("\n").append("-----END PUBLIC KEY-----");
        return pubKeyBuffer.toString();
    }

    /**
     * 创建密钥文件
     * @param KeyStr 密钥字符串
     * @param type 1：公钥 2：私钥
     * @return File
     */
    private File createFile(String KeyStr, int type) {
        String fileName = type == KeyEnum.PRIVATE_KEY.type ? "private.key" : "public.key";
        File txt = new File(fileName);
        try {
            if(!txt.exists()){
                txt.createNewFile();
            }
            byte bytes[] = new byte[2048];
            bytes = KeyStr.getBytes();
            int b= bytes.length;
            FileOutputStream fos = new FileOutputStream(txt);
            fos.write(bytes,0,b);
            fos.close();
            return txt;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param files key文件路径
     * @param request
     * @param response
     * @throws Exception
     */
    public void down(String[] files, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String filePath = "key.zip";
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
            ZipOutputStream zos = new ZipOutputStream(bos);
            ZipEntry ze = null;
            for (int i = 0; i < files.length; i++) {
                BufferedInputStream bis = new BufferedInputStream(
                        new FileInputStream(files[i]));
                ze = new ZipEntry(
                        files[i].substring(files[i].lastIndexOf(File.separator)));
                zos.putNextEntry(ze);
                int s = -1;
                while ((s = bis.read()) != -1) {
                    zos.write(s);
                }
                bis.close();
            }
            zos.flush();
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String agent = request.getHeader("User-Agent").toUpperCase();
        // 判断浏览器代理并分别设置响应给浏览器的编码格式
        String finalFileName = null;
        if ((agent.indexOf("MSIE") > 0)
                || ((agent.indexOf("RV") != -1) && (agent.indexOf("FIREFOX") == -1))) {
            finalFileName = URLEncoder.encode("key.zip", "UTF-8");
        } else {
            finalFileName = new String("key.zip".getBytes("UTF-8"), "ISO8859-1");
        }
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment;filename=\""
                + finalFileName + "\"");
        ServletOutputStream servletOutputStream = response.getOutputStream();
        DataOutputStream temps = new DataOutputStream(servletOutputStream);

        DataInputStream in = new DataInputStream(new FileInputStream(filePath));
        byte[] b = new byte[2048];
        File reportZip = new File(filePath);
        try {
            while ((in.read(b)) != -1) {
                temps.write(b);
            }
            temps.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (temps != null) {
                temps.close();
            }
            if (in != null) {
                in.close();
            }
            if (reportZip != null) {
                reportZip.delete();
            }
            if (files != null && files.length > 0) {
                for (String path : files) {
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
            servletOutputStream.close();
        }
    }


    public enum KeyEnum {
        PUBLIC_KEY(1, "publicKey"),
        PRIVATE_KEY(2, "privateKey");

        int type;
        String name;

        KeyEnum(int type, String name) {
            this.type = type;
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
