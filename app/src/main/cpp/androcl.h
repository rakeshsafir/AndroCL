#ifndef ANDROCL_ANDROCL_H
#define ANDROCL_ANDROCL_H


#include <time.h>
#include <math.h>

#include <android/log.h>
#define app_name "AndroCL"

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, app_name, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, app_name, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, app_name, __VA_ARGS__))

#endif //ANDROCL_ANDROCL_H
