package com.aurea.sample;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");

        Ability ab = new Person("xx");
        Person p = new Person("ss");

        Person[] lp = { new Person("a"), new Person("b") };
        System.out.println(lp);// must know that println is from a PrintStream and does call toString()

        ArrayList<String> list = new ArrayList<>();
        list.add("maria");

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

        Hashtable a = new Hashtable();

    }

    static class Person implements Ability {
        String name;
        Person(String n) {
            name = n;
        }
        public void doesFast() {

        }
    }

    interface Ability {
        void doesFast();
    }
}
