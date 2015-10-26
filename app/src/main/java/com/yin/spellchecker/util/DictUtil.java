package com.yin.spellchecker.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * Created by yinchuandong on 15/10/26.
 */
public class DictUtil {

    private final static String PATH_INDEX = "/system/usr/hmm/index.dat";
    private final static String PATH_DICT = "/system/usr/hmm/dict.dat";
    private HashMap<String, String> indexMap;
    private RandomAccessFile dictFile;

    public DictUtil(){

    }

    private void init(){
        indexMap = new HashMap<String, String>();
    }

    private void loadIndex(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(PATH_INDEX)));
            String buff = null;
            while((buff = reader.readLine()) != null){
                String[] arr = buff.split(",");
                
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
