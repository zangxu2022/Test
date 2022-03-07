package com.example.demo.ao;

import com.example.demo.common.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TouchUserDataAO implements TouchUserDataInterface{

    public static final String local_file_path = "C:\\touchUser1.txt";

    @Override
    public Boolean alread() {
        File file = new File(local_file_path);
        return file.exists() && file.isFile();
    }

    @Override
    public <T> TouchUserDataResponse<T> load(Long offset, Long size, Class<T> classz) {

        TouchUserDataResponse<T> response =
                TouchUserDataResponse.<T>builder()
                        .list(this.loadData(offset, size, classz))
                        .total(this.totalRows())
                        .size(size)
                        .build();
        response.setOffset(offset + response.getList().size());

        return response;
    }

    private Long totalRows(){
        long result = 0L;

        try{
            File file = new File(local_file_path);
            LineNumberReader reader = new LineNumberReader(new FileReader(file));
            reader.skip(file.length());
            result = (long)reader.getLineNumber();
            reader.close();
            //result = Files.lines(Paths.get(local_file_path)).count();
        }catch (IOException e){
            log.error(String.format("指定的文件【%s】不存在或无法打开！", local_file_path), e);
        }
        return result;
    }

    private <T> List<T> loadData(Long offset, Long size, Class<T> classz){
        List<T> result = new ArrayList<>();
        File file = new File(local_file_path);
        try{
            BufferedReader buffer = new BufferedReader(new FileReader(file));
            Long currentOffset = 0L;

            //空读，跳过已经读取的行
            while(currentOffset < offset){
                buffer.readLine();
                currentOffset++;
            }

            for(int i = 0; i < size; i++){
                String currentLine = buffer.readLine();
                if(Strings.isEmpty(currentLine)){
                    break;
                }
                result.add(this.str2obj(currentLine, classz));
            }
            buffer.close();
        }catch (IOException e){
            log.error(String.format("指定的文件【%s】不存在或无法打开！", local_file_path), e);
        }
        return result;
    }

    private <T> T str2obj(String str, Class<T> classz){
        /*if(classz.getName().equals(String.class.getName())){
            return (T)str;
        }*/

        String[] strList = str.split("\\|\\|\\|");

        try {
            Field[] fields = classz.getDeclaredFields();
            Map<String, Integer> indexMap = this.getIndexMap();
            T newRowObj = classz.newInstance();
            for(Field field : fields){
                field.setAccessible(Boolean.TRUE);
                field.set(newRowObj, strList[indexMap.get(field.getName())]);
            }
            return newRowObj;
        } catch (Exception e) {
            log.error("error", e);
        }

        return null;
    }

    private Map<String, Integer> getIndexMap(){
        Map<String, Integer> map = new HashMap<>();
        map.put("userId", 0);
        return map;
    }

}
