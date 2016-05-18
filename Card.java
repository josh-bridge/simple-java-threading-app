/**
 * This class is to be used as an instance for each thread.
 * It randomly generates deposits or withdrawals for 20 times, then prints its net transaction amount and exits.
 *
 * @author  14032908
 * @version 1.7
 * @since   2015-09-30
 */
public class Card implements Runnable
{
	private Account account;
	private Deadlock deadlock;
	private int netTransaction;
	private long threadID;

	/**
	* Card constructor, initialising class variables
	* 
	* @param account object reference, for it to pass into each transaction
	* @param deadlock object reference, also for it to pass into each transaction
	*/
	public Card(Account account, Deadlock deadlock) {
		this.account = account;
		this.deadlock = deadlock;
	}

	/**
	* The main thread, automatically called in parent process via Thread.start(),
	*/
	public void run() {
		this.threadID = Thread.currentThread().getId();

		// Initialise this thread in the deadlock object to monitor its status
		deadlock.openThread(this.threadID);

		for (int i = 0; i < 20; i++) { 
			try {		
				int transactionAmount = (int)(Math.random()*10);

				if (Math.random() > 0.5) {
					account.withdraw(transactionAmount, this.threadID);
					// Store withdrawal as negative
					transactionAmount = 0 - transactionAmount;
				} else {
					account.deposit(transactionAmount, this.threadID);
				}

				this.netTransaction += transactionAmount;
				
				Thread.sleep(200);
			} catch (InterruptedException ie) {
				deadlock.closeThread(this.threadID);
				return;
			}
		}
		
		// Stop monitoring this thread in the deadlock object, now it has completed
		deadlock.closeThread(this.threadID);

		System.out.println("THREAD "+ this.threadID + " " + (0 - this.netTransaction));
	}

}
