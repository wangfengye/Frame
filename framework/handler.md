## Handler.


### 流程
1. Looper.prepare() 初始化Looper.ActivityThread启动时调用.
2. Loop.loop(); 开始轮询
3. 获取到message,通过message.target.dispatch 分发.
4. 回到handler的handleMessage


### [java层自实现Handler逻辑](https://github.com/wangfengye/DesignPattern/tree/master/src/handler)

#### 机制特性

* 消息入队: Handler.newInstance.sendMessage-> queue.enqueueMessage;
* 消息出队: Looper.loop -> queue.next
* MessageQueue:消息队列
* activity中的onCreate,onResume等执行都是通过Handler机制调用的.
* `android.os.Trace`用于产生数据追踪文件.
* Handler 分类
	* 系统`Handler`,`ActivityThread`中变量`mH`,继承`自Handler`的类`H`
		* 用于接收四大组件的生命周期调用.
	* 应用`Handler`
#### Message复用
* Message.obtain() 方法复用池中对象.核心是回收时机, loop()函数中执行完handler.HandleMessage后,回收Message.
* messageQueue中Message采用链表形式.(message中隐藏字段next),新msg入队时,
会遍历队列,找到执行时间在插入msg执行时间之后的节点,把msg插入该节点之前

#### 问题记录
* app强制退出时,需要关闭所有Activity,再退出进程,否则会被认为异常退出,自动重启.所以我在主Activity的destroy中执行关闭所有Activity的操作,退出时finish主Activity,关闭进程.结果关闭失败
> reason: finish操作调用生命周期是通过Handler机制异步调用.finish后直接杀进程,而handler机制导致finish发出的消息在当期代码块执行完后才会调用,而当前代码块直接干掉了进程,后续代码无法执行.此外,系统Handler中,接收`EXIT_APPLICATION`指令Looper会直接退出,其中尚未执行的消息,不会执行.