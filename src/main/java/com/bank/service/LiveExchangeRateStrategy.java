package com.bank.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class LiveExchangeRateStrategy implements ExchangeRateStrategy {

    @Value("${exchange.rate.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public LiveExchangeRateStrategy() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Cacheable(value = "exchangeRates", key = "#fromCurrency + '_' + #toCurrency")
    @SuppressWarnings("unchecked")
    public BigDecimal getRate(String fromCurrency, String toCurrency) {
        try {
            String url = apiUrl + fromCurrency.toUpperCase();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("rates")) {
                throw new RuntimeException("Failed to fetch exchange rates");
            }

            Map<String, Object> rates = (Map<String, Object>) response.get("rates");
            Object rate = rates.get(toCurrency.toUpperCase());

            if (rate == null) {
                throw new RuntimeException("Currency not supported: " + toCurrency);
            }

            return new BigDecimal(rate.toString());
        } catch (Exception e) {
            throw new RuntimeException("Exchange rate fetch failed: " + e.getMessage());
        }
    }
}
