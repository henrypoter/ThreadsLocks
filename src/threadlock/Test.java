package threadlock;

public class Test {
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
