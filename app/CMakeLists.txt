cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_VERBOSE_MAKEFILE on)

include_directories(src/main/cpp/include)

include_directories(${ANDROID_NDK}/sources/android/native_app_glue)
add_library(app-glue STATIC ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c)

add_library( # Lib Name.
             androcl

             # Shared.
             SHARED

             # src list
             src/main/cpp/androcl.cpp
             src/main/cpp/imgprocessor.cpp )


add_library( OpenCL SHARED IMPORTED )
set_target_properties(OpenCL PROPERTIES IMPORTED_LOCATION /home/saket/AndroCL/app/src/main/jniLibs/${ANDROID_ABI}/libOpenCL.so)

find_library( log-lib log )

find_library( jnigraphics-lib jnigraphics )

find_library( opencl-lib OpenCL )

target_link_libraries( androcl ${log-lib} ${jnigraphics-lib} app-glue OpenCL )