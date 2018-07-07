/**
 * 
 */
package com.proj.ts.service;

import static org.junit.Assert.assertEquals;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.proj.ts.exception.FutureTransactionException;
import com.proj.ts.exception.TransactionTimedOutException;
import com.proj.ts.model.TransactionDTO;

/**
 * @author Rahul Kumar
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {

	@Autowired
	private TransactionService transactionService;
	
	@Before
	public void cleanLastMinute(){
		transactionService.cleanLastMinuteData();
	}
	
	@Test
	public void addLastMinuteTransaction() {
		TransactionDTO dto = new TransactionDTO();
		dto.setAmount(34.7);
		dto.setTimestamp(Instant.now().minusSeconds(10).toEpochMilli());
		transactionService.add(dto);
		assertEquals(1,transactionService.getLastMinuteTransactionAggregate().getCount());
	}
	
	@Test(expected=TransactionTimedOutException.class)
	public void addExpiredTransaction() {
		TransactionDTO dto = new TransactionDTO();
		dto.setAmount(34.7);
		dto.setTimestamp(Instant.now().minusSeconds(61).toEpochMilli());
		transactionService.add(dto);
	}
	
	@Test(expected=FutureTransactionException.class)
	public void addFutureTransaction() {
		TransactionDTO dto = new TransactionDTO();
		dto.setAmount(34.7);
		dto.setTimestamp(Instant.now().plusSeconds(5).toEpochMilli());
		transactionService.add(dto);
	}
	
	@Test
	public void transactionExpiry() throws InterruptedException {
		TransactionDTO dto = new TransactionDTO();
		dto.setAmount(34.7);
		dto.setTimestamp(Instant.now().minusSeconds(10).toEpochMilli());
		transactionService.add(dto);
		Thread.sleep(51*1000);
		assertEquals(0,transactionService.getLastMinuteTransactionAggregate().getCount());
	}
	
	@Test
	public void minuteExpiry() throws InterruptedException {
		TransactionDTO dto = new TransactionDTO();
		dto.setAmount(34.7);
		dto.setTimestamp(Instant.now().minusSeconds(10).toEpochMilli());
		transactionService.add(dto);
		Thread.sleep(49*1000);
		assertEquals(1,transactionService.getLastMinuteTransactionAggregate().getCount());
	}
	
	@Test
	public void addMultipleTransaction() {
		TransactionDTO dto = new TransactionDTO();
		dto.setAmount(34.7);
		dto.setTimestamp(Instant.now().minusSeconds(10).toEpochMilli());
		transactionService.add(dto);
		TransactionDTO dto2 = new TransactionDTO();
		dto2.setAmount(4.7);
		dto2.setTimestamp(Instant.now().minusSeconds(15).toEpochMilli());
		transactionService.add(dto2);
		assertEquals(2,transactionService.getLastMinuteTransactionAggregate().getCount());
		assertEquals(Double.valueOf(4.7),transactionService.getLastMinuteTransactionAggregate().getMin());
	}
}
