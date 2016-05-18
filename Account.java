import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The main account class which handles deposits and withdrawals from each Card,
 * it also keeps track of every transaction via the Transaction class.
 * 
 * @author  14032908
 * @version 2.0
 * @since   2015-09-30
 */
public class Account
{
	private int balance;
	private int initBalance;
	/**
	 * Boolean to track whether a transaction is currently taking place
	 */
	private boolean finished = true;
	/**
	 * Transactions as a synchronized list, therefore transactions will only be added one after the other instead of multiple at once.
	 */
	private List<Transaction> transactions = Collections.synchronizedList(new ArrayList<Transaction>());
	/**
	 * The object reference to the deadlock object currently in use
	 */
	private Deadlock deadlock;
	
	/**
	 * Account constructor, initializes balance and passes deadlock object reference.
	 * 
	 * @param balance to initialize class variables balance and initBalance (To remember the initial account balance)
	 * @param deadlock set the deadlock reference within the class in order to be able to use its functions
	 */
	public Account(int balance, Deadlock deadlock) {
		this.balance = this.initBalance = balance;
		this.deadlock = deadlock;
	}

	/**
	 * Set the account balance. 
	 * Private because only the account should be able to directly edit the balance.
	 * 
	 * @param amount as new balance
	 * @throws InterruptedException
	 */
	private void setBalance(int amount) throws InterruptedException { 
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {throw e;}

		while (finished) { 	//  Wait for other thread to finish transaction (Should never reach this - getBalance always comes before setBalance)
			try { wait(); }
			catch (InterruptedException e) {throw e;}
		}

		balance = amount;
		this.finished = true;
		notifyAll();
	}
	
	/**
	 * Get the account balance.
	 * Public because returning the balance doesn't affect program function.
	 * 
	 * @param justReturn if true, just return the balance, don't lock the function using this.finished
	 * @return currentBalance as integer
	 * @throws InterruptedException
	 */
	public int getBalance(boolean justReturn) throws InterruptedException {
		while (!finished)  {	// Wait for other thread to finish transaction
			try { wait(); }
			catch (InterruptedException e) {throw e;}
		}
		
		// Wait for other threads to finish before attempting balance retrieval
		if(justReturn)
			return this.balance;
		
		this.finished = false;
		int currentBalance = this.balance;

		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {throw e;}
		return currentBalance;
	}

	/**
	 * Deposit a set amount into the account balance.
	 * 
	 * @param amount as amount to deposit
	 * @param threadId as the thread that ordered the deposit, to store in the transaction log
	 * @throws InterruptedException
	 */
	public void deposit(int amount, long threadId) throws InterruptedException {
		int startBalance, resultBalance;
		synchronized(this) {
			try {startBalance = getBalance(false);}
				catch (InterruptedException e) {throw e;}
			
			resultBalance = startBalance + amount;
			
			try {this.setBalance(resultBalance);}
				catch(InterruptedException e) {throw e;}
		}
		this.transactions.add(new Transaction(amount, resultBalance, threadId));
	}
	
	/**
	 * Withdraw a set amount from the account balance.
	 * 
	 * @param amount as amount to withdraw
	 * @param threadId as the thread that ordered the deposit, to store in the transaction log
	 * @throws InterruptedException
	 */
	public void withdraw(int amount, long threadId) throws InterruptedException {
		boolean belowZero;
		int startBalance, resultBalance = -1;

		do {
			belowZero = false;
			synchronized(this) {
				try {startBalance = this.getBalance(false);}
					catch (InterruptedException e) {throw e;}
				
				if(startBalance <= 0 || startBalance - amount <= 0)
					belowZero = true;
				
				if(!belowZero) {
					resultBalance = startBalance - amount;
					try{this.setBalance(resultBalance);}
						catch(InterruptedException e) {throw e;}
				} else {
					// If balance was too low to proceed, notify all other threads to attempt proceeding
					this.continueThreads();
				}
			}

			if(belowZero) {
				// Preemptively add lock to current thread
				// If all other threads are locked then the program cannot continue from here anyway
				this.deadlock.addLock(threadId);
				
				if(this.deadlock.allThreadsLocked())
					Controller.deadlocked(); // Terminate the program if all threads locked
				
				try {
					Thread.sleep((int) Math.random() * 200); 
				} catch (InterruptedException e) {throw e;}
			}
		} while(belowZero);
		
		// If program managed to withdraw successfully, remove its locked status
		this.deadlock.removeLock(threadId);
		this.transactions.add(new Transaction((0 - amount), resultBalance, threadId));
	}

	/**
	 * If the balance is too close to 0, this will allow other threads to continue (if they are depositing).
	 */
	private synchronized void continueThreads() {
		this.finished = true;
		notifyAll();
	}

	/**
	 * Output the transactions from the log into a table, from the startAt to endAt parameters.
	 * 
	 * @param startAt index position to start from
	 * @param endAt index position to end at
	 */
	public void outputTransactions(int startAt, int endAt) {		
		// Output transaction table
		System.out.println();

		String format = "%-13s%-13s%-13s%-13s\n";
		System.out.format(format, "Transaction", "Withdrawal", "Deposit", "Balance");

		System.out.format(format, "", "", "", this.initBalance);

		String deposit, withdrawal;
		int currentBalance, threadID, netTransAmount, posTransAmount, prevBalance = this.initBalance;

		for (int i = startAt; i < endAt; i++) {
			netTransAmount	   = this.transactions.get(i).returnAmount(); 
			currentBalance 	   = this.transactions.get(i).returnResultBalance(); 
			threadID 		   = (int) this.transactions.get(i).returnThreadID();

			if (netTransAmount < 0) {
				// Withdrawals are stored as negative amount, so convert to positive number
				posTransAmount = 0 - netTransAmount;
				withdrawal = Integer.toString(posTransAmount);
				deposit = "";
			} else {
				withdrawal = "";
				deposit = Integer.toString(netTransAmount);
			}

    		System.out.format(format, (i+1)+"("+threadID+")", withdrawal, deposit, currentBalance);

    		// Check if transaction gave invalid result
    		if(prevBalance + netTransAmount != currentBalance)
    			System.out.println(" ERROR CONDITION ERROR CONDITION ERROR CONDITION");

    		prevBalance = currentBalance;
		}
	}

	/**
	 * Return the size of the transactions ArrayList
	 * 
	 * @return size as integer
	 */
	public int transactionsSize() {
		return this.transactions.size();
	}

}
