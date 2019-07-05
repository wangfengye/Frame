### IOC框架(Butterknife原理)

1. DIP(dependency inverse principle)
> 高层模块不应该依赖低层模块，二者都应该依赖其抽象；抽象不应该依赖细节；细节应该依赖抽象

2. IOC(inverse of control)
> 一种反转流,依赖,接口的方式,将控制权向上层转移,
(DIP的实现方式)

3. DI(dependency injection)
> 组件通过构造函数,setter方法,将依赖暴露给上层,上层
获取组件的依赖,并传递给组件;(IOC的实现方式)

4. IOC容器
DI框架,用于映射依赖,管理对象创建和生存周期

#### IOC版butterknife
* 注解传入布局id, Activity onCreate()中调用Inject处理注解,

* 新特性,注入事件, setOnclickListenter() 中的内部类通过动态代理,执行注入的方法.