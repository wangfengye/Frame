#### Android FrameWork[参考文献](http://gityuan.com/2016/10/09/app-process-create-2/)
* Java frameWork层服务都在(system_server进程)
	* AMS: Activity manager service
	* WMS: Window manager service
	* PMS: package manager service
	* 其他服务 电量等
* JNI FrameWork服务
	* 影视频播放
	* 页面刷新服务
* Zygote进程
	* native进程,开始名为'app_process',通过系统调用将自己名字改为Zygote;
	* 所有app进程由`Zygote` fork产生
	* 对外通过socket通信
	* Zygote由`init`进程孵化
* Zygote fork的常见进程
	* system_server, Java层framework的services依赖此进程.
	* com.android.phone 电话应用
	* android.process.acore 通讯录
	* android.process.media 多媒体应用
	* com.android.settings 设置进程
	* com.android.wifi Wifi应用
	* 桌面进程 (nexus上是: com.android.systemui)

##### app启动流程

1. 点击图标,点击事件由桌面应用进程接收,执行`startActivity()`
2. 通过Binder方式发送消息到AMS服务.
3. AMS检测目标进程是否存在,存在进入(6),不存在(4)
4. socket方式通知`Zygote`进程fork目标App进程
5 . app进程中,mian方法开始执行
	1. 实例化ActivityThread.
	2. 创建ApplicationaThread,Looper,Hander对象
	3. 调用attach方法进行binder通信
	4. looper启动循环,
	5. attch方法内部获取`ActivityMangerProxy`,其实现了`IActivityManager`接口,
		调用改接口将信息通知AMS.
6. 回到AMS, AMS调用`ActivityManagerNative.onTranscat`,核心逻辑在`IAppliccationThread`中.
通过代理对象与app进程通信(本质还是Binder)
7 . app进程调用`ActivityThread.bindApplication`向主线程发送消息,打开Activity.

[app启动流程 api调用图](start_process.jpg)

> android 四大组件都是通过类似方式进行同信信,创建.
	