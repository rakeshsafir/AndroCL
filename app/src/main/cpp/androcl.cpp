#include <jni.h>
#include <string>
#include <android/bitmap.h>

#include "imgprocessor.h"

extern "C"
jint
Java_com_example_linuxdev_myapplication_MainActivity_runOpenCL(JNIEnv* env, jclass clazz, jobject bitmapIn, jobject bitmapOut, jintArray info)
{

    void*	bi;
    void*   bo;

    jint* i = env->GetIntArrayElements(info, NULL);

    AndroidBitmap_lockPixels(env, bitmapIn, &bi);
    AndroidBitmap_lockPixels(env, bitmapOut, &bo);

    openCLNR((unsigned char *)bi, (unsigned char *)bo, (int *)i);

    AndroidBitmap_unlockPixels(env, bitmapIn);
    AndroidBitmap_unlockPixels(env, bitmapOut);
    env->ReleaseIntArrayElements(info, i, 0);

    return 0;
}


extern "C"
jint
Java_com_example_linuxdev_myapplication_MainActivity_runNativeC(JNIEnv* env, jclass clazz, jobject bitmapIn, jobject bitmapOut, jintArray info)
{
    void*	bi;
    void*   bo;

    jint* i = env->GetIntArrayElements(info, NULL);

    AndroidBitmap_lockPixels(env, bitmapIn, &bi);
    AndroidBitmap_lockPixels(env, bitmapOut, &bo);

    refNR((unsigned char *)bi, (unsigned char *)bo, (int *)i);

    AndroidBitmap_unlockPixels(env, bitmapIn);
    AndroidBitmap_unlockPixels(env, bitmapOut);
    env->ReleaseIntArrayElements(info, i, 0);

    return 0;
}