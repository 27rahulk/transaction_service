/**
 * 
 */
package com.proj.ts.exception;

/**
 * @author Rahul Kumar
 *
 */
public class FutureTransactionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5986440797790913444L;
	
	public FutureTransactionException() {
		super();
	}
	
	public FutureTransactionException(String message){
		super(message);
	}

}
