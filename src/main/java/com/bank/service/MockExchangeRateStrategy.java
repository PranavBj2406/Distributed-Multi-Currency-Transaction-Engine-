package com.bank.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

// This is used in unit tests — mocks the live API
// Strategy Pattern: swap this in place of LiveExchangeRateStrategy
@Service
public class MockExchangeRateStrategy implements ExchangeRateStrategy {

    private static final Map<String, BigDecimal> MOCK_RATES = new HashMap<>();

    static {
        MOCK_RATES.put("USD_INR", new BigDecimal("83.50"));
        MOCK_RATES.put("INR_USD", new BigDecimal("0.012"));
        MOCK_RATES.put("USD_EUR", new BigDecimal("0.92"));
        MOCK_RATES.put("EUR_USD", new BigDecimal("1.09"));
        MOCK_RATES.put("USD_GBP", new BigDecimal("0.79"));
        MOCK_RATES.put("GBP_USD", new BigDecimal("1.27"));
        MOCK_RATES.put("INR_EUR", new BigDecimal("0.011"));
        MOCK_RATES.put("EUR_INR", new BigDecimal("90.50"));
    }

    @Override
    public BigDecimal getRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return BigDecimal.ONE;
        }
        String key = fromCurrency.toUpperCase() + "_" + toCurrency.toUpperCase();
        BigDecimal rate = MOCK_RATES.get(key);
        if (rate == null) {
            throw new RuntimeException("Mock rate not found for: " + key);
        }
        return rate;
    }
}
