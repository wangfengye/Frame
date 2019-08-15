## Animator
1. ObjectAnimator.ofFloat 创建动画
2. PropertyValueHolder.ofFloat 实例化FloateProtyValuesHolder.
3. FloatePropertyVlaueHolder 封装动画执行类
4. 调用setFloatValues 方法.
5. KeyframeSet.ofFloat 可变参数关键帧
6. Keyfame.ofFloat 实例化一帧画面

7. ObjectAnimator-->start 开启动画
8. 调用父类的start方法,获取线程单例AnimationHandler,插入`doAnimationFrame`回调,在该线程回调时执行,改线程16ms执行一次.
9. doAnimationFrame-->ValueAnimator.animateBasedOntime, 计算动画执行百分比
10. ObjectAnimator.animateValue 获取插值器,变换百分比
11. ProteryValueHolder.calculate();给mAnimatorValue变量赋值.
12. ObjectAnimator.initAnimator()(重点:先调用子类方法,再调用父类方法).

#### note
* ObjectAnimtor 中'scaleX'是通过反射遍历`view`中方法名进行名称匹配找到最终执行方法的.
* Choreographer Choreographer是线程级别的单例，并且具有处理当前线程消息循环队列的功能。Choreographer控制将绘制工作统一到vsync之后.(即接收信号,开启一次绘制)
	