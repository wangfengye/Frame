## Window
> Window适用于盛放View的容器.Android实现了PhoneWindow.

### window类型
* System window: 键盘,Toast,通话等.
* Application window: activity
* sub window: dialog, 该类型必须依赖一个父Window
### 源码日志
* winow类型用`type`标记,类型用int区分.0-1000:application windows;1000-2000:SUB WINDOW;:2000+:SYSTEM_WINDOW.里面还有每个数值段还有细分类.
* WINDOW加载最终实现类,`WindowManagerImpl`,由系统创建,通过getSystemService调用

### Application window 创建流程
1. 入口 Activity.startActivityForResult()
2. Instrumentation.execStartActivity() 处理Activity实现的类.
3. ActivityManager.getService().startActivity():ActivityManager是个单例
4. 跨进程暂未查看
5. ApplicationThread.scheduleLaunchActivity(): Activity生命周期由它触发
6. 发送系统handler`H.LAUNCH_ACTIVITY`
7. ActivityThread.handleLaunchActivity():接收到事件 开始创建
	1. WindowManagerGlobal.initialize().初始化
	2. performLaunchActivity()启动的核心实现.构造`ActivityInfo`,`ComponentName`,'context`等信息.加载对应的Activity类.初始化.
		1. 调用 Activity.attach()初始化Window.
	3. 在Activity调用setContentView时,最终调用的是window的setContentView,如果没有根布局会调用installDecor()创建根布局.这个流程在onCreate中.
	4. handleResumeActivity()
		1.WindowManagerImpl wm = activity.getWindowManager. activity中创建一个本地的Window管理器.
		2. wm.addView(decor,l),l是窗口类型, 
		3. WindowManagerGlobal.addView(). 通过7 初始化的WindowManagerGlobal.addView;
			* tip:重符setContentView(),会在这一步抛出异常.
		4. 初始化ViewRootImpl,,通过aidl获取远程服务WMS的获取WindowSession(openSession)
		5. ViewRootImpl.setView()->WindowSession.addToDisplay()-远程调用WMS->WindowManagerService.addWindow().其中有个WindowState,存储Window状态.



1. ActivityThread WMS(WindowManagerService)
2. 创建Activity.
3. 调用 attach,初始化window.
4. 初始化window,会调用installDecor创建跟布局
	* activity中setContentView,最终调用的是window的setContentView,将布局植入根布局
		1. preparePopup().创建DecorView.
		2. invokePopup()-> mWindowManager(decorView,p)将DecorView,通过WindowManger发送的WMS.
