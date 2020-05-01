#pragma once

#include <jni.h>
/* Header for class Java_main_kotlin_Win32 */

#ifndef _Included_win32
#define _Included_win32
#ifdef __cplusplus
extern "C" {
#endif
    /*
    * Class:     Java_main_kotlin_Win32
    * Method:    leftDoubleClick
    * Signature: (II)V
    */
    JNIEXPORT void JNICALL Java_main_kotlin_Win32_leftDoubleClick
    (JNIEnv*, jobject, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
