package org.businesskeeper.test.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EXCHANGE_RATE")
public class ExchangeRateEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private LocalDate date;
	private String baseCurrency;
	private String targetCurrency;
	private Double rate;
	
	public ExchangeRateEntity() {
	}

	public ExchangeRateEntity(LocalDate date, String baseCurrency, String targetCurrency, Double rate) {
		this.date = date;
		this.baseCurrency = baseCurrency;
		this.targetCurrency = targetCurrency;
		this.rate = rate;
	}
	
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
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
