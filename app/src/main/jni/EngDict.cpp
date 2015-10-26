//
// Created by 尹川东 on 15/10/26.
//
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <string>
#include <unordered_map>
#include "json/json.h"
#include "EngDict.h"

EngDict::EngDict() {

}

EngDict::~EngDict() {
    fclose(dictFile);
}

int EngDict::init() {
    indexFile = fopen(this->pathDictIndex, "rb");
    if (indexFile == NULL) {
        return -1;
    }

    dictFile = fopen(this->pathDictData, "rb");
    if (dictFile == NULL) {
        return -2;
    }

    //load the index to memory
    char buff[256];
    char temp[256];
    const char *c = ",";
    while (!feof(indexFile)) {
        memset(buff, 0, sizeof(buff));
        memset(temp, 0, sizeof(temp));

        fgets(buff, sizeof(buff), indexFile);
        strcpy(temp, buff);

        char *key = strtok(temp, c);
        long startAddr = atol(strtok(NULL, c));
        long size = atol(strtok(NULL, c));
        long endAddr = atol(strtok(NULL, c));

        Addr wAddr;
        wAddr.start = startAddr;
        wAddr.end = endAddr;
        wAddr.size = size;
        indexMap.insert(TIndexMap::value_type(key, wAddr));
    }


    fclose(indexFile);
    return 0;
}

string EngDict::find(string key) {
    TIndexMap::iterator iter = indexMap.find(key);
    if (iter == indexMap.end()) {
        return "";
    }

    Addr wAddr = iter->second;
    fseek(dictFile, wAddr.start, SEEK_SET);
    char *rBuff = new char[wAddr.size];
    fread(rBuff, (unsigned long)wAddr.size, 1, dictFile);
    rBuff = rBuff + 8;

    return rBuff;
}
