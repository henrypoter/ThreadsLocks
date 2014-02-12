package threadlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public interface Chapter16 {
void run();
}
class RunnableThreadExample implements Chapter16, java.lang.Runnable{
	public int count=0;
	public void run(){
		System.out.println("RunnableThread starting.");
		try{
			while(count<5){
				Thread.sleep(500);
				count++;
			}
		}catch(InterruptedException exc){
			System.out.println("RunnableThread interrupted.");
			
		}
		System.out.println("RunnableThread terminating.");
	}
	
	
	
}

class Test {
	public static void main(String[] args){
		RunnableThreadExample instance=new RunnableThreadExample();
		Thread thread=new Thread(instance);
		thread.start();
		//waits until above thread counts to 5(slowly)

	while(instance.count!=5){
		try{
			Thread.sleep(250);
		}catch(InterruptedException exc){
			exc.printStackTrace();
		}
		
	}
	
	MyObject obj1=new MyObject();
	MyObject obj2=new MyObject();
	MyClass thread1=new MyClass(obj1, "1");
	MyClass thread2=new MyClass(obj2,"2");
	
	thread1.start();
	thread2.start();
	
	MyObject obj=new MyObject();
	MyClass thread3=new MyClass(obj, "1");
	MyClass thread4=new MyClass(obj, "2");
	thread3.start();
	thread4.start();
	}
	
}

class ThreadExample extends Thread{
int count=0;
public void run(){
	System.out.println("Thread starting.");
	try{
		while(count<5){
			Thread.sleep(500);
			System.out.println("In Thread, count is"+count);
			count++;
		}
	}catch(InterruptedException exc){
		System.out.println("Thread interrupted.");
	}
	System.out.println("Thread terminating.");
}
}
class ExampleB {
public static void main(String args[])
{
	ThreadExample instance=new ThreadExample();
	
instance.start();
while(instance.count!=5){
	try{
		Thread.sleep(250);
		
	}catch(InterruptedException exc){
		exc.printStackTrace();
	}
}

}

}


class MyClass extends Thread {
private String name;
private MyObject myObj;
public MyClass(MyObject obj, String n){
	name=n;
	myObj=obj;
}
public void run(){
	if(name.equals("1")) MyObject.foo(name);
	else if(name.equals("2")) MyObject.bar(name);
	myObj.foo(name);
	
}
}
class MyObject {
/*public synchronized void foo(String name){
	try{
		System.out.println("Thread"+name+".foo(): starting");
		Thread.sleep(3000);
		System.out.println("Thread"+name+"foo.():ending");
		
	}catch(InterruptedException exc){
		System.out.println("Thread"+name+":interrupted.");
	}
}*/

//public static synchronized void foo(String name){}
public static synchronized void foo(String name){
	//synchronized(this)
	{}
}

public static synchronized void bar(String name){}
}


class LockedATM {
private Lock lock;
private int balance=100;
public LockedATM(){
	lock=new ReentrantLock();
}
public int withdraw(int value){
	lock.lock();
	int temp=balance;
	try{
		Thread.sleep(100);
		temp=temp-value;
		Thread.sleep(100);
		balance=temp;
		
	}catch(InterruptedException e){}
	lock.unlock();
	return temp;
}
public int deposit(int value){
	lock.lock();
	int temp=balance;
	try{
		Thread.sleep(100);
		temp=temp+value;
		Thread.sleep(300);
		balance=temp;
	}catch(InterruptedException e){}
	lock.unlock();
	return temp;
}
}

