package com.yin.spellchecker.util;

import com.yin.spellchecker.lib.SpellChecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * Created by yinchuandong on 15/10/26.
 */
public class DictUtil {

    private static DictUtil instance;

    private HashMap<String, Addr> indexMap;
    private RandomAccessFile dictFile;

    private String[] pathArr;

    public class Addr{
        long start;
        long end;
        long size;
    }

    private DictUtil(String[] pathArr){
        this.pathArr = pathArr;
        init();
        loadIndex();
    }

    public static DictUtil getInstance(){
        if(instance == null){
            SpellChecker checker = new SpellChecker();
            String[] pathArr = checker.loadDict();
            instance = new DictUtil(pathArr);
        }
        return instance;
    }

    private void init(){
        indexMap = new HashMap<String, Addr>();
        try {
            dictFile = new RandomAccessFile(new File(pathArr[1]), "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadIndex(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(pathArr[0])));
            String buff = null;
            while((buff = reader.readLine()) != null){
                String[] arr = buff.split(",");

                Addr addr = new Addr();
                addr.start = Long.parseLong(arr[1]);
                addr.size = Long.parseLong(arr[2]);
                addr.end = Long.parseLong(arr[3]);

                indexMap.put(arr[0], addr);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String find(String key){
        String ret = "";

        Addr addr = indexMap.get(key);
        if(addr == null){
           return ret;
        }

        try {
            byte[] buff = new byte[(int)addr.size];
            dictFile.seek(addr.start + 1);// skip space
            dictFile.read(buff, 0, (int) addr.size);
            ret = new String(buff);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void closeDict(){
        try {
            dictFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
