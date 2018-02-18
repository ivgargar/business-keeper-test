package org.businesskeeper.test.service;

import java.time.LocalDate;

/**
 * Interface for api.fixer.io REST services.
 * @author ivgargar
 *
 */
public interface FixerIoService {
	/**
	 * Gets the exchange rate for a date, base currency and target currency.
	 * @param date the input date
	 * @param baseCurrency the base currency
	 * @param targetCurrency the target currency
	 * @return the exchange rate for the input date, base currency and target currency
	 */
	public Double getExchangeRate(LocalDate date, String baseCurrency, String targetCurrency);
	
	/**
	 * Checks if the input string is corresponding to a valid currency.
	 * @param baseCurrency the input currency string
	 * @return true if string corresponds to a valid currency
	 */
	public boolean isValidCurrency(String baseCurrency);
}
