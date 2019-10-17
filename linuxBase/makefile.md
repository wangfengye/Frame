# Makefile
> 构建工具

## C编译(Makefile控制这个过程)
1. gcc 将.c文件生成 .o文件
2. 链接库,得到可执行程序

### 一个 c文件编译
1. 编写.c文件
2. 编译 `gcc -c .c文件`
3. 生成可执行程序 `gcc -o 生成的程序名 .o文件列表`(该过程由于项目存在依赖关系,多文件需要同时执行)
4. 执行 echo $(./程序), echo作用是打印输出.


### Makefile 三要素
> 目标,依赖,命令;目录下使用make命令会自动搜索当前目录下Makefile文件
```
myapp: main.o divi.o minus.o multi.o plus.o
	gcc main.o divi.o minus.o multi.o plus.o -o myapp
main.o: main.c
	gcc -c mian.c
divi.o: divi.c
	gcc -c divi.c
minus.o: minus.c
	gcc -c minus.c
multi.o: multi.c
	gcc -c multi.c
plus.o: plus.c
	gcc -c plus.c
```
### Makefile样例
* [基础样例](Makefile1)
* [进阶样例变量,函数等](Makefile2) 
### android.mk
* `LOCAL_PATH := $(call mydir)`,调用my-dir函数,返回Andorid.mk所在目录,ndk中的函数
* `include $(变量)`引入其他的makefile文件.
* `inclue $(CLEAR_VARS)` 清空变量.
* LOCAL_MODULE 模块名称
* LOCAL_SRC_FILES 编译需要的源文件
* LOCAL_C_INCLUDES 需要的头文件
* LOCAL_SHARED_LIBRARIES 编译需要的动态库

