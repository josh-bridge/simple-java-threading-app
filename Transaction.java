/**
* This class is the basic structure for a transaction with Account.
* It stores basic data for each transaction.
*
* @author  14032908
* @version 1.9
* @since   2015-09-30
*/

public class Transaction
{
	private int amount;
	private long threadId;
	private int resultBalance;

	/**
	* Transaction constructor, initialising class variables
	* 
	* @param amount the transaction amount, to either deposit or withdraw
	* @param resultBalance the balance after the transaction was completed
	* @param threadId the id of the parent thread that initialised this transaction
	*/
	public Transaction(int amount, int resultBalance, long threadId) {
		this.amount = amount;
		this.resultBalance = resultBalance;
		this.threadId = threadId;
	}
	
	/**
	 * Return the transaction amount
	 * 
	 * @return amount as integer
	 */
	public int returnAmount() {
		return this.amount;
	}
	
	/**
	 * Return the balance after the transaction completed
	 * 
	 * @return balance as integer
	 */
	public int returnResultBalance() {
		return this.resultBalance;
	}
	
	/**
	 * Return the ID of the thread that ordered the transaction
	 * 
	 * @return threadID as long
	 */
	public long returnThreadID() {
		return this.threadId;
	}

	/*
	public int returnInitialBalance() {
		return this.resultBalance + (0 - this.amount);
	}
	*/
}