package com.gacha.util;

public class StringValidator {
    public static boolean isEmpty(String... strs){
        for(String str : strs){
            if(str == null || str.isBlank()){
                return true;
            }
        }

        return false;
    }
}
