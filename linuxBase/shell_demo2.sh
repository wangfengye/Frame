#!/bin/bash
filename=$(basename $0)
# 内建参数(即输入命令后面跟着的参数)
echo $0
echo $1
echo $2
echo $3

# 遍历参数
echo "遍历入参"
for param in "$@"
do 
  echo "param:$param"
done
# 指定输出文件
# 命令行指定输出目录 命令 &> 文件名
echo "输出重定向"> test1
# 标准输出
echo "a" >&1
# 标准错误输出
echo "b" >&2

# 永久重定向输出, 1,是标准,2是标准错误,之后的数字是自定义,输出时需要指定 >&数字
# exec 1>test2
echo "test2"
ls -l

# 函数
function add
{
  echo $[$1 + $2]
}
value=$(add 10 23)
echo "value=$value"
show() { 
echo "$1/show"
}

echo $(show first)

# 引入脚本,可调用该脚本函数
# source ./another.sh 
# . ./anothersh .可以替换source
