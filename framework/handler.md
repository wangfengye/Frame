## Handler.

* 消息入队: Handler.newInstance.sendMessage-> queue.enqueueMessage;
* 消息出队: Looper.loop -> queue.next
* MessageQueue:消息队列
* activity中的onCreate,onResume等执行都是通过Handler机制调用的.

### 流程
1. Looper.prepare() 初始化Looper.ActivityThread启动时调用.
2. Loop.loop(); 开始轮询
3. 获取到message,通过message.target.dispatch 分发.
4. 回到handler的handleMessage


### [java层自实现Handler逻辑](https://github.com/wangfengye/DesignPattern/tree/master/src/handler)