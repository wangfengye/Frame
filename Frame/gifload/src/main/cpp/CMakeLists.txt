

cmake_minimum_required(VERSION 3.4.1)

#[[set(SOURCES)
# GLOB_RECURSE选项将会生成一个类似于通常的GLOB选项的list，只是它会寻访所有那些匹配目录的子路径并同时匹配查询表达式的文件
# file(GLOB_RECURSE SOURCES ${CMAKE_SOURCE_DIR}/*.cpp ${CMAKE_SOURCE_DIR}/*.c)]]
add_library(
        gif-load

        SHARED
       #${SOURCES}
        gifload.cpp
        giflib/dgif_lib.c
        giflib/gif_err.c
        giflib/gifalloc.c
        giflib/openbsd-reallocarray.c
)
set(LIBS)
list(APPEND LIBS
        jnigraphics
        log)


target_link_libraries(
        gif-load
        ${LIBS} )