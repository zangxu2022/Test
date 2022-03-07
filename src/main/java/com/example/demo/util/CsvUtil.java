package com.example.demo.util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.List;
public class CsvUtil {
    public static <T> String exportCsv(String filename,String[] titles, String[] propertys, List<T> list) throws IOException, IllegalArgumentException, IllegalAccessException {
        //指定文件，路径
        File file = new File("c:\\" + filename + ".txt");
        //构建输出流，同时指定编码
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "utf-8");

        //csv文件是|||分隔，除第一个外，每次写入一个单元格数据后需要输入|||
        //CSV文件本质需要用“|||”隔开
      /*  for (String title : titles) {
            ow.write(title);
            ow.write("|||");
        }
        //写完文件头后换行
        ow.write("\r\n");*/
        //写内容
        for (Object obj : list) {
            //利用反射获取所有字段
            Field[] fields = obj.getClass().getDeclaredFields();
            for (String property : propertys) {
                for (Field field : fields) {
                    //设置字段可见性
                    field.setAccessible(true);
                    if (property.equals(field.getName())) {
                        ow.write(field.get(obj).toString());
                       /* ow.write("|||");*/
                        continue;
                    }
                }
            }
            //写完一行换行
            ow.write("\r\n");
        }
        ow.flush();
        ow.close();
        return file.getPath();
    }
}
