package com.example.demo.common;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListSubHelper {

    public static void main(String args[]){
        List<String> names = new ArrayList<>();
        for(int i = 0; i <= 1000; i++){
            StringBuilder stringBuilder = new StringBuilder();
            for(int j = 0; j <= 3000; j++){
                stringBuilder.append(UUID.randomUUID().toString());
            }
            names.add(stringBuilder.toString());
        }

        //按100兆分割，理论上会分为971个1组、30个一组
        List<List<String>> namesList =
                ListSubHelper.subList(100L * 1024 * 1024, names);

        System.out.println(namesList);
    }

    /***
     * 将list按pageSize分割成若干list，pageSize的单位为字节
     * @param pageSize
     * @param list
     * @param <E>
     * @return
     */
    public static <E> List<List<E>> subList(Long pageSize, List<E> list){
        if(pageSize <= 0 || list == null || list.size() == 0){
            return null;
        }

        List<List<E>> result = new ArrayList<>();

        //low copy
        List<E> copyList = new ArrayList<>(list);

        while(copyList.size() > 0){
            result.add(subListBySize(pageSize, copyList));
        }

        return result;
    }

    private static <E> List<E> subListBySize(Long size, List<E> list){
        if(list == null || list.size() == 0){
            return null;
        }
        List<E> newList = new ArrayList<>();
        while(size > 0 && !list.isEmpty()){
            E obj = list.get(0);
            Long eSize = calcObjectSize(obj);
            size -= eSize;
            newList.add(obj);
            list.remove(0);
        }
        return newList;
    }

    private static Long calcObjectSize(Object obj){
        return (long) JSONObject.toJSONString(obj).getBytes().length;
    }
}
