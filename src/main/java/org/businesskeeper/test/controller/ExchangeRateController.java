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
import org.businesskeeper.test.model.ExchangeRateHistory;
import org.businesskeeper.test.service.FixerIoService;
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
 * REST Controller for Exchange Rate API Test.
 * @author Ivan
 *
 */
@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	FixerIoService fixerIoService;
	
    @Autowired
    ExchangeRateRepository exchangeRateRepository;
    
    @Autowired
    CurrencyRepository currencyRepository;
	
    /**
     * Service to display exchange rates.
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
		if (currencyRepository.findByName(baseCurrency) == null) {
			currencyRepository.save(baseCurr);
		}
		
		log.info("Validating target currency");
		if (!isValidCurrencyFormat(targetCurrency)) {
			throw new IncorrectInputParameterException("Incorrect target currency format. Currency parameter must be 3 capital letters long");
		}
		if (!isValidCurrency(targetCurrency)) {
			throw new IncorrectInputParameterException("Target currency " + targetCurrency + " not available");
		}
		CurrencyEntity targetCurr = new CurrencyEntity(targetCurrency);
		if (currencyRepository.findByName(targetCurrency) == null) {
			currencyRepository.save(targetCurr);
		}
		
		
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
    
    /**
     * Returns the daily history.
     * @param year the requested year
     * @param month the requested month
     * @param day the requested day
     * @return a list of exchange rates queried that date
     */
    @RequestMapping("/history/daily/{yyyy}/{MM}/{dd}")
    public List<ExchangeRateHistory> getDailyHistory(@PathVariable("yyyy") String year, @PathVariable("MM") String month, @PathVariable("dd") String day) {
    		List<ExchangeRateEntity> exchangeRateEntityList = exchangeRateRepository.findByDate(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)));
    		
    		List<ExchangeRateHistory> exchangeRateHistoryList = new ArrayList<ExchangeRateHistory>();
    		for (ExchangeRateEntity exchangeRateEntity : exchangeRateEntityList) {
    			ExchangeRateHistory exchangeRateHistory = new ExchangeRateHistory(exchangeRateEntity.getDate().toString(), exchangeRateEntity.getBaseCurrency(), exchangeRateEntity.getTargetCurrency(), exchangeRateEntity.getRate());
    			exchangeRateHistoryList.add(exchangeRateHistory);
    		}
    		
    		return exchangeRateHistoryList;
    }
    
    /**
     * Returns the monthly history.
     * @param year the requested year
     * @param month the requested month
     * @return a list of exchange rates queried that date
     */
    @RequestMapping("/history/monthly/{yyyy}/{MM}")
    public List<ExchangeRateHistory> getMonthlyHistory(@PathVariable("yyyy") String year, @PathVariable("MM") String month) {
	    	LocalDate min = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
	    	LocalDate max = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), min.lengthOfMonth()); 
	    	List<ExchangeRateEntity> exchangeRateEntityList = exchangeRateRepository.findByDateBetween(min, max);
    		List<ExchangeRateHistory> exchangeRateHistoryList = new ArrayList<ExchangeRateHistory>();
    		for (ExchangeRateEntity exchangeRateEntity : exchangeRateEntityList) {
    			ExchangeRateHistory exchangeRateHistory = new ExchangeRateHistory(exchangeRateEntity.getDate().toString(), exchangeRateEntity.getBaseCurrency(), exchangeRateEntity.getTargetCurrency(), exchangeRateEntity.getRate());
    			exchangeRateHistoryList.add(exchangeRateHistory);
    		}
    		
    		return exchangeRateHistoryList;
    }
    
    /**
     * Validates the date format.
     * @param date the input string with the date
     * @return true if date has correct format
     */
	public boolean isValidDateFormat(String date) {
		if (!date.matches(Constants.ISO_DATE_FORMAT)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Validates the date.
	 * @param date the input string with the date
	 * @return true if date is correct
	 */
	public boolean isValidDate(String date) {
		try {
			@SuppressWarnings("unused")
			LocalDate localDate = LocalDate.parse(date);
		} catch (DateTimeParseException ex) {
			return false;
		}
		return true;
	}
	
	/**
	 * Validates if an input date is between min and max dates.
	 * @param date the input date
	 * @param min the minimun date
	 * @param max the maximum date
	 * @return true if the input date is between min and max
	 */
	public boolean isValidDateRange(LocalDate date, LocalDate min, LocalDate max) {
		if (date.isBefore(min) || date.isAfter(max)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Validates currency format.
	 * @param currency the currency
	 * @return true if currency has correct format
	 */
	public boolean isValidCurrencyFormat(String currency) {
		if (!currency.matches(Constants.CURRENCY_FORMAT)) {
			return false;
		}
		return true;
	}
    
	/**
	 * Validates currency.
	 * @param currency the currency
	 * @return true if the currency is valid
	 */
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
