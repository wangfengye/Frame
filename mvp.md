### MVP
> MVP核心思想:将activityzhong UI逻辑抽象出Iview接口.把业务逻辑抽象成Presenter    Model还是原来的Model

* MVC Model--View-- Control
* MVP Model--View--Persenter
* 所谓的MVP解决,内存泄漏问题.本质是:将原本持有activity引用通过presenter层做个隔离,onDestory将presenter中持有的activity引用释放. 所谓的解决只是通过这个隔离层,统一将对activity的引用释放掉.

* BaseActivity ,peresenter初始化抽象接口 ,Butterknife初始化.
* BasePresenter ,初始化MOdel,attchView(拿到View的引用) 
* BaseModel 

### MVVM
> Model -- View -- ViewModel 核心databinding;用ViewModel替代P层,使用Databinding技术,实现V和MV层交互, MV和M层交互没有改变.

* 数据绑定,导致异常传递,难以定位问题.
* 自动生成大量代码,调试困难,包增大,性能降低.
* 内存占用增加, 
* 编程过程依赖生成代码,经常需要rebuild,开发体验差
* databinding 使用代码生成技术生成代码,用tag标记xml中的控件,用于区分控件.

* 编译期将生成纯粹的xml布局,及绑定关系的记录文件(build\intermediates\data_binding_layout_info_type_merge\debug\mergeDebugResources\out\*.xml)

