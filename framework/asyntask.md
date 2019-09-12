#### AsynTask
> AsynTask 是模板模式在android中的典型应用.

* AsynTask使用了一个静态变量保存线程池,
	* 全局AsynTask共用一个线程池.
	* 线程设置: min =Math.max(2,Math.min(cpuCount-1,4)),max=2*cpuCount+1,以充分利用cpu,尽量保持每个核心都有个线程,由于io操作,存在阻塞浪费cpu,允许同时允许双倍于cpu数量的线程.
	* 阻塞有界队列容量为128,超出容量执行默认拒绝策略,抛异常
* 计数变量大量使用了 Atomic** 原子类操作.
* AsynTask && HandlerThread&& IntentService&& Loader
	* 多次启动AsynTask任务,是并行的,适合无关任务
	* HandlerThread:使用场景,子线程有异步回调的代码,,回调代码执行在异步的线程,想切回子线程必然要提供切换线程的功能,即handler机制.
	* Android中相机模块通过handler机制回调数据,当前线程无Looper,会使用主线程Looper,即使用无looper线程启动相机,回调会到主线程,有Looper线程回调会到启动相机的线程.
	* IntentService = HandlerThread+service+Intent,其中我们重写的`onHandleIntent`就是被handler的handleMessage调用
	* Loader 异步加载数据
		* 提供异步加载数据功能；
		* 对数据源变化进行监听，实时更新数据；
		* 在Activity配置发生变化（如横竖屏切换）时不避免数据重复加载；

* 被重写方法的执行逻辑 
 `onPreExecute()->doInbackground->onProgerssUpdate->onPostExecute`;
 
 * 入口函数`execute->excuteOnExecutor`,初始化操作,将任务放入线程池.开始执行
 * 在子线程中执行`doInbackground`.执行完成调用在`finally`执行`postResult`通过handler机制回到主线程调用`onProgerssUpdate`,`onPostExecute`等方法
 
 * `   Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)`
 执行子线程时 会设置自身的进程优先级
 