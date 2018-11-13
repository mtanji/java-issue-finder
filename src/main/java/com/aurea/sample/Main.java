package com.aurea.sample;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");

        List<String> names1 = new ArrayList<>();
        names1.add("name1");
        names1.add("name2");
        boolean b1 = names1.contains("name1");

        List<String> names2 = Arrays.asList("name1", "name2");
        boolean b2 = names2.contains("name1");

        Set<String> names3 = new HashSet<>();
        names3.add("name1");
        names3.add("name2");
        boolean b3 = names3.contains("name1");

        Set<String> names4 = new HashSet<>(names2);
        boolean b4 = names4.contains("name1");

    }
}
