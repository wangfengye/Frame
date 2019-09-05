## JobScheduler

* 常用类: `JobScheduler`,`JobInfo`,`JobService`;
* JobScheduler 本质是系统服务.涉及到跨进程通信
```
进程1:
	MyService{
		MyBinder extends MyInterface.Stub{
			a(){//todo}
		}
	}
	MyInterface.aidl{
		a();
	}
进程2:
	MyInterface.aidl{
		a();
	}
	bindservice(Service, new ServiceConnection(){
		onServiceConnnected(IBinder binder){
			MyInterface interface=binder.asInterface();
		}
	}
```
* mTrackedJobs存储任务列表.
* `ConnectivityController` 开广播监听网络状态变更.,变化时`updateTrackeedJobs()`尝试执行任务.
* `StateController`是各种控制器的基类.
* `mPendingJobs`延时任务队列.
* `JobSrviceContext.class`最终和执行任务的服务通信,通过aidl调用`service.startJob(mParams)`
* 总结:任务在用户进程提交.jobScheduler 进程储存任务列表,通过各种控制器,选择何时的时间,启动任务,通过aidl通知用户进程的service执行任务.
#### JobSchedulerService启动.
1. zygote进程--Linux核心
2. 启动系统进程SystemServer,开启一系列关键服务,AMS/PMS/WMS/JobSchedulerService.
3. `/data/system/job/jobs.xml`读取重启保留任务
```
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<job-info version="0">
    <job jobid="1" package="com.maple.mvvm" class="com.maple.mvvm.MainActivity$JobAction" sourcePackageName="com.maple.mvvm" sourceUserId="0" uid="10098" priority="0" flags="0">
        <constraints idle="true" charging="true" />
        <one-off />
        <extras />
    </job>
</job-info>

```


