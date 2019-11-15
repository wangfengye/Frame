# OOM
> 参考美团Probe
## OOM原因
	* java堆内存溢出
	* 无足够连续内存空间
	* FD数量超出限制
	* 线程数量超出限制
	* 虚拟内存不足
### 具体分析

* OOM异常抛出源码位置`art/runtime/thread.cc`;
* java堆分配失败`/art/runtime/gc/heap.cc`
	* java堆内存资源耗尽
	* 创建大对象时,无连续的足够空间,一般是由于存在大量内存碎片时会出现的异常:源码位置`art/runtime/gc/allocator/rosalloc.cc`
	```
	//异常样例
	failed due to fragmentation (required continguous free "<< required_bytes << " bytes for a new buffer where largest contiguous free " <<  largest_continuous_free_pages << " bytes)
	```
	
* JAVA线程创建分两步.
	1. 创建JNIEnv时,异常`/art/runtime/thread.cc`
		* 通过Android匿名共享内存,分配4k内核态内存.步骤需要打开/dev/ashmem文件,需要FD.若FD数达上限,抛出异常.
			```
			E/art: ashmem_create_region failed for 'indirect ref table': Too many open files
			java.lang.OutOfMemoryError: Could not allocate JNI Env
			at java.lang.Thread.nativeCreate(Native Method)
			at java.lang.Thread.start(Thread.java:730)
			```
		* 通过mmap调用映射到用户态虚拟地址空间.mmap需要进程虚拟地址空间,若耗尽抛出异常
			```
			E/art: Failed anonymous mmap(0x0, 8192, 0x3, 0x2, 116, 0): Operation not permitted. See process maps in the log.
			java.lang.OutOfMemoryError: Could not allocate JNI Env
			at java.lang.Thread.nativeCreate(Native Method)
			at java.lang.Thread.start(Thread.java:1063)
			```
	2. 创建线程
		* mmap分配栈内存(匿名内存映射,虚拟内存),
		```
			W/libc: pthread_create failed: couldn't allocate 1073152-bytes 	mapped space: Out of memory
			W/tch.crowdsourc: Throwing OutOfMemoryError with VmSize  4191668 kB "pthread_create (1040KB stack) failed: Try again"
			java.lang.OutOfMemoryError: pthread_create (1040KB stack) failed: Try again
			at java.lang.Thread.nativeCreate(Native Method)
			at java.lang.Thread.start(Thread.java:753)
		```
		* clone方法进行线程创建,会检测进程线程上限,配置目录`/proc/sys/kernel/threads-max`
		```
			W/libc: pthread_create failed: couldn't allocate 1073152-bytes mapped space: Out of memory
			W/tch.crowdsourc: Throwing OutOfMemoryError with VmSize  4191668 kB "pthread_create (1040KB stack) failed: Try again"
			java.lang.OutOfMemoryError: pthread_create (1040KB stack) failed: Try again
			at java.lang.Thread.nativeCreate(Native Method)
			at java.lang.Thread.start(Thread.java:753)
		```
### Probe 处理方案
> 内存溢出爆出的异常往往只是最后一根稻草,要想处理这个问题往往需要完整的内存快照.

* 采用HAHA库解析分析Java内存快照
* 减少分析进程的内存占用:对同类型实例,设置了阈值,超出阈值的只计算数量,不计内容.
* 优化分析链路,优先分析同类实例,链路最深的对象,
* 为了解决HAHA算法中检测不到基础类型泄漏的问题，我们在遍历堆中的Instance时，如果发现是ArrayInstance，且是byte类型时，将它自身舍弃掉，并将它的RetainSize加在它的父Instance上，然后用父Instance进行后面的排序
* 客户端日志采集,为防止oom后日志缺失,设定预警值,提前保存快照
* 上传,优化dump日志大小,去除基本类型数值,上传后分析是用0填充.去除逻辑通过hook插入到日志写入文件的操作
		
	