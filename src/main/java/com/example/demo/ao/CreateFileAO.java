package com.example.demo.ao;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreateFileAO {

    public static final Long load_size = 1000L;

    public static final String file_prefix = "temp_";

    public static final String file_suffix = ".csv";

    /***
     * 创建本地文件
     * @param size
     * @param dataInterface
     * @param classz
     * @return
     */
    public List<File> createFile(Long size, TouchUserDataInterface dataInterface, Class<?> classz){

        List<File> result = new ArrayList<>();

        TouchUserDataResponse<?> response =
                dataInterface.load(0L, load_size, classz);

        int index = 1;
        while(response.success()){

            //创建并打开临时文件
            File currentFile = this.newFile(index++);
            OutputStream out = this.openFile(currentFile);

            //写入CSV头部
            List<String> head = this.writeHead(out, classz);
            long currentSize = size;

            while (currentSize > 0){
                Long costSize = this.writeBody(out, head, currentSize, response.getList());
                if(costSize.equals(-1L)){
                    break;
                }
                currentSize -= costSize;
                response = dataInterface.load(response.getOffset(), load_size, classz);
            }

            this.closeFile(out);
            result.add(currentFile);
        }

        return result;
    }

    /***
     * 写入CSV文件头部
     * @param out
     * @param classz
     */
    public List<String> writeHead(OutputStream out, Class<?> classz){
        List<String> headList =
                Arrays.stream(classz.getDeclaredFields())
                        .peek(field->field.setAccessible(Boolean.TRUE))
                        .map(Field::getName)
                        .collect(Collectors.toList());
        this.write(
                out,
                String.join("|||", headList)
        );
        return headList;
    }

    /***
     * 写入行
     * @param out
     * @param size
     * @param list
     * @return
     */
    public Long writeBody(OutputStream out, List<String> headList, Long size, List<?> list){

        Long listLength = (long)JSONArray.toJSONString(list).getBytes().length;
        if(CollectionUtils.isEmpty(list) || listLength > size){
            return -1L;
        }

        list.forEach(obj->{

            StringBuilder stringBuilder = new StringBuilder();

            headList.forEach(head->{
                try{
                    Field field = obj.getClass().getDeclaredField(head);
                    field.setAccessible(Boolean.TRUE);
                    stringBuilder.append(field.get(obj)).append("|||");
                }catch (Exception e){
                    return;
                }
            });

            String bodyLine = stringBuilder.toString();
            bodyLine = bodyLine.substring(0, bodyLine.length() -3);
            this.write(out, bodyLine);
        });

        return listLength;
    }

    public void write(OutputStream outputStream, String line){
        try{
            outputStream.write(line.concat("\r\n").getBytes());
            outputStream.flush();
        }catch (Exception e){
            return;
        }
    }

    /***
     * 创建临时文件
     * @param index
     * @return
     */
    private File newFile(Integer index){
        try{
            return File.createTempFile(file_prefix.concat(index.toString()), file_suffix);
        }catch (Exception e){
            return null;
        }
    }

    private OutputStream openFile(File file){
        try{
            return new FileOutputStream(file);
        }catch (Exception e){
            return null;
        }
    }

    private void closeFile(OutputStream outputStream){
        try{
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            return;
        }
    }

}
