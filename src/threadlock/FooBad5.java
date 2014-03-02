package threadlock;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 16.5 The same instance of Foo will bepassed to three different threads.
 * ThreadA will call first, threads will call second, and threadC will call
 * third. Design a mecha- nism to ensure that first is called before second and
 * second is called before third.
 */
/*
 * general logic is to check if first() has completed before executing second(),
 * and if second() has completed before calling third(). Because we need to be
 * very careful about thread safety, simple boolean flags won't do the job. what
 * about using a lock to do something like the below code?
 */
public class FooBad5 {
	public int pauseTime = 1000;
	public ReentrantLock lock1, lock2, lock3;

	public FooBad5() {
		try {
			lock1 = new ReentrantLock();
			lock2 = new ReentrantLock();
			lock3 = new ReentrantLock();
			lock1.lock();
			lock2.lock();
			lock3.lock();
		} catch (Exception e) {
		}

	}

	public void first() {
		try {
			lock1.unlock();// mark finished with first()
		} catch (Exception e) {
		}
	}

	public void second() {
		try {
			lock1.lock();// wait until finished with first()
			lock1.unlock();
			lock2.unlock();// mark finished with second(
		} catch (Exception e) {
		}

	}

	public void third() {
		try {
			lock2.lock();// wait until finished wiht third(

			lock2.unlock();
		} catch (Exception e) {
		}
	}
}

/*
 * this code won't actually quite work due to the concept of lock ownership. one
 * thread is actually performing the lock( in the FooBad constructor), but
 * different threads attempt to unlock the locks. this is not allowed, you code
 * will raise an exception. a lock in Java is owned by the same thread which
 * locked it.
 */
/*
 * instead, we can replicate this behavior with semaphores. logic is identical.
 */
class Foo {
	public Semaphore sem1, sem2, sem3;

	public Foo() {
		try {
			sem1 = new Semaphore(1);
			sem2 = new Semaphore(1);
			sem3 = new Semaphore(1);
			sem1.acquire();
			sem2.acquire();
			sem3.acquire();
		} catch (Exception e) {
		}
	}

	public void first() {
		try {
			sem1.release();
		} catch (Exception e) {
		}
	}

	public void second() {
		try {
			sem1.acquire();
			sem2.release();
			sem2.release();
		} catch (Exception e) {
		}
	}

	public void third() {
		try {
			sem2.acquire();
			sem2.release();
		} catch (Exception e) {
		}
	}
}
