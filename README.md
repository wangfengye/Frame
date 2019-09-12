# Frame
框架手写练习

#### [数据库框架](otherPackage/sqlite.md)


#### LiveDataBus
> 基于Android 新组件LiveData的事件总线,简化代码,减少第三方依赖, 
数据持有类,持有数据并提供数据监听的能力,与Lifecycle绑定,在生命周期内有限.
通信方案
* Handler
	* 原生,线程通信
	* 劣:高耦合,易泄露,代码复杂
* Broadcast
	* 简单
	* 低性能,传播数据有限,
* Interface
	*简单,快
	* 耦合严重.
* EventBus
	* 接入简单
	* 代码逻辑松散,易泄露
* Rxbus	
	* 效率高,无泄漏
	* 包大,学习成本高
* LivedataBus
	* 感知生命周期(Activity,Fragment,Service),基于官方LiveData.无泄漏,
	* 个人感觉进阶版RxBus(加入了生命周期感知,官方支持)

#### arouter
* 阿里ARouter路由框架.
* 组件化问题
    * 资源重名问题
       * gradle配置资源前缀resourcePrefix,只是对xml文件的警告,非强制,无法限制图片等资源
       * 抽出common基础组件用于统一管理资源,不同模块工作都会耦合到common.
       
#### [Tinker热修复](tinker.md)
    
#### AOP
* Java动态代理
    * 纯原生实现,思路清晰,代码量少
    * 解耦不如aspectJ彻底,(例如:调用必须自己建立代理类;
    注解传参,反射麻烦)
* AspectJ框架
    * notice
        * 必须使用对应的最新版,使用低版本可能build失败
    * 通过注解指定切面,插入切面代码,完成后使用简单
    * 编写切面相对复杂,需引入三方包
	* 静态织入扩展java编译器,在编译期根据定义的规则修改代码
* Spring aop
	* 动态织入
	* JDK 动态代理用于对接口的代理，动态产生一个实现指定接口的类，注意动态代理有个约束：目标对象一定是要有接口的，没有接口就不能实现动态代理，只能为接口创建动态代理实例，而不能对类创建动态代理。

	* CGLIB 用于对类的代理，把被代理对象类的 class 文件加载进来，修改其字节码生成一个继承了被代理类的子类。使用 cglib 就是为了弥补动态代理的不足。


#### 插件化 (mainApplication,pluginmain, plugincore)

> 核心: Java类加载机制. 通过代理ProxyActivity加载第三方activity. 难点(构造插件的资源管理器,插件activity方法必须使用ProxyActivity
的context,需要重写大量方法.)

实现过程
 * 创建DexClassLoader加载dex
 * 创建 Resource加载资源
 * 提供代理Activity 控制生命周期,提供展示路径
 
 存在问题
 * 第三方插件必须按规范编写,所有Activity必须基于BaseActivity.
 * 插件中用到的所content相关方法必须重写.
##### [APK优化](youhua.md)

##### [IOC(butterknife)](ioc.md)
##### [sopHix](sopHix.md)
##### [UI渲染](uiRender.md)
##### [EventBus实现]
> 全局单例,观察者模式, 注解标记,反射缓存方法,

#####[网络状态监听框架实现]
> 类似EventBus的实现;`ConnectivityManager.NetworkCallback`代替广播监听系统网络切换.


### 网络框架(OKhttp)
> 使用Java线程池,双队列,阻塞队列用于提交任务; 延时队列用于任务重试,
	分别开线程死循环获取两队列任务,提交线程池执行网络通信

### [应用保活](keepalive.md)
### [图片优化](skia.md)
### [Gif加载]
> 引用[Giflib](https://sourceforge.net/projects/giflib/)高效加载gif的库,提升Gif性能,练习JNI基础,
native部分主要通过库函数解析gif文件获取信息, 将像素信息填充到bitmap对应的C结构中.

* 测试看来,首次加载,性能优于Glide的默认加载.
* [基于giflib封装的一个andorid gif加载库](https://github.com/koral--/android-gif-drawable)

### 不规则图形的绘制
* 纯色图, 取像素颜色进行点击目标的区分
* `Region`判断点击区域.

#### [一键换肤](skin/Readme.md)
#### [HTTP/HTTPS协议](https.md)
#### [Ocr身份证号识别](Ioc/Readme.md)
#### [Android虚拟机](vm.md)
#### ANR
* 查看traces.txt(目录data/anr)中线程名,堆栈信息,线程专题,binder call等信息.(app anr,响应慢等时产生).
* traceview,系统性能分析工具,定位耗时操作.
* systrace - Android4.1新增的应用性能数据采样和分析工具

#### [FrameWrok之App启动](framework/framework.md)
#### [模板方法AsynTask解析](framework/asyntask.md)
#### [观察者模式 ListView](framework/listview.md)
#### [策略模式--ValueAnimator](framework/animator.md)
#### [Htttp框架,下载等](otherPackage/http.md)
#### [图片加载框架分析](otherPackage/imageLoader.md)
#### [Rxjava1.0框架分析](Rxjava/readme.md)
#### [FastJson框架分析](JsonFramework/readme.md)
#### [MVC,MVP,MVVM](mvp.md)
## 性能优化
#### [内存泄漏](oom.md)
#### [电量监控](电量监控.md)
#### [JobScheduler解析](jobScheduler.md)
#### [Handler机制](framework/handler.md)
#### [启动页优化](splash.md)