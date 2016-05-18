 /**
 * The main class to be run, which simulates a bank account and creates n amount of threads to withdraw and deposit from the account
 * This class is to be run from the command line with args[0] as starting balance, and args[1] as amount of threads to create
 * 
 * @author  14032908
 * @version 1.6
 * @since   2015-09-30
 */
public class Controller
{
	/**
	 *  Create a single account with inputted balance
	 */
	private static Account account;
	/**
	 *  Create a new thread array with inputted card number
	 */
	private static Thread[] threads;
	private static Deadlock deadlock;
	private static int cardsAmt;
	private static int accStartBalance;
	
	/**
	 * If program has become deadlocked, output data and exit to prevent hanging
	 */
	public static void deadlocked() {
		for(int i = 0; i < cardsAmt; i++) {
			threads[i].interrupt();
		}
		System.err.println("");
		System.err.println("DEADLOCK DETECTED. Show all successful transactions then EXIT.");
		account.outputTransactions(0, (account.transactionsSize() - (cardsAmt - 1)));
		System.exit(-1);
	}
	
	/**
	 * Main function, this creates all the objects and threads, sanitises args[] inputs and manages execution.
	 * 
	 * @param args[0] = starting account balance (integer), [1] = amount of card threads to create (integer)
	 */
	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.err.println("Please enter Card amount & Start Balance. EXITING.");
			System.exit(0);
		}

		try {
			accStartBalance = (int) Integer.parseInt(args[1]);
			cardsAmt = (int) Integer.parseInt(args[0]);
		} catch (NumberFormatException n) {
			System.err.println("Arguments were not valid Integers. EXITING.");
			System.exit(0);
		}
		
		deadlock = new Deadlock(cardsAmt);
		account = new Account(accStartBalance, deadlock);
		threads = new Thread[cardsAmt];

		// Initialize and start each thread as object Card
		for(int i = 0; i < cardsAmt; i++) {
			threads[i] = new Thread(new Card(account, deadlock));
			threads[i].start();
		}
		
		// End all threads on completion
		for(int i = 0; i < cardsAmt; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) { }
		}
		
		account.outputTransactions(0, account.transactionsSize());
	}
}
