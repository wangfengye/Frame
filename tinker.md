#### hotFix(Tinker方式,冷启动修复;java层代码修复)
> 修复原理:classLoader加载机制,同名类默认加载第一个,android dex加载按dexElements顺序加载,由此通过反射等操作将修复的类打包
成dex文件,并将其插入dexElements首位,顶替被修复的类.(懒加载机制,dexElements数组按序加载.)

* 注意点    
    * 在进程最早的位置调用插桩代码,完成类替换
    * 测试方式:安装apk,修复apk,打包,将其中修改的dex,冲入sdcard根目录,点击加载文件,重启修复完成.
    * multiDexKeepFile 指定文本,配置主dex中的类,避免热修复替换不需要的这些类.
	* 单独打包 `dex  dx --dex  --output=[输出目录] [源文件目录]` dx命令在sdk/build-tools下,源文件需要包含包目录,即从com目录开始.新版使用d8替换了dx
	* d8指令会导致multiDex指定分包失效,选文档重新设置分包指定文档.
	* d8指令是对dx的优化,提升了20%的性能
* 分包问题
    * min=19时指定分包有效,min=21时无效todo
    * 指定文件后,会将该文件调用的部分类一同打入主包.
    * android.enableD8=false 必须设为false(gradle.properties)
    * 修改maindexlist后建议删除build重新编译
    * 分包测试使用的是gradle命令执行打包