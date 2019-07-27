
# 指定cmake版本
cmake_minimum_required(VERSION 3.4.1)

# 将nativ-lib.cpp,b.cpp编译为名为demo的可执行文件,(android编译so用不到)
#add_executable(demo native-lib.cpp b.cpp)

# 批量加入源文件,查找指定目录(dir)下的所有源文件,将结果存入varible
# aux_sourc_directory(<dir> <varible>)

# 添加子目录math,处理math下的cmakeLists.txt.
# add_subdirectory(math)

find_library(jnigraphics-lib jnigraphics)

add_library(
        jpegbither
        SHARED
        IMPORTED
)
 set_target_properties(jpegbither
         PROPERTIES IMPORTED_LOCATION
       ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libjpegbither.so)

# 生成链接库
# add_library(<name> [STATIC | SHARED | MODULE]
#       [EXCLUDE_FROM_ALL]
#        [source1] [source2] [...])
add_library(
        native-lib
        SHARED
        src/main/cpp/native-lib.cpp

)
include_directories(
        src/main/cpp/
)
# find_library (<VAR> name1 [path1 path2 ...])
find_library(
        log-lib

        log)



# 添加链接库
# target_link_libraries(<target> [item1] [item2] [...]
#        [[debug|optimized|general] <item>] ...)
target_link_libraries(
        native-lib
        jpegbither
        ${log-lib}
${jnigraphics-lib})