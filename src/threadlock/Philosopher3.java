package threadlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * 
16.3 In the famous dining philosophers problem,
 a bunch of philosophers are sitting around 
 a circular table with one chopstick between 
 each of them. A philosopher needs both 
 chopsticks to eat, and always picks up the 
 left chopstick before the right one. 
 A deadlock could potentially occur if 
 all the philosophers reached for the left 
 chopstick at the same time. Using threads 
 and locks, implement a simulation of the 
 dining philosophers problem that 
prevents deadlocks.
 */
public class Philosopher3 {
private int bites=10;
private Chopstick left;
private Chopstick right;
public Philosopher3(Chopstick left, Chopstick rigth){
	this.left=left;
	this.right=right;
}
public void eat(){
	if(pickUp()){
	chew();
	putDown();
}
}
public boolean pickUp(){
	//attempt to pick up
	if(!left.pickUp()){
		return false;}
	if(!right.pickUp()){
		left.putDown();
		return false;
	}
return true;

}
public void chew(){}
public void putDown(){
	left.putDown();
	right.putDown();
}
public void run(){
	for(int i=0; i<bites;i++){
		eat();
	}
}
}
class Chopstick {
	private Lock lock;
	public Chopstick(){
		lock=new ReentrantLock();
	}
	
public boolean pickUp(){
	//void lock.lock();
	return lock.tryLock();
}
public void putDown(){
	lock.unlock();
}
}
