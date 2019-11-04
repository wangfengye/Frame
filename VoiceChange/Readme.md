## [FMOD](https://www.fmod.com)
1. 下载下来的示例程序中找出需要的头文件(fmodstudioapi20004android\api\core\inc)和示例cpp文件(modstudioapi20004android\api\core\examples)
2. 复制fmod.so,fmodL.so库
3. 修改cmake将c文件及so加入编译
4. 引入jar库,复制示例MainActivity;
5. 删除不需要的so库加载,修改com_platform.cpp中jni接口的包名
6. 复制 assets音频资源.

### 不同的示例代码
* 示例代码中包含入口函数`FMOD_Main`,只导入一个包含该函数的cpp即可执行,
    * 查看不同示例,需要引入不同的包含入口函数的cpp文件



#### 坑
1. 未授予读取文件权限,Cpp运行时报错.