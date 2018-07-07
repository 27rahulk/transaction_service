/**
 * 
 */
package com.proj.ts.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proj.ts.model.TransactionAggregate;
import com.proj.ts.model.TransactionDTO;
import com.proj.ts.service.TransactionService;
import com.proj.ts.store.TransactionStore;

/**
 * @author Rahul Kumar
 *
 */
@Service
public class TransactionServiceImpl implements TransactionService {
	
	@Autowired
	private TransactionStore transactionStore;

	@Override
	public void add(TransactionDTO dto) {
		transactionStore.add(dto);		
	}

	@Override
	public TransactionAggregate getLastMinuteTransactionAggregate() {
		return transactionStore.getLastMinuteTransactionAggregate();
	}

	@Override
	public void cleanLastMinuteData() {
		transactionStore.cleanLastMinuteData();		
	}

}
