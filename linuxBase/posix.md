# 多线程
## POSIX 标准
* api包 pthread.h
* linux下 `man pthread_create`查看帮助文档
	* 若没有文档,`apt-get install manpages-posix-dev`进行安装.
* [子线程](nthread.c)
* [互斥锁](nthread2.c)
* [观察者模式:condition](nthread3.c)