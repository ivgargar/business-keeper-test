package org.businesskeeper.test.dao;

import org.businesskeeper.test.entity.CurrencyEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Crud repository for currencies.
 * @author ivgargar
 *
 */
public interface CurrencyRepository  extends CrudRepository<CurrencyEntity, Long> {
	CurrencyEntity findByName(String name);
}
