### Binder机制
> android 进程通信:Binder:最常用进程通信<br/>Ashmem(匿名共享内存):适用于传递大量数据(binder传递数据有1m的限制)
##### 分析linuxIPC机制
* 管道:
> 可用于具有亲缘关系进程间通信.内存中分配一块缓冲区,一个进程读,一个进行写,无消息是,读进程等待
	* 缺陷:缓存区大小受限,不适合andorid中大量的通信

* 消息队列
> 消息队列提供了一种从一个进程向另一个进程发送一个数据块的方法。 每个数据块都被认为含有一个类型，接收进程可以独立地接收含有不同类型的数据结构。我们可以通过发送消息来避免命名管道的同步和阻塞问题。但是消息队列与命名管道一样，每个数据块都有一个最大长度的限制.

	* 缺陷: 两次复制(用户态->内核态->用户态)
* 共享内存
> 共享内存就是允许两个不相关的进程访问同一个逻辑内存。共享内存是在两个正在运行的进程之间共享和传递数据的一种非常有效的方式。不同进程之间共享的内存通常安排为同一段物理内存。进程可以将同一段共享内存连接到它们自己的地址空间中，所有进程都可以访问共享内存中的地址
	* 优势:高效,无需复制,共享缓冲区直接附加到进程虚拟地址空间;
	* 缺陷:同步实现复杂;安全问题;所有app处于同一块内存存在安全隐患.
	
* 套接字:更通用的接口，传输效率低，主要用于不通机器或跨网络的通信；
* 信号量
#### Binder特性
* Binder内存限制是1m-8k,因此单次传输数据应小于改值(由于该内存块由多线程共享,Binder的线程池数量默认是15个,所以实际可用更小一般单次小于512k)
* 读写数据的IBinder驱动使用C++实现.
* 数据序列化使用Parcel,并实现了复用池
* Parcel.enforceInterface(),Parcel.writeInterfaceToken(DESCRIPTOR),用于数据校验.
* inout修饰符,作用于入参.in表示执行远程函数前写入数据;out表示执行远程函数后,读出对应数据赋给入参.因此,基本类型只能使用 in修饰.
* Stub初始化时调用this.attachInterface(this, DESCRIPTOR). this传递对象,用于同进程调用; Stub.asInterface(),检查本地是否有(同进程),有则使用,没有创建代理`Proxy`实例.	

#### 一次远程调用
> 前提: 客户端持有代理类`LocManager.Stub.Proxy`.服务端:自己实现的`LocManager.Stub`类
1. 客服端调用Proxy类的函数.入参序列化,阻塞等待数据返回.
2. 调用mRemote.transact().(方法实现在Binder.BinderProxy中),
3. 调用native方法transactNative.将数据写入 //todo:native函数暂未分析
4. native调用服务端的execTransact()->onTransact().调用实现的函数,将结果序列化.
5. native执行完函数.将`reply`(服务端序列化数据),发送给客户端.客户端收到数据,结束阻塞.

#### 服务绑定流程bindService
1. context.bindService() 最终实现在`ContextImpl`中
2.  ActivityManager.getService().bindService(),进入系统服务,由它来进行绑定服务.
	```
	//service,通过Singleton单例创建,实现类为:ActivityManagerService.
	IActivityManager am = IActivityManager.Stub.asInterface(b);
	```
3. ActiveServices.bindServiceLocked().
	* ActiveServices是系统服务中记录Service的类,因此bind操作通过它来找到/创建服务
4. ActiveServices.requestServiceBindingLocked()
5. ` r.app.thread.scheduleBindService(r, i.intent.getIntent(), rebind,
                        r.app.repProcState);` 方法实现在.ActivityThread$ApplicationThread.class
	* r:ServiceRecord位于system_server进程，是AMS管理各个app中service的基本单位。 ServiceRecord继承于Binder对象,作为Binder IPC的Bn端；Binder将其传递到Service进程的Bp端， 保存在Service.mToken, 即ServiceRecord的代理对象
6. 发送系统handler`BIND_SERVICE`.
7. ActivityThread.handleBindService
8. s.onBinder() 返回binder引用.s为服务端创建的Service.ActivityThread中用ArrayMap存储已存在的Service. 
9.  ActivityManager.getService().publishService(
                                data.token, data.intent, binder);
10. ActiveServices.publishServiceLocked()公开服务,
	```
		...
		// 调用该函数把binder塞到回调.
		c.conn.connected(r.name, service, false);
	```
10. 上述过程进程分析:1.客户端;2进入系统服务;34在系统服务;5.找到服务所在进程,进入该进程进行创建/绑定操作,678在服务端,9.进入系统服务,10.跨进程把binder返回客户端.



