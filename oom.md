## 性能优化

#### 内存泄漏.
> 生命周期长的对象持有短的对象,导致,本该被回收的对象,不能及时回收.

* 内存抖动,频繁创建变量对象,导致gc频繁,造成性能降低,卡顿.生成大量内存碎片,效率降低.

##### 内存分配策略
1. 静态
	* 静态存储区,内存在编译时已分配好,运行期一直存在.主要存放静态数据,常量.
2. 栈
	* 执行函数时,函数内部变量的存储,函数执行结束自动释放.
	* 分配运算快,内置于处理器,容量有限.常见栈溢出异常
	* 栈式连续的内存区域,大小由操作系统决定.
3. 堆
	* 动态内存分配,用 malloc 或new申请分配内存.c/c++需要自己负责释放(java使用gc)
	* 堆是不连续的内存区域.
	
###### 特性
* 成员变量全部存储在堆中(包括基本数据类型,引用,及应用的对象实体)本质原因类对象存储在堆区.
* 局部变量基本数据类型,引用在栈区,实例化的对象在堆区.
* 处理占用内存大的对象,尽量使用软引用,弱引用.
* 无限循环动画未关闭

###### 常见泄漏
* 静态变量持有引用.
* 非静态内部类引起, 内部类依赖于外部类的实例,导致泄漏.
	* 改为静态内部类.
* 单例持有`activity`,`context`等
* handler
* 设置监听,回调;因此大部分回调需要在页面销毁时注销(Handler,广播等)
###### 分析技巧
* 横竖屏切换,反复使用同一功能,看内存变化,及是否产生大量同一类型变量
* 看内存生命周期变化是否合理
* 手动gc,对比两侧的.hprof分析.
* 退出app ,查看是否仍有view,activitys实例存在
###### 分析工具
* Android Memory monitor androidStudio自带(Android Profiler)
* eclipse memory analyzer (MAT)分析.hprof文件图形化展示内存占用,并提供内存隐患(消耗大量内存的栈信息),提供快照对比功能.
	* Dump 内存快照hprof,分析内存异常的类
	* 进入Histogram过滤某一个异常类
	* 分析持有此类对象引用的外部对象(右键list objects -->with incoming references)
	* 过滤软弱虚引用.(右键merge shortest paths to gc roots-->exclude all phantom...)
	* 逐个分析gc路径是否正常,对照代码找出异常点.
* Allation Tracking(Android Profiler中.alloc) 追踪内存分配信息,可以直观看到某个操作的内存逐步分配.
* android device monitor as已移除(被Android Profiler取代),进入sdk tools目录下 cmd执行monitor开启
* LeakCanary 检测activity泄漏,可扩展fragment泄漏检测.
* Lint 静态代码分析.提供代码规范建议
	* 也会提供可能存在的内存泄漏信息, analyze->inspect code 生成目录下,android-lint-performance-static field leaks.