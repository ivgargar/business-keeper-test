package org.businesskeeper.test.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDate;

import org.businesskeeper.test.constants.Constants;
import org.businesskeeper.test.dao.CurrencyRepository;
import org.businesskeeper.test.entity.CurrencyEntity;
import org.businesskeeper.test.exception.IncorrectInputParameterException;
import org.businesskeeper.test.model.ExchangeRate;
import org.businesskeeper.test.service.impl.FixerIoServiceImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ExchangeRateControllerTest {
	
    @Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Mock
	FixerIoServiceImpl exchangeRateService;
	
	@Mock
	FixerIoServiceImpl fixerIoService;
	
	@Mock
    CurrencyRepository currencyRepository;
	
	@InjectMocks
	private ExchangeRateController exchangeRateController = new ExchangeRateController();
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testInvalidDateFormat() {
		assertFalse(exchangeRateController.isValidDateFormat("aaaa"));
	}
	
	@Test
	public void testValidDateFormat() {
		assertTrue(exchangeRateController.isValidDateFormat("2000-01-01"));
	}
	
	@Test
	public void testInvalidDate() {
		assertFalse(exchangeRateController.isValidDate("3000-34-56"));
	}
	
	@Test
	public void testValidDate() {
		assertTrue(exchangeRateController.isValidDate("2000-01-01"));
	}
	
	@Test
	public void testInvalidRange() {
		LocalDate date = LocalDate.parse(Constants.MINIMUM_DATE).minusDays(1);
		LocalDate min = LocalDate.parse(Constants.MINIMUM_DATE);
		LocalDate max = LocalDate.now().minusDays(1);
		assertFalse(exchangeRateController.isValidDateRange(date, min, max));
		
		date = LocalDate.now();
		assertFalse(exchangeRateController.isValidDateRange(date, min, max));
	}
	
	@Test
	public void testValidRange() {
		LocalDate date = LocalDate.parse("2000-01-01");
		LocalDate min = LocalDate.parse(Constants.MINIMUM_DATE);
		LocalDate max = LocalDate.now().minusDays(1);
		assertTrue(exchangeRateController.isValidDateRange(date, min, max));
	}
	
	@Test
	public void testInvalidCurrencyFormat() {
		assertFalse(exchangeRateController.isValidCurrencyFormat("a"));
		assertFalse(exchangeRateController.isValidCurrencyFormat(" "));
		assertFalse(exchangeRateController.isValidCurrencyFormat("eur"));
		assertFalse(exchangeRateController.isValidCurrencyFormat("DUMMY"));
		assertFalse(exchangeRateController.isValidCurrencyFormat("123"));
	}
	
	@Test
	public void testValidCurrencyFormat() {
		assertTrue(exchangeRateController.isValidCurrencyFormat("EUR"));
		assertTrue(exchangeRateController.isValidCurrencyFormat("USD"));
	}
	
	@Test
	public void testValidCurrencyInDb() {
		CurrencyEntity eurCurrencyEntity = new CurrencyEntity("EUR");
		Mockito.when(currencyRepository.findByName("EUR")).thenReturn(eurCurrencyEntity);
		assertTrue(exchangeRateController.isValidCurrency("EUR"));
	}
	
	@Test
	public void testValidCurrencyNotInDb() {
		Mockito.when(currencyRepository.findByName("EUR")).thenReturn(null);
		Mockito.when(fixerIoService.isValidCurrency("EUR")).thenReturn(true);
		assertTrue(exchangeRateController.isValidCurrency("EUR"));
	}
	
	@Test
	public void testNotValidCurrency() {
		Mockito.when(currencyRepository.findByName("PIP")).thenReturn(null);
		Mockito.when(fixerIoService.isValidCurrency("PIP")).thenReturn(false);
		assertFalse(exchangeRateController.isValidCurrency("PIP"));
	}
	
	
	@Test
	public void testGetExchangeRateWithInvalidDateFormat() {
        thrown.expect(IncorrectInputParameterException.class);
        thrown.expectMessage("Incorrect date format. Date must have yyyy-mm-dd format");
        
		ResponseEntity<ExchangeRate> exchangeRate = exchangeRateController.getExchangeRate("aaaa", "EUR", "USD");
	}
	
	@Test
	public void testGetExchangeRateWithInvalidDate() {
        thrown.expect(IncorrectInputParameterException.class);
        thrown.expectMessage("Not a valid date.");
        
		ResponseEntity<ExchangeRate> exchangeRate = exchangeRateController.getExchangeRate("3000-34-56", "EUR", "USD");
	}
	
	@Test
	public void testGetExchangeRateWithInvalidDateMaxRange() {
        thrown.expect(IncorrectInputParameterException.class);
        thrown.expectMessage("Not a valid date. Date must be between");
        
		ResponseEntity<ExchangeRate> exchangeRate = exchangeRateController.getExchangeRate(LocalDate.now().toString(), "EUR", "USD");
	}
	
	@Test
	public void testGetExchangeRateWithInvalidDateMinRange() {
        thrown.expect(IncorrectInputParameterException.class);
        thrown.expectMessage("Not a valid date. Date must be between");
        
		ResponseEntity<ExchangeRate> exchangeRate = exchangeRateController.getExchangeRate(LocalDate.parse(Constants.MINIMUM_DATE).minusDays(1).toString(), "EUR", "USD");
	}	
	
	@Test
	public void testGetExchangeRateWithInvalidBaseCurrencyFormat() {
		
	}
	
	@Test
	public void testGetExchangeRateWithInvalidTargetCurrencyFormat() {
		
	}
	
	@Test
	public void testGetExchangeRateWithInvalidBaseCurrency() {
		
	}
	
	@Test
	public void testGetExchangeRateWithInvalidTargetCurrency() {
		
	}
	
	
	@Ignore
	@Test
	public void testGetExchangeRate() throws JsonParseException, JsonMappingException, IncorrectInputParameterException, IOException {
		ResponseEntity<ExchangeRate> exchangeRate = exchangeRateController.getExchangeRate("2000-01-01", "EUR", "USD");
		
		assertNotNull(exchangeRate);
	}
	
}
