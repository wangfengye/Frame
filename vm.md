#### Dalvik/Art区别
* 垃圾收集优化
	* 一次Gc暂停(Dalvik两次:根标记,回收)让线程自己标记根.
	* gc暂停时并行处理
	* 对最近分配的短时对象的回收优化
	* 优化功效,GC_FOR_ALLOC几率更低
	* 压缩GC以减少后台内存使用及碎片
* 编译模式区别
	* JIT(即时编译) Dalvik(android2)
		* 每次启动时编译
		* 运行较为耗电
	* AOT(AheadOfTime 预编译)Art(android 5)
		* 安装时编译,安装升级耗时较长.编译后文件更大
		* 运行时无需编译
	* JIT,AOT 混用Art(android 7)
		* 第一次启动类似JIT编译(第一次启动较慢)
		* 运行中生成profile文件记录热点函数.
		* 手机空闲状态根据profile进行AOT编译.