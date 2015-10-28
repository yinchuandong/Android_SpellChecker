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

    //remove the noisy char at the end of result
    for(int i = wAddr.size - 1; i >= 0; i--){
        if(rBuff[i] != '}'){
            rBuff[i] = '\0';
        }else{
            break;
        }
    }

    return rBuff;
}

char* EngDict::filterAscii(char *str)
{
    int oldPtr = 0;
    int newPtr = 0;

    while(str[oldPtr] != '/0')
    {
        if(str[oldPtr] > 0x81 || str[oldPtr] == 0x81 && str[oldPtr + 1] >= 0x41)
        {
            str[newPtr++] = str[oldPtr++];
            str[newPtr++] = str[oldPtr++];
        }
        else if(str[oldPtr] >= '0' && str[oldPtr] <= '9'
                || str[oldPtr] >= 'a' && str[oldPtr] <= 'z'
                || str[oldPtr] >= 'A' && str[oldPtr] <= 'Z')
        {
            oldPtr++;
        }
        else
            str[newPtr++] = str[oldPtr++];
    }
    str[newPtr] = '/0';

    return str;
}