package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;

import com.example.demo.ao.CreateFileAO;
import com.example.demo.ao.TouchUserDataInterface;
import com.example.demo.module.UserInfo;
import com.example.demo.util.FileUtil;
import com.example.demo.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class TouchUserDataService {

    public static final Long timeout = 60L * 1000 * 10;

    public static final Long sub_file_size = 1L * 1024 * 1024;

    public static final String base_api_url = "";

    @Autowired
    private TouchUserDataInterface dataInterface;

    @Autowired
    private CreateFileAO createFileAO;

    /***
     * 开始触达任务
     * @return
     */
    public String start() {
        String flowNo = UUID.randomUUID().toString();

        CompletableFuture.runAsync(()->{

            if(!dataInterface.alread()){
                return;
            }

            List<File> localFiles =
                    createFileAO.createFile(sub_file_size, dataInterface, UserInfo.class);
            System.out.println(localFiles);
            //FileUtil.deleteFile(localFiles);
           /* try{
                this.uploadResult(localFiles);
            }catch (Exception e){

            }*/
        });

        return flowNo;
    }

    private void uploadResult(List<File> localFiles){
        localFiles.forEach(localFile->{
            Map<String, String> param = new HashMap<>();
            Map<String, String> head = new HashMap<>();
            String contentType = "";

            try{
                String response =
                        HttpUtil.postFormDataFile(
                                base_api_url,
                                param,
                                head,
                                new FileInputStream(localFile),
                                contentType,
                                "uploadFile",
                                localFile.getName(),
                                timeout.intValue()
                        );
                Object responseObj = JSONObject.parseObject(response, Object.class);

            }catch (FileNotFoundException fileNotFoundException){
            }catch (Exception e){
                throw e;
            }
        });
    }

    /***
     * 查询指定触达任务是否执行成功
     * @param flowNo
     * @return
     */
    public Boolean queryTask(String flowNo) {
        return null;
    }

}
