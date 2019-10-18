#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
int ready=0;
pthread_mutex_t mutex;
pthread_cond_t has_product;
void* producer(void* arg){
 printf("producer start\n"); 
int i;
 int no = (int)arg;
 for(i=0;i<5;i++){
   pthread_mutex_lock(&mutex);
   ready++;
   printf("produce:%d\n",no);
  // 通知消费者
   pthread_cond_signal(&has_product);
   printf("flush cond:%d\n",no);
   pthread_mutex_unlock(&mutex);
   sleep(1);
 }
}
void* consumer(void* arg){
  int num=(int)arg;
 for(;;){
  pthread_mutex_lock(&mutex);
  while(ready==0){//循环避免`superious wake`(惊群效应)导致的pthread_cound_wait 获取锁
    printf("watting: %d\n",num);
   // 开始等待, 释放锁, 等pthread_cond_signal通知,重新申请锁.	 
   pthread_cond_wait(&has_product,&mutex);
  }
  ready--;
  printf("consume: %d\n",num);
  pthread_mutex_unlock(&mutex);
  sleep(1);	
 }
}
void main(){
  pthread_t tid_p,tid_c;
  pthread_mutex_init(&mutex,NULL);
  pthread_cond_init(&has_product,NULL);
  printf("main-init\n");
  pthread_create(&tid_p,NULL,producer,(void*)1);
 
  pthread_create(&tid_c,NULL,consumer,(void*)2);
 sleep(10); 
 pthread_join(tid_p,NULL); 
 pthread_join(tid_c,NULL);
 pthread_mutex_destroy(&mutex);
 pthread_cond_destroy(&has_product);
}
