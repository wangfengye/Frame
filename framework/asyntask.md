#### AsynTask
> AsynTask 是模板模式在android中的典型应用.

* AsynTask使用了一个静态变量保存线程池,
	* 全局AsynTask共用一个线程池.
	* 线程设置: min =Math.max(2,Math.min(cpuCount-1,4)),max=2*cpuCount+1,以充分利用cpu,尽量保持每个核心都有个线程,由于io操作,存在阻塞浪费cpu,允许同时允许双倍于cpu数量的线程.
	* 阻塞有界队列容量为128
* 计数变量大量使用了 Atomic** 原子类操作.

* 被重写方法的执行逻辑 
 `onPreExecute()->doInbackground->onProgerssUpdate->onPostExecute`;
 
 * 入口函数`execute->excuteOnExecutor`,初始化操作,将任务放入线程池.开始执行
 * 在子线程中执行`doInbackground`.执行完成调用在`finally`执行`postResult`通过handler机制回到主线程调用`onProgerssUpdate`,`onPostExecute`等方法
 
 * `   Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)`
 执行子线程时 会设置自身的进程优先级