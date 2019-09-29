

cmake_minimum_required(VERSION 3.4.1)

# 查找文件系统中指定模式的路径，如/* 匹配根目录的文件（注意路径）
file(GLOB bzip_source ${CMAKE_SOURCE_DIR}/src/main/cpp/bzip/*.c)

add_library(
        native-lib

        SHARED

        src/main/cpp/native-lib.cpp
        src/main/cpp/bspatch.c
        src/main/cpp/bsdiff.c
        #这个相当与别名引用，上面设置了一个别名
        ${bzip_source}
)


find_library(
        log-lib

        log)


target_link_libraries(
        native-lib

        ${log-lib})