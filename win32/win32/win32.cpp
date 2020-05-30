#pragma once

#include "pch.h"
#include <jni.h>
#include <windows.h>
#include <vector>
#include <string>
#include "win32.h"
#include "SpecialKeys.h"

using namespace std;

int GetSpecialKeyVk(int specialKey);

JNIEXPORT void JNICALL Java_main_kotlin_Win32_leftClick
(JNIEnv* env, jobject obj) {
	INPUT input = { 0 };
	input.type = INPUT_MOUSE;
	input.mi.dwFlags = MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP;
	SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_rightClick
(JNIEnv* env, jobject obj) {
	INPUT input = { 0 };
	input.type = INPUT_MOUSE;
	input.mi.dwFlags = MOUSEEVENTF_RIGHTDOWN | MOUSEEVENTF_RIGHTUP;
	SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_middleClick
(JNIEnv* env, jobject obj) {
	INPUT input = { 0 };
	input.type = INPUT_MOUSE;
	input.mi.dwFlags = MOUSEEVENTF_MIDDLEDOWN | MOUSEEVENTF_MIDDLEUP;
	SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_leftDown
(JNIEnv* env, jobject obj) {
	INPUT input = { 0 };
	input.type = INPUT_MOUSE;
	input.mi.dwFlags = MOUSEEVENTF_LEFTDOWN;
	SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_leftUp
(JNIEnv* env, jobject obj) {
	INPUT input = { 0 };
	input.type = INPUT_MOUSE;
	input.mi.dwFlags = MOUSEEVENTF_LEFTUP;
	SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_move
(JNIEnv* env, jobject obj, jint dx, jint dy) {
	INPUT input = { 0 };
	input.type = INPUT_MOUSE;
	input.mi.dwFlags = MOUSEEVENTF_MOVE;
	input.mi.dx = dx;
	input.mi.dy = dy;
	SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_scroll
(JNIEnv* env, jobject obj, jint dy) {
	INPUT input = { 0 };
	input.type = INPUT_MOUSE;
	input.mi.dwFlags = MOUSEEVENTF_WHEEL;
	input.mi.mouseData = dy;
	SendInput(1, &input, sizeof(INPUT));
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_sendSymbol
(JNIEnv* env, jobject obj, jchar symbol) {
	INPUT input = { 0 };
	input.type = INPUT_KEYBOARD;
	input.ki.wVk = 0;
	input.ki.wScan = symbol;
	input.ki.time = 0;
	input.ki.dwFlags = KEYEVENTF_UNICODE;
	SendInput(1, &input, sizeof(INPUT)); // press the key down

	input.ki.dwFlags |= KEYEVENTF_KEYUP;
	SendInput(1, &input, sizeof(INPUT)); // release the key
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_sendSpecialKeys
(JNIEnv* env, jobject obj, jintArray specialKeys) {

	INPUT input = { 0 };
	input.type = INPUT_KEYBOARD;
	input.ki.wVk = 0;
	input.ki.wScan = 0;
	input.ki.time = 0;
	input.ki.dwFlags = 0;

	jsize len = env->GetArrayLength(specialKeys);
	jint* specialKeysArray = env->GetIntArrayElements(specialKeys, NULL);
	int* wVk = new int[len];
	for (int i = 0; i < len; i++) {
		wVk[i] = GetSpecialKeyVk((int)specialKeysArray[i]);
		if (wVk[i] != -1) {
			input.ki.wVk = wVk[i];
			SendInput(1, &input, sizeof(INPUT)); // press the special key down
		}
	}

	// release the special keys in reverse order
	input.ki.dwFlags = KEYEVENTF_KEYUP;
	for (int i = len - 1; i >= 0; i--)
	{
		if (wVk[i] != -1) {
			input.ki.wVk = wVk[i];
			SendInput(1, &input, sizeof(INPUT));
		}
	}

	env->ReleaseIntArrayElements(specialKeys, specialKeysArray, NULL);
	delete[] wVk;
}

JNIEXPORT void JNICALL Java_main_kotlin_Win32_sendKeys
(JNIEnv* env, jobject obj, jintArray specialKeys, jchar symbol) {

	INPUT input = { 0 };
	input.type = INPUT_KEYBOARD;
	input.ki.wVk = 0;
	input.ki.wScan = 0;
	input.ki.time = 0;
	input.ki.dwFlags = 0;

	jsize len = env->GetArrayLength(specialKeys);
	jint* specialKeysArray = env->GetIntArrayElements(specialKeys, NULL);
	int* wVk = new int[len];
	for (int i = 0; i < len; i++) {
		wVk[i] = GetSpecialKeyVk((int)specialKeysArray[i]);
		if (wVk[i] != -1) {
			input.ki.wVk = wVk[i];
			SendInput(1, &input, sizeof(INPUT)); // press the special key down
		}
	}

	// Use virtual key codes to make key combinations work.
	// The low-order byte of the return value of VkKeyScanExW contains
	// the virtual-key code. GetKeyboardLayout(0) - get ID of the input
	// language for the active thread.
	short symbolVk = VkKeyScanExW(symbol, GetKeyboardLayout(0));
	if (symbolVk != -1) {
		input.ki.wVk = symbolVk & 0xFF;
		SendInput(1, &input, sizeof(INPUT)); // press the key down

		input.ki.dwFlags |= KEYEVENTF_KEYUP;
		SendInput(1, &input, sizeof(INPUT)); // release the key
	}
	else {
		input.ki.wVk = 0;
		input.ki.wScan = symbol;
		input.ki.dwFlags = KEYEVENTF_UNICODE;
		::SendInput(1, &input, sizeof(INPUT)); // press the key down

		input.ki.dwFlags |= KEYEVENTF_KEYUP;
		::SendInput(1, &input, sizeof(INPUT)); // release the key
	}

	// release the special keys in reverse order
	input.ki.dwFlags = KEYEVENTF_KEYUP;
	for (int i = len - 1; i >= 0; i--)
	{
		if (wVk[i] != -1) {
			input.ki.wVk = wVk[i];
			SendInput(1, &input, sizeof(INPUT));
		}
	}

	env->ReleaseIntArrayElements(specialKeys, specialKeysArray, NULL);
	delete[] wVk;
}

int GetSpecialKeyVk(int specialKey) {
	switch (specialKey)
	{
	case BACKSPACE: return VK_BACK;
	case ENTER: return VK_RETURN;
	case WIN: return VK_LWIN;
	case CTRL: return VK_CONTROL;
	case SHIFT: return VK_SHIFT;
	case ALT: return VK_MENU;
	case ESC: return VK_ESCAPE;
	case TAB: return VK_TAB;
	case INSERT: return VK_INSERT;
	case DEL: return VK_DELETE;
	case HOME: return VK_HOME;
	case END: return VK_END;
	case PAGE_UP: return VK_PRIOR;
	case PAGE_DOWN: return VK_NEXT;
	case UP: return VK_UP;
	case LEFT: return VK_LEFT;
	case RIGHT: return VK_RIGHT;
	case DOWN: return VK_DOWN;
	case PRINT_SCREEN: return VK_SNAPSHOT;
	case F1: return VK_F1;
	case F2: return VK_F2;
	case F3: return VK_F3;
	case F4: return VK_F4;
	case F5: return VK_F5;
	case F6: return VK_F6;
	case F7: return VK_F7;
	case F8: return VK_F8;
	case F9: return VK_F9;
	case F10: return VK_F10;
	case F11: return VK_F11;
	case F12: return VK_F12;
	default: return -1;
	}
}
