package com.example.demo.common;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Strings {

    private static final Pattern NUM = Pattern.compile("^[-\\\\+]?([0-9]+\\\\.?)?[0-9]+$");

    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(StringUtils.trimWhitespace(str));
    }

    public static boolean isNum(String str){
        if(isEmpty(str)){
            return false;
        }
        Matcher matcher = NUM.matcher(str);
        return matcher.matches();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String trim(String description) {
        if (isEmpty(description)) {
            return description;
        }
        return description.trim();
    }

    /**
     * str脱敏
     *
     * @param item   脱敏符号，入 *
     * @param str    需要脱敏的字符串
     * @param before 前面保留位数
     * @param after  后面保留位数
     * @return 脱敏后str
     */
    public static String middleHidden(String item, String str, int before, int after) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        int len = str.length();
        if (len < before + after || before < 0 || after < 0) {
            return str;
        }
        String bef = str.substring(0, before);
        String aft = str.substring(len - after);
        return bef + generate(item, len - before - after) + aft;
    }

    private static String generate(String item, int times) {
        return Stream.iterate(item, i -> i).limit(times).reduce((v1, v2) -> v1 + v2).orElse("");
    }

    /**
     * 返回匹配条件的值
     * @param type 条件
     * @param arg 前者为值，紧着后者为值对应的返回值
     * @return
     */
    public static String decode(String type,String ... arg){
        for (int i=0;i<arg.length;i++){
            if(type.equals(arg[i])){
                return arg[i+1];
            }
        }
        return arg[arg.length-1];
    }

}
