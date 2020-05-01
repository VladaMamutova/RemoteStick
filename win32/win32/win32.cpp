#pragma once

#include "pch.h"
#include <jni.h>
#include <windows.h>
#include "win32.h"

JNIEXPORT void JNICALL Java_main_kotlin_Win32_doubleClick(JNIEnv* env, jobject obj) {
    //// dx and dy contain normalized absolute coordinates between 0 and 65 535.
    //jint dx = x * 65535 / GetSystemMetrics(0);
    //jint dy = y * 65535 / GetSystemMetrics(1);

    //// mouse_event(MOUSEEVENTF dwFlags, int dx, int dy, int dwData, UIntPtr dwExtraInfo);
    //mouse_event(MOUSEEVENTF_ABSOLUTE | MOUSEEVENTF_MOVE, dx, dy, 0, 0);
    //mouse_event(MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP, dx, dy, 0, 0);
    //Sleep(150);
    //mouse_event(MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP, dx, dy, 0, 0);

    INPUT input = { 0 };
    input.type = INPUT_MOUSE;
    input.mi.dwFlags = MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP;
    SendInput(1, &input, sizeof(INPUT));
    Sleep(150);
    SendInput(1, &input, sizeof(INPUT));
}

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

JNIEXPORT void JNICALL Java_main_kotlin_Win32_moveMouse(JNIEnv* env, jobject obj, jint x, jint y) {
    INPUT input = { 0 };
    input.type = INPUT_MOUSE;
    input.mi.dwFlags = MOUSEEVENTF_MOVE;
    input.mi.dx = x;
    input.mi.dy = y;
    SendInput(1, &input, sizeof(INPUT));
}