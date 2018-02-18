package org.businesskeeper.test.dao;

import java.time.LocalDate;

import org.businesskeeper.test.entity.ExchangeRateEntity;
import org.springframework.data.repository.CrudRepository;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRateEntity, Long> {
	ExchangeRateEntity findByDateAndBaseCurrencyAndTargetCurrency(LocalDate localDate, String baseCurrency, String targetCurrency);
}
