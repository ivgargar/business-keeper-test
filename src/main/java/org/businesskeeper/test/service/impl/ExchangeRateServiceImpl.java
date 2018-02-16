package org.businesskeeper.test.service.impl;

import java.io.IOException;
import java.time.LocalDate;

import org.businesskeeper.test.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Double getExchangeRate(LocalDate date, String baseCurrency, String targetCurrency) throws JsonProcessingException, IOException {
        ResponseEntity<String> response = restTemplate.getForEntity("https://api.fixer.io/" + date + "?base=" + baseCurrency + "&symbols=" + targetCurrency, String.class);
        String body = response.getBody();
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(body);
        return jsonNode.get("rates").get(targetCurrency).asDouble();
	}

}
