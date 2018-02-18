package org.businesskeeper.test.service.impl;

import java.time.LocalDate;

import org.businesskeeper.test.service.FixerIoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of api.fixer.io REST services. 
 * @author ivgargar
 *
 */
@Service
public class FixerIoServiceImpl implements FixerIoService {
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Double getExchangeRate(LocalDate date, String baseCurrency, String targetCurrency) {
		try {
	        ResponseEntity<String> response = restTemplate.getForEntity("https://api.fixer.io/" + date + "?base=" + baseCurrency + "&symbols=" + targetCurrency, String.class);
	        String body = response.getBody();
	        
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode jsonNode = objectMapper.readTree(body);
	        return jsonNode.get("rates").get(targetCurrency).asDouble();
		} catch (HttpClientErrorException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean isValidCurrency(String baseCurrency) {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity("https://api.fixer.io/latest?base=" + baseCurrency, String.class);
			String body = response.getBody();
			
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode jsonNode = objectMapper.readTree(body);
	        return jsonNode.hasNonNull("base");
		} catch (HttpClientErrorException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}

}
