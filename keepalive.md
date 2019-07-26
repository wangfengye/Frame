### 应用保活

kill app 原因
* 厂商优化,检测后台服务,杀进程
* android系统自身的回收机制,杀死优先级低占内存多的app

进程优先级
1. 活动进程 0
2. 可见进程,
3. 次要服务进程
4. 后台进程
5. 内容提供者:Provider
6. 空进程
	* 不含任何活动应用组件的进程
	* 用于缓存,缩短下次启动时间.
	
oom_adj
>进程优先级由adj值反映,它是linux内核分配.进程回收机制根据改值决定是否回收.
使用`cat /proc/进程id/oom_adj`查看进程adj值.

测试(原生)
|状态|oom_adj|
|!--!|!--!|
|前台|0|
|aidl调用的服务|16|
|后台服务|2|

保活策略
* 一像素Activity.监听开关屏,
	* Activity设置` android:excludeFromRecents="true"android:taskAffinity="包"`
*前台服务
*双进程守护(java层,不同进程的前台service)
* JobShcedu