# Frame
框架手写练习

#### 数据库框架
* 初步实现插入功能
@Entity注解表名称, @DbField 注解变量名,
BaseDao,初始化(打开数据库,建表,拿到数据库引用,根据注解获取字段名表名实现,增删改查操作.)
DaoFactory,获取各个类的操作对象,(缓存对象,防止重复创建)\

#### LiveDataBus
* 基于Android 新组件LiveData的事件总线,简化代码,减少第三方依赖

#### 插件化 
//todo:
> 通过代理ProxyActivity加载第三方activity.


