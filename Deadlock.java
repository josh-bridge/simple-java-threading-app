/**
 * Deadlock class, to detect when the Card threads are all in a Wait() state, as the balance is too low and all threads are trying to withdraw.
 * 
 * @author  14032908
 * @version 1.6
 * @since   2015-11-03
 */
public class Deadlock {
	private int threadNum;
	/**
	 *  Array of threadID's, purely to map threadID's to array index's
	 */
	private long[] threads;
	/**
	 * Array of thread status'. 
	 * Top array is thread index, bottom array is thread activity and thread locked status.
	 */
	private boolean[][] threadStatus;
	private int activeThreadsCount = 0;

	/**
	 * Deadlock constructor, initialize arrays using the amount of threads that will be created.
	 * 
	 * @param threadNum as the amount of threads that will need to be monitored
	 */
	public Deadlock(int threadNum) {
		this.threadNum = threadNum;
		this.threads = new long[threadNum];
		this.threadStatus = new boolean[threadNum][2];
		
		// In-case a thread has ID = 0, set initial values to -1
		for(int i = 0; i < threadNum; i++) {
			this.threads[i] = (long) -1;
		}
	}
	
	/**
	 * Initialise a thread by taking in its threadID so it can be tracked.
	 * 
	 * @param threadID the ID of the calling thread as long
	 */
	public void openThread(long threadID) {
		for(int i = 0; i < this.threadNum; i++) {
			if(this.threads[i] == (long) -1) {
				this.threads[i] = threadID;
				this.threadStatus[i][0] = true; // true = thread active, false = thread suspended 
				this.threadStatus[i][1] = false; // true = locked, false = free
				this.activeThreadsCount++;
				return;
			}
		}
	}
	
	/**
	 * Close a thread so its status is now ignored in deadlock checking.
	 * 
	 * @param threadID as the ID of the calling thread as long
	 */
	public void closeThread(long threadID) {
		for(int i = 0; i < this.threadNum; i++) {
			if(this.threads[i] == threadID) {
				this.threads[i] = threadID;
				this.threadStatus[i][0] = false;
				this.threadStatus[i][1] = false;
				this.activeThreadsCount--;
				return;
			}
		}
	}
	
	/**
	 * Change the calling threads deadlock status to locked
	 * 
	 * @param threadID as the ID of the calling thread as long
	 */
	public void addLock(long threadID) {
		this.threadStatus[this.getThreadIndex(threadID)][1] = true;
	}
	
	/**
	 * Change the calling threads deadlock status to unlocked
	 * 
	 * @param threadID as the ID of the calling thread as long
	 */
	public void removeLock(long threadID) {
		this.threadStatus[this.getThreadIndex(threadID)][1] = false;
	}
	
	/**
	 * Using a thread ID, find out its array index and return it as an integer
	 * 
	 * @param threadID as the ID of the calling thread as long
	 * @return the array index of the supplied threadID as an integer
	 */
	public int getThreadIndex(long threadID) {
		for(int i = 0; i < this.threadNum; i++) {
			if(this.threads[i] == threadID) {
				return i;
			}
		}
		// Should never return this
		return -1;
	}
	
	/**
	 * If all of the running threads have a locked status, return true
	 * 
	 * @return boolean of whether all threads are locked
	 */
	public boolean allThreadsLocked() {
		if (this.lockedThreadsCount() == this.activeThreadsCount)
			return true;
		return false;
	}
	
	/**
	 * Return the number of threads that have a status of locked
	 * 
	 * @return number of locked threads as integer
	 */
	public int lockedThreadsCount() {
		int lockedCount = 0;
		
		for(int i = 0; i < this.threadNum; i++) {
			if(this.threadStatus[i][1])
				lockedCount++;
		}
		
		return lockedCount;
	}
	
	// No longer needed code - kept just incase

	/*	

	public boolean isLocked(int id) {
		if(this.threadStatus[id][0] && this.threadStatus[id][1])
			return true;
		return false;
	}
	
	public int[] getActiveThreads() {
		boolean[] active = new boolean[this.threadNum];
		int activeCount = 0;
		
		for(int i = 0; i < this.threadNum; i++) {
			if(this.threadStatus[i][0]) {
				active[i] = true;
				activeCount++;
			} else {
				active[i] = false;
			}
		}
		
		int[] activeThreads = new int[activeCount];
		
		for(int i = 0; i < this.threadNum; i++) {
			if(active[i]) {
				for(int activeThread : activeThreads) {
					if(activeThread == 0) {
						activeThread = i;
					}
				}
			}
		}
		
		return activeThreads;
	}
	
	*/
}
