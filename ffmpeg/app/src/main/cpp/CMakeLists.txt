

cmake_minimum_required(VERSION 3.4.1)
include_directories(${CMAKE_SOURCE_DIR}/include)#头文件目录
file(GLOB_RECURSE wavlib_source ${CMAKE_SOURCE_DIR}/include/wavlib/*.c)
file(GLOB_RECURSE main_source ${CMAKE_SOURCE_DIR}/*.cpp)

add_library(native-lib SHARED audio_play.c ${main_source} ${wavlib_source})


find_library(log-lib log)
find_library(android-lib android)
find_library(OpenSL-lib OpenSLES)
add_library(avcodec SHARED IMPORTED)
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}../../../../libs/${ANDROID_ABI}/libavcodec.so)
add_library(avdevice SHARED IMPORTED)
set_target_properties(avdevice PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}../../../../libs/${ANDROID_ABI}/libavdevice.so)
add_library(avfilter SHARED IMPORTED)
set_target_properties(avfilter PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}../../../../libs/${ANDROID_ABI}/libavfilter.so)
add_library(avformat SHARED IMPORTED)
set_target_properties(avformat PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}../../../../libs/${ANDROID_ABI}/libavformat.so)

add_library(avutil SHARED IMPORTED)
set_target_properties(avutil PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}../../../../libs/${ANDROID_ABI}/libavutil.so)
add_library(swresample SHARED IMPORTED)
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}../../../../libs/${ANDROID_ABI}/libswresample.so)
add_library(swscale SHARED IMPORTED)
set_target_properties(swscale PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}../../../../libs/${ANDROID_ABI}/libswscale.so)
add_library(yuv SHARED IMPORTED)
set_target_properties(yuv PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}../../../../libs/${ANDROID_ABI}/libyuv.so)
target_link_libraries(native-lib ${log-lib} ${android-lib} ${OpenSL-lib} avcodec avdevice avfilter avformat avutil swresample swscale yuv)