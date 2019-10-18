# gdb调试

1. 编译时添加`-g`: `gcc *.c -g -o demo`
2. 进入调试 `gdb 程序`
3. 开始执行 `start`
4. 显示代码 `list`简写`l`
	* 查看函数内容 `list 函数名称`
	* 查看某行 `list 行号`
5. 下一步 `next`简写`n`
6. 查看变量  `print 变量名`
7. 进入函数 `step 函数名`
8. 设置断点 `break gdb中的行号`
9. 运行到下一个断点 `continue` 简写`c` 
10. 查看断点信息 `info breakpoints`
11. 删除断点 `delete breakpoints 断点编号`
12. 修改变量值 `set var 变量=值`
13. 程序调用堆栈 当前函数之前的所有已调用函数列表，每一个都分配一个“帧”，最近调用的函数在0号帧里
`backtrace`-简写`bt`
14. 切换栈帧 frame 1
15. 自动显示/取消  `display 变量名` `undisplay 自动显示的行号`
16. 查看内存布局 `x /20 地址`地址开始.20个元素
## 常见问题
###程序非正常退出，如何查看错误？
1.ulimit -a 查看core文件是否分配大小
2.ulimit -c 1024 创建的core文件大小为1024字节
3.gcc test2.c -g -o test2 编译链接得到带有-g选项的可执行程序
4../test2 执行程序，会生成core前缀的日志文件
5.gdb test2 [core文件] 打开日志文件，定位错误信息