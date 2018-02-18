package org.businesskeeper.test.controller;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.businesskeeper.test.exception.IncorrectInputParameterException;
import org.businesskeeper.test.model.ExchangeRate;
import org.businesskeeper.test.service.impl.FixerIoServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ExchangeRateControllerTest {
	
	@Mock
	FixerIoServiceImpl exchangeRateService;
	
	@InjectMocks
	private ExchangeRateController exchangeRateController = new ExchangeRateController();
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	//TODO test incorrect input parameters
	
	//TODO test correct response
	
	
	@Test
	public void testGetExchangeRate() throws JsonParseException, JsonMappingException, IncorrectInputParameterException, IOException {
		ResponseEntity<ExchangeRate> exchangeRate = exchangeRateController.getExchangeRate("2000-01-01", "EUR", "USD");
		
		assertNotNull(exchangeRate);
	}
	
}
