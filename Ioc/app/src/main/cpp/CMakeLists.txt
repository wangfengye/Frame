

cmake_minimum_required(VERSION 3.4.1)


include_directories(include)

file(GLOB SOURCES ${CMAKE_SOURCE_DIR}/*.cpp ${CMAKE_SOURCE_DIR}/*.c)
add_library(
        card-ocr
        SHARED
        ${SOURCES}
)

add_library(
        lib_opencv
        SHARED
        IMPORTED
)
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jnilibs/${ANDROID_ABI}/libopencv_java4.so)


find_library(log-lib log)
find_library(jnigraphics-lib jnigraphics)


target_link_libraries(
        card-ocr
        lib_opencv
        ${log-lib}
        ${jnigraphics-lib}
)