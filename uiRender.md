#### UI布局渲染,

> 栅格化: 将向量图形格式转换成位图用于显示.<br/>
  VSYNC: android刷新机制,垂直同步机制,每16ms,将图像绘制一次.卡顿本质是当vsync信号发出后16ms,上层没有计算好要绘制的图像,那绘制进程只能绘制上一次的图像,造成视觉上的卡顿.

##### APP卡顿
* 主线程阻塞,IO,sqlite,等主线程耗时操作
* gc问题,
	* 内存抖动问题,频繁创建对象,导致频繁gc
	* gc回收耗时过长

##### xml布局显示到屏幕流程 (android单次渲染16ms)

1. xml文件通过 LayoutInflater  加载到内存
2. cpu进行计算 生成向量图
3. 通过OPENGL 调用GPU栅格化绘制图形

> 16ms由来,当画面超出60fps,人类无法感知交互过程中的卡顿.Android卡顿原因即当vsync信号发送时,上述绘制流程未全部完成,导致该画面延后显示.或丢失.

* 16ms消耗分析
	* UI对象转换成多边形及纹理
	* CPU传递数据到GPU,GPU进行绘制
* 优化
	* 减少xml转换对象时间
	* GPU减少重复绘制

过度绘制
> GPU根据CPU传递的指令进行绘制,对覆盖的图形也进行绘制,浪费操作

* 布局层级过深,有多重图形重复(布局文件添加背景会被绘制)
* 自定义中,onDraw绘制过多



##### 常见措施
* 减少布局背景
* `android:windowBackground=@null` 取消window背景,减少层级,会导致启动黑屏
* 自定义View中,通过裁剪canvas,将重叠部分裁减掉,减少重复绘制
```
        Canvas canvas = null; 
        canvas.save();
        canvas.clipRect(new RectF(0,1,0,1));
        //canvas绘制
        canvas.restore();
```

* 调试模式过度绘制分析工具
* AS -> tools -> layout inspector