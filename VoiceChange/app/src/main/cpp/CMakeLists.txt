# gunstl_static //支持C++处理,标准模块
cmake_minimum_required(VERSION 3.4.1)


file(GLOB fmod_c "*")
add_library( # Sets the name of the library.
             native-lib

             SHARED

             native-lib.cpp
            ${fmod_c})



find_library(
              log-lib
              log )

add_library(fmod SHARED IMPORTED)
set_target_properties(fmod PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libfmod.so)

add_library(fmodL SHARED IMPORTED)
set_target_properties(fmodL PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libfmodL.so)
target_link_libraries( native-lib

                        fmod
                        fmodL
                       ${log-lib} )