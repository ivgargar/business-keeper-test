package org.businesskeeper.test.controller;


import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.businesskeeper.test.model.ExchangeRate;
import org.businesskeeper.test.service.impl.ExchangeRateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {
	
	@Autowired
	ExchangeRateServiceImpl exchangeRateService;
	
    @RequestMapping("/{date}/{baseCurrency}/{targetCurrency}")
    public ExchangeRate getExchangeRate(@PathVariable String date, @PathVariable String baseCurrency, @PathVariable String targetCurrency) throws JsonParseException, JsonMappingException, IOException {
	    	// parse date
	    	LocalDate localDate = LocalDate.parse(date);
	    	
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
    		
    		
        return exchangeRate;
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
