#pragma once

#include "pch.h"
#include <jni.h>
#include <windows.h>
#include "win32.h"

JNIEXPORT void JNICALL Java_main_kotlin_Win32_leftClick(JNIEnv* env, jobject obj) {
    INPUT input = { 0 };
    input.type = INPUT_MOUSE;
    input.mi.dwFlags = MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP;
    SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_rightClick(JNIEnv* env, jobject obj) {
    INPUT input = { 0 };
    input.type = INPUT_MOUSE;
    input.mi.dwFlags = MOUSEEVENTF_RIGHTDOWN | MOUSEEVENTF_RIGHTUP;
    SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_middleClick(JNIEnv* env, jobject obj) {
    INPUT input = { 0 };
    input.type = INPUT_MOUSE;
    input.mi.dwFlags = MOUSEEVENTF_MIDDLEDOWN | MOUSEEVENTF_MIDDLEUP;
    SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_leftDown(JNIEnv* env, jobject obj) {
    INPUT input = { 0 };
    input.type = INPUT_MOUSE;
    input.mi.dwFlags = MOUSEEVENTF_LEFTDOWN;
    SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_leftUp(JNIEnv* env, jobject obj) {
    INPUT input = { 0 };
    input.type = INPUT_MOUSE;
    input.mi.dwFlags = MOUSEEVENTF_LEFTUP;
    SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_move(JNIEnv* env, jobject obj, jint dx, jint dy) {
    INPUT input = { 0 };
    input.type = INPUT_MOUSE;
    input.mi.dwFlags = MOUSEEVENTF_MOVE;
    input.mi.dx = dx;
    input.mi.dy = dy;
    SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_sendKeys(JNIEnv* env, jobject obj, jchar symbol) {
    INPUT input = { 0 };
    input.type = INPUT_KEYBOARD;
    input.ki.wVk = 0;
    input.ki.wScan = symbol;
    input.ki.time = 0;
    input.ki.dwFlags = KEYEVENTF_UNICODE;
    SendInput(1, &input, sizeof(INPUT));

    //INPUT input = { 0 };
    //input.type = INPUT_KEYBOARD;

    //input.ki.wVk = 0;
    //input.ki.wScan = symb;
    //input.ki.dwFlags = KEYEVENTF_UNICODE;
    //SendInput(1, &input, sizeof(INPUT));

    //input.ki.dwFlags = KEYEVENTF_KEYUP | KEYEVENTF_UNICODE;
    //SendInput(1, &input, sizeof(INPUT));
}