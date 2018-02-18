package org.businesskeeper.test.controller;


import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.businesskeeper.test.dao.ExchangeRateRepository;
import org.businesskeeper.test.entity.ExchangeRateEntity;
import org.businesskeeper.test.enums.Trend;
import org.businesskeeper.test.exception.IncorrectInputParameterException;
import org.businesskeeper.test.model.ExchangeRate;
import org.businesskeeper.test.service.impl.ExchangeRateServiceImpl;
import org.businesskeeper.test.util.TrendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {
	
	@Autowired
	ExchangeRateServiceImpl exchangeRateService;
	
    @Autowired
    ExchangeRateRepository exchangeRateRepository;
	
    @RequestMapping(method = RequestMethod.GET, 
                    value = "/{date}/{baseCurrency}/{targetCurrency}", 
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExchangeRate> getExchangeRate(@PathVariable String date, @PathVariable String baseCurrency, @PathVariable String targetCurrency) throws JsonParseException, JsonMappingException, IOException, IncorrectInputParameterException {
	    	
    		//TODO validate input parameters
    		//if (baseCurrency.equals("EUR")) {
    		//	throw new IncorrectInputParameterException("Incorrect Base Currency");
    		//}
    		
    		// parse date
    		LocalDate localDate = null;
    		try {
    			localDate = LocalDate.parse(date);
    		} catch (DateTimeParseException ex) {
    			throw new IncorrectInputParameterException("Incorrect Date Format. Correct format is yyyy-mm-dd", ex);
    		}
    		
    		//validate date range between minimum date range and yesterday
    		if (localDate.isBefore(LocalDate.parse("2000-01-01"))) {
    			throw new IncorrectInputParameterException("Incorrect Date. Date before " + "2000-01-01");
    		}
    		LocalDate yesterday = LocalDate.now().minusDays(1);
    		if (localDate.isAfter(yesterday)) {
    			throw new IncorrectInputParameterException("Incorrect Date. Date after " + yesterday);
    		}
	    	
    		ExchangeRate exchangeRate = new ExchangeRate();
    		
    		// check if the rate already exists in DB
    		ExchangeRateEntity exchangeRateEntity = exchangeRateRepository.findByDateAndBaseCurrencyAndTargetCurrency(localDate, baseCurrency, targetCurrency);
    		if (exchangeRateEntity != null) {
    			exchangeRate.setRate(exchangeRateEntity.getRate());
    		} else {
    			// if the rate doesn't exist call the fixer service
    			// then insert the data in DB
    			Double rate = exchangeRateService.getExchangeRate(localDate, baseCurrency, targetCurrency);
    			exchangeRate.setRate(rate);
				exchangeRateRepository.save(new ExchangeRateEntity(localDate, baseCurrency, targetCurrency, rate));
    		}
    		
    		// get the exchange rate for the previous 5 days excluding Saturday and Sunday
    		int lastFiveDaysCont = 1;
    		int cont = 1;
    		List<Double> lastFiveDaysExchangeRateList = new ArrayList<Double>();
    		while (lastFiveDaysCont <= 5) {
    			LocalDate auxLocalDate = localDate.minusDays(cont);
    			if (!DayOfWeek.SATURDAY.equals(DayOfWeek.of(auxLocalDate.get(ChronoField.DAY_OF_WEEK)))
    					&& !DayOfWeek.SUNDAY.equals(DayOfWeek.of(auxLocalDate.get(ChronoField.DAY_OF_WEEK)))) {
    				Double exchange = exchangeRateService.getExchangeRate(auxLocalDate, baseCurrency, targetCurrency);
    				lastFiveDaysExchangeRateList.add(exchange);
    				lastFiveDaysCont++;
    			}
    			cont++;
    		}
    		double average = lastFiveDaysExchangeRateList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
    		exchangeRate.setAverage(average);
    		
    		// calculate trend
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
    		
    		exchangeRate.setTrend(trend.getTrend());
    		
        return new ResponseEntity<ExchangeRate>(exchangeRate, HttpStatus.OK);
    }
    
    @RequestMapping("/history/daily/{yyyy}/{MM}/{dd}")
    public String getHistoryDaily(@PathVariable String year, @PathVariable String month, @PathVariable String day) {
    		return "History Daily";
    }
    
    @RequestMapping("/history/monthly/{yyyy}/{MM}")
    public String getHistoryMonthly(@PathVariable String year, @PathVariable String month) {
    		return "History Daily";
    }
    
}
