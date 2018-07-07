/**
 * 
 */
package com.proj.ts.store;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.proj.ts.exception.FutureTransactionException;
import com.proj.ts.exception.TransactionTimedOutException;
import com.proj.ts.model.TransactionAggregate;
import com.proj.ts.model.TransactionDTO;

/**
 * @author Rahul Kumar
 *
 */
@Repository
public class TransactionStore {
	
	private static final Logger LOG = LoggerFactory.getLogger(TransactionStore.class);
	private static final int SECONDS_IN_MINUTE = 60;
	private static final int ONE = 1;
	
	private List<TransactionDTO> transactionLog = new LinkedList<>(); // holds all dtos
	private ScheduledExecutorService executor =new ScheduledThreadPoolExecutor(10);
	private volatile KeySetView<TransactionDTO, Boolean> perMinuteData = ConcurrentHashMap.newKeySet(); // holds dtos for last minute
	private volatile TransactionAggregate lastMinuteAggregate = new TransactionAggregate(); 
	private List<ScheduledFuture<?>> schedules = new LinkedList<>();
	
	public void add(TransactionDTO dto) {
		transactionLog.add(dto);
		LOG.error("difference timestamp dto :{} , calculated : {}", new Object[] {dto.getTimestamp(), Instant.now().minusSeconds(SECONDS_IN_MINUTE).toEpochMilli()});
		if(dto.getTimestamp() < Instant.now().minusSeconds(SECONDS_IN_MINUTE).toEpochMilli()) {
			throw new TransactionTimedOutException("transaction time is more than minute old");
		}else if(Instant.now().isBefore(Instant.ofEpochMilli(dto.getTimestamp()))) {
			throw new FutureTransactionException("Invalid future transaction");
		}
		
		long delay = Instant.ofEpochMilli(dto.getTimestamp()).plusSeconds(SECONDS_IN_MINUTE).toEpochMilli() - Instant.now().toEpochMilli();
		if(delay>0) {
			perMinuteData.add(dto);
			scheduleTransactionExpiry(delay);
			addToAggregate(dto);
		}
		LOG.debug("delay : {}",delay);
		LOG.debug("lastMinuteAggregate : {}",lastMinuteAggregate);
	}

	public TransactionAggregate getLastMinuteTransactionAggregate() {
		return lastMinuteAggregate;
	}
	
	private void addToAggregate(TransactionDTO dto) {
		synchronized (lastMinuteAggregate) {
			long count = lastMinuteAggregate.getCount()+ONE;
			Double sum = (lastMinuteAggregate.getSum()==null?0:lastMinuteAggregate.getSum())+dto.getAmount();
			Double min = (lastMinuteAggregate.getMin()==null || lastMinuteAggregate.getMin()>dto.getAmount())?dto.getAmount():lastMinuteAggregate.getMin();
			Double max = (lastMinuteAggregate.getMax()==null || lastMinuteAggregate.getMax()<dto.getAmount())?dto.getAmount():lastMinuteAggregate.getMax();
			lastMinuteAggregate = new TransactionAggregate(count, sum, min, max);
		}
	}

	private void scheduleTransactionExpiry(long delay){
		schedules.add(executor.schedule(new Runnable() {
			@Override
			public void run() {
				LOG.debug("refreshing minute aggregate at : {}", Instant.now().toString());
				refreshPerMinuteDTO();
			}}, delay+ONE, TimeUnit.MILLISECONDS));
	}

	private void refreshPerMinuteDTO() {
		perMinuteData.removeIf(t->{return (t.getTimestamp() < Instant.now().minusSeconds(SECONDS_IN_MINUTE).toEpochMilli());});
		recalculateAggregate();
	}
	
	private void recalculateAggregate() {
		double sum  = 0; double max= 0;double min = Double.MAX_VALUE;long count = 0;
		for(TransactionDTO dto : perMinuteData) {
			LOG.debug("amount addition : {}",dto.getAmount());
			sum += dto.getAmount();
			max = dto.getAmount()>max?dto.getAmount():max;
			min = dto.getAmount()<min?dto.getAmount():min;
			count++;
		}
		synchronized(lastMinuteAggregate) {
			lastMinuteAggregate = new TransactionAggregate(count, sum, min, max);
		}
	}

	public void cleanLastMinuteData() {
		perMinuteData.clear();
	}

}
