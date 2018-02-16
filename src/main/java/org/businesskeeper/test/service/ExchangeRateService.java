package org.businesskeeper.test.service;

import java.io.IOException;
import java.time.LocalDate;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ExchangeRateService {
	public Double getExchangeRate(LocalDate date, String baseCurrency, String targetCurrency) throws JsonProcessingException, IOException;
}
