#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <jni.h>
#include <set>
#include <map>
#include <hash_map>
#include <vector>
#include <android/log.h>

using namespace std;

#define  LOG_TAG    "SpellChekcer"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


const char* PATH_OXFORD_WORDS = "/system/usr/hmm/oxford-words.dat";
const char* PATH_INIT_PROB = "/system/usr/hmm/init_prob.hmm";
const char* PATH_TRAN_PROB = "/system/usr/hmm/tran_prob.hmm";
const char* PATH_CONFUSEING_WORDS = "/system/usr/hmm/confusing_word.dat";
const char* PATH_CANDIDATE_SET = "/system/usr/hmm/candidate_set_new.dat";
const char* PATH_DICT = "/system/usr/hmm/dict.json";


#ifdef __cplusplus
extern "C" {
#endif

void testRead(const char* filename) {
	LOGD("testread");
	FILE* file = NULL;
	if ((file = fopen(filename, "r")) == NULL) {
		LOGD("open false");
		return;
	}

	LOGD("filename %s", filename);
	int count = 0;
	char* buff = new char[1024];
	while (!feof(file)) {
		fgets(buff, 1024, file);
		count++;
		if (count % 10000 == 0) {
			LOGD("now is reading: %d lines", count);
		}
	}

}

JNIEXPORT jobjectArray JNICALL Java_com_yin_spellchecker_lib_SpellChecker_init(
		JNIEnv *env, jobject obj) {

	int size = 6;
	char* arr[6] =
			{ "/system/usr/hmm/oxford-words.dat",
					"/system/usr/hmm/init_prob.hmm",
					"/system/usr/hmm/tran_prob.hmm",
					"/system/usr/hmm/confusing_word.dat",
					"/system/usr/hmm/candidate_set_new.dat",
					"/system/usr/hmm/dict.json" };

//	testRead(arr[2]);

	jobjectArray ret;
	ret = (jobjectArray) env->NewObjectArray(size,
			env->FindClass("java/lang/String"), env->NewStringUTF(""));
	for (int i = 0; i < size; i++) {
		env->SetObjectArrayElement(ret, i, env->NewStringUTF(arr[i]));
	}
	return ret;
}


/**
 * 加载牛津词典,只有单词
 */
JNIEXPORT jobject JNICALL Java_com_yin_spellchecker_lib_SpellChecker_loadOxfordWords(JNIEnv *env, jobject obj){
	jclass cls_HashSet = env->FindClass("java/util/HashSet");
	jmethodID construct = env->GetMethodID(cls_HashSet, "<init>", "()V");
	jobject obj_HashSet = env->NewObject(cls_HashSet, construct, "");
	jmethodID HashSet_add = env->GetMethodID(cls_HashSet,"add","(Ljava/lang/Object;)Z");

	FILE* file = NULL;
	if ((file = fopen(PATH_OXFORD_WORDS, "r")) == NULL) {
		LOGD("open false PATH_OXFORD_WORDS");
		return obj_HashSet;
	}

	char buff[128];
	int count = 0;
	set<string> w_set;
	while (!feof(file)) {
		fgets(buff, 128, file);
		jstring word = env->NewStringUTF(buff);
		env->CallObjectMethod(obj_HashSet, HashSet_add, word);
		env->DeleteLocalRef(word);
//		env->ReleaseStringUTFChars(word, buff);
		count++;
		if (count % 1000 == 0){
			LOGD("loadOxfordWord: %d ", count);
		}
//		break;
	}
//	delete buff;
	fclose(file);
	return obj_HashSet;
}

/**
 * 加载候选集合
 */
JNIEXPORT jobject JNICALL Java_com_yin_spellchecker_lib_SpellChecker_loadCandidateMap(
		JNIEnv *env, jobject obj, jobjectArray wordsArr) {

	jsize size = env->GetArrayLength(wordsArr);
	set<string> wordsSet;
	for (int i = 0; i < size; i++)
	{
		jstring str = (jstring)(env->GetObjectArrayElement(wordsArr, i));
		const char *word = env->GetStringUTFChars(str, NULL);
		wordsSet.insert(word);
		LOGD("---- %s", word);
	}

//	set<string>::iterator iter;
//	iter = wordsSet.find("like");
//	if (iter == wordsSet.end()) {
//		LOGD("not found");
//	}else{
//		LOGD("founded");
//	}

	jclass cls_ArrayList = env->FindClass("java/util/ArrayList");
	jmethodID construct = env->GetMethodID(cls_ArrayList, "<init>", "()V");
	jobject obj_ArrayList = env->NewObject(cls_ArrayList, construct, "");
	jmethodID arrayList_add = env->GetMethodID(cls_ArrayList,"add","(Ljava/lang/Object;)Z");

	FILE* file = NULL;
	if ((file = fopen(PATH_CANDIDATE_SET, "r")) == NULL) {
		LOGD("open false PATH_CANDIDATE_SET");
		return obj_ArrayList;
	}

	char* buff = new char[512];
	char* tmp = new char[512];
	while (!feof(file)) {
		fgets(buff, 512, file);
		strcpy(tmp, buff);
		char* p = strtok(tmp, " ");
		if(wordsSet.find(p) == wordsSet.end()){
			continue;
		}
		jstring lineStr = env->NewStringUTF(buff);
		env->CallObjectMethod(obj_ArrayList, arrayList_add, lineStr);
		env->DeleteLocalRef(lineStr);
	}
	fclose(file);
	delete buff;
	delete tmp;
//	env->CallObjectMethod(obj_ArrayList, arrayList_add, env->NewStringUTF("val1"));
	return obj_ArrayList;
}



/**
 * 加载初始概率
 */
JNIEXPORT jobject JNICALL Java_com_yin_spellchecker_lib_SpellChecker_loadInitProb(
		JNIEnv *env, jobject obj, jobjectArray wordsArr) {

	jsize size = env->GetArrayLength(wordsArr);
	set<string> wordsSet;
	for (int i = 0; i < size; i++)
	{
		jstring str = (jstring)(env->GetObjectArrayElement(wordsArr, i));
		const char *word = env->GetStringUTFChars(str, NULL);
		wordsSet.insert(word);
		LOGD("---- %s", word);
	}

	jclass cls_hashmap = env->FindClass("java/util/HashMap");
	jmethodID hashmap_init = env->GetMethodID(cls_hashmap, "<init>", "()V");
	jobject hashMap = env->NewObject(cls_hashmap, hashmap_init, "");

	jmethodID hashmap_put = env->GetMethodID(cls_hashmap, "put",
			"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

	FILE* file = NULL;
	if ((file = fopen(PATH_INIT_PROB, "r")) == NULL) {
		LOGD("open false PATH_INIT_PROB");
		return hashMap;
	}

	char* buff = new char[128];
	while (!feof(file)) {
		fgets(buff, 128, file);
		char* p = strtok(buff, "\t");
		if(wordsSet.find(p) == wordsSet.end()){
			continue;
		}
		char* prob = strtok(NULL, "\t");
		jstring key = env->NewStringUTF(p);
		jstring val = env->NewStringUTF(prob);
		env->CallObjectMethod(hashMap, hashmap_put, key, val);
		env->DeleteLocalRef(key);
		env->DeleteLocalRef(val);
	}
	fclose(file);
	delete buff;
	return hashMap;
}

/**
 * 加载转移概率
 */
JNIEXPORT jobject JNICALL Java_com_yin_spellchecker_lib_SpellChecker_loadTranProb(
		JNIEnv *env, jobject obj, jobjectArray itemArr) {

	jsize size = env->GetArrayLength(itemArr);
	set<string> wordsSet;
	for (int i = 0; i < size; i++)
	{
		jstring str = (jstring)(env->GetObjectArrayElement(itemArr, i));
		const char *word = env->GetStringUTFChars(str, NULL);
		wordsSet.insert(word);
		LOGD("---- %s", word);
	}

	jclass cls_hashmap = env->FindClass("java/util/HashMap");
	jmethodID hashmap_init = env->GetMethodID(cls_hashmap, "<init>", "()V");
	jobject hashMap = env->NewObject(cls_hashmap, hashmap_init, "");

	jmethodID hashmap_put = env->GetMethodID(cls_hashmap, "put",
			"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

	FILE* file = NULL;
	if ((file = fopen(PATH_TRAN_PROB, "r")) == NULL) {
		LOGD("open false PATH_TRAN_PROB");
		return hashMap;
	}

	char* buff = new char[128];
	char* tmp = new char[128]; //用来缓存buff
	while (!feof(file)) {
		fgets(buff, 128, file);
		strcpy(tmp, buff);
		char* iWord = strtok(tmp, "\t");
		char* jWord = strtok(NULL, "\t");
		char item[128];
		sprintf(item, "%s|%s", iWord, jWord);
		if(wordsSet.find(item) == wordsSet.end()){
			continue;
		}
		LOGD("tran prob: %s", buff);
		char* prob = strtok(NULL, "\t");
		jstring key = env->NewStringUTF(item);
		jstring val = env->NewStringUTF(prob);
		env->CallObjectMethod(hashMap, hashmap_put, key, val);
		env->DeleteLocalRef(key);
		env->DeleteLocalRef(val);
	}
	fclose(file);
	delete buff;
	delete tmp;
	return hashMap;
}


JNIEXPORT jobject JNICALL Java_com_yin_spellchecker_lib_SpellChecker_getTranMap(
		JNIEnv *env, jobject obj) {
	jclass cls_hashmap = env->FindClass("java/util/HashMap");
	jmethodID hashmap_init = env->GetMethodID(cls_hashmap, "<init>", "()V");
	jobject hashMap = env->NewObject(cls_hashmap, hashmap_init, "");

	jmethodID hashmap_put = env->GetMethodID(cls_hashmap, "put",
			"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
//	jstring key1 = env->NewStringUTF("key1");
//	jstring val1 = env->NewStringUTF("val1");
//	env->CallObjectMethod(hashMap, hashmap_put, key1, val1);

	const char* filename = "/system/usr/hmm/tran_prob.hmm";
	FILE* file = NULL;
	if ((file = fopen(filename, "r")) == NULL) {
		LOGD("open false");
		return hashMap;
	}

	map<string,string> wordMap;

	LOGD("filename %s", filename);
	int count = 0;
	char* buff = new char[128];
	const char c[] = "\t";
	char* iWord;
	char* jWord;
	char* prob;
	char tmp[100];
	while (!feof(file)) {
		fgets(buff, 128, file);
		count++;

		iWord = strtok(buff, c);
//		if (strcmp(iWord, "like") != 0) {
//			continue;
//		}
//		iter = wordSet.find(iWord);
//		if (iter == wordSet.end()) {
//			continue;
//		}

		jWord = strtok(NULL, c);
		prob = strtok(NULL, c);

		memset(tmp, 0, 100);
		sprintf(tmp, "%s|%s", iWord, jWord);
		wordMap.insert(make_pair(iWord, jWord));


//		jstring key1 = env->NewStringUTF(tmp);
//		jstring val1 = env->NewStringUTF(prob);
//		env->CallObjectMethod(hashMap, hashmap_put, key1, val1);
//		env->DeleteLocalRef(key1);
//		env->DeleteLocalRef(val1);

//		if (count % 10000 == 0) {
//			LOGD("now is reading: %d lines", count);
//		}

	}
	delete buff;
//	LOGD("word set size: %d", wordSet.size());
	fclose(file);
	return hashMap;
}

JNIEXPORT void JNICALL Java_com_yin_spellchecker_lib_SpellChecker_test(
		JNIEnv *env, jobject obj) {
	LOGD("test");
	const char* filename = "/system/usr/hmm/tran_prob.hmm";
	char str[100];
	strcpy(str, "hello,everyone");
	char* p = strtok(str, ",");
	while (p) {
		LOGD("the word is: %s \n", p);
		p = strtok(NULL, ",");
	}

}

#ifdef __cplusplus
}
#endif
