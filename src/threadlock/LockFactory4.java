package threadlock;

/**
 * 16.4 Design a class which provides 
 * a lock only if there are no possible deadlocks,
 */
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import threadlock.LockNode.VisitState;

/*
 * require a process to declare upfront what locks it will need.
 * then verify if a dead-lock would be created by issuing
 * these locks, and we can fail if so.
 * detect deadlocks: 
 * this was the order of locks requested:
 * A={1,2,3,4}
 * B={1,3,5}
 * C={7,5,9,2}
 * this may create a deadlock because we cuold have:
 * A locks 2, waits on 3
 * B locks 3, waits on 5
 * C locks 5, waits on 2
 * An edge (w,v) exist in the 
 * graph if a process declares that it will
 * request lock v immediately after lock w.
 * (1,2)(2,3)(3,4)(1,3)(3,5)(7,5)(5,9)(9,2).
 * 
 */

/*
 * This class will need a declare method, which threads
 * and processes will use to declare what order they will
 * request resources in. This declare method will iterate through
 * the declare order, adding each contiguous pair of elements
 * (v,w) to the graph. afterwards, it will check to see if 
 * any cycles have been created. If any cycles have been
 * created, it will backtrack, removing these edges from
 * the graph, and then exit.
 * 
 * we just have one final component to discuss: how
 * do we detect a cycle? we can detect a cycle by doing
 * a depth first search through each connected component(i.e., each
 * connected part of the graph). Complex algorithms exist
 * to select all the connected components of a graph, but our
 * work in this problem does not require this degree of complexity
 * 
 * we know that if a cycle was created, one of our new edges
 * must be to blame. thus, as long as our depth first search 
 * touches all of these edges at some point, then we know
 * that we have fully searched for a cycle.
 * 
 */

/*
 * we may do several depth first searches, but touchNodes
 * is only initialized once. we iterate until all the values
 * in touchedNodes are false.
 * 
 */

/*
 * assume that all locks and processes(owners) are ordered
 * sequentially.
 */
public class LockFactory4 {
	private static LockFactory4 instance;
	private int numberOfLocks = 5;// default
	private LockNode[] locks;
	// maps from a process or owner to the order that the owner
	// claimed it would call the locks in
	private Hashtable<Integer, LinkedList<LockNode>> lockOrder;

	private LockFactory4(int count) {
	}

	public static LockFactory4 getInstance() {
		return instance;
	}

	public static synchronized LockFactory4 initialize(int count) {
		if (instance == null)
			instance = new LockFactory4(count);
		return instance;
	}

	public boolean hasCycle(Hashtable<Integer, Boolean> touchedNodes,
			int[] resourcesInOrder) {
		// check for a cycle
		for (int resource : resourcesInOrder) {
			if (touchedNodes.get(resource) == false) {
				LockNode n = locks[resource];
				if (n.hasCycle(touchedNodes)) {
					return true;
				}
			}
		}
		return false;
	}

	// to prevent deadlocks, force the processes to declare upfront
	// what order they will need the locks in. verity that this order does not
	// create a deadlock(a cycle in a directed graph
	public boolean declare(int ownerId, int[] resourcesInOrder) {
		Hashtable<Integer, Boolean> touchedNodes = new Hashtable<Integer, Boolean>();
		// add nodes to graph
		int index = 1;
		touchedNodes.put(resourcesInOrder[0], false);
		for (index = 1; index < resourcesInOrder.length; index++) {
			LockNode prev = locks[resourcesInOrder[index - 1]];
			// LockNode prev=locks[resourcesInOrder[index-1]];
			LockNode curr = locks[resourcesInOrder[index]];
			prev.joinTo(curr);
			touchedNodes.put(resourcesInOrder[index], false);
		}
		// if we created a cycle, destroy this resource list and
		// return false
		if (hasCycle(touchedNodes, resourcesInOrder)) {
			for (int j = 1; j < resourcesInOrder.length; j++) {
				LockNode p = locks[resourcesInOrder[j - 1]];
				LockNode c = locks[resourcesInOrder[j]];
				p.remove(c);
			}
			return false;
		}
		// no cycles detected. save the order that was declared,
		// so that we can verify that the process is really calling
		// the locks in the order it said it would
		LinkedList<LockNode> list = new LinkedList<LockNode>();
		for (int i = 0; i < resourcesInOrder.length; i++) {
			LockNode resource = locks[resourcesInOrder[i]];
			list.add(resource);
		}
		lockOrder.put(ownerId, list);

		return true;
	}

	// get the lock, verifying firth that the process is really
	// calling the locks in the order it said it would
	public Lock getLock(int ownerId, int resourceID) {
		LinkedList<LockNode> list = lockOrder.get(ownerId);
		if (list == null)
			return null;
		LockNode head = list.getFirst();
		if (head.getId() == resourceID) {
			list.removeFirst();
			return head.getLock();
		}
		return null;
	}

}

class LockNode {
	public enum VisitState {
		FRESH, VISITING, VISITED
	};

	private ArrayList<LockNode> children;
	private int lockId;
	private Lock lock;
	private int maxLocks;

	public LockNode(int id, int max) {
	}

	// join this to node, checking to make sure that it do not create
	// a cycle
	public void joinTo(LockNode node) {
		children.add(node);
	}

	public void remove(LockNode node) {
		children.remove(node);
	}

	// check for a cycle by doing a depth-first-search
	public boolean hasCycle(Hashtable<Integer, Boolean> touchedNodes) {
		VisitState[] visited = new VisitState[maxLocks];
		for (int i = 0; i < maxLocks; i++) {
			visited[i] = VisitState.FRESH;
		}
		return hasCycle(visited, touchedNodes);
	}

	private boolean hasCycle(VisitState[] visited,
			Hashtable<Integer, Boolean> touchedNodes) {
		if (touchedNodes.containsKey(lockId)) {
			touchedNodes.put(lockId, true);
		}
		if (visited[lockId] == VisitState.VISITING) {
			// we looped back to his node while still visiting
			// it, so we know there is a cycle
			return true;
		} else if (visited[lockId] == VisitState.FRESH) {
			visited[lockId] = VisitState.VISITING;
			for (LockNode n : children) {
				if (n.hasCycle(visited, touchedNodes)) {
					return true;
				}
			}
			visited[lockId] = VisitState.VISITED;
		}
		return false;
	}

	public Lock getLock() {
		if (lock == null)
			lock = new ReentrantLock();
		return lock;
	}

	public int getId() {
		return lockId;
	}

}
/*
 * sketch out pseudocode and possibly implement one of these methods.
 */
