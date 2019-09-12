### 提升应用的启动速度 和 splash页面的设计
* 启动方式
	* 冷启动:走创建进程开始
	* 热启动: app退出不会直接杀进程,再次启动时会复用该进程跳过application初始化,直接打开activity;
	
* 测试启动速度
	* `adb shell am start -W [PackageName]/[PackageName.入口Activity]`
	* 结果
	*  ThisTime: 165 指当前指定的MainActivity的启动时间
	* TotalTime: 165 整个应用的启动时间，Application+Activity的使用的时间。
	* WaitTime: 175 包括系统的影响时间
	
* 时间消耗:Application初始化 + MainActivity的界面加载绘制时间。
* 优化
	* 添加Splash用于全局初始化的的展示,Splash使用Fragment,轻量
	* 主View 通过 ViewStub 延时加载
	