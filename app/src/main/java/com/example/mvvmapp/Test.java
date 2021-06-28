package com.example.mvvmapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzh on 2020/12/3 20:03.
 */
class Test {

    public static void main(String[] args) {
        String ss="暗室逢灯";
        int offset=ss.indexOf("1");
        System.out.println("offset:"+offset);

        List<String> stringList=new ArrayList<>();
        stringList.add(0,"");
        stringList.remove(0);
     }
}
