#pragma once
/* Header for class Java_main_kotlin_Win32 */

#include <jni.h>
#include <endpointvolume.h>

#define MAX_VOLUME  100
#define SOUND_SETTINGS_NUMBER  2

#ifndef _Included_win32
#define _Included_win32
#ifdef __cplusplus
extern "C" {
#endif
	IAudioEndpointVolume* endpointVolume = NULL;
	bool isVolumeValid = false;

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    init
	* Signature: ()Z
	*/
	JNIEXPORT jboolean JNICALL Java_main_kotlin_Win32_init
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    release
	* Signature: ()
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_release
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
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    leftUp
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_leftUp
	(JNIEnv*, jobject);

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
	* Signature: (I)V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_scroll
	(JNIEnv*, jobject, jint);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    sendSymbol
	* Signature: (C)V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_sendSymbol
	(JNIEnv*, jobject, jchar);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    sendSpecialKeys
	* Signature: ([I)V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_sendSpecialKeys
	(JNIEnv*, jobject, jintArray);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    sendKeys
	* Signature: ([IC)V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_sendKeys
	(JNIEnv*, jobject, jintArray, jchar);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    volumeUp
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_volumeUp
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    volumeDown
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_volumeDown
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    volumeMute
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_volumeMute
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    playPause
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_playPause
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    nextTrack
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_nextTrack
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    prevTrack
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_prevTrack
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    stop
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_main_kotlin_Win32_stop
	(JNIEnv*, jobject);

	/*
	* Class:     Java_main_kotlin_Win32
	* Method:    init
	* Signature: ()I
	*/
	JNIEXPORT jintArray JNICALL Java_main_kotlin_Win32_getVolumeAndMute
	(JNIEnv*, jobject);

#ifdef __cplusplus
}
#endif
#endif
