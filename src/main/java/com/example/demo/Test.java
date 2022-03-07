package com.example.demo;

import com.example.demo.module.TouchUser;
import com.example.demo.util.CsvUtil;
import com.example.demo.util.HttpUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Test {
    public static void main(String args[]){
        System.setProperty("http.proxySet", "true");

        System.setProperty("http.proxyHost", "127.0.0.1");

        System.setProperty("http.proxyPort", "8888");

        Map<String,String> params= new HashMap<>();
        params.put("orderId","123");
        params.put("empNo","01332840");

        Map<String,String> header = new HashMap<>();
        header.put("sign","..................");
        header.put("appId","hxc");

        File file = new File("C:\\Users\\Administrator\\Desktop\\test\\temp_12929439477037291445.csv");
        try {
            HttpUtil.postFormDataFile("http://localhost.:8080/api/open/touch/user/testFile",params,header,new FileInputStream(file),null,"files",file.getName(),6000000);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
