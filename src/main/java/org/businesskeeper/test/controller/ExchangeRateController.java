package org.businesskeeper.test.controller;


import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.businesskeeper.test.exception.IncorrectInputParameterException;
import org.businesskeeper.test.model.ExchangeRate;
import org.businesskeeper.test.service.impl.ExchangeRateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${minimum.date.range}")
	private String minimumDateRange;

	@Autowired
	ExchangeRateServiceImpl exchangeRateService;
	
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
    		if (localDate.isBefore(LocalDate.parse(minimumDateRange))) {
    			throw new IncorrectInputParameterException("Incorrect Date. Date before " + minimumDateRange);
    		}
    		LocalDate yesterday = LocalDate.now().minusDays(1);
    		if (localDate.isAfter(yesterday)) {
    			throw new IncorrectInputParameterException("Incorrect Date. Date after " + yesterday);
    		}
    		
	    	
    		// get the exchange rate for the given date
    		ExchangeRate exchangeRate = new ExchangeRate();
    		Double rate = exchangeRateService.getExchangeRate(localDate, baseCurrency, targetCurrency);
    		exchangeRate.setRate(rate);
    		
    		// get the exchange rate for the previous 5 days excluding Saturday and Sunday
    		int lastFiveDaysCont = 1;
    		int cont = 1;
    		List<Double> lastFiveDaysExchangeRateList = new ArrayList<Double>();
    		while (lastFiveDaysCont < 5) {
    			LocalDate auxLocalDate = localDate.minusDays(cont);
    			if (!DayOfWeek.SATURDAY.equals(DayOfWeek.of(auxLocalDate.get(ChronoField.DAY_OF_WEEK)))
    					&& !DayOfWeek.SUNDAY.equals(DayOfWeek.of(auxLocalDate.get(ChronoField.DAY_OF_WEEK)))) {
    				lastFiveDaysExchangeRateList.add(exchangeRateService.getExchangeRate(auxLocalDate, baseCurrency, targetCurrency));
    				lastFiveDaysCont++;
    			}
    			cont++;
    		}
    		double average = lastFiveDaysExchangeRateList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
    		exchangeRate.setAverage(average);
    		
    		// calculate trend
    		Collections.reverse(lastFiveDaysExchangeRateList);
    		
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
