/**
 * 
 */
package com.proj.ts.service;

import com.proj.ts.model.TransactionAggregate;
import com.proj.ts.model.TransactionDTO;

/**
 * @author Rahul Kumar
 *
 */
public interface TransactionService {

	void add(TransactionDTO dto);

	TransactionAggregate getLastMinuteTransactionAggregate();
	
	void cleanLastMinuteData();

}
