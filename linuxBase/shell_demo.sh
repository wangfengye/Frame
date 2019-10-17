#!/bin/bash
# 打印,shell脚本对空格敏感,不能随意加空格.
echo $PATH
#定义变量,使用时家$前缀
file=/maple/cmdtest/
ls -la $file
# $需要转义
echo \$
# 两种方式 执行命令将结果给text1.
text1=`date`
echo $text1
text2=$(date)
echo $text2
#休眠单位s
sleep 2
echo $text2

# 查找用户
if grep maple2 /etc/passwd
then
  echo "exist"
elif grep maple /etc/passwd
  then
    echo "maple exist"
else
  echo "disable"
fi
# 数值
# -eq	等于则为真
# -ne	不等于则为真
# -gt	大于则为真
# -ge	大于等于则为真
# -lt	小于则为真
# -le	小于等于则为真
a=10
if [ $a -gt 4 ]
then
  echo "a>4"
else
  echo "a<=4"	
fi
# 字符串
# =	等于则为真
# !=	不相等则为真
# -z 字符串	字符串的长度为零则为真
# -n 字符串	字符串的长度不为零则为真
str=mine
if [ -n $str ]
then 
  echo "mine"
else
  echo "null"
fi
# 文件判断 条件组合
# -e 文件名	如果文件存在则为真
# -r 文件名	如果文件存在且可读则为真
# -w 文件名	如果文件存在且可写则为真
# -x 文件名	如果文件存在且可执行则为真
# -s 文件名	如果文件存在且至少有一个字符则为真
# -d 文件名	如果文件存在且为目录则为真
# -f 文件名	如果文件存在且为普通文件则为真
# -c 文件名	如果文件存在且为字符型特殊文件则为真
# -b 文件名	如果文件存在且为块特殊文件则为真
bahsFile=/maple/cmdtest/t1.sh
if [ -f $bashFile ] && [ -x $bashFIle]
then
 echo 't1存在'
else
 echo 't1 is not exist'
fi
# 数学运算符 (())包含
if ((5>4))
then
  echo '(())运算符'
fi

# case 命令
caseValue=2
case $caseValue in
1)
echo $caseValue case 1
;;
2)
echo $caseValue case 2
;;
esac

# for循环,数组
array_name="a b c d"
for var in $array_name
do
   echo "$var show"
done
array2="a--b--c"
IFS=$'--'
for var in $array2
do
  echo "$var fsdf"
done
# '|' 管道, 符号前的输出作为符号后的操作的输入
# 浮点运算 使用bc计算器,需要下载bc
e=$(echo "scale=4; 10 / 3" |bc)
echo $e

# 内联输入重定向,使用多个输入
f=$(bc << EOF
scale=4
f1 = (3*2)
f1/3
EOF
)
echo f=$f

# 命令 判断
if ls -la
then
echo "列表打印成功:"
fi
# 纯字符拼接用'/'隔开变量,/会被输出
NDK=a
S=$NDK/b
echo $S
# expr 表达式, 数值,字符串计算
echo $(expr 5/3)
ech $(expr length 'sdsd')
# shell中各种括号区别(https://blog.csdn.net/tttyd/article/details/11742241)

