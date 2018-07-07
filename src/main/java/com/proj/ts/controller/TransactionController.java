/**
 * 
 */
package com.proj.ts.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proj.ts.exception.FutureTransactionException;
import com.proj.ts.exception.TransactionTimedOutException;
import com.proj.ts.model.TransactionAggregate;
import com.proj.ts.model.TransactionDTO;
import com.proj.ts.service.TransactionService;

/**
 * @author Rahul Kumar
 *
 */
@RestController
public class TransactionController {
	
	private static final Logger LOG = LoggerFactory.getLogger(TransactionController.class);
	
	@Autowired
	private TransactionService transactionService;
	
	@RequestMapping(path="/transactions", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public void addTransactions(@RequestBody TransactionDTO dto, HttpServletResponse response) throws IOException {
		LOG.info("adding transaction : {}", dto);
		try {
			transactionService.add(dto);
			response.setStatus(HttpStatus.CREATED.value());
		}catch(TransactionTimedOutException ex) {
			response.setStatus(HttpStatus.NO_CONTENT.value());
		}catch(FutureTransactionException ex) {
			response.sendError(HttpStatus.BAD_REQUEST.value(), "invalid future transaction");
		}
		
	}
	
	@RequestMapping(path="/statistics", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public TransactionAggregate getLastMinuteTransactions() {
		return transactionService.getLastMinuteTransactionAggregate();
	}

}
