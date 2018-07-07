/**
 * 
 */
package com.proj.ts.model;

/**
 * @author Rahul Kumar
 *
 */
public final class TransactionAggregate {
	
	private Double sum;
	
	private Double avg;
	
	private Double max;
	
	private Double min;
	
	private long count;

	public TransactionAggregate(long count, double sum, double min, double max) {
		if(count<=0) {
			return;
		}
		this.count=count;
		this.sum = sum;
		this.avg = this.sum/this.count ;
		this.max = max;
		this.min = min;
	}

	public TransactionAggregate() {
	}

	public Double getSum() {
		return sum;
	}

	public Double getAvg() {
		return avg;
	}

	public Double getMax() {
		return max;
	}

	public Double getMin() {
		return min;
	}

	public long getCount() {
		return count;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransactionAggregate [sum=");
		builder.append(sum);
		builder.append(", avg=");
		builder.append(avg);
		builder.append(", max=");
		builder.append(max);
		builder.append(", min=");
		builder.append(min);
		builder.append(", count=");
		builder.append(count);
		builder.append("]");
		return builder.toString();
	}	

}
