

cmake_minimum_required(VERSION 3.4.1)


add_library(
             native-lib

             SHARED

             src/main/jni/mainactivity.cpp
             src/main/jni/art_method.h
             )



find_library(
              log-lib

              log )


target_link_libraries(
                       native-lib

                       ${log-lib} )