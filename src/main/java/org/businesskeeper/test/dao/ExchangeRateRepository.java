package org.businesskeeper.test.dao;

import java.time.LocalDate;
import java.util.List;

import org.businesskeeper.test.entity.ExchangeRateEntity;
import org.springframework.data.repository.CrudRepository;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRateEntity, Long> {
	ExchangeRateEntity findByDateAndBaseCurrencyAndTargetCurrency(LocalDate date, String baseCurrency, String targetCurrency);
	
	List<ExchangeRateEntity> findByDate(LocalDate date);
	
	List<ExchangeRateEntity> findByDateBetween(LocalDate min, LocalDate max);
}
