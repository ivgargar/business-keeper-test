package org.businesskeeper.test.service;

import java.time.LocalDate;

public interface FixerIoService {
	public Double getExchangeRate(LocalDate date, String baseCurrency, String targetCurrency);
	
	public boolean isValidCurrency(String baseCurrency);
}
