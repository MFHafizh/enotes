//
// Created by M Fakhruddin Hafizh on 27/12/2019.
//

#include <string.h>
#include <jni.h>
#include <iostream>
#define OUT 0
#define IN 1

extern "C" JNIEXPORT jint JNICALL
Java_id_ac_ui_cs_mobileprogramming_muhammadfakhruddinhafizh_enotes_AddNoteActivity_countWord( JNIEnv* env,
                                                  jobject thiz, jstring str)
{
    int state = OUT;
    unsigned wc = 0; // word count

    // Scan all characters one by one
    const char *nativeString = env->GetStringUTFChars(str, 0);
    int count = 0, i;
    for (i = 0; nativeString[i] != '\0';i++)
    {
        if (nativeString[i] == ' ' || nativeString[i] == '\n' || nativeString[i] == '\t')
            count++;
    }
    return count;
}
