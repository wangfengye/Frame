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

#### 插件化 
//todo:
> 通过代理ProxyActivity加载第三方activity.


