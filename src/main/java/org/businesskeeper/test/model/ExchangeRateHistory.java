package org.businesskeeper.test.model;

public class ExchangeRateHistory {
	private String date;
	private String baseCurrency;
	private String targetCurrency;
	private Double rate;
	
	public ExchangeRateHistory() {
		
	}
	
	public ExchangeRateHistory(String date, String baseCurrency, String targetCurrency, Double rate) {
		this.date = date;
		this.baseCurrency = baseCurrency;
		this.targetCurrency = targetCurrency;
		this.rate = rate;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getBaseCurrency() {
		return baseCurrency;
	}
	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}
	public String getTargetCurrency() {
		return targetCurrency;
	}
	public void setTargetCurrency(String targetCurrency) {
		this.targetCurrency = targetCurrency;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	
	
}
