package threadlock;

public class MyObject {
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
