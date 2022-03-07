package com.example.demo.util;


import com.example.demo.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.*;
import java.util.Collection;
import java.util.UUID;

@Slf4j
public class FileUtil {

    public static File bytes2File(byte[] bytes, String originFileName, String companyCode){
        if(bytes == null || bytes.length == 0 || Strings.isEmpty(originFileName) || Strings.isEmpty(companyCode)){
            return null;
        }
        // 获取文件后缀
        String prefix = originFileName.substring(originFileName.lastIndexOf("."));
        String fileName = companyCode + "/" + UUID.randomUUID().toString();
        // 用uuid作为文件名，防止生成的临时文件重复
        File excelFile = null;
        try {
            excelFile = File.createTempFile(fileName, prefix);
            FileOutputStream fileOutputStream = new FileOutputStream(excelFile);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            log.error("文件上传异常", e);
            throw new ServiceException(String.format("文件上传异常：%s", e.getMessage()));
        }
        return excelFile;
    }

    public static boolean checkFileSize(Long len, int size, String unit) {
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            return false;
        }
        return true;
    }

    public static byte[] file2bytes(File file){
        ByteArrayOutputStream outputStream = null;
        FileInputStream inputStream = null;
        byte[] result = null;
        try{
            try{
                outputStream = new ByteArrayOutputStream();
                inputStream = new FileInputStream(file);
                int offset = -1;
                byte[] buffer = new byte[1024];
                while((offset = inputStream.read(buffer, 0, buffer.length))>0){
                    outputStream.write(buffer, 0, offset);
                }
                result = outputStream.toByteArray();
            }finally {
                inputStream.close();
                outputStream.close();
            }
            return result;
        }catch (IOException e){
            log.error("读取文件时异常！", e);
            return null;
        }
    }

    public static void deleteFile(File... files) {
        if (files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }

    public static void deleteFile(Collection<? extends File> files) {
        if (files == null || files.size() == 0) {
            return;
        }
        for (File file : files) {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }

    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception ex) {
            log.error("删除文件【{}】异常", filePath);
        }
    }

}
