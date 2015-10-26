//
// Created by 尹川东 on 15/10/26.
//

#ifndef ANDROID_SPELLCHECKER_ENGDICT_H
#define ANDROID_SPELLCHECKER_ENGDICT_H

#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <string>
#include <unordered_map>
#include "json/json.h"

using namespace std;


typedef struct tagAddr
{
    long start;
    long end;
    long size;
} Addr;

typedef unordered_map<string, Addr> TIndexMap;

class EngDict {

private:
    const char *pathDictIndex = "/system/usr/hmm/index.dat";
    const char *pathDictData = "/system/usr/hmm/dict.dat";
    FILE *indexFile;
    FILE *dictFile;

    TIndexMap indexMap;

public:
    EngDict();
    ~EngDict();
    int init();
    string find(string key);

};


#endif //ANDROID_SPELLCHECKER_ENGDICT_H
