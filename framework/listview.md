## ListView 观察者模式
* RecycleBin回收池
	* AbstractListView通过该类获取View.
	* 核心目的复用view,减少反射获取布局控件
	* activeViews.缓存的可见view//直接可用
	* scrapViews:缓存的移除屏幕的view.//缓存时会从父布局移除,刷新数据后可用,
	* attachViewToParent/addViewInLayout 两者都是将控件添加到父布局,前者性能更高,后者可执行动画等效果.
	
* 使用`itemView.setTag`保存`ViewHolder`,本质上是保存了itemView中对子View的引用,减少重复设置数据时,根据id寻址的时间消耗.