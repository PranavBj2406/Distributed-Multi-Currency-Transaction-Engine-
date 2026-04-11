package com.bank.service;

import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Transaction;
import com.bank.model.Wallet;
import com.bank.repository.TransactionRepository;
import com.bank.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TransferService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LiveExchangeRateStrategy exchangeRateStrategy;

    // Core transfer logic — cross currency supported
    @Transactional
    public Transaction transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {

        // Fetch both wallets
        Wallet fromWallet = walletRepository.findById(fromWalletId)
            .orElseThrow(() -> new ResourceNotFoundException("Source wallet not found"));

        Wallet toWallet = walletRepository.findById(toWalletId)
            .orElseThrow(() -> new ResourceNotFoundException("Destination wallet not found"));

        // Check sufficient balance
        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance in source wallet");
        }

        // Get exchange rate
        BigDecimal rate = exchangeRateStrategy.getRate(
            fromWallet.getCurrency(),
            toWallet.getCurrency()
        );

        // Calculate converted amount
        BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        // Debit source wallet
        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));

        // Credit destination wallet
        toWallet.setBalance(toWallet.getBalance().add(convertedAmount));

        // Save updated wallets
        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        // Save immutable audit record
        Transaction transaction = new Transaction(
            fromWalletId, toWalletId, amount,
            fromWallet.getCurrency(), toWallet.getCurrency(),
            rate, convertedAmount
        );

        return transactionRepository.save(transaction);
    }

    // Get transaction history for a wallet
    public List<Transaction> getTransactionHistory(Long walletId) {
        return transactionRepository.findByFromWalletIdOrToWalletId(walletId, walletId);
    }
}
