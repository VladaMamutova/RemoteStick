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
	* Method:    leftClick
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_leftClick
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    middleClick
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_middleClick
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
	* Method:    leftDown
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_leftDown
	(JNIEnv*, jobject, jint, jint);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    leftUp
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_leftUp
	(JNIEnv*, jobject, jint, jint);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    move
	* Signature: (II)V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_move
	(JNIEnv*, jobject, jint, jint);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    move
	* Signature: (C)V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_sendKeys
	(JNIEnv*, jobject, jchar);

#ifdef __cplusplus
}
#endif
#endif
