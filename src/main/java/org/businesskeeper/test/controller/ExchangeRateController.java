package org.businesskeeper.test.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.businesskeeper.test.constants.Constants;
import org.businesskeeper.test.dao.CurrencyRepository;
import org.businesskeeper.test.dao.ExchangeRateRepository;
import org.businesskeeper.test.entity.CurrencyEntity;
import org.businesskeeper.test.entity.ExchangeRateEntity;
import org.businesskeeper.test.enums.Trend;
import org.businesskeeper.test.exception.IncorrectInputParameterException;
import org.businesskeeper.test.model.ExchangeRate;
import org.businesskeeper.test.service.impl.FixerIoServiceImpl;
import org.businesskeeper.test.util.DateUtil;
import org.businesskeeper.test.util.TrendUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Exchange Rate API Test
 * @author Ivan
 *
 */
@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	FixerIoServiceImpl fixerIoService;
	
    @Autowired
    ExchangeRateRepository exchangeRateRepository;
    
    @Autowired
    CurrencyRepository currencyRepository;
	
    /**
     * REST service to display exchange rates.
     * @param date the requested date
     * @param baseCurrency the base currency
     * @param targetCurrency the target currency
     * @return ExchangeRate the requested exchange rate or error following JSON API
     */
    @RequestMapping(method = RequestMethod.GET, 
                    value = "/{date}/{baseCurrency}/{targetCurrency}", 
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExchangeRate> getExchangeRate(@PathVariable String date, @PathVariable String baseCurrency, @PathVariable String targetCurrency) {
    	
    	log.info("/api/exchange-rate/" + date + "/"+ baseCurrency + "/" + targetCurrency + " - START");
    	
		// validate date
    	log.info("Validating date " + date);
		if (!isValidDateFormat(date)) {
			throw new IncorrectInputParameterException("Incorrect date format. Date must have yyyy-mm-dd format");
		}
		if (!isValidDate(date)) {
			throw new IncorrectInputParameterException("Not a valid date.");
		}
		
		LocalDate localDate = LocalDate.parse(date);
		LocalDate minimum = LocalDate.parse(Constants.MINIMUM_DATE);
		LocalDate yesterday = LocalDate.now().minusDays(1);
		if (!isValidDateRange(localDate, minimum, yesterday)) {
			throw new IncorrectInputParameterException("Not a valid date. Date must be between " + minimum.toString() + " and yesterday");
		}
		
		
		// validate currencies
		log.info("Validating base currency");
		if (!isValidCurrencyFormat(baseCurrency)) {
			throw new IncorrectInputParameterException("Incorrect base currency format. Currency parameter must be 3 capital letters long");
		}
		if (!isValidCurrency(baseCurrency)) {
			throw new IncorrectInputParameterException("Base currency " + baseCurrency + " not available");
		}
		CurrencyEntity baseCurr = new CurrencyEntity(baseCurrency);
		currencyRepository.save(baseCurr);
		
		log.info("Validating target currency");
		if (!isValidCurrencyFormat(targetCurrency)) {
			throw new IncorrectInputParameterException("Incorrect target currency format. Currency parameter must be 3 capital letters long");
		}
		if (!isValidCurrency(targetCurrency)) {
			throw new IncorrectInputParameterException("Target currency " + targetCurrency + " not available");
		}
		CurrencyEntity targetCurr = new CurrencyEntity(targetCurrency);
		currencyRepository.save(targetCurr);
		
		
		ExchangeRate exchangeRate = new ExchangeRate();
		
		// check if the rate already exists in DB
		ExchangeRateEntity exchangeRateEntity = exchangeRateRepository.findByDateAndBaseCurrencyAndTargetCurrency(localDate, baseCurrency, targetCurrency);
		if (exchangeRateEntity != null) {
			exchangeRate.setRate(exchangeRateEntity.getRate());
		} else {
			// if the rate doesn't exist call the fixer service then insert the data in DB
			Double rate = fixerIoService.getExchangeRate(localDate, baseCurrency, targetCurrency);
			if (rate == null) {
				return new ResponseEntity<ExchangeRate>(HttpStatus.NO_CONTENT);
			}
			exchangeRate.setRate(rate);
			exchangeRateRepository.save(new ExchangeRateEntity(localDate, baseCurrency, targetCurrency, rate));
		}
		
		// get the exchange rate for the previous 5 days excluding Saturday and Sunday
		log.info("Calculating average exchange rate for the previous 5 days");
		List<Double> lastFiveDaysExchangeRateList = new ArrayList<Double>();
		
		LocalDate auxLocalDate = localDate;
		for (int i = 0; i < 5; i++) {
			auxLocalDate = DateUtil.getPreviousValidDate(auxLocalDate);
			log.debug("Day " + i + " for calculating average exchange rate " + auxLocalDate.toString());
			Double exchange = fixerIoService.getExchangeRate(auxLocalDate, baseCurrency, targetCurrency);
			log.debug("Exchange rate for day " + auxLocalDate.toString() + " is " + exchange);
			if (exchange != null) {
				lastFiveDaysExchangeRateList.add(exchange);
			} else {
				lastFiveDaysExchangeRateList.add(new Double(0.0));
			}
		}
		
		Double average = lastFiveDaysExchangeRateList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
		log.debug("Average rate for the previous 5 days " + average);
		exchangeRate.setAverage(average);
		
		// calculate trend
		log.info("Calculating trend rate for the previous 5 days");
		Collections.reverse(lastFiveDaysExchangeRateList);
		
		double[] lastFiveDaysExchangeRateArray = lastFiveDaysExchangeRateList.stream().mapToDouble(Double::doubleValue).toArray();
		
		Trend trend = Trend.UNDEFINED;
		if (TrendUtil.isAscendingTrend(lastFiveDaysExchangeRateArray)) {
			trend = Trend.ASCENDING;
		} else if (TrendUtil.isDescendingTrend(lastFiveDaysExchangeRateArray)) {
			trend = Trend.DESCENDING;
		} else if (TrendUtil.isConstantTrend(lastFiveDaysExchangeRateArray)) {
			trend = Trend.CONSTANT;
		}
		
		log.debug("Trend is " + trend.getTrend());
		exchangeRate.setTrend(trend.getTrend());
		
		log.info("/api/exchange-rate/" + date + "/"+ baseCurrency + "/" + targetCurrency + " - END");
    		
        return new ResponseEntity<ExchangeRate>(exchangeRate, HttpStatus.OK);
    }
    
    @RequestMapping("/history/daily/{yyyy}/{MM}/{dd}")
    public List<ExchangeRateEntity> getHistoryDaily(@PathVariable("yyyy") String year, @PathVariable("MM") String month, @PathVariable("dd") String day) {
    	return exchangeRateRepository.findByDate(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)));
    }
    
    @RequestMapping("/history/monthly/{yyyy}/{MM}")
    public List<ExchangeRateEntity> getHistoryMonthly(@PathVariable("yyyy") String year, @PathVariable("MM") String month) {
    	LocalDate min = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
    	LocalDate max = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), min.lengthOfMonth()); 
    	return exchangeRateRepository.findByDateBetween(min, max);
    }
    
	public boolean isValidDateFormat(String date) {
		if (!date.matches(Constants.ISO_DATE_FORMAT)) {
			return false;
		}
		return true;
	}
	
	public boolean isValidDate(String date) {
		try {
			LocalDate localDate = LocalDate.parse(date);
		} catch (DateTimeParseException ex) {
			return false;
		}
		return true;
	}
	
	public boolean isValidDateRange(LocalDate date, LocalDate min, LocalDate max) {
		if (date.isBefore(min) || date.isAfter(max)) {
			return false;
		}
		return true;
	}
	
	public boolean isValidCurrencyFormat(String currency) {
		if (!currency.matches(Constants.CURRENCY_FORMAT)) {
			return false;
		}
		return true;
	}
    
	public boolean isValidCurrency(String currency) {
		CurrencyEntity curr = currencyRepository.findByName(currency);
		if (curr == null) {
			if(!fixerIoService.isValidCurrency(currency)) {
				return false;
			}
		}
		return true;
	}
}
