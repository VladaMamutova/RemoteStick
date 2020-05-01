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
	* Method:    doubleClick
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_doubleClick
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    leftClick
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_leftClick
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    rightClick
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_rightClick
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    moveMouse
	* Signature: (II)V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_moveMouse
	(JNIEnv*, jobject, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
