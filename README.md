# Frame
框架手写练习

#### 数据库框架
* 初步实现插入功能
@Entity注解表名称, @DbField 注解变量名,
BaseDao,初始化(打开数据库,建表,拿到数据库引用,根据注解获取字段名表名实现,增删改查操作.)
DaoFactory,获取各个类的操作对象,(缓存对象,防止重复创建)\

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
       
#### hotFix(Tinker方式,冷启动修复;java层代码修复)
> 修复原理:classLoader加载机制,同名类默认加载第一个,android dex加载按dexElements顺序加载,由此通过反射等操作将修复的类打包
成dex文件,并将其插入dexElements首位,顶替被修复的类.(懒加载机制,dexElements数组按序加载.)

* 注意点    
    * 在进程最早的位置调用插桩代码,完成类替换
    * 测试方式:安装apk,修复apk,打包,将其中修改的dex,冲入sdcard根目录,点击加载文件,重启修复完成.
    * multiDexKeepFile 指定文本,配置主dex中的类,避免热修复替换不需要的这些类.
* 分包问题
    * min=19时指定分包有效,min=21时无效todo
    * 指定文件后,会将该文件调用的部分类一同打入主包.
    * android.enableD8=false 必须设为false(gradle.properties)
    * 修改maindexlist后建议删除build重新编译
    * 分包测试使用的是gradle命令执行打包
    
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
