#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
int i=0;
pthread_mutex_t mutex;
void* thr_fun(void* arg){
	char* no =(char*) arg;
	//pthread_mutex_lock(&mutex);
	for(;i<5;i++){
		printf("%s thread,i.%d\n",no,i);
		sleep(1);
	}
	i=0;
	//pthread_mutex_unlock(&mutex);
}
void main(){
 pthread_t tid1,tid2;
 //初始化互斥锁
 pthread_mutex_init(&mutex,NULL);

pthread_create(&tid1,NULL,thr_fun,"NO1");
pthread_create(&tid2,NULL,thr_fun,"NO2");
pthread_join(tid1,NULL);
pthread_join(tid2,NULL);
//销毁锁
pthread_mutex_destroy(&mutex);
}
