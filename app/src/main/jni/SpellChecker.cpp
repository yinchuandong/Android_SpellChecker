#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <jni.h>
#include <android/log.h>
#include "json/json.h"
#include "EngDict.h"

using namespace std;

#define  LOG_TAG    "SpellChekcer"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


#ifdef __cplusplus
extern "C" {
#endif

void testRead(const char *filename) {
    LOGD("testread");
    FILE *file = NULL;
    if ((file = fopen(filename, "r")) == NULL) {
        LOGD("open false");
        return;
    }

    LOGD("filename %s", filename);
    int count = 0;
    char *buff = new char[1024];
    while (!feof(file)) {
        fgets(buff, 1024, file);
        count++;
        if (count % 10000 == 0) {
            LOGD("now is reading: %d lines", count);
        }
    }
    fclose(file);

}

void testJson(){
    Json::Value item;
    item["word"] = "hello";
    item["pp"] = "pp_hello";
    const char* json_str = item.toStyledString().c_str();

    LOGD("%s", json_str);
}

JNIEXPORT jobjectArray JNICALL Java_com_yin_spellchecker_lib_SpellChecker_init(
        JNIEnv *env, jobject obj) {

    int size = 2;
    const char *arr[2] =
            {"key_yin_2015",
             "secret_2015_yin",
            };
    jobjectArray ret;
    ret = (jobjectArray) env->NewObjectArray(size,
                                             env->FindClass("java/lang/String"),
                                             env->NewStringUTF(""));
    for (int i = 0; i < size; i++) {
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(arr[i]));
    }

    return ret;
}

JNIEXPORT jobjectArray  JNICALL
Java_com_yin_spellchecker_lib_SpellChecker_loadDict(JNIEnv *env, jobject instance) {
    int size = 2;
    const char *arr[2] =
            {"/system/usr/hmm/index.dat",
             "/system/usr/hmm/dict.dat",
            };
    jobjectArray ret;
    ret = (jobjectArray) env->NewObjectArray(size,
                                             env->FindClass("java/lang/String"),
                                             env->NewStringUTF(""));
    for (int i = 0; i < size; i++) {
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(arr[i]));
    }

    return ret;
}



/**
 * 采用index1.dat和dict1.dat词典，注意和java的分开了
 * 暂时没用到
 */
JNIEXPORT jstring JNICALL
Java_com_yin_spellchecker_lib_SpellChecker_findDict(JNIEnv *env, jobject instance, jstring key_) {
    const char *key = env->GetStringUTFChars(key_, 0);

    EngDict *pEngDict = new EngDict();
    pEngDict->init();
    LOGD("findDict: %s", key);
    string result = pEngDict->find(key);
    if(result.length() == 0){
        LOGD("not found in dictionary");
        return env->NewStringUTF("");
    }

    LOGD("old result: %s", result.c_str());
    env->ReleaseStringUTFChars(key_, key);
    return env->NewStringUTF(result.c_str());
}



#ifdef __cplusplus
}
#endif
