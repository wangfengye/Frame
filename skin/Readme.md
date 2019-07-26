## APP换肤

#### 切换主题
> 通过切换主题Theme 实现,主题内容需要直接编写到主app theme中,apk过大,无法动态新增主题,不支持实时切换(需重新加载页面),但实现简单
适合夜间模式这种变化不大的主题切换.

#### 加载资源包.
>知识点: `PackageInfo`获取, 反射构造皮肤包的`Resources`;`getIdentifier()`获取指定包下的资源,
Android加载控件的机制,(LayoutInflater.Factory2).<br/>因此我们可以通过重写
Factory2的View创建函数,获取所有view及其属性,将我们获取到的皮肤包资源设置的获取的View中,实现换肤功能.<br/>
监听View创建也可以通过`LayoutInflaterCompat.setFactory2(getLayoutInflater(), myFactory2);`的方式实现


