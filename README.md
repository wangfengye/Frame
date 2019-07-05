# Frame
框架手写练习

#### 数据库框架
* 初步实现插入功能
@Entity注解表名称, @DbField 注解变量名,
BaseDao,初始化(打开数据库,建表,拿到数据库引用,根据注解获取字段名表名实现,增删改查操作.)
DaoFactory,获取各个类的操作对象,(缓存对象,防止重复创建)\

#### LiveDataBus
* 基于Android 新组件LiveData的事件总线,简化代码,减少第三方依赖

#### arouter
* 阿里ARouter路由框架.
* 组件化问题
    * 资源重名问题
       * gradle配置资源前缀resourcePrefix,只是对xml文件的警告,非强制,无法限制图片等资源
       * 抽出common基础组件用于统一管理资源,不同模块工作都会耦合到common.
       
#### hotFix(Tinker方式,冷启动修复;java层代码修复)
> 修复原理:classLoader加载机制,同名类默认加载第一个,android dex加载按dexElements顺序加载,由此通过反射等操作将修复的类打包
成dex文件,并将其插入dexElements首位,顶替被修复的类.

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

##### [APK优化](youhua.md)


