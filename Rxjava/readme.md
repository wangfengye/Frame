## RxJava [UML](otherPackage/other.mdj)

* 泛型参数限制关键字
	* ? super T :所传参数必须是T或被T继承.
	* ? extends T: 所传参数必须是T或继承自T
	* PECS (Productor Extends Consum Super)
		* 获取数据 ? extends
		* 存数据 ? super
* Observable.create 
创建Observable对象,将被观察者对象复制
* Observable.subscribe开始执行

* 链式调用的本质, 最后一个Obserable.subscribe 开始调用链,递归找到链起点,开始执行.
##### map
* map修饰符本质是创建一个新的Observable,对上一个数据进行转换,取代上一个Observable,新的Observable或持有subscribe实现的匿名内部类对象.
* 无map ,Ob1 执行 OnSubscribe.call -> subscribe.onNext;
* 有map, Ob1 执行 OnSubscribe.call -> Ob2.call-> subscribe.onNext;

*subscribeOn() : 影响的是最开始的被观察者所在的线程。当使用多个 subscribeOn() 的时候，只有第一个 subscribeOn() 起作用


