package com.bank.service;

import java.math.BigDecimal;

// Strategy Pattern — C++ equivalent: pure abstract class
// interface = abstract class with all pure virtual methods in C++
public interface ExchangeRateStrategy {
    BigDecimal getRate(String fromCurrency, String toCurrency);
}
