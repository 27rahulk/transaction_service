/**
 * 
 */
package com.proj.ts.exception;

/**
 * @author Rahul Kumar
 *
 */
public class TransactionTimedOutException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4510672963148425478L;
	
	public TransactionTimedOutException() {
		super();
	}
	
	public TransactionTimedOutException(String message){
		super(message);
	}

}
