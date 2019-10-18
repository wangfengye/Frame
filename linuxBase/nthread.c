#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>

void* thr_fun(void* arg){
 char* no = (char*)arg; 
 printf("args:%s\n",no);
 //线程退出 
 pthread_exit(22);
 return 1;
}

void main(){
  printf("main thread\n");
  pthread_t tid;
 //线程id,线程属性,runable,标志
  pthread_create(&tid,NULL,thr_fun,"1");
  int rval=0;
  //等待线程退出.
  pthread_join(tid,&rval);
  printf("main-rval:%d\n",rval);
}

