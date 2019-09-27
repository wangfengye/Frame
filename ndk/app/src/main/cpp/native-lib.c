#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <string.h>
#include <strings.h>
#include <unistd.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <dirent.h>


#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"fileUtil",FORMAT,__VA_ARGS__)

char *strchrLast(char *s, char c) {
    char *i;
    char *last = 0;
    char *tmp = s;
    while ((i = strchr(tmp, c)) != 0) {
        last = i;
        tmp = i + 1;
    }
    return last;
}

int strchrLastIndex(char *s, char c) {
    char *p = strchrLast(s, c);
    if (p == 0)return (int) strlen(s);
    return (int) ((p - s) / sizeof(char));
}

JNIEXPORT jstring JNICALL
Java_com_maple_ndk_FileUtil_diff(JNIEnv *env, jclass type, jstring path_, jint count) {
    const char *path = (*env)->GetStringUTFChars(env, path_, 0);
    char **patches = malloc(sizeof(char *) * count);
    LOGI("path:%s", path);
    // 数组不初始化赋值,内部的值可能是任意值.(具体依赖于编译器)
    char *suf = (char *) malloc(sizeof(char) * 50);
    bzero(suf, 50);
    memcpy(suf, path, (size_t) strchrLastIndex((char *) path, '.'));
    memcpy(suf + strlen(suf), "-tmp/", sizeof(char) * 5);
    LOGI("生成文件夹:%s", suf);
    if (access(suf, 0) != 0) {
        if (0 != mkdir(suf, 777)) {
            LOGI("生成文件目录异常:%d", 1);
        }
    }
    for (int i = 0; i < count; i++) {
        patches[i] = malloc(sizeof(char *) * 100);
        //名称 源文件名/a_i.后缀
        sprintf(patches[i], "%sa-%d.%s", suf, i, "tmp");
    }
    LOGI("文件名初始化%d", count);
    FILE *fp_source = fopen(path, "rb");
    LOGI("读取文件完成%s", "s");
    fseek(fp_source, 0, SEEK_END);
    const int size = (int) ftell(fp_source);
    fseek(fp_source, 0, SEEK_SET);
    const int diff_size = size / count;
    LOGI("写文件%s", "start");

    for (int i = 0; i < count - 1; i++) {
        FILE *tmp = fopen(patches[i], "wb");
        for (int j = 0; j < diff_size; j++) {
            fputc(fgetc(fp_source), tmp);
        }
        fclose(tmp);
    }
    LOGI("写文件%s", "expect last");
    FILE *tmpLast = fopen(patches[count - 1], "wb");
    int ch;
    while ((ch = fgetc(fp_source)) != EOF) {
        fputc(ch, tmpLast);
    }
    LOGI("写文件%s", "finished");
    fclose(tmpLast);
    fclose(fp_source);
    for (int i = 0; i < count; i++) {
        free(patches[i]);
    }
    free(patches);
    (*env)->ReleaseStringUTFChars(env, path_, path);
    jstring res = (*env)->NewStringUTF(env, suf);
    free(suf);
    return res;
}
/**
 *  一个奇葩问题.
 *  diff函数重复释放同一个jstring.
 *  在调用diff后调用combine 函数导致 tmpFiles内容丢失. 运行结果 ,tmpFiles_的值和 out_值相同,都是java层
 *  传入的out
 */

JNIEXPORT void JNICALL
Java_com_maple_ndk_FileUtil_combine(JNIEnv *env, jclass type, jstring tmpFiles_, jstring out_) {
    const char *tmpFiles = (*env)->GetStringUTFChars(env, tmpFiles_, 0);
    const char *out = (*env)->GetStringUTFChars(env, out_, 0);

    LOGI("tmpFiles,%p,%s", tmpFiles_, tmpFiles);
    LOGI("out,%p,%s", out_, out);

    DIR *dir = opendir(tmpFiles);
    if (dir == NULL) {
        LOGI("文件为空%s", tmpFiles);
        LOGI("文件为空%s", out);
        return;
    }
    struct dirent *ent;
    FILE *outFile = fopen(out, "wb");
    char *suf = (char *) malloc(sizeof(char) * 50);
    bzero(suf, 50);

    memcpy(suf, tmpFiles, strlen(tmpFiles));
    while ((ent = readdir(dir)) != NULL) {
        LOGI("filename %s", ent->d_name);

        if (strstr(ent->d_name, ".tmp") == NULL) {
            LOGI("非文件碎片:%s", ent->d_name);
            continue;
        }
        memcpy(suf + strlen(tmpFiles), ent->d_name, sizeof(ent->d_name));
        FILE *tmpFile = fopen(suf, "rb");
        if (tmpFile == NULL) {
            LOGI("subFile open failed%d", 1);
            return;
        }
        int ch;
        while ((ch = fgetc(tmpFile)) != EOF) {
            fputc(ch, outFile);
        }
        fclose(tmpFile);
    }
    fclose(outFile);
    free(suf);
    LOGI("关闭完成.%d", 1);


    (*env)->ReleaseStringUTFChars(env, tmpFiles_, tmpFiles);
    (*env)->ReleaseStringUTFChars(env, out_, out);
}